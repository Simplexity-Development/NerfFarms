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
import java.util.logging.Logger;

public class MobDamageListener implements Listener {
    public static final NamespacedKey nerfMob = new NamespacedKey(NerfFarms.plugin, "nerfMob");
    public static final NamespacedKey environmentalDamage = new NamespacedKey(NerfFarms.plugin, "environmentalDamage");
    private static boolean debugSetting;
    private static Logger logger;
    private static final byte f = 0;
    private static final byte t = 1;

    @EventHandler
    public void onMobDamage(EntityDamageEvent damageEvent) {
        debugSetting = ConfigParser.isDebug();
        logger = NerfFarms.plugin.getLogger();
        Entity damagedEntity = damageEvent.getEntity();

        // Ignore Event Checks
        if (!isMob(damagedEntity)) { return; }
        if (isNerfed(damagedEntity)) { return; }
        if (isHostileNerf(damagedEntity)) { return; }
        if (isExemptedSpawnReason(damagedEntity)) { return; }
        if (isExemptedMob(damagedEntity)) { return; }

        // Pre-Death Nerfing Checks
        if (isNerfableEnvironmentally(damageEvent)) { return; }

        // Death Check
        if (!isDying(damageEvent)) { return; }

        // On-Death Nerfing Checks
        if (isNerfableDamageType(damageEvent)) { return; }
        if (isNerfableAboveBlock(damageEvent)) { return; }
        if (isNerfableInBlock(damageEvent)) { return; }

        // On-Death Nerfing Checks (EntityDamagedByEntity)
        if (isNerfableNonPlayerKill(damageEvent)) { return; }
        if (isNerfableBlockedPath(damageEvent)) { return; }

        if (debugSetting) {
            logger.info(damagedEntity.getName() + " has died and not been nerfed.");
        }
    }

    private boolean hasPathToPlayer(Player p, Mob m) {
        if (!ConfigParser.isRequireTargeting()) {
            if (debugSetting) {
                logger.info("Ignoring pathfinding check on " + m.getName() + " because require targetting is false.");
            }
            return false;
        }

        Location playerLoc = p.getLocation();
        Pathfinder.PathResult entityPath = m.getPathfinder().findPath(p);

        if (debugSetting) {
            logger.info("Performing hasPathToPlayer on " + m.getName());
        }

        if (entityPath == null) { return false; }

        Location finalLoc = entityPath.getFinalPoint();
        if (finalLoc == null) { return false; }

        // TODO: Make configurable distance.
        return playerLoc.distance(finalLoc) < 1;
    }

    private void addPDCDamage(PersistentDataContainer mobPDC, double damage) {
        double damageTotal = mobPDC.getOrDefault(environmentalDamage, PersistentDataType.DOUBLE, 0.0);
        damageTotal += damage;
        mobPDC.set(environmentalDamage, PersistentDataType.DOUBLE, damageTotal);
    }

