package adhdmc.nerffarms.listener.damagehandling.bypasschecks;

import adhdmc.nerffarms.util.NFConfig;
import adhdmc.nerffarms.listener.damagehandling.DamageListener;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class IsBlacklistedSpawnReason {
    /**
     * Checks mob spawn reason, nerfs and returns true if the SpawnReason is on the configured blacklist
     * @param nerfMob NamespacedKey
     * @param entity Damaged Entity
     * @return boolean
     */
    public static boolean isBlacklistedSpawnReason(NamespacedKey nerfMob, Entity entity) {
        Util.debugLvl1("Performing isBlacklistedSpawnReason on " + entity.getName());
        if (NFConfig.getBlacklistedSpawnReasonList().contains(entity.getEntitySpawnReason())) {
            entity.getPersistentDataContainer().set(nerfMob, PersistentDataType.BYTE, DamageListener.t);
            Util.debugLvl2("Nerfing " + entity.getName() + " because they spawned with the spawn reason "
                    + entity.getEntitySpawnReason() + " which is blacklisted. Setting mob as nerfed. Returning true");
            return true;
        }
        Util.debugLvl2("Cleared all 'isBlacklistedSpawnReason' checks. Returning false");
        return false;
    }
}
