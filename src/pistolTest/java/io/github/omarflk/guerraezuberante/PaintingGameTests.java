package io.github.omarflk.guerraezuberante;

import io.github.omarflk.guerraezuberante.registry.ModPaintings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.HashSet;

/** Confere que os quadros fornecidos pelo autor entram no registro em ambos os pacotes de teste. */
@GameTestHolder(GuerraEzuberanteMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PaintingGameTests {
    @GameTest(template = "empty", batch = "paintings")
    public static void allTenPaintingsAreRegistered(GameTestHelper helper) {
        helper.assertTrue(ModPaintings.ALL.size() == 10, "Esperados dez quadros próprios");
        helper.assertTrue(ModPaintings.ALL.stream().allMatch(entry -> entry.isPresent() &&
                        entry.get().getWidth() >= 32 && entry.get().getHeight() >= 32),
                "Todos os quadros devem estar registrados com dimensões válidas");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "paintings")
    public static void onlyAuthorPaintingsCanBePlaced(GameTestHelper helper) {
        var actual = new HashSet<ResourceLocation>();
        BuiltInRegistries.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE)
                .forEach(holder -> actual.add(holder.unwrapKey().orElseThrow().location()));
        var expected = new HashSet<ResourceLocation>();
        ModPaintings.ALL.forEach(entry -> expected.add(entry.getId()));
        helper.assertTrue(actual.equals(expected), "Catálogo deve conter apenas os dez quadros do autor: " + actual);
        BlockPos anchor = wall(helper);
        for (int attempt = 0; attempt < 32; attempt++) {
            var painting = Painting.create(helper.getLevel(), anchor, Direction.NORTH);
            helper.assertTrue(painting.isPresent(), "Parede livre deve aceitar um quadro do autor");
            helper.assertTrue(expected.contains(painting.orElseThrow().getVariant().unwrapKey().orElseThrow().location()),
                    "Seleção real retornou um quadro vanilla");
        }
        // Removing support down to one block must not fall back to a tiny vanilla painting.
        clearWall(helper, anchor);
        helper.getLevel().setBlockAndUpdate(anchor.south(), Blocks.STONE.defaultBlockState());
        helper.assertTrue(Painting.create(helper.getLevel(), anchor, Direction.NORTH).isEmpty(),
                "Parede 1 x 1 não pode selecionar arte vanilla nem esticar a arte do autor");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "paintings", timeoutTicks = 40)
    public static void savedVanillaPaintingBecomesAuthorArtWithoutLosingIdentity(GameTestHelper helper) {
        BlockPos anchor = wall(helper);
        var vanilla = new Painting(helper.getLevel(), anchor, Direction.NORTH,
                BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(ResourceKey.create(
                        Registries.PAINTING_VARIANT, new ResourceLocation("minecraft", "fighters"))));
        vanilla.setCustomName(Component.literal("Decoração preservada"));
        var uuid = vanilla.getUUID();
        CompoundTag saved = new CompoundTag();
        vanilla.save(saved);
        var restored = (Painting) EntityType.loadEntityRecursive(saved, helper.getLevel(), entity -> entity);
        helper.assertTrue(restored != null && helper.getLevel().addFreshEntity(restored), "NBT do quadro não carregou");
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(!restored.isRemoved() && restored.getUUID().equals(uuid), "Quadro perdeu identidade");
            helper.assertTrue(restored.getVariant().unwrapKey().orElseThrow().location().getNamespace()
                    .equals(GuerraEzuberanteMod.MOD_ID), "Quadro vanilla carregado não foi substituído");
            helper.assertTrue(restored.getPos().equals(anchor) && restored.getDirection() == Direction.NORTH
                    && restored.getWidth() <= 64 && restored.getHeight() <= 32 && restored.survives(),
                    "Substituição moveu, ampliou ou deixou o quadro sem apoio");
            helper.assertTrue(restored.getCustomName().getString().equals("Decoração preservada"), "Nome perdido");
            restored.discard();
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "paintings", timeoutTicks = 40)
    public static void tinyVanillaPaintingReturnsItemAndLeavesWallUntouched(GameTestHelper helper) {
        BlockPos anchor = helper.absolutePos(new BlockPos(3, 3, 3)).atY(230);
        helper.getLevel().setBlockAndUpdate(anchor.south(), Blocks.STONE.defaultBlockState());
        var tiny = new Painting(helper.getLevel(), anchor, Direction.NORTH,
                BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(ResourceKey.create(
                        Registries.PAINTING_VARIANT, new ResourceLocation("minecraft", "kebab"))));
        helper.getLevel().addFreshEntity(tiny);
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(tiny.isRemoved(), "Arte vanilla 1 x 1 continuou pendurada");
            var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(anchor).inflate(3),
                    item -> item.getItem().is(Items.PAINTING));
            helper.assertTrue(drops.size() == 1 && drops.get(0).getItem().getCount() == 1, "Item não foi devolvido uma única vez");
            helper.assertTrue(helper.getLevel().getBlockState(anchor.south()).is(Blocks.STONE), "Parede foi alterada");
            drops.forEach(ItemEntity::discard);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "paintings", timeoutTicks = 40)
    public static void existingAuthorPaintingKeepsExactVariant(GameTestHelper helper) {
        BlockPos anchor = wall(helper);
        var holder = BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(ResourceKey.create(
                Registries.PAINTING_VARIANT, ModPaintings.RACHADINHA_TRES_PORCENTO.getId()));
        var painting = new Painting(helper.getLevel(), anchor, Direction.NORTH, holder);
        helper.getLevel().addFreshEntity(painting);
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(!painting.isRemoved() && painting.getVariant().equals(holder) && painting.survives(),
                    "Quadro original do autor foi trocado ou removido");
            painting.discard();
            helper.succeed();
        });
    }

    private static BlockPos wall(GameTestHelper helper) {
        BlockPos anchor = helper.absolutePos(new BlockPos(3, 3, 3)).atY(220);
        for (int x = -2; x <= 2; x++) for (int y = -2; y <= 2; y++) {
            helper.getLevel().setBlockAndUpdate(anchor.offset(x, y, 1), Blocks.STONE.defaultBlockState());
        }
        return anchor;
    }

    private static void clearWall(GameTestHelper helper, BlockPos anchor) {
        for (int x = -2; x <= 2; x++) for (int y = -2; y <= 2; y++) {
            helper.getLevel().removeBlock(anchor.offset(x, y, 1), false);
        }
    }

    private PaintingGameTests() {}
}
