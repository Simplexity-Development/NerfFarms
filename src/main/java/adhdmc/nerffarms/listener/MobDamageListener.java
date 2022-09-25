package adhdmc.nerffarms.listener;

import adhdmc.nerffarms.NerfFarms;
import adhdmc.nerffarms.config.ConfigParser;
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
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class MobDamageListener implements Listener {
    public static final NamespacedKey nerfMob = new NamespacedKey(NerfFarms.plugin, "nerf-mob");
    public static final NamespacedKey disallowedDamage = new NamespacedKey(NerfFarms.plugin, "disallowed-damage");
    private static final Map<ConfigParser.ConfigToggles, Boolean> configToggles = ConfigParser.getConfigToggles();
    boolean debugSetting = false;
    private static Logger logger;
    private static final byte t = 1;

    @EventHandler
    public void onMobDamage(EntityDamageEvent damageEvent) {
        debugSetting = configToggles.get(ConfigParser.ConfigToggles.DEBUG);
        logger = NerfFarms.plugin.getLogger();
        Entity damagedEntity = damageEvent.getEntity();
        // Ignore Event Checks
        if (!isMob(damagedEntity)) {
            return;
        }
        if (isNerfed(damagedEntity)) {
            return;
        }
        if (isHostileNerf(damagedEntity)) {
            return;
        }
        if (isExemptedSpawnReason(damagedEntity)) {
            return;
        }
        if (isExemptedMob(damagedEntity)) {
            return;
        }
        // Nerfable Damage Checks
        if (isNerfableNonPlayerDamage(damageEvent)) {
            return;
        }
        if (isProjectileDamage(damageEvent)){
            return;
        }
        if (hasBlockedLineofSight(damageEvent)) {
            return;
        }
        if (canMobMoveToward(damageEvent)) {
            return;
        }
        if (isNerfableEnvironmentally(damageEvent)) {
            return;
        }
        if (isNerfableAboveBlock(damageEvent)) {
            return;
        }
        if (isNerfableInBlock(damageEvent)) {
            return;
        }
        if (debugSetting) {
            logger.info(damagedEntity.getName() + " has reached the end of mob damage calculations");
        }
    }

    private boolean isProjectileDamage(EntityDamageEvent event){
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return false;
        }
        if (!(((EntityDamageByEntityEvent) event).getDamager() instanceof Projectile projectile)) {
            return false;
        }
        Entity entity = event.getEntity();
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();
        double hitDamage = event.getFinalDamage();
        if (!configToggles.get(ConfigParser.ConfigToggles.ALLOW_PROJECTILE_DAMAGE)){
            if (debugSetting) {
                logger.info("Arrow damage is not allowed");
            }
            addPDCDamage(mobPDC, hitDamage);
            disallowedDamagePercent(mobPDC, entity);
            return true;
        }
        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Player player)){
            return false;
        }
        Location playerLocation = player.getLocation();
        Location entityLocation = entity.getLocation();
        isWithinDistance(event, entityLocation, playerLocation);
        return false;
    }

    private void isWithinDistance(EntityDamageEvent event, Location entityLoc, Location playerLoc) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return;
        }
        Entity entity = event.getEntity();
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();
        double distanceBetween = entityLoc.distance(playerLoc);
        double hitDamage = event.getFinalDamage();
        if (distanceBetween > ConfigParser.getMaxDistance()) {
            if (debugSetting) {
                logger.info(entity.getName() + " is above the max distance from the player");
            }
            addPDCDamage(mobPDC, hitDamage);
            disallowedDamagePercent(mobPDC, entity);
        }
    }

    private void addPDCDamage(PersistentDataContainer mobPDC, double damage) {
        double damageTotal = mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0);
        damageTotal += damage;
        mobPDC.set(disallowedDamage, PersistentDataType.DOUBLE, damageTotal);
    }

    private boolean isMob(Entity entity) {

        if (debugSetting) {
            logger.info("Performing isMob on " + entity.getName());
        }

        if (!(entity instanceof Mob)) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + entity.getName() + " is not a mob.");
            }
            return false;
        }
        return true;
    }

    private boolean isNerfed(Entity entity) {
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();

        if (debugSetting) {
            logger.info("Performing isNerfed on " + entity.getName());
        }

        if (mobPDC.has(nerfMob)) {
            if (debugSetting) {
                logger.info(entity.getName() + " is already nerfed, ignoring...");
            }
            return true;
        }
        return false;
    }

    private boolean isHostileNerf(Entity entity) {

        if (debugSetting) {
            logger.info("Performing isHostileNerf on " + entity.getName());
        }

        if (configToggles.get(ConfigParser.ConfigToggles.ONLY_NERF_HOSTILES) && !(entity instanceof Monster)) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + entity.getName() + " is not a Monster and Nerf Hostiles Only is True.");
            }
            return true;
        }
        return false;
    }

    private boolean isExemptedSpawnReason(Entity entity) {

        if (debugSetting) {
            logger.info("Performing isExemptedSpawnReason on " + entity.getName());
        }

        if (ConfigParser.getSpawnReasonList().contains(entity.getEntitySpawnReason())) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + entity.getName() + " spawned from " + entity.getEntitySpawnReason() + " which isn't nerfed.");
            }
            return true;
        }
        return false;
    }

    private boolean isExemptedMob(Entity entity) {

        if (debugSetting) {
            logger.info("Performing isExemptedMob on " + entity.getName());
        }

        if (ConfigParser.getBypassList().contains(entity.getType())) {
            if (debugSetting) {
                logger.info("Ignoring onMobDamage because " + entity.getName() + " is on the bypass list as " + entity.getType());
            }
            return true;
        }
        return false;
    }

    private boolean isNerfableEnvironmentally(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();
        double hitDamage = event.getFinalDamage();

        if (debugSetting) {
            logger.info("Performing isNerfableEnvironmentally on " + entity.getName());
        }

        if (ConfigParser.getdisallowedDamageTypesSet().contains(event.getCause())) {
            if (debugSetting) {
                logger.info("Noting environmental damage of " + hitDamage + " to " + entity.getName() + "."
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0));
            }
            addPDCDamage(mobPDC, hitDamage);
            disallowedDamagePercent(mobPDC, entity);
        }
        return false;
    }

    private void disallowedDamagePercent(PersistentDataContainer mobPDC, Entity entity) {
        int maxDisallowedDamage = ConfigParser.getMaxDisallowedDamage();
        double nerfedDamage = mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0);
        double maxHealth = Objects.requireNonNull(((Mob) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        int percentDamage = (int) ((nerfedDamage / maxHealth) * 100);

        if (percentDamage >= maxDisallowedDamage) {
            if (debugSetting) {
                logger.info("Nerfing " + entity.getName() + " because they took " + percentDamage + "% total damage from nerfable causes");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
        }
    }

    private boolean isNerfableNonPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        double damageAmount = event.getDamage();
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();

        if (!(event instanceof EntityDamageByEntityEvent)) {
            return false;
        }
        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

        if (debugSetting) {
            logger.info("Performing isNerfableNonPlayerKill on " + entity.getName());
        }
        if (damager instanceof AbstractSkeleton &&
                entity instanceof Creeper &&
                configToggles.get(ConfigParser.ConfigToggles.ALLOW_SKELETON_CREEPER_DAMAGE)) {
            if (debugSetting) {
                logger.info("Skipping nerf on " + entity.getName() + "because 'Skeletons can damage creepers' is 'true'");
            }
            return true;
        }
        if (damager instanceof Wither &&
                configToggles.get(ConfigParser.ConfigToggles.ALLOW_WITHER_DAMAGE)) {
            if (debugSetting) {
                logger.info("Skipping nerf on " + entity.getName() + "because 'Withers can damage entities' is 'true'");
            }
            return true;
        }
        if (damager instanceof Projectile && configToggles.get(ConfigParser.ConfigToggles.ALLOW_PROJECTILE_DAMAGE)){
            return false;
        }

        if (!(damager instanceof Player)) {
            if (debugSetting) {
                logger.info("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because " + damager +" is not a player"
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0));
            }
            addPDCDamage(mobPDC, damageAmount);
            disallowedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean isNerfableAboveBlock(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Location mobStandingOnLocation = entity.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        double damageAmount = event.getDamage();
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();

        if (debugSetting) {
            logger.info("Performing isNerfableAboveBlock on " + entity.getName());
        }

        if (ConfigParser.getStandOnBlackList().contains(entityStandingOn)) {
            if (debugSetting) {
                logger.info("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they are standing on " + entityStandingOn
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0));
            }
            addPDCDamage(mobPDC, damageAmount);
            disallowedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean isNerfableInBlock(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();
        double damageAmount = event.getDamage();
        Material entityStandingIn = entity.getLocation().getBlock().getType();

        if (debugSetting) {
            logger.info("Performing isNerfableInBlock on " + entity.getName());
        }

        if (ConfigParser.getInsideBlackList().contains(entityStandingIn)) {
            if (debugSetting) {
                logger.info("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they are standing in " + entityStandingIn
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0));
            }
            addPDCDamage(mobPDC, damageAmount);
            disallowedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean hasBlockedLineofSight(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return false;
        if (!(event.getEntity() instanceof LivingEntity entity)) return false;
        if (!configToggles.get(ConfigParser.ConfigToggles.REQUIRE_LINE_OF_SIGHT)) return true;

        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();
        double damageAmount = event.getDamage();
        boolean lineofsight = entity.hasLineOfSight(damager);
        if (!lineofsight) {
            if (debugSetting) {
                logger.info("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they do not have a valid line of sight to the damager"
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0));
            }
            addPDCDamage(mobPDC, damageAmount);
            disallowedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean canMobMoveToward(EntityDamageEvent event) {
        if (!configToggles.get(ConfigParser.ConfigToggles.REQUIRE_PATH)) return false;
        if (!(event instanceof EntityDamageByEntityEvent)) return false;
        if (!(event.getEntity() instanceof LivingEntity entity)) return false;
        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
        Location targetLoc = damager.getLocation();
        Pathfinder.PathResult entityPath = ((Mob) entity).getPathfinder().findPath(targetLoc);
        PersistentDataContainer mobPDC = entity.getPersistentDataContainer();
        double damageAmount = event.getDamage();

        if (entityPath == null) {
            return false;
        }
        if (!(entityPath.getPoints().size() > 1)) {
            if (debugSetting) {
                logger.info("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they do not have a valid way to move towards the damager"
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(disallowedDamage, PersistentDataType.DOUBLE, 0.0));
            }
            addPDCDamage(mobPDC, damageAmount);
            disallowedDamagePercent(mobPDC, entity);
        }

        return (entityPath.getNextPoint() != null);
    }

}
