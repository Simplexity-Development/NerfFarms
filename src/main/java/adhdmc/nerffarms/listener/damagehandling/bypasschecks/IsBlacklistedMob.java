package adhdmc.nerffarms.listener.damagehandling.bypasschecks;

import adhdmc.nerffarms.util.NFConfig;
import adhdmc.nerffarms.listener.damagehandling.DamageListener;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class IsBlacklistedMob {
    /**
     * Checks mob type, nerfs and returns true if the mob is on the configured blacklist
     * @param nerfMob NamespacedKey
     * @param entity Damaged Entity
     * @return boolean
     */
    public static boolean isBlacklistedMob(NamespacedKey nerfMob, Entity entity) {
        Util.debugLvl1("Performing isWhitelistedMob on " + entity.getName());
        if (NFConfig.getBlacklistedMobList().contains(entity.getType())) {
            entity.getPersistentDataContainer().set(nerfMob, PersistentDataType.BYTE, DamageListener.t);
            Util.debugLvl2("Nerfing " + entity.getName() + " Because they are on the blacklisted mob types as "
                    + entity.getType() + ". Nerfing and returning true");
            return true;
        }
        Util.debugLvl2("Cleared all 'isWhitelistedMob' checks. Returning false");
        return false;
    }
}
