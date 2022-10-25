package adhdmc.nerffarms.util;

import adhdmc.nerffarms.NerfFarms;
import adhdmc.nerffarms.config.ConfigToggle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CheckUtils {

    /**
     * Checks if the real cause of the damage was done by a LivingEntity
     * @param event EntityDamageByEntityEvent
     * @return LivingEntity
     */
    public static LivingEntity getRealDamager(EntityDamageByEntityEvent event){
        LivingEntity shooter;
        if (event.getDamager() instanceof LivingEntity entity){
            NerfFarms.debugLvl2("getRealDamager check, damager is a living entity, returning damager value");
            return entity;
        }
        if (!(event.getDamager() instanceof Projectile projectile)){
            NerfFarms.debugLvl2("getRealDamager check, damage was not done by a projectile or a living entity. Returning null");
            return null;
        }
        if (!ConfigToggle.ALLOW_PROJECTILE_DAMAGE.isEnabled()){
            NerfFarms.debugLvl2("getRealDamager check, damage was done by a projectile, and projectile damage configured off. Returning null");
            return null;
        }
        if (!(projectile.getShooter() instanceof LivingEntity)){
            NerfFarms.debugLvl2("getRealDamager check, shooter not a LivingEntity, returning null shooter value");
            return null;
        }
        shooter = (LivingEntity) projectile.getShooter();
        NerfFarms.debugLvl2("getRealDamager check, shooter is a LivingEntity, returning player value of: " + shooter);
        return shooter;
    }

    public static Block getBlockAbove(Location location, Entity entity){
        double entityHeight = entity.getHeight();
        if (entityHeight > 1) {
            int addToLoc = (int) Math.round(entityHeight - 1);
            location = location.add(0, addToLoc, 0);
        }
        NerfFarms.debugLvl3("getBlockAbove: MaterialType " + location.getBlock().getType() + "\nLocation: " + location);
        return location.getBlock();
    }

    public static Material getBlockBelow(Location location) {
        return location.getBlock().getType();
    }
}
