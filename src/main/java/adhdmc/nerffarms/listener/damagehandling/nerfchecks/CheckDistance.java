package adhdmc.nerffarms.listener.damagehandling.nerfchecks;

import adhdmc.nerffarms.util.NFConfig;
import adhdmc.nerffarms.listener.damagehandling.AddPDCDamage;
import adhdmc.nerffarms.util.CheckUtils;
import adhdmc.nerffarms.util.Util;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class CheckDistance {
    /**
     * Checks the distance between the player and the damaged entity, compares it to the configured max distance
     * Returns true if the number is above the max distance
     * @param nerfMob NamespacedKey
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    public static boolean checkDistance(NamespacedKey nerfMob, EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        Util.debugLvl1("Performing checkDistance on " + entity.getName());
        LivingEntity damager = CheckUtils.getRealDamager(event);
        if (damager == null) {
            Util.debugLvl2("Cannot check distance because the return value of getRealDamager is null. Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Location entityLoc = entity.getLocation();
        Location damagerLoc = damager.getLocation();
        double entityHeight = entityLoc.getY();
        double damagerHeight = damagerLoc.getY();
        double distanceBetween = entityLoc.distance(damagerLoc);
        double heightDifference = Math.abs(entityHeight - damagerHeight);
        if (distanceBetween > NFConfig.getMaxDistance()) {
            Util.debugLvl2(entity.getName() + " is above the max configured distance from the damager. Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        if (heightDifference > NFConfig.getMaxHeightDifference()) {
            Util.debugLvl2(entity.getName() + " is above the max configured height difference from the damager. Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Util.debugLvl2("Cleared all 'checkDistance' checks. Returning false");
        return false;
    }
}
