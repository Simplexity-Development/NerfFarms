package simplexity.nerffarms.listener.damagehandling.nerfchecks;

import simplexity.nerffarms.util.NerfFarmsConfig;
import simplexity.nerffarms.listener.damagehandling.AddPDCDamage;
import simplexity.nerffarms.util.Debug;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class CheckStandingOn {
    /**
     * Checks the block the damaged mob is standing on, and compares it to the configured blacklist
     * Returns true if the block matches one of the blacklisted type
     * @param nerfMob NamespacedKey
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    public static boolean checkStandingOn(NamespacedKey nerfMob, EntityDamageEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        Debug.debugLvl1("Performing checkStandingOn on " + entity.getName());
        Location mobStandingOnLocation = entity.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        if (NerfFarmsConfig.getStandOnBlackList().contains(entityStandingOn)) {
            Debug.debugLvl2(entityStandingOn + " is a 'blacklisted-below' block. Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Debug.debugLvl2("Cleared all CheckStandingOn checks. Returning false");
        return false;
    }
}
