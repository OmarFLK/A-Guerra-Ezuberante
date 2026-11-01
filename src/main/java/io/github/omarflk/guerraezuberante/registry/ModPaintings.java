package io.github.omarflk.guerraezuberante.registry;

import io.github.omarflk.guerraezuberante.GuerraEzuberanteMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/** Quadros satíricos fornecidos pelo autor para a decoração das bases e vilas. */
public final class ModPaintings {
    private static final DeferredRegister<PaintingVariant> REGISTER =
            DeferredRegister.create(Registries.PAINTING_VARIANT, GuerraEzuberanteMod.MOD_ID);

    public static final RegistryObject<PaintingVariant> FELIZ_DATAS = painting("feliz_datas", 64, 32);
    public static final RegistryObject<PaintingVariant> HAPPY_NATION = painting("happy_nation", 64, 32);
    public static final RegistryObject<PaintingVariant> ME_RESPEITA = painting("me_respeita", 64, 64);
    public static final RegistryObject<PaintingVariant> PERGUNTA = painting("pergunta", 32, 32);
    public static final RegistryObject<PaintingVariant> RACHADINHA_TRES_PORCENTO = painting("rachadinha_tres_porcento", 32, 32);
    public static final RegistryObject<PaintingVariant> SACOS_LULA_FLAVIO = painting("sacos_lula_flavio", 64, 64);
    public static final RegistryObject<PaintingVariant> MESTRE_DOS_BAGOS = painting("mestre_dos_bagos", 32, 48);
    public static final RegistryObject<PaintingVariant> BOLSONARO_JOINHA = painting("bolsonaro_joinha", 32, 32);
    public static final RegistryObject<PaintingVariant> UM_PAU = painting("um_pau", 64, 32);
    public static final RegistryObject<PaintingVariant> TWEET_AUTODECLARACAO = painting("tweet_autodeclaracao", 64, 48);

    public static final List<RegistryObject<PaintingVariant>> ALL = List.of(
            FELIZ_DATAS, HAPPY_NATION, ME_RESPEITA, PERGUNTA, RACHADINHA_TRES_PORCENTO,
            SACOS_LULA_FLAVIO, MESTRE_DOS_BAGOS, BOLSONARO_JOINHA, UM_PAU, TWEET_AUTODECLARACAO);

    private static RegistryObject<PaintingVariant> painting(String id, int width, int height) {
        return REGISTER.register(id, () -> new PaintingVariant(width, height));
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    private ModPaintings() {}
}
