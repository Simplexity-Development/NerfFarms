package adhdmc.nerffarms;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class MobDamageListener implements Listener {
    public static final NamespacedKey nerfMob = new NamespacedKey(NerfFarms.plugin, "nerfMob");
    public static final NamespacedKey environmentalDamage = new NamespacedKey(NerfFarms.plugin, "environmentalDamage");
    byte f = 0;
    byte t = 1;

    @EventHandler
    public void onMobDamage(EntityDamageByEntityEvent damageEvent) {
        boolean d = ConfigParser.isDebug();
        Logger l = NerfFarms.plugin.getLogger();
        Entity damagedEntity = damageEvent.getEntity();
        if (!(damagedEntity instanceof Mob)) {
            if (d) {
                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " is not a mob.");
            }
            return;
        }
        PersistentDataContainer mobPDC = damagedEntity.getPersistentDataContainer();
        if (mobPDC.has(nerfMob)) {
            if (d) {
                l.info(damagedEntity.getName() + " is already nerfed, ignoring...");
            }
            return;
        }
//        if (ConfigParser.isNerfHostilesOnly() && !(damagedEntity instanceof Monster)) {
//            if (d) {
//                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " is not a Monster and Nerf Hostiles Only is True.");
//            }
//            return;
//        }
//        if (!ConfigParser.getSpawnReasonList().contains(damagedEntity.getEntitySpawnReason())) {
//            if (d) {
//                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " spawned from " + damagedEntity.getEntitySpawnReason() + " which isn't nerfed.");
//            }
//            return;
//        }
//        if (ConfigParser.getBypassList().contains(damagedEntity.getType())) {
//            if (d) {
//                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " is on the bypass list as " + damagedEntity.getType());
//            }
//            return;
//        }
        double entityHealth = ((Mob) damagedEntity).getHealth();
        double hitDamage = damageEvent.getFinalDamage();
//        int percentFromEnvironment = ConfigParser.getPercentFromEnvironment();
//        if (ConfigParser.getEnvironmentalDamageSet().contains(damageEvent.getCause())) {
//            if (d) {
//                l.info("Noting environmental damage of " + hitDamage + " to " + damagedEntity.getName() + ".");
//            }
//            addPDCDamage(mobPDC, hitDamage);
//        }
//        double envDamage = mobPDC.getOrDefault(environmentalDamage, PersistentDataType.DOUBLE, 0.0);
//        double maxHealth = Objects.requireNonNull(((Mob) damagedEntity).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
//        int percentDamage = (int) ((envDamage / maxHealth) * 100);
//        if (percentDamage >= percentFromEnvironment) {
//            if (d) {
//                l.info("Nerfing " + damagedEntity.getName() + " because they took " + percentDamage + "% total damage from the environment.");
//            }
//            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
//            return;
//        }
        if (!(entityHealth - hitDamage <= 0)) {
            l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " is not dying.");
            return;
        }
        mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
//        Location mobLocation = damagedEntity.getLocation();
//        Location mobStandingOnLocation = damagedEntity.getLocation().subtract(0, 1, 0);
//        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
//        Material entityStandingIn = mobLocation.getBlock().getType();
//        EntityDamageEvent.DamageCause damageType = damageEvent.getCause();
//        Entity entityDamager = damageEvent.getDamager();
//        if (!ConfigParser.getDamageCauseWhitelist().contains(damageType)) {
//            if (d) {
//                l.info("Nerfing " + damagedEntity.getName() + " due to " + damageType);
//            }
//            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
//            return;
//        }
//        if (!(entityDamager instanceof Player)) {
//            if (d) {
//                l.info("Nerfing " + damagedEntity.getName() + " because killer is not a player");
//            }
//            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
//            return;
//        }
//        if (!hasPathToPlayer((Player) entityDamager, (Mob) damagedEntity)) {
//            if (d) {
//                l.info("Nerfing " + damagedEntity.getName() + " because they never could reach the player.");
//            }
//            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
//            return;
//        }
//        if (ConfigParser.getStandOnBlackList().contains(entityStandingOn)) {
//            if (d) {
//                l.info("Nerfing " + damagedEntity.getName() + " since they are standing on " + entityStandingOn);
//            }
//            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
//            return;
//        }
//        if (ConfigParser.getInsideBlackList().contains(entityStandingIn)) {
//            if (d) {
//                l.info("Nerfing " + damagedEntity.getName() + " since they are standing in " + entityStandingIn);
//            }
//            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
//        }
    }

    private boolean hasPathToPlayer(Player p, Mob m) {
        Location playerLoc = p.getLocation();
        Pathfinder.PathResult entityPath = m.getPathfinder().findPath(p);
        if (entityPath == null) { return false; }
        Location finalLoc = entityPath.getFinalPoint();
        if (finalLoc == null) { return false; }
        // TODO: Make configurable distance.
        return playerLoc.distance(finalLoc) < 1;
    }

    private static void addPDCDamage(PersistentDataContainer mobPDC, double damage) {
        double damageTotal = mobPDC.getOrDefault(environmentalDamage, PersistentDataType.DOUBLE, 0.0);
        damageTotal += damage;
        mobPDC.set(environmentalDamage, PersistentDataType.DOUBLE, damageTotal);
    }

}
