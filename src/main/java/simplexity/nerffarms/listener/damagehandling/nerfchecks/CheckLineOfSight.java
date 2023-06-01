package simplexity.nerffarms.listener.damagehandling.nerfchecks;

import simplexity.nerffarms.util.ConfigToggle;
import simplexity.nerffarms.listener.damagehandling.AddPDCDamage;
import simplexity.nerffarms.util.CheckUtils;
import simplexity.nerffarms.util.Debug;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class CheckLineOfSight {
    /**
     * Checks if the mob has a valid line of sight to the player
     * Returns false if they do not
     * @param nerfMob NamespacedKey
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    public static boolean checkLineOfSight(NamespacedKey nerfMob, EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        Debug.debugLvl1("Performing checkLineOfSight on " + entity.getName());
        if (!(entity instanceof LivingEntity)) return true;
        if (!ConfigToggle.REQUIRE_LINE_OF_SIGHT.isEnabled()) return true;
        Entity damager = CheckUtils.getRealDamager(event);
        if (damager == null) {
            Debug.debugLvl2("Mob does not have a viable line-of-sight because 'getRealDamager' has returned a null value. " +
            "Returning false");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return false;
        }
        boolean lineOfSight = ((LivingEntity) entity).hasLineOfSight(damager);
        if (!lineOfSight) {
            Debug.debugLvl2("Mob does not have a viable line-of-sight to " + damager + ". Returning false");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return false;
        }
        Debug.debugLvl2("Cleared all line-of-sight checks. Returning true");
        return true;
    }
}
