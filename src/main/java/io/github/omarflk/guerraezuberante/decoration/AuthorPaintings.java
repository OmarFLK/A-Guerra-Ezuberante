package io.github.omarflk.guerraezuberante.decoration;

import io.github.omarflk.guerraezuberante.GuerraEzuberanteMod;
import io.github.omarflk.guerraezuberante.registry.ModPaintings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/** Keeps loaded vanilla decoration consistent with the author's exclusive painting catalogue. */
@Mod.EventBusSubscriber(modid = GuerraEzuberanteMod.MOD_ID)
public final class AuthorPaintings {
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof Painting painting) || !isVanilla(painting)) return;
        // Entity loading can run off-thread. Wait for the server rather than querying chunks from the join event.
        level.getServer().tell(new TickTask(level.getServer().getTickCount() + 1, () -> {
            if (!painting.isRemoved() && level.getEntity(painting.getUUID()) == painting && isVanilla(painting)) {
                replaceVanilla(painting);
            }
        }));
    }

    private static boolean isVanilla(Painting painting) {
        return painting.getVariant().unwrapKey()
                .map(key -> key.location().getNamespace().equals("minecraft")).orElse(false);
    }

    private static void replaceVanilla(Painting painting) {
        var original = painting.getVariant();
        int width = painting.getWidth(), height = painting.getHeight();
        var candidates = new ArrayList<>(ModPaintings.ALL);
        Collections.shuffle(candidates, new Random(painting.getUUID().getLeastSignificantBits()));
        candidates.sort(Comparator.comparingInt((RegistryObject<PaintingVariant> entry) ->
                entry.get().getWidth() * entry.get().getHeight()).reversed());
        for (var entry : candidates) {
            PaintingVariant variant = entry.get();
            // Never enlarge an existing decoration or overlap its neighbours.
            if (variant.getWidth() > width || variant.getHeight() > height) continue;
            painting.setVariant(BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(
                    ResourceKey.create(Registries.PAINTING_VARIANT, entry.getId())));
            if (painting.survives()) return;
        }
        // Some vanilla paintings are only 1 block wide/high; the author's smallest art is 2 x 2.
        // Return the normal item (respecting doEntityDrops) instead of stretching or cropping an image.
        painting.setVariant(original);
        painting.dropItem(null);
        painting.discard();
    }

    private AuthorPaintings() {}
}
