package io.github.omarflk.guerraezuberante.faction;

import io.github.omarflk.guerraezuberante.GuerraEzuberanteMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Blocks a power puts in the world for a few seconds (the leaves of Raízes da Amazônia). Only on empty space, only
 * where the player may build (for a character: only with mobGriefing on), and protection mods can cancel the placement. They go away on time whatever happens to
 * the caster. Shutdown clears loaded blocks and persists cleanup debts for unloaded chunks; a block someone already
 * changed is left alone.
 */
@Mod.EventBusSubscriber(modid = GuerraEzuberanteMod.MOD_ID)
public final class TemporaryBlocks {
    private record Placed(BlockPos pos, BlockState state, long removeAt) {}

    /** Per-dimension data survives unloaded chunks, normal shutdowns and saved checkpoints. */
    static final class Pending extends SavedData {
        final Map<BlockPos, Placed> entries = new LinkedHashMap<>();

        static Pending load(CompoundTag tag) {
            var result = new Pending();
            for (Tag value : tag.getList("Blocks", Tag.TAG_COMPOUND)) {
                CompoundTag block = (CompoundTag) value;
                BlockPos pos = BlockPos.of(block.getLong("Pos"));
                BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), block.getCompound("State"));
                result.entries.put(pos, new Placed(pos, state, block.getLong("Until")));
            }
            return result;
        }

        @Override public CompoundTag save(CompoundTag tag) {
            var blocks = new ListTag();
            for (Placed placed : entries.values()) {
                var block = new CompoundTag();
                block.putLong("Pos", placed.pos().asLong());
                block.put("State", NbtUtils.writeBlockState(placed.state()));
                block.putLong("Until", placed.removeAt());
                blocks.add(block);
            }
            tag.put("Blocks", blocks);
            return tag;
        }
    }

    static Pending data(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(Pending::load, Pending::new, "guerra_ezuberante_temporary_blocks");
    }

    /** Places the block for {@code ticks}; returns false when the spot is not free or the caster may not build there. */
    public static boolean place(LivingEntity actor, BlockPos pos, BlockState state, int ticks) {
        if (!(actor.level() instanceof ServerLevel level) || !level.hasChunkAt(pos) || !level.getWorldBorder().isWithinBounds(pos)
                || !level.getBlockState(pos).isAir()) {
            return false;
        }
        boolean allowed = actor instanceof ServerPlayer player ? player.mayBuild() && level.mayInteract(player, pos)
                : ForgeEventFactory.getMobGriefingEvent(level, actor);
        if (!allowed) {
            return false;
        }
        var snapshot = BlockSnapshot.create(level.dimension(), level, pos);
        level.setBlock(pos, state, Block.UPDATE_ALL);
        // Asked after placing, like vanilla: a protection mod that cancels gets the spot back as it was.
        if (MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(snapshot, level.getBlockState(pos.relative(Direction.DOWN)), actor))) {
            snapshot.restore(true, false);
            return false;
        }
        Pending pending = data(level);
        pending.entries.put(pos.immutable(), new Placed(pos.immutable(), state, level.getGameTime() + ticks));
        pending.setDirty();
        return true;
    }

    public static boolean isPlaced(Level level, BlockPos pos) {
        return level instanceof ServerLevel server && data(server).entries.containsKey(pos);
    }

    private static void remove(ServerLevel level, Placed placed) {
        if (level.getBlockState(placed.pos()) != placed.state()) { return; }
        // Break particles and sound, no drops.
        level.levelEvent(2001, placed.pos(), Block.getId(placed.state()));
        level.setBlock(placed.pos(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        for (ServerLevel level : event.getServer().getAllLevels()) { cleanup(level, false); }
    }

    static void cleanup(ServerLevel level, boolean stopping) {
        Pending pending = data(level);
        var iterator = pending.entries.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Placed placed = entry.getValue();
            if (!level.hasChunkAt(placed.pos())) {
                // Keep the debt on disk; next load must remove this block even if its timer had time left.
                if (stopping) {
                    entry.setValue(new Placed(placed.pos(), placed.state(), Long.MIN_VALUE));
                    pending.setDirty();
                }
                continue;
            }
            if (!stopping && level.getGameTime() < placed.removeAt()) { continue; }
            remove(level, placed);
            iterator.remove();
            pending.setDirty();
        }
    }

    @SubscribeEvent
    public static void stopping(ServerStoppingEvent event) {
        for (ServerLevel level : event.getServer().getAllLevels()) { cleanup(level, true); }
    }

    private TemporaryBlocks() {}
}
