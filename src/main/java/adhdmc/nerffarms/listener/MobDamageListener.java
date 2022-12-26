package adhdmc.nerffarms.listener;

import adhdmc.nerffarms.NerfFarms;
import adhdmc.nerffarms.config.ConfigParser;
import adhdmc.nerffarms.config.ConfigToggle;
import adhdmc.nerffarms.util.CheckUtils;
import adhdmc.nerffarms.util.Util;
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

import java.util.List;
import java.util.Objects;

public class MobDamageListener implements Listener {
    private final NamespacedKey nerfMob = Util.nerfMob;
    public static final NamespacedKey blacklistedDamage = Util.blacklistedDamage;
    private static final byte t = 1;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDamage(EntityDamageEvent damageEvent) {
        Entity damagedEntity = damageEvent.getEntity();
        PersistentDataContainer mobPDC = damagedEntity.getPersistentDataContainer();
        // Ignore Event Checks
        if (!isMob(damagedEntity)) return;
        if (isNerfed(damagedEntity, mobPDC)) return;
        if (!checkHostile(damagedEntity)) return;
        if (isWhitelistedSpawnReason(damagedEntity)) return;
        if (isBlacklistedSpawnReason(damagedEntity)) return;
        if (isWhitelistedMob(damagedEntity)) return;
        if (isBlacklistedMob(damagedEntity)) return;

        double damageAmount = damageEvent.getFinalDamage();

        // Nerfable Damage Checks
        if (damageEvent instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (checkDamager(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (checkDistance(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (!checkLineOfSight(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
            if (checkPath(damageByEntityEvent, damagedEntity, mobPDC, damageAmount)) return;
        }

        if (checkDamageType(damageEvent, damagedEntity, mobPDC, damageAmount)) return;
        if (checkStandingOn(damageEvent, damagedEntity, mobPDC, damageAmount)) return;
        if (checkStandingInside(damageEvent, damagedEntity, mobPDC, damageAmount)) return;

        NerfFarms.debugLvl1(damagedEntity.getName() + " has reached the end of mob damage calculations");
    }

    /**
     * Adds nerfed damage to the mob's Persistent data container.
     * @param mobPDC Mob's Persistent Data Container
     * @param damage double Total Damage Dealt
     */
    private void addPDCDamage(EntityDamageEvent event, PersistentDataContainer mobPDC, double damage) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        double totalHealth = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        double damageTotal = mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0);
        if (damageTotal + damage > totalHealth) {
            double currentHealth = ((LivingEntity) event.getEntity()).getHealth();
            damageTotal += currentHealth;
            NerfFarms.debugLvl3("damageTotal + damage was greater than total health, adding current health to PDC, which is: " + currentHealth
            + ". Total damage is now " + damageTotal);
        } else {
            damageTotal += damage;
            NerfFarms.debugLvl3("added " + damage + " to mob's PDC. Total damage is now " + damageTotal);
        }
        mobPDC.set(blacklistedDamage, PersistentDataType.DOUBLE, damageTotal);
    }

    /**
     * Checks if the configured maximum blacklisted damage threshold has been reached, and if it has, marks the mob to be nerfed
     * @param mobPDC Mob's Persistent Data Container
     * @param entity double Total Damage Dealt
     */
    private void checkDamageThreshold(PersistentDataContainer mobPDC, Entity entity) {
        int maxBlacklistedDamage = ConfigParser.getMaxBlacklistedDamage();
        double nerfedDamage = mobPDC.getOrDefault(blacklistedDamage, PersistentDataType.DOUBLE, 0.0);
        double maxHealth = Objects.requireNonNull(((Mob) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        int percentDamage = (int) ((nerfedDamage / maxHealth) * 100);
        if (percentDamage >= maxBlacklistedDamage) {
            NerfFarms.debugLvl3("Nerfing " + entity.getName() + " because they took " + percentDamage + "% total damage from nerfable causes");
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return;
        }
        NerfFarms.debugLvl3(entity + " is not above the damage threshold. They are not being marked as nerfed.");
    }

    /**
     * Checks if the damaged entity is a mob. Returns False if it is not a mob
     * @param entity Damaged Entity
     * @return boolean
     */
    private boolean isMob(Entity entity) {
        NerfFarms.debugLvl1("Performing isMob on " + entity.getName());
        if (!(entity instanceof Mob)) {
            NerfFarms.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " is not a mob. Returning false");
            return false;
        }
        return true;
    }

    /**
     * Checks if the mob has already been marked for nerfing, returns true if it has
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @return boolean
     */
    private boolean isNerfed(Entity entity, PersistentDataContainer mobPDC) {
        NerfFarms.debugLvl1("Performing isNerfed on " + entity.getName());
        if (mobPDC.has(nerfMob)) {
            NerfFarms.debugLvl2(entity.getName() + " is already nerfed, ignoring, and returning true");
            return true;
        }
        return false;
    }

    /**
     * Checks if the mob is hostile, and compares to the configured settings for 'only-nerf-hostiles'
     * Returns false if the mob is not a monster, and the config is set 'only-nerf-hostiles: true'
     * @param entity Damaged Entity
     * @return boolean
     */
    private boolean checkHostile(Entity entity) {
        NerfFarms.debugLvl1("Performing isHostileNerf on " + entity.getName());
        if (ConfigToggle.ONLY_NERF_HOSTILES.isEnabled() && !(entity instanceof Monster)) {
            NerfFarms.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " is not a Monster and Nerf Hostiles Only is True. Returning false");
            return false;
        }
        NerfFarms.debugLvl2("Cleared all 'checkHostile' checks. Returning true");
        return true;
    }

    /**
     * Checks mob spawn reason, returns true if the SpawnReason is on the configured whitelist
     * @param entity Damaged Entity
     * @return boolean
     */
    private boolean isWhitelistedSpawnReason(Entity entity) {
        NerfFarms.debugLvl1("Performing isWhitelistedSpawnReason on " + entity.getName());
        if (ConfigParser.getWhitelistedSpawnReasonList().contains(entity.getEntitySpawnReason())) {
            NerfFarms.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " spawned from "
            + entity.getEntitySpawnReason() + " which isn't nerfed. Returning true");
            return true;
        }
        NerfFarms.debugLvl2("Cleared all 'isWhitelistedSpawnReason' checks. Returning false");
        return false;
    }

    /**
     * Checks mob spawn reason, nerfs and returns true if the SpawnReason is on the configured blacklist
     * @param entity Damaged Entity
     * @return boolean
     */
    private boolean isBlacklistedSpawnReason(Entity entity) {
        NerfFarms.debugLvl1("Performing isBlacklistedSpawnReason on " + entity.getName());
        if (ConfigParser.getBlacklistedSpawnReasonList().contains(entity.getEntitySpawnReason())) {
            entity.getPersistentDataContainer().set(nerfMob, PersistentDataType.BYTE, t);
            NerfFarms.debugLvl2("Nerfing " + entity.getName() + " because they spawned with the spawn reason "
                    + entity.getEntitySpawnReason() + " which is blacklisted. Setting mob as nerfed. Returning true");
            return true;
        }
        NerfFarms.debugLvl2("Cleared all 'isBlacklistedSpawnReason' checks. Returning false");
        return false;
    }

    /**
     * Checks mob type, returns true if the mob is on the configured whitelist
     * @param entity Damaged Entity
     * @return boolean
     */
    private boolean isWhitelistedMob(Entity entity) {
        NerfFarms.debugLvl1("Performing isWhitelistedMob on " + entity.getName());
        if (ConfigParser.getWhitelistedMobList().contains(entity.getType())) {
            NerfFarms.debugLvl2("Ignoring onMobDamage because " + entity.getName() + " is on the Whitelisted Mob list as "
            + entity.getType() + ". Returning true");
            return true;
        }
        NerfFarms.debugLvl2("Cleared all 'isWhitelistedMob' checks. Returning false");
        return false;
    }

    /**
     * Checks mob type, nerfs and returns true if the mob is on the configured blacklist
     * @param entity Damaged Entity
     * @return boolean
     */
    private boolean isBlacklistedMob(Entity entity) {
        NerfFarms.debugLvl1("Performing isWhitelistedMob on " + entity.getName());
        if (ConfigParser.getBlacklistedMobList().contains(entity.getType())) {
            entity.getPersistentDataContainer().set(nerfMob, PersistentDataType.BYTE, t);
            NerfFarms.debugLvl2("Nerfing " + entity.getName() + " Because they are on the blacklisted mob types as "
                    + entity.getType() + ". Nerfing and returning true");
            return true;
        }
        NerfFarms.debugLvl2("Cleared all 'isWhitelistedMob' checks. Returning false");
        return false;
    }

    /**
     * Checks the type of damage, compares to the configured blacklisted damage types.
     * Returns true if the damage matches one of the blacklisted types
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    private boolean checkDamageType(EntityDamageEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugLvl1("Performing checkDamageType on " + entity.getName());
        if (ConfigParser.getBlacklistedDamageTypesSet().contains(event.getCause())) {
            NerfFarms.debugLvl2(event.getCause() + " is a blacklisted damage type. Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        NerfFarms.debugLvl2("Cleared all checkDamageType checks. Returning false");
        return false;
    }

    /**
     * Checks the block the damaged mob is standing on, and compares it to the configured blacklist
     * Returns true if the block matches one of the blacklisted type
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    private boolean checkStandingOn(EntityDamageEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugLvl1("Performing checkStandingOn on " + entity.getName());
        Location mobStandingOnLocation = entity.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        if (ConfigParser.getStandOnBlackList().contains(entityStandingOn)) {
            NerfFarms.debugLvl2(entityStandingOn + " is a 'blacklisted-below' block. Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        NerfFarms.debugLvl2("Cleared all CheckStandingOn checks. Returning false");
        return false;
    }

    /**
     * Checks the block the damaged mob is standing inside, and compares it to the configured blacklist
     * Returns true if the block matches one of the blacklisted types
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    private boolean checkStandingInside(EntityDamageEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugLvl1("Performing checkStandingInside on " + entity.getName());
        Material entityStandingIn = entity.getLocation().getBlock().getType();
        if (ConfigParser.getInsideBlackList().contains(entityStandingIn)) {
            NerfFarms.debugLvl2(entityStandingIn + " is a 'blacklisted-in' block. Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        NerfFarms.debugLvl2("Cleared all 'checkStandingInside' checks. Returning false");
        return false;
    }

    /**
     * Checks if the mob has a valid line of sight to the player
     * Returns false if they do not
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    private boolean checkLineOfSight(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugLvl1("Performing checkLineOfSight on " + entity.getName());
        if (!(entity instanceof LivingEntity)) return true;
        if (!ConfigToggle.REQUIRE_LINE_OF_SIGHT.isEnabled()) return true;
        Entity damager = CheckUtils.getRealDamager(event);
        if (damager == null) {
            NerfFarms.debugLvl2("Mob does not have a viable line-of-sight because 'getRealDamager' has returned a null value. " +
            "Returning false");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return false;
        }
        boolean lineOfSight = ((LivingEntity) entity).hasLineOfSight(damager);
        if (!lineOfSight) {
            NerfFarms.debugLvl2("Mob does not have a viable line-of-sight to " + damager + ". Returning false");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return false;
        }
        NerfFarms.debugLvl2("Cleared all line-of-sight checks. Returning true");
        return true;
    }

    /**
     * Checks the damager of the entity, and compares it to multiple configurations
     * Checks for Skeleton attacking creeper, compares to configuration "skeletons-can-attack-creepers"
     * Checks for Wither attacking entity, compares to configuration "withers-can-attack-entities"
     * Checks for Projectile damage, compares to "allow-projectile-damage"
     * If all these checks pass, and the damager is not a player, returns true
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    private boolean checkDamager(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugLvl1("Performing checkDamager on " + entity.getName());
        Entity damager = CheckUtils.getRealDamager(event);
        if (damager instanceof IronGolem && ConfigToggle.ALLOW_IRON_GOLEM_DAMAGE.isEnabled()) {
            NerfFarms.debugLvl2("Skipping nerf on " + entity.getName() + "because 'Iron golems can damage entities' is 'true'. Returning true");
            return true;
        }
        if (damager instanceof Wither && ConfigToggle.ALLOW_WITHER_DAMAGE.isEnabled()) {
            NerfFarms.debugLvl2("Skipping nerf on " + entity.getName() + "because 'Withers can damage entities' is 'true'. Returning true");
            return true;
        }
        if (damager instanceof AbstractSkeleton && entity instanceof Creeper && ConfigToggle.ALLOW_SKELETON_CREEPER_DAMAGE.isEnabled()) {
            NerfFarms.debugLvl2("Skipping nerf on " + entity.getName() + "because 'Skeletons can damage creepers' is 'true'. Returning true (non-projectile damage)");
            return true;
        }
        if (damager instanceof Frog && entity instanceof Slime slimeEntity) {
            if (slimeEntity.getType().equals(EntityType.MAGMA_CUBE) && ConfigToggle.ALLOW_FROG_MAGMA_CUBE_DAMAGE.isEnabled()) {
                NerfFarms.debugLvl2("Skipping nerf on " + entity.getName() + "because 'frogs can eat magma cubes' is 'true'. Returning true");
                return true;
            }
            if (slimeEntity.getType().equals(EntityType.SLIME) && ConfigToggle.ALLOW_FROG_SLIME_DAMAGE.isEnabled()) {
                NerfFarms.debugLvl2("Skipping nerf on " + entity.getName() + "because 'frogs can eat slime' is 'true'. Returning true");
                return true;
            }
        }
        if (!(damager instanceof Player)) {
            NerfFarms.debugLvl1("Damager is not a player, returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        NerfFarms.debugLvl2("Cleared all 'checkDamager' checks. Returning false");
        return false;
    }

    /**
     * Checks if the mob has a calculated path to the player, and compares to configuration "require-path"
     * Returns true if there is not a calculated path
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    private boolean checkPath(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugLvl1("Performing checkPath on " + entity.getName());
        if (!ConfigToggle.REQUIRE_PATH.isEnabled()) return false;
        if (!(entity instanceof LivingEntity)) return false;
        Entity damager = CheckUtils.getRealDamager(event);
        if (damager == null){
            NerfFarms.debugLvl2("Entity does not have a path to the player, because 'getRealDamager' returned a null value. " +
            "Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        Location targetLoc = damager.getLocation();
        Pathfinder.PathResult entityPath = ((Mob) entity).getPathfinder().findPath(targetLoc);
        if (entityPath == null) {
            NerfFarms.debugLvl2("Entity's path is null. Returning false");
            return false;
        }
        int pathLength =  entityPath.getPoints().size();
        List<Location> pathPoints = entityPath.getPoints();
        if (pathLength <= 1 ) {
            NerfFarms.debugLvl2("Entity does not have a path to the player (Path length less than or equal to 1). Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        if (!CheckUtils.getBlockAbove(pathPoints.get(1), entity).isPassable()) {
            NerfFarms.debugLvl2("Entity does not have a path to the player (2nd path point is blocked). Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        if (pathLength >= 3 && !CheckUtils.getBlockAbove(pathPoints.get(2), entity).isPassable()) {
            NerfFarms.debugLvl2("Entity does not have a path to the player (3rd path point is blocked). Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        NerfFarms.debugLvl2("cleared all 'checkPath' checks. Returning false");
        return false;
    }

    /**
     * Checks the distance between the player and the damaged entity, compares it to the configured max distance
     * Returns true if the number is above the max distance
     * @param event EntityDamageEvent
     * @param entity Damaged Entity
     * @param mobPDC Mob's Persistent Data Container
     * @param hitDamage double Final Damage
     * @return boolean
     */
    private boolean checkDistance(EntityDamageByEntityEvent event, Entity entity, PersistentDataContainer mobPDC, double hitDamage) {
        NerfFarms.debugLvl1("Performing checkDistance on " + entity.getName());
        LivingEntity damager = CheckUtils.getRealDamager(event);
        if (damager == null) {
            NerfFarms.debugLvl2("Cannot check distance because the return value of getRealDamager is null. Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        Location entityLoc = entity.getLocation();
        Location damagerLoc = damager.getLocation();
        double distanceBetween = entityLoc.distance(damagerLoc);
        if (distanceBetween > ConfigParser.getMaxDistance()) {
            NerfFarms.debugLvl2(entity.getName() + " is above the max configured distance from the damager. Returning true");
            addPDCDamage(event, mobPDC, hitDamage);
            checkDamageThreshold(mobPDC, entity);
            return true;
        }
        NerfFarms.debugLvl2("Cleared all 'checkDistance' checks. Returning false");
        return false;
    }
}