    private boolean isMob(Entity e) {

        if (debugSetting) {
            logger.info("Performing isMob on " + e.getName());
        }

        if (!(e instanceof Mob)) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + e.getName() + " is not a mob.");
            }
            return false;
        }
        return true;
    }

    private boolean isNerfed(Entity e) {
        PersistentDataContainer mobPDC = e.getPersistentDataContainer();

        if (debugSetting) {
            logger.info("Performing isNerfed on " + e.getName());
        }

        if (mobPDC.has(nerfMob)) {
            if (debugSetting) {
                logger.info(e.getName() + " is already nerfed, ignoring...");
            }
            return true;
        }
        return false;
    }

    private boolean isHostileNerf(Entity e) {

        if (debugSetting) {
            logger.info("Performing isHostileNerf on " + e.getName());
        }

        if (ConfigParser.isNerfHostilesOnly() && !(e instanceof Monster)) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + e.getName() + " is not a Monster and Nerf Hostiles Only is True.");
            }
            return true;
        }
        return false;
    }

    private boolean isExemptedSpawnReason(Entity e) {

        if (debugSetting) {
            logger.info("Performing isExemptedSpawnReason on " + e.getName());
        }

        if (!ConfigParser.getSpawnReasonList().contains(e.getEntitySpawnReason())) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + e.getName() + " spawned from " + e.getEntitySpawnReason() + " which isn't nerfed.");
            }
            return true;
        }
        return false;
    }

    private boolean isExemptedMob(Entity e) {

        if (debugSetting) {
            logger.info("Performing isExemptedMob on " + e.getName());
        }

        if (ConfigParser.getBypassList().contains(e.getType())) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + e.getName() + " is on the bypass list as " + e.getType());
            }
            return true;
        }
        return false;
    }

    private boolean isNerfableEnvironmentally(EntityDamageEvent event) {
        Entity e = event.getEntity();
        PersistentDataContainer mobPDC = e.getPersistentDataContainer();
        double hitDamage = event.getFinalDamage();
        int percentFromEnvironment = ConfigParser.getPercentFromEnvironment();

        if (debugSetting) {
            logger.info("Performing isNerfableEnvironmentally on " + e.getName());
        }

        if (ConfigParser.getEnvironmentalDamageSet().contains(event.getCause())) {
            if (debugSetting) {
                logger.info("Noting environmental damage of " + hitDamage + " to " + e.getName() + ".");
            }
            addPDCDamage(mobPDC, hitDamage);
        }

        double envDamage = mobPDC.getOrDefault(environmentalDamage, PersistentDataType.DOUBLE, 0.0);
        double maxHealth = Objects.requireNonNull(((Mob) e).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        int percentDamage = (int) ((envDamage / maxHealth) * 100);

        if (percentDamage >= percentFromEnvironment) {
            if (debugSetting) {
                logger.info("Nerfing " + e.getName() + " because they took " + percentDamage + "% total damage from the environment.");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return true;
        }
        return false;
    }

    private boolean isDying(EntityDamageEvent event) {
        Entity e = event.getEntity();
        double health = ((Mob) e).getHealth();
        double hitDamage = event.getDamage();

        if (debugSetting) {
            logger.info("Performing isDying on " + e.getName());
        }

        if (!(health - hitDamage <= 0)) {
            logger.info("Ignoring onMobDamage because " + e.getName() + " is not dying.");
            return false;
        }
        return true;
    }

    private boolean isNerfableDamageType(EntityDamageEvent event) {
        Entity e = event.getEntity();
        EntityDamageEvent.DamageCause damageType = event.getCause();
        PersistentDataContainer mobPDC = e.getPersistentDataContainer();

        if (debugSetting) {
            logger.info("Performing isNerfableDamageType on " + e.getName());
        }

        if (!ConfigParser.getDamageCauseWhitelist().contains(damageType)) {
            if (debugSetting) {
                logger.info("Nerfing " + e.getName() + " due to " + damageType);
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return true;
        }
        return false;
    }

    private boolean isNerfableNonPlayerKill(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            if (debugSetting) {
                logger.info("isNerfableNonPlayerKill is not an EntityDamageByEvent");
            }
            return true;
        }

        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
        Entity e = event.getEntity();
        PersistentDataContainer mobPDC = e.getPersistentDataContainer();

        if (debugSetting) {
            logger.info("Performing isNerfableNonPlayerKill on " + e.getName());
        }

        if (!(damager instanceof Player)) {
            if (debugSetting) {
                logger.info("Nerfing " + e.getName() + " because killer is not a player");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return true;
        }
        return false;
    }

    private boolean isNerfableBlockedPath(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            if (debugSetting) {
                logger.info("isNerfableBlockedPath is not an EntityDamageByEvent");
            }
            return true;
        }

        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
        Entity e = event.getEntity();
        PersistentDataContainer mobPDC = e.getPersistentDataContainer();

        if (debugSetting) {
            logger.info("Performing isNerfableBlockedPath on " + e.getName());
        }

        if (!hasPathToPlayer((Player) damager, (Mob) e)) {
            if (debugSetting) {
                logger.info("Nerfing " + e.getName() + " because they never could reach the player.");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return true;
        }
        return false;
    }

    private boolean isNerfableAboveBlock(EntityDamageEvent event) {
        Entity e = event.getEntity();
        Location mobStandingOnLocation = e.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        PersistentDataContainer mobPDC = e.getPersistentDataContainer();

        if (debugSetting) {
            logger.info("Performing isNerfableAboveBlock on " + e.getName());
        }

        if (ConfigParser.getStandOnBlackList().contains(entityStandingOn)) {
            if (debugSetting) {
                logger.info("Nerfing " + e.getName() + " since they are standing on " + entityStandingOn);
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return true;
        }
        return false;
    }

    private boolean isNerfableInBlock(EntityDamageEvent event) {
        Entity e = event.getEntity();
        PersistentDataContainer mobPDC = e.getPersistentDataContainer();
        Material entityStandingIn = e.getLocation().getBlock().getType();

        if (debugSetting) {
            logger.info("Performing isNerfableInBlock on " + e.getName());
        }

        if (ConfigParser.getInsideBlackList().contains(entityStandingIn)) {
            if (debugSetting) {
                logger.info("Nerfing " + e.getName() + " since they are standing in " + entityStandingIn);
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return true;
        }
        return false;
    }

}
