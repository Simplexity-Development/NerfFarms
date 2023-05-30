package simplexity.nerffarms.listener.damagehandling.nerfchecks;

import simplexity.nerffarms.util.NFConfig;
import simplexity.nerffarms.listener.damagehandling.AddPDCDamage;
import simplexity.nerffarms.util.Debug;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class CheckStandingInside {
    /**
     * Checks the block the damaged mob is standing inside, and compares it to the configured blacklist
     * Returns true if the block matches one of the blacklisted types
     * @param nerfMob NamespacedKey
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    public static boolean checkStandingInside(NamespacedKey nerfMob, EntityDamageEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        Debug.debugLvl1("Performing checkStandingInside on " + entity.getName());
        Material entityStandingIn = entity.getLocation().getBlock().getType();
        if (NFConfig.getInsideBlackList().contains(entityStandingIn)) {
            Debug.debugLvl2(entityStandingIn + " is a 'blacklisted-in' block. Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Debug.debugLvl2("Cleared all 'checkStandingInside' checks. Returning false");
        return false;
    }
}
