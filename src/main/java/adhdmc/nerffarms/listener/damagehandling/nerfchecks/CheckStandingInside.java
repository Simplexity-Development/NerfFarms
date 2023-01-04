package adhdmc.nerffarms.listener.damagehandling.nerfchecks;

import adhdmc.nerffarms.config.ConfigParser;
import adhdmc.nerffarms.listener.damagehandling.AddPDCDamage;
import adhdmc.nerffarms.util.Util;
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
        Util.debugLvl1("Performing checkStandingInside on " + entity.getName());
        Material entityStandingIn = entity.getLocation().getBlock().getType();
        if (ConfigParser.getInsideBlackList().contains(entityStandingIn)) {
            Util.debugLvl2(entityStandingIn + " is a 'blacklisted-in' block. Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Util.debugLvl2("Cleared all 'checkStandingInside' checks. Returning false");
        return false;
    }
}
