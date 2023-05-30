package simplexity.nerffarms.listener.damagehandling;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import simplexity.nerffarms.events.MobValidationEvent;
import simplexity.nerffarms.listener.damagehandling.bypasschecks.*;
import simplexity.nerffarms.listener.damagehandling.nerfchecks.*;
import simplexity.nerffarms.util.NFKey;
import simplexity.nerffarms.util.Debug;

public class DamageListener implements Listener {
    private final NamespacedKey nerfMob = NFKey.NERF_MOB.getKey();
    private final NamespacedKey bypassEntity = NFKey.BYPASS_MOB.getKey();
    public static final NamespacedKey blacklistedDamage = NFKey.BLACKLISTED_DAMAGE.getKey();
    public static final byte t = 1;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDamage(EntityDamageEvent damageEvent) {
        Entity damagedEntity = damageEvent.getEntity();
        if (!(damagedEntity instanceof Mob mob)) return;
        MobValidationEvent mobValidationEvent = new MobValidationEvent(mob);
        Bukkit.getPluginManager().callEvent(mobValidationEvent);
        if (mobValidationEvent.isCancelled()) return;
        mobValidationEvent.runChecks();
        if (mobValidationEvent.isCancelled()) return;
         = damageEvent.getFinalDamage();

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

        Debug.debugLvl1(damagedEntity.getName() + " has reached the end of mob damage calculations");
    }
}
