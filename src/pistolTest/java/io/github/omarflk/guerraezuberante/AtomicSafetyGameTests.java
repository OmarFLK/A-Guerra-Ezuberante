package io.github.omarflk.guerraezuberante;

import io.github.omarflk.guerraezuberante.combat.AtomicBlast;
import io.github.omarflk.guerraezuberante.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import java.util.function.Consumer;

@GameTestHolder(GuerraEzuberanteMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AtomicSafetyGameTests {
    @GameTest(template="empty", batch="ge_atomic_start")
    public static void cancelledAtomicStartPreservesWorldAndVictims(GameTestHelper h) {
        var pos = h.absolutePos(new BlockPos(3, 240, 3));
        h.getLevel().setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        var source = EntityType.HUSK.create(h.getLevel());
        source.moveTo(Vec3.atBottomCenterOf(pos)); source.setNoAi(true); h.getLevel().addFreshEntity(source);
        Consumer<ExplosionEvent.Start> cancel = event -> {
            if (event.getExplosion().getDirectSourceEntity() == source) { event.setCanceled(true); }
        };
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, cancel);
        try {
            float health = source.getHealth();
            h.assertTrue(AtomicBlast.detonate(h.getLevel(), source.position(), source) == 0, "Cancelled atomic blast hit entities");
            h.assertTrue(source.getHealth() == health && h.getLevel().getBlockState(pos.below()).is(Blocks.STONE),
                    "Cancelled atomic blast changed the world");
        } finally { MinecraftForge.EVENT_BUS.unregister(cancel); source.discard(); }
        h.succeed();
    }

    @GameTest(template="empty", batch="ge_atomic_filter", timeoutTicks=120)
    public static void atomicDetonateFiltersEntitiesAndEachBlockBatch(GameTestHelper h) {
        var pos = h.absolutePos(new BlockPos(3, 240, 3));
        var source = EntityType.HUSK.create(h.getLevel());
        source.moveTo(Vec3.atBottomCenterOf(pos)); source.setNoAi(true); h.getLevel().addFreshEntity(source);
        double radius = ModConfig.ENEIAS_NUKE_RADIUS.get();
        int crater = ModConfig.ENEIAS_NUKE_CRATER_RADIUS.get();
        boolean breaks = ModConfig.ENEIAS_NUKE_BREAK_BLOCKS.get();
        ModConfig.ENEIAS_NUKE_RADIUS.set(6D); ModConfig.ENEIAS_NUKE_CRATER_RADIUS.set(3); ModConfig.ENEIAS_NUKE_BREAK_BLOCKS.set(true);
        var protectedPos = pos.below();
        var openPos = pos.offset(1, -1, 0);
        h.getLevel().setBlockAndUpdate(protectedPos, Blocks.STONE.defaultBlockState());
        h.getLevel().setBlockAndUpdate(openPos, Blocks.STONE.defaultBlockState());
        int[] batches = {0};
        Consumer<ExplosionEvent.Detonate> filter = event -> {
            if (event.getExplosion().getDirectSourceEntity() != source) { return; }
            event.getAffectedEntities().clear();
            if (!event.getAffectedBlocks().isEmpty()) { batches[0]++; }
            event.getAffectedBlocks().remove(protectedPos);
        };
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, filter);
        Runnable cleanup = () -> {
            MinecraftForge.EVENT_BUS.unregister(filter); source.discard();
            ModConfig.ENEIAS_NUKE_RADIUS.set(radius); ModConfig.ENEIAS_NUKE_CRATER_RADIUS.set(crater);
            ModConfig.ENEIAS_NUKE_BREAK_BLOCKS.set(breaks);
        };
        try {
            h.assertTrue(AtomicBlast.detonate(h.getLevel(), source.position(), source) == 0, "Filtered victim took damage");
        } catch (RuntimeException | Error failure) { cleanup.run(); throw failure; }
        h.runAfterDelay(15, () -> {
            try {
                h.assertTrue(batches[0] > 0 && h.getLevel().getBlockState(protectedPos).is(Blocks.STONE)
                        && h.getLevel().getBlockState(openPos).isAir(), "Atomic crater ignored block filter or did not excavate");
            } finally { cleanup.run(); }
            h.succeed();
        });
    }
}
