package adhdmc.nerffarms.listener.damagehandling.bypasschecks;

import adhdmc.nerffarms.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public class IsMobCheck {
    /**
     * Checks if the damaged entity is a mob. Returns False if it is not a mob
     * @param entity Damaged Entity
     * @return boolean
     */
    public static boolean isMob(Entity entity) {
        Util.debugLvl1("Performing isMob on " + entity.getName());
        if (!(entity instanceof Mob)) {
            Util.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " is not a mob. Returning false");
            return false;
        }
        return true;
    }
}
