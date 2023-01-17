package adhdmc.nerffarms.listener.damagehandling.nerfchecks;

import adhdmc.nerffarms.util.NFConfig;
import adhdmc.nerffarms.listener.damagehandling.DamageListener;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class CheckDamageThreshold {
    /**
     * Checks if the configured maximum blacklisted damage threshold has been reached, and if it has, marks the mob to be nerfed
     * @param nerfMob NamespacedKey
     * @param mobPDC Mob's Persistent Data Container
     * @param entity double Total Damage Dealt
     */
    public static void checkDamageThreshold(NamespacedKey nerfMob, PersistentDataContainer mobPDC, Entity entity) {
        int maxBlacklistedDamage = NFConfig.getMaxBlacklistedDamage();
        double nerfedDamage = mobPDC.getOrDefault(DamageListener.blacklistedDamage, PersistentDataType.DOUBLE, 0.0);
        double maxHealth = Objects.requireNonNull(((Mob) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        int percentDamage = (int) ((nerfedDamage / maxHealth) * 100);
        if (percentDamage >= maxBlacklistedDamage) {
            Util.debugLvl3("Nerfing " + entity.getName() + " because they took " + percentDamage + "% total damage from nerfable causes");
            mobPDC.set(nerfMob, PersistentDataType.BYTE, DamageListener.t);
            return;
        }
        Util.debugLvl3(entity + " is not above the damage threshold. They are not being marked as nerfed.");
    }
}
