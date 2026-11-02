package io.github.omarflk.guerraezuberante.faction;

import io.github.omarflk.guerraezuberante.GuerraEzuberanteMod;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(GuerraEzuberanteMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TemporaryBlockPersistenceGameTests {
    @GameTest(template="empty", batch="ge_temporary_persistence")
    public static void unloadedBlocksKeepTheirCleanupDebtAcrossSavedDataReload(GameTestHelper h) {
        var pos = new BlockPos(1_000_000, 200, 1_000_000);
        h.assertTrue(!h.getLevel().hasChunkAt(pos), "Fixture chunk was loaded");
        var block = new CompoundTag();
        block.putLong("Pos", pos.asLong());
        block.put("State", NbtUtils.writeBlockState(Blocks.JUNGLE_LEAVES.defaultBlockState()));
        block.putLong("Until", h.getLevel().getGameTime() + 100);
        var list = new ListTag(); list.add(block);
        var saved = new CompoundTag(); saved.put("Blocks", list);
        var pending = TemporaryBlocks.data(h.getLevel());
        try {
            pending.entries.putAll(TemporaryBlocks.Pending.load(saved).entries);
            TemporaryBlocks.cleanup(h.getLevel(), true);
            var afterStop = pending.save(new CompoundTag());
            var loaded = TemporaryBlocks.Pending.load(afterStop);
            h.assertTrue(loaded.entries.containsKey(pos), "Shutdown forgot unloaded temporary block");
            h.assertTrue(afterStop.getList("Blocks", 10).stream().map(t -> (CompoundTag) t)
                    .anyMatch(t -> t.getLong("Pos") == pos.asLong() && t.getLong("Until") == Long.MIN_VALUE),
                    "Saved cleanup debt did not expire on shutdown");
            h.assertTrue(!h.getLevel().hasChunkAt(pos), "Cleanup loaded a distant chunk");
        } finally { pending.entries.remove(pos); pending.setDirty(); }
        h.succeed();
    }
}
