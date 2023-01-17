package adhdmc.nerffarms.listener.damagehandling.bypasschecks;

import adhdmc.nerffarms.util.NFConfig;
import adhdmc.nerffarms.listener.damagehandling.DamageListener;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class IsWhitelistedSpawnReasonCheck {
    /**
     * Checks mob spawn reason, returns true if the SpawnReason is on the configured whitelist
     * @param mobPDC Mob's persistent data container
     * @param entity Damaged Entity
     * @param bypass NamespacedKey
     * @return boolean
     */
    public static boolean isWhitelistedSpawnReason(PersistentDataContainer mobPDC, Entity entity, NamespacedKey bypass) {
        Util.debugLvl1("Performing isWhitelistedSpawnReason on " + entity.getName());
        if (NFConfig.getWhitelistedSpawnReasonList().contains(entity.getEntitySpawnReason())) {
            Util.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " spawned from "
            + entity.getEntitySpawnReason() + " which is whitelisted. Marking to skip future checks and returning true");
            mobPDC.set(bypass, PersistentDataType.BYTE, DamageListener.t);
            return true;
        }
        Util.debugLvl2("Cleared all 'isWhitelistedSpawnReason' checks. Returning false");
        return false;
    }
}
