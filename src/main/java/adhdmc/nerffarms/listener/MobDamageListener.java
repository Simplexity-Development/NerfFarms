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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Objects;

public class MobDamageListener implements Listener {
    public static final NamespacedKey nerfMob = new NamespacedKey(NerfFarms.plugin, "nerf-mob");
    public static final NamespacedKey blacklistedDamage = new NamespacedKey(NerfFarms.plugin, "blacklisted-damage");
    private static final Map<ConfigParser.ConfigToggles, Boolean> configToggles = ConfigParser.getConfigToggles();
    private static final byte t = 1;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDamage(EntityDamageEvent damageEvent) {
        Entity damagedEntity = damageEvent.getEntity();
        PersistentDataContainer mobPDC = damagedEntity.getPersistentDataContainer();
        // Ignore Event Checks
        if (!isMob(damagedEntity)) return;
        if (isNerfed(damagedEntity, mobPDC)) return;
        if (isHostileNerf(damagedEntity)) return;
        if (isExemptedSpawnReason(damagedEntity)) return;
        if (isExemptedMob(damagedEntity)) return;

        double damageAmount = damageEvent.getFinalDamage();

        // Nerfable Damage Checks
        if (damageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (checkForNonPlayerDamage(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (checkForProjectileDamage(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (checkForBlockedLineOfSight(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (checkIfMobCanMoveToward(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
        }

        if (checkForEnvironmentDamage(damageEvent, damagedEntity, mobPDC, damageAmount)) return;
        if (checkForStandingOnBlacklistedBlock(damagedEntity, mobPDC, damageAmount)) return;
        if (checkForStandingInBlacklistedBlock(damagedEntity, mobPDC, damageAmount)) return;

        NerfFarms.debugMessage(damagedEntity.getName() + " has reached the end of mob damage calculations");
    }

    private void addPDCDamage(PersistentDataContainer mobPDC, double damage) {
        double damageTotal = mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0);
        damageTotal += damage;
        mobPDC.set(blacklistedDamage, PersistentDataType.DOUBLE, damageTotal);
    }

    private void checkIfPassedBlacklistedDamagePercent(PersistentDataContainer mobPDC, Entity entity) {
        int maxBlacklistedDamage = ConfigParser.getMaxBlacklistedDamage();
        double nerfedDamage = mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0);
        double maxHealth = Objects.requireNonNull(((Mob) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        int percentDamage = (int) ((nerfedDamage / maxHealth) * 100);

        if (percentDamage >= maxBlacklistedDamage) {
            NerfFarms.debugMessage("Nerfing " + entity.getName() + " because they took " + percentDamage + "% total damage from nerfable causes");
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
        }
    }

    private boolean isMob(Entity entity) {
        NerfFarms.debugMessage("Performing isMob on " + entity.getName());
        if (!(entity instanceof Mob)) {
            NerfFarms.debugMessage("Ignoring onMobDamage because " + entity.getName() + " is not a mob.");
            return false;
        }
        return true;
    }

    private boolean isNerfed(Entity entity, PersistentDataContainer mobPDC) {
        NerfFarms.debugMessage("Performing isNerfed on " + entity.getName());
        if (mobPDC.has(nerfMob)) {
            NerfFarms.debugMessage(entity.getName() + " is already nerfed, ignoring...");
            return true;
        }
        return false;
    }

    private boolean isHostileNerf(Entity entity) {
        NerfFarms.debugMessage("Performing isHostileNerf on " + entity.getName());
        if (configToggles.get(ConfigParser.ConfigToggles.ONLY_NERF_HOSTILES) && !(entity instanceof Monster)) {
            NerfFarms.debugMessage("Ignoring onMobDamage because " + entity.getName() + " is not a Monster and Nerf Hostiles Only is True.");
            return true;
        }
        return false;
    }

    private boolean isExemptedSpawnReason(Entity entity) {
        NerfFarms.debugMessage("Performing isExemptedSpawnReason on " + entity.getName());
        if (ConfigParser.getSpawnReasonList().contains(entity.getEntitySpawnReason())) {
            NerfFarms.debugMessage("Ignoring onMobDamage because " + entity.getName() + " spawned from " + entity.getEntitySpawnReason() + " which isn't nerfed.");
            return true;
        }
        return false;
    }

    private boolean isExemptedMob(Entity entity) {
        NerfFarms.debugMessage("Performing isExemptedMob on " + entity.getName());
        if (ConfigParser.getBypassList().contains(entity.getType())) {
            NerfFarms.debugMessage("Ignoring onMobDamage because " + entity.getName() + " is on the bypass list as " + entity.getType());
            return true;
        }
        return false;
    }

    private boolean checkForEnvironmentDamage(EntityDamageEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugMessage("Performing isNerfableEnvironmentally on " + entity.getName());
        if (ConfigParser.getblacklistedDamageTypesSet().contains(event.getCause())) {
            NerfFarms.debugMessage("Noting environmental damage of " + hitDamage + " to " + entity.getName() + "."
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0));
            addPDCDamage(mobPDC, hitDamage);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
        }
        return false;
    }

    private boolean checkForStandingOnBlacklistedBlock(Entity entity, PersistentDataContainer mobPDC, double damageAmount) {
        Location mobStandingOnLocation = entity.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();

        NerfFarms.debugMessage("Performing isNerfableAboveBlock on " + entity.getName());

        if (ConfigParser.getStandOnBlackList().contains(entityStandingOn)) {
            NerfFarms.debugMessage("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they are standing on " + entityStandingOn
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0));
            addPDCDamage(mobPDC, damageAmount);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean checkForStandingInBlacklistedBlock(Entity entity, PersistentDataContainer mobPDC, double damageAmount) {
        Material entityStandingIn = entity.getLocation().getBlock().getType();

        NerfFarms.debugMessage("Performing isNerfableInBlock on " + entity.getName());

        if (ConfigParser.getInsideBlackList().contains(entityStandingIn)) {
            NerfFarms.debugMessage("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they are standing in " + entityStandingIn
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0));
            addPDCDamage(mobPDC, damageAmount);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean checkForBlockedLineOfSight(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double damageAmount) {
        if (!(entity instanceof LivingEntity)) return false;
        if (!configToggles.get(ConfigParser.ConfigToggles.REQUIRE_LINE_OF_SIGHT)) return true;
        Entity damager = event.getDamager();
        boolean lineofsight = ((LivingEntity) entity).hasLineOfSight(damager);
        if (!lineofsight) {
            NerfFarms.debugMessage("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they do not have a valid line of sight to the damager"
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0));
            addPDCDamage(mobPDC, damageAmount);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean checkForNonPlayerDamage(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double damageAmount) {
        Entity damager = event.getDamager();

        NerfFarms.debugMessage("Performing isNerfableNonPlayerKill on " + entity.getName());
        if (damager instanceof AbstractSkeleton &&
                entity instanceof Creeper
                && configToggles.get(ConfigParser.ConfigToggles.ALLOW_SKELETON_CREEPER_DAMAGE)) {
            NerfFarms.debugMessage("Skipping nerf on " + entity.getName() + "because 'Skeletons can damage creepers' is 'true'");
            return true;
        }
        if (damager instanceof Wither && configToggles.get(ConfigParser.ConfigToggles.ALLOW_WITHER_DAMAGE)) {
            NerfFarms.debugMessage("Skipping nerf on " + entity.getName() + "because 'Withers can damage entities' is 'true'");
            return true;
        }
        if (damager instanceof Projectile && configToggles.get(ConfigParser.ConfigToggles.ALLOW_PROJECTILE_DAMAGE)){
            return false;
        }

        if (!(damager instanceof Player)) {
            NerfFarms.debugMessage("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because " + damager +" is not a player"
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0));
            addPDCDamage(mobPDC, damageAmount);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
            return true;
        }
        return false;
    }

    private boolean checkIfMobCanMoveToward(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double damageAmount) {
        if (!configToggles.get(ConfigParser.ConfigToggles.REQUIRE_PATH)) return false;
        if (!(entity instanceof LivingEntity)) return false;
        Entity damager = event.getDamager();
        Location targetLoc = damager.getLocation();
        Pathfinder.PathResult entityPath = ((Mob) entity).getPathfinder().findPath(targetLoc);

        if (entityPath == null) return false;

        if (!(entityPath.getPoints().size() > 1)) {
            NerfFarms.debugMessage("Adding " + damageAmount + " to " + entity.getName() + "'s PDC because they do not have a valid way to move towards the damager"
                        + "\nCurrent PDC amount is: " + mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0));
            addPDCDamage(mobPDC, damageAmount);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
        }

        return (entityPath.getNextPoint() != null);
    }

    private boolean checkForProjectileDamage(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage){
        if (!(event.getDamager() instanceof Projectile projectile)) return false;

        if (!configToggles.get(ConfigParser.ConfigToggles.ALLOW_PROJECTILE_DAMAGE)){
            NerfFarms.debugMessage("Arrow damage is not allowed");
            addPDCDamage(mobPDC, hitDamage);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
            return true;
        }
        if (!(projectile.getShooter() instanceof Player player)) return false;

        Location playerLocation = player.getLocation();
        Location entityLocation = entity.getLocation();
        checkForWithinDistance(event, entityLocation, playerLocation, entity, mobPDC);
        return false;
    }

    private void checkForWithinDistance(EntityDamageByEntityEvent event, Location entityLoc, Location playerLoc, Entity entity, PersistentDataContainer mobPDC) {
        double distanceBetween = entityLoc.distance(playerLoc);
        double hitDamage = event.getFinalDamage();
        if (distanceBetween > ConfigParser.getMaxDistance()) {
            NerfFarms.debugMessage(entity.getName() + " is above the max distance from the player");
            addPDCDamage(mobPDC, hitDamage);
            checkIfPassedBlacklistedDamagePercent(mobPDC, entity);
        }
    }

}
