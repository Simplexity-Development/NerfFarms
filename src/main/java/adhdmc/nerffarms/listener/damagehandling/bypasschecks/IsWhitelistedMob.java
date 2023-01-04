package adhdmc.nerffarms.listener.damagehandling.bypasschecks;

import adhdmc.nerffarms.config.ConfigParser;
import adhdmc.nerffarms.listener.damagehandling.DamageListener;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class IsWhitelistedMob {
    /**
     * Checks mob type, sets mob to bypass future checks and returns true if the mob is on the configured whitelist
     * @param entity Damaged Entity
     * @param mobPDC Mob's persistent data container
     * @param bypass NamespacedKey
     * @return boolean
     */
    public static boolean isWhitelistedMob(Entity entity, PersistentDataContainer mobPDC, NamespacedKey bypass) {
        Util.debugLvl1("Performing isWhitelistedMob on " + entity.getName());
        if (ConfigParser.getWhitelistedMobList().contains(entity.getType())) {
            Util.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " is on the Whitelisted Mob list as "
            + entity.getType() + ". Setting mob to bypass future checks. Returning true");
            mobPDC.set(bypass, PersistentDataType.BYTE, DamageListener.t);
            return true;
        }
        Util.debugLvl2("Cleared all 'isWhitelistedMob' checks. Returning false");
        return false;
    }
}
