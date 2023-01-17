package adhdmc.nerffarms.listener.damagehandling.nerfchecks;

import adhdmc.nerffarms.util.NFToggles;
import adhdmc.nerffarms.listener.damagehandling.AddPDCDamage;
import adhdmc.nerffarms.util.CheckUtils;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class CheckDamager {
    /**
     * Checks the damager of the entity, and compares it to multiple configurations
     * Checks for Skeleton attacking creeper, compares to configuration "skeletons-can-attack-creepers"
     * Checks for Wither attacking entity, compares to configuration "withers-can-attack-entities"
     * Checks for Projectile damage, compares to "allow-projectile-damage"
     * If all these checks pass, and the damager is not a player, returns true
     * @param nerfMob NamespacedKey
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    public static boolean checkDamager(NamespacedKey nerfMob, EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        Util.debugLvl1("Performing checkDamager on " + entity.getName());
        Entity damager = CheckUtils.getRealDamager(event);
        if (damager instanceof IronGolem && NFToggles.ALLOW_IRON_GOLEM_DAMAGE.isEnabled()) {
            Util.debugLvl2("Skipping nerf on " + entity.getName() + "because 'Iron golems can damage entities' is 'true'. Returning true");
            return true;
        }
        if (damager instanceof Wither && NFToggles.ALLOW_WITHER_DAMAGE.isEnabled()) {
            Util.debugLvl2("Skipping nerf on " + entity.getName() + "because 'Withers can damage entities' is 'true'. Returning true");
            return true;
        }
        if (damager instanceof AbstractSkeleton && entity instanceof Creeper && NFToggles.ALLOW_SKELETON_CREEPER_DAMAGE.isEnabled()) {
            Util.debugLvl2("Skipping nerf on " + entity.getName() + "because 'Skeletons can damage creepers' is 'true'. Returning true (non-projectile damage)");
            return true;
        }
        if (damager instanceof Frog && entity instanceof Slime slimeEntity) {
            if (slimeEntity.getType().equals(EntityType.MAGMA_CUBE) && NFToggles.ALLOW_FROG_MAGMA_CUBE_DAMAGE.isEnabled()) {
                Util.debugLvl2("Skipping nerf on " + entity.getName() + "because 'frogs can eat magma cubes' is 'true'. Returning true");
                return true;
            }
            if (slimeEntity.getType().equals(EntityType.SLIME) && NFToggles.ALLOW_FROG_SLIME_DAMAGE.isEnabled()) {
                Util.debugLvl2("Skipping nerf on " + entity.getName() + "because 'frogs can eat slime' is 'true'. Returning true");
                return true;
            }
        }
        if (!(damager instanceof Player)) {
            Util.debugLvl1("Damager is not a player, returning true");
            AddPDCDamage.addPDCDamage(event, mobPDC, hitDamage);
            CheckDamageThreshold.checkDamageThreshold(nerfMob, mobPDC, entity);
            return true;
        }
        Util.debugLvl2("Cleared all 'checkDamager' checks. Returning false");
        return false;
    }
}
