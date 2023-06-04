package simplexity.nerffarms.listener.damagehandling.nerfchecks;

import simplexity.nerffarms.config.ConfigToggle;
import simplexity.nerffarms.listener.damagehandling.AddPDCDamage;
import simplexity.nerffarms.util.CheckUtils;
import simplexity.nerffarms.util.Debug;
import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public class CheckPath {
    /**
     * Checks if the mob has a calculated path to the player, and compares to configuration "require-path"
     * Returns true if there is not a calculated path
     * @param nerfMob NamespacedKey
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    public static boolean checkPath(NamespacedKey nerfMob, EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        Debug.debugLvl1("Performing checkPath on " + entity.getName());
        if (!ConfigToggle.REQUIRE_PATH.isEnabled()) return false;
        if (!(entity instanceof LivingEntity)) return false;
        Entity damager = CheckUtils.getRealDamager(event);
        if (damager == null){
            Debug.debugLvl2("Entity does not have a path to the player, because 'getRealDamager' returned a null value. " +
            "Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Location targetLoc = damager.getLocation();
        Pathfinder.PathResult entityPath = ((Mob) entity).getPathfinder().findPath(targetLoc);
        if (entityPath == null) {
            Debug.debugLvl2("Entity's path is null. Returning false");
            return false;
        }
        int pathLength =  entityPath.getPoints().size();
        List<Location> pathPoints = entityPath.getPoints();
        if (pathLength <= 1 ) {
            Debug.debugLvl2("Entity does not have a path to the player (Path length less than or equal to 1). Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        if (!CheckUtils.getBlockAbove(pathPoints.get(1), entity).isPassable()) {
            Debug.debugLvl2("Entity does not have a path to the player (2nd path point is blocked). Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        if (pathLength >= 3 && !CheckUtils.getBlockAbove(pathPoints.get(2), entity).isPassable()) {
            Debug.debugLvl2("Entity does not have a path to the player (3rd path point is blocked). Returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Debug.debugLvl2("cleared all 'checkPath' checks. Returning false");
        return false;
    }
}
