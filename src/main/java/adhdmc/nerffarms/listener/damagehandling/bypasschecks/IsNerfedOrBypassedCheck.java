package adhdmc.nerffarms.listener.damagehandling.bypasschecks;

import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;

public class IsNerfedOrBypassedCheck {
    /**
     * Checks if the mob has already been marked for nerfing, returns true if it has
     * @param nerfMob NamespacedKey
     * @param bypass NamespacedKey
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @return boolean
     */
    public static boolean isNerfedOrBypassed(NamespacedKey nerfMob, NamespacedKey bypass, Entity entity, PersistentDataContainer mobPDC) {
        Util.debugLvl1("Performing isNerfedOrBypassed on " + entity.getName());
        if (mobPDC.has(nerfMob)) {
            Util.debugLvl2(entity.getName() + " is already nerfed, ignoring, and returning true");
            return true;
        }
        if (mobPDC.has(bypass)) {
            Util.debugLvl2(entity.getName() + " is bypassed, ignoring, and returning true");
            return true;
        }
        return false;
    }
}
