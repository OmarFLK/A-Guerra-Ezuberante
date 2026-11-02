package io.github.omarflk.guerraezuberante.combat;

import io.github.omarflk.guerraezuberante.GuerraEzuberanteMod;
import io.github.omarflk.guerraezuberante.faction.EsquerdaPowers;
import io.github.omarflk.guerraezuberante.summon.OwnedSummon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Damage cancellation also rejects the extra movement and status effects belonging to that hit. */
@Mod.EventBusSubscriber(modid = GuerraEzuberanteMod.MOD_ID)
public final class CombatSafety {
    public static boolean hurtIgnoringFrames(LivingEntity victim, DamageSource source, float amount) {
        int invulnerability = victim.invulnerableTime;
        victim.invulnerableTime = 0;
        boolean effective = hurt(victim, source, amount);
        if (!effective) { victim.invulnerableTime = invulnerability; }
        return effective;
    }

    public static boolean hurt(LivingEntity victim, DamageSource source, float amount) {
        float health = victim.getHealth(), absorption = victim.getAbsorptionAmount();
        var movement = victim.getDeltaMovement();
        boolean accepted = victim.hurt(source, amount);
        boolean effective = accepted && (victim.getHealth() < health || victim.getAbsorptionAmount() < absorption);
        if (!effective) { victim.setDeltaMovement(movement); }
        return effective;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cannotAttackFromParis(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof OwnedSummon summon && summon.getOwnerId() != null) {
            attacker = attacker.level().getPlayerByUUID(summon.getOwnerId());
        }
        if (attacker instanceof ServerPlayer player && EsquerdaPowers.inParis(player)) {
            event.setCanceled(true);
        }
    }

    private CombatSafety() {}
}
