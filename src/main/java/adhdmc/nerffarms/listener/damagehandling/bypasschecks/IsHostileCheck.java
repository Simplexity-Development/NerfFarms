package adhdmc.nerffarms.listener.damagehandling.bypasschecks;

import adhdmc.nerffarms.config.ConfigToggle;
import adhdmc.nerffarms.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

public class IsHostileCheck {
    /**
     * Checks if the mob is hostile, and compares to the configured settings for 'only-nerf-hostiles'
     * Returns false if the mob is not a monster, and the config is set 'only-nerf-hostiles: true'
     * @param entity Damaged Entity
     * @return boolean
     */
    public static boolean checkHostile(Entity entity) {
        Util.debugLvl1("Performing isHostileNerf on " + entity.getName());
        if (ConfigToggle.ONLY_NERF_HOSTILES.isEnabled() && !(entity instanceof Monster)) {
            Util.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " is not a Monster and Nerf Hostiles Only is True. Returning false");
            return false;
        }
        Util.debugLvl2("Cleared all 'checkHostile' checks. Returning true");
        return true;
    }
}
