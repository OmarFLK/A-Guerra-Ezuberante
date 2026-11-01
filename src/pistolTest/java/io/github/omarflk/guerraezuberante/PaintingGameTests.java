package io.github.omarflk.guerraezuberante;

import io.github.omarflk.guerraezuberante.registry.ModPaintings;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

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

    private PaintingGameTests() {}
}
