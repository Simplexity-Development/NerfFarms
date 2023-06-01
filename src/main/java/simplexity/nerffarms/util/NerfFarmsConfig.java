package simplexity.nerffarms.util;

import simplexity.nerffarms.NerfFarms;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class NerfFarmsConfig {
    private static final HashSet<Material> standOnBlacklist = new HashSet<>();
    private static final HashSet<Material> insideBlacklist = new HashSet<>();
    private static final HashSet<EntityType> whitelistedMobList = new HashSet<>();
    private static final HashSet<EntityType> blacklistedMobList = new HashSet<>();
    private static final HashSet<CreatureSpawnEvent.SpawnReason> whitelistedSpawnReasonList = new HashSet<>();
    private static final HashSet<CreatureSpawnEvent.SpawnReason> blacklistedSpawnReasonList = new HashSet<>();
    private static final HashSet<EntityDamageEvent.DamageCause> blacklistedDamageTypes = new HashSet<>();
    private static final HashSet<EntityType> blacklistedPickupMobs = new HashSet<>();
    private static int maxDistance = 0;
    private static int maxHeightDifference;
    private static int errorCount = 0;
    private static int maxBlacklistedDamage = 100;
    private static int debug = 0;

    public static void validateConfig() {
        //you're doing the best you can, NerfFarms.plugin.getConfig().
        //clear any set stuff.
        standOnBlacklist.clear();
        insideBlacklist.clear();
        whitelistedMobList.clear();
        blacklistedMobList.clear();
        whitelistedSpawnReasonList.clear();
        blacklistedSpawnReasonList.clear();
        blacklistedDamageTypes.clear();
        blacklistedPickupMobs.clear();
        maxDistance = 0;
        maxHeightDifference = 0;
        errorCount = 0;
        debug = 0;
        maxBlacklistedDamage = 100;
        List<String> standStringList = NerfFarms.getInstance().getConfig().getStringList("blacklisted-below");
        List<String> inStringList = NerfFarms.getInstance().getConfig().getStringList("blacklisted-in");
        List<String> whitelistedMobStringList = NerfFarms.getInstance().getConfig().getStringList("whitelisted-mobs");
        List<String> blacklistedMobStringList = NerfFarms.getInstance().getConfig().getStringList("blacklisted-mobs");
        List<String> whitelistedSpawnReasonStringList = NerfFarms.getInstance().getConfig().getStringList("whitelisted-spawn-reasons");
        List<String> blacklistedSpawnReasonStringList = NerfFarms.getInstance().getConfig().getStringList("blacklisted-spawn-reasons");
        List<String> blacklistedDamageTypesList = NerfFarms.getInstance().getConfig().getStringList("blacklisted-damage-types");
        List<String> blacklistedPickupMobsList = NerfFarms.getInstance().getConfig().getStringList("blacklisted-pickups-mob");
        int maxDistanceInt = NerfFarms.getInstance().getConfig().getInt("max-distance");
        int maxHeightDifferenceInt = NerfFarms.getInstance().getConfig().getInt("max-height-difference");
        int maxBlacklistedDamageConfig = NerfFarms.getInstance().getConfig().getInt("max-blacklisted-damage-percent");
        debug = NerfFarms.getInstance().getConfig().getInt("debug");

        // Assemble the Stand On BlackList
        for (String type : standStringList) {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock()) {
                standOnBlacklist.add(materialType);
            } else {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid block for mobs to stand on, please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Assemble the Inside BlackList
        for (String type : inStringList) {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock()) {
                insideBlacklist.add(materialType);
            } else {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid block for mobs to be inside, please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Mob Whitelist
        for (String type : whitelistedMobStringList) {
            if (type == null || type.equalsIgnoreCase("")) break;
            try {
                EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid entity to whitelist. Please choose another.");
                errorCount = errorCount + 1;
                continue;
            }
            EntityType entityType = EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            if (entityType.isAlive()) {
                whitelistedMobList.add(entityType);
            } else {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid entity to whitelist. Please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Mob Blacklist
        for (String type : blacklistedMobStringList) {
            if (type == null || type.equalsIgnoreCase("")) break;
            try {
                EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                errorCount = errorCount + 1;
                continue;
            }
            EntityType entityType = EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            if (entityType.isAlive()) {
                blacklistedMobList.add(entityType);
            } else {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Generate Spawn Reasons
        for (String type : whitelistedSpawnReasonStringList) {
            if (type == null || type.equalsIgnoreCase("")) break;
            try {
                CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid spawn reason (whitelisted-spawn-reasons). Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            whitelistedSpawnReasonList.add(CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH)));
        }

        // Generate Spawn Reasons
        for (String type : blacklistedSpawnReasonStringList) {
            if (type == null || type.equalsIgnoreCase("")) break;
            try {
                CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid spawn reason (blacklisted-spawn-reasons). Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            blacklistedSpawnReasonList.add(CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH)));
        }
        for (String type : blacklistedPickupMobsList) {
            if (type == null || type.equalsIgnoreCase("")) break;
            try {
                EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                errorCount = errorCount + 1;
                continue;
            }
            EntityType entityType = EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            if (entityType.isAlive()) {
                blacklistedPickupMobs.add(entityType);
            } else {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                errorCount = errorCount + 1;
            }
        }
        // Generate Environmental Causes
        for (String type : blacklistedDamageTypesList) {
            try {
                EntityDamageEvent.DamageCause.valueOf(type);
            } catch (IllegalArgumentException exception) {
                NerfFarms.getInstance().getLogger().warning(type + " is not a valid damage type. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            blacklistedDamageTypes.add(EntityDamageEvent.DamageCause.valueOf(type));
        }
        // Determine modType
        ModType.setModType();

        // Determine Distance
        if (!(maxDistanceInt > 1 && maxDistanceInt < 120)) {
            NerfFarms.getInstance().getLogger().warning("Max player distance must be between 1 and 120, setting distance to 20");
            errorCount = errorCount + 1;
            maxDistance = 20;
        } else {
            maxDistance = maxDistanceInt;
        }

        // Determine height difference
        if (!(maxHeightDifferenceInt >= 0)) {
            NerfFarms.getInstance().getLogger().warning("Max height difference must be a positive integer. Setting to 10");
            errorCount = errorCount + 1;
            maxHeightDifference = 10;
        } else {
            maxHeightDifference = maxHeightDifferenceInt;
        }

        // Determine Percent Damage from Environment
        if (maxBlacklistedDamageConfig <= 0 || maxBlacklistedDamageConfig > 100) {
            NerfFarms.getInstance().getLogger().warning("Percent damage from Environment must be between 1 and 100, setting to 100");
            errorCount = errorCount + 1;
            maxBlacklistedDamage = 100;
        } else {
            maxBlacklistedDamage = maxBlacklistedDamageConfig;
        }
        ConfigToggle.reloadToggles();
    }

    public static Set<Material> getStandOnBlackList() {
        return Collections.unmodifiableSet(standOnBlacklist);
    }

    public static Set<Material> getInsideBlackList() {
        return Collections.unmodifiableSet(insideBlacklist);
    }

    public static Set<EntityType> getWhitelistedMobList() {
        return Collections.unmodifiableSet(whitelistedMobList);
    }
    public static Set<EntityType> getBlacklistedMobList() {
        return Collections.unmodifiableSet(blacklistedMobList);
    }

    public static Set<CreatureSpawnEvent.SpawnReason> getWhitelistedSpawnReasonList() {
        return Collections.unmodifiableSet(whitelistedSpawnReasonList);
    }
    public static Set<CreatureSpawnEvent.SpawnReason> getBlacklistedSpawnReasonList() {
        return Collections.unmodifiableSet(blacklistedSpawnReasonList);
    }

    public static Set<EntityDamageEvent.DamageCause> getBlacklistedDamageTypesSet() {
        return Collections.unmodifiableSet(blacklistedDamageTypes);
    }

    public static HashSet<EntityType> getBlacklistedPickupMobs() {
        return blacklistedPickupMobs;
    }

    /**
     * Returns the modType setting, defining if EXP and/or Drops should be cleared.
     *
     * @return ModType enum, defaults to NEITHER
     */

    public static int getMaxDistance() {
        return maxDistance;
    }

    public static int getMaxHeightDifference() {
        return maxHeightDifference;
    }

    public static int getErrorCount() {
        return errorCount;
    }

    public static int getMaxBlacklistedDamage() {
        return maxBlacklistedDamage;
    }

    public static int debugLevel() {
        return debug;
    }

}
