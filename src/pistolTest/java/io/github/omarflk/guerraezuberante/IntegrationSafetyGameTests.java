package io.github.omarflk.guerraezuberante;

import com.mojang.authlib.GameProfile;
import io.github.omarflk.guerraezuberante.faction.EsquerdaPowers;
import io.github.omarflk.guerraezuberante.faction.Faction;
import io.github.omarflk.guerraezuberante.faction.Factions;
import io.github.omarflk.guerraezuberante.faction.ZemaCaiadoPowers;
import io.github.omarflk.guerraezuberante.progression.PlayerClass;
import io.github.omarflk.guerraezuberante.progression.PlayerClasses;
import io.github.omarflk.guerraezuberante.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import java.util.UUID;
import java.util.function.Consumer;

/** Regressions for cancellation and Paris, reproduced during the fork review. */
@GameTestHolder(GuerraEzuberanteMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class IntegrationSafetyGameTests {
    @GameTest(template="empty", batch="ge_integration_cancel")
    public static void cancelledDamagePreservesEffectsAndMovement(GameTestHelper h) {
        var caster = FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "ReviewCancel"));
        caster.setGameMode(GameType.SURVIVAL);
        var at = Vec3.atBottomCenterOf(h.absolutePos(new BlockPos(3, 220, 3)));
        caster.moveTo(at.x, at.y, at.z, 0, 0);
        var victim = EntityType.HUSK.create(h.getLevel());
        victim.setNoAi(true);
        victim.moveTo(at.x + 8, at.y, at.z, 0, 0);
        h.getLevel().addFreshEntity(victim);
        Consumer<LivingAttackEvent> cancel = event -> { if (event.getEntity() == victim) event.setCanceled(true); };
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, cancel);
        float health = victim.getHealth();
        try {
            victim.setDeltaMovement(Vec3.ZERO);
            ZemaCaiadoPowers.laco(caster, victim, 1);
            h.assertTrue(victim.getHealth() == health && victim.getDeltaMovement().lengthSqr() < .0001,
                    "Cancelled lasso moved the target");
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1));
            ZemaCaiadoPowers.privatizar(caster, victim, 1);
            h.assertTrue(victim.getHealth() == health && victim.hasEffect(MobEffects.MOVEMENT_SPEED)
                    && !caster.hasEffect(MobEffects.MOVEMENT_SPEED) && !victim.hasEffect(ModEffects.PARALYZED.get()),
                    "Cancelled privatization transferred effects or paralyzed");
        } finally { MinecraftForge.EVENT_BUS.unregister(cancel); victim.discard(); }
        h.succeed();
    }

    @GameTest(template="empty", batch="ge_integration_paris")
    public static void parisBlocksClassSkillsAndDamage(GameTestHelper h) {
        var player = FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "ReviewParis"));
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        Factions.assign(player, Faction.ESQUERDA);
        PlayerClasses.assign(player, PlayerClass.COMBATENTE);
        player.setShiftKeyDown(true);
        var at = Vec3.atBottomCenterOf(h.absolutePos(new BlockPos(3, 220, 3)));
        player.moveTo(at.x, at.y, at.z, 0, 0);
        var victim = EntityType.HUSK.create(h.getLevel());
        victim.setNoAi(true);
        victim.moveTo(at.x + .5, at.y, at.z, 0, 0);
        h.getLevel().addFreshEntity(victim);
        float health = victim.getHealth();
        player.getPersistentData().putLong("GuerraEzuberanteParisUntil", h.getLevel().getServer().overworld().getGameTime() + 200);
        try {
            h.assertTrue(EsquerdaPowers.inParis(player) && !PlayerClasses.useSkill(player),
                    "Paris allowed class skill");
            PlayerClasses.tickCharge(player);
            h.assertTrue(victim.getHealth() == health, "Paris charge damaged the enemy");
            victim.hurt(h.getLevel().damageSources().playerAttack(player), 6);
            h.assertTrue(victim.getHealth() == health, "Paris allowed direct or projectile player damage");
        } finally {
            player.getPersistentData().remove("GuerraEzuberanteParisUntil");
            victim.discard();
            PlayerClasses.clear(player);
            Factions.clear(player);
        }
        h.succeed();
    }
}
