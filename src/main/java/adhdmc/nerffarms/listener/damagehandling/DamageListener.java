package adhdmc.nerffarms.listener.damagehandling;

import adhdmc.nerffarms.listener.damagehandling.bypasschecks.*;
import adhdmc.nerffarms.listener.damagehandling.nerfchecks.*;
import adhdmc.nerffarms.util.NFKey;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class DamageListener implements Listener {
    private final NamespacedKey nerfMob = NFKey.NERF_MOB.getKey();
    private final NamespacedKey bypassEntity = NFKey.BYPASS_MOB.getKey();
    public static final NamespacedKey blacklistedDamage = NFKey.BLACKLISTED_DAMAGE.getKey();
    public static final byte t = 1;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDamage(EntityDamageEvent damageEvent) {
        Entity damagedEntity = damageEvent.getEntity();
        PersistentDataContainer mobPDC = damagedEntity.getPersistentDataContainer();
        // Ignore Event Checks
        if (!IsMobCheck.isMob(damagedEntity)) return;
        if (IsNerfedOrBypassedCheck.isNerfedOrBypassed(nerfMob, bypassEntity, damagedEntity, mobPDC)) return;
        if (!IsHostileCheck.checkHostile(damagedEntity)) return;
        if (IsWhitelistedSpawnReasonCheck.isWhitelistedSpawnReason(mobPDC, damagedEntity, bypassEntity)) return;
        if (IsBlacklistedSpawnReason.isBlacklistedSpawnReason(nerfMob, damagedEntity)) return;
        if (IsWhitelistedMob.isWhitelistedMob(damagedEntity, mobPDC, bypassEntity)) return;
        if (IsBlacklistedMob.isBlacklistedMob(nerfMob, damagedEntity)) return;

        double damageAmount = damageEvent.getFinalDamage();

        // Nerfable Damage Checks
        if (damageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (CheckDamager.checkDamager(nerfMob, damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (CheckDistance.checkDistance(nerfMob, damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (!CheckLineOfSight.checkLineOfSight(nerfMob, damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (CheckPath.checkPath(nerfMob, damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
        }

        if (CheckDamageType.checkDamageType(nerfMob, damageEvent, damagedEntity, mobPDC, damageAmount)) return;
        if (CheckStandingOn.checkStandingOn(nerfMob, damageEvent, damagedEntity, mobPDC, damageAmount)) return;
        if (CheckStandingInside.checkStandingInside(nerfMob, damageEvent, damagedEntity, mobPDC, damageAmount)) return;

        Util.debugLvl1(damagedEntity.getName() + " has reached the end of mob damage calculations");
    }
}
