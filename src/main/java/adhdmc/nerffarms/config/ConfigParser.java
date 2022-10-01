package adhdmc.nerffarms.config;

import adhdmc.nerffarms.NerfFarms;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ConfigParser {
    public enum ModType {EXP, DROPS, BOTH, NEITHER}
    public enum ConfigToggles {
        //Bypass toggles
        ONLY_NERF_HOSTILES, ALLOW_SKELETON_CREEPER_DAMAGE, ALLOW_WITHER_DAMAGE,
        //Nerfing checks
        REQUIRE_PATH, REQUIRE_LINE_OF_SIGHT, ALLOW_PROJECTILE_DAMAGE
        }

    private static final HashSet<Material> standOnBlacklist = new HashSet<>();
    private static final HashSet<Material> insideBlacklist = new HashSet<>();
    private static final HashSet<EntityType> bypassList = new HashSet<>();
    private static final HashSet<CreatureSpawnEvent.SpawnReason> spawnReasonList = new HashSet<>();
    private static final HashSet<EntityDamageEvent.DamageCause> blacklistedDamageTypes = new HashSet<>();
    private static ModType modType = ModType.NEITHER;
    private static final HashMap<ConfigToggles, Boolean> configToggles = new HashMap<>();
    private static int maxDistance = 0;
    private static int errorCount = 0;
    private static int maxBlacklistedDamage = 100;
    private static boolean debug = false;

    public static void validateConfig() {
        //you're doing the best you can, config.
        //clear any set stuff.
        standOnBlacklist.clear();
        insideBlacklist.clear();
        bypassList.clear();
        spawnReasonList.clear();
        blacklistedDamageTypes.clear();
        modType = null;
        configToggles.clear();
        maxDistance = 0;
        errorCount = 0;
        maxBlacklistedDamage = 100;
        FileConfiguration config = NerfFarms.plugin.getConfig();
        List<String> standStringList = config.getStringList("blacklisted-below");
        List<String> inStringList = config.getStringList("blacklisted-in");
        List<String> bypassStringList = config.getStringList("bypass");
        List<String> spawnReasonStringList = config.getStringList("whitelisted-spawn-types");
        List<String> blacklistedDamageTypesList = config.getStringList("blacklisted-damage-types");
        String modificationTypeString = config.getString("modification-type");
        int maxDistanceInt = config.getInt("max-distance");
        int maxBlacklistedDamageConfig = config.getInt("max-blacklisted-damage-percent");
        boolean nerfHostilesBoolean = config.getBoolean("only-nerf-hostiles");
        boolean requirePathBoolean = config.getBoolean("require-path");
        boolean requireLineOfSightBoolean = config.getBoolean("require-line-of-sight");
        boolean allowProjectileDamageBoolean = config.getBoolean("allow-projectile-damage");
        boolean skeletonsDamageCreepersBoolean = config.getBoolean("skeletons-can-damage-creepers");
        boolean withersDamageEntitiesBoolean = config.getBoolean("withers-can-damage-entities");
        debug = config.getBoolean("debug");

        // Assemble the Stand On BlackList
        for (String type : standStringList) {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock()) {
                standOnBlacklist.add(materialType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid block for mobs to stand on, please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Assemble the Inside BlackList
        for (String type : inStringList) {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock()) {
                insideBlacklist.add(materialType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid block for mobs to be inside, please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Mob Bypass
        for (String type : bypassStringList) {
            if (type == null || type.equalsIgnoreCase("")) break;
            try {
                EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                errorCount = errorCount + 1;
                continue;
            }
            EntityType entityType = EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            if (entityType.isAlive()) {
                bypassList.add(entityType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid entity for bypass. Please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Generate Spawn Reasons
        for (String type : spawnReasonStringList) {
            try {
                CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid spawn reason. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            spawnReasonList.add(CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH)));
        }

        // Generate Environmental Causes
        for (String type : blacklistedDamageTypesList) {
            try {
                EntityDamageEvent.DamageCause.valueOf(type);
            } catch (IllegalArgumentException exception) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid damage type. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            blacklistedDamageTypes.add(EntityDamageEvent.DamageCause.valueOf(type));
        }

        // Determine modType
        try {
            modType = ModType.valueOf(modificationTypeString);
        } catch (IllegalArgumentException exception) {
            NerfFarms.plugin.getLogger().severe(modificationTypeString + " is not a valid modification type. Plugin will not function properly until this is fixed.");
            modType = ModType.NEITHER;
        }

        // Determine Distance
        if (!(maxDistanceInt > 1 && maxDistanceInt < 120)) {
            NerfFarms.plugin.getLogger().warning("Max player distance must be between 1 and 120, setting distance to 20");
            errorCount = errorCount + 1;
            maxDistance = 20;
        } else {
            maxDistance = maxDistanceInt;
        }

        // Determine Percent Damage from Environment
        if (maxBlacklistedDamageConfig <= 0 || maxBlacklistedDamageConfig > 100) {
            NerfFarms.plugin.getLogger().warning("Percent damage from Environment must be between 1 and 100, setting to 100");
            errorCount = errorCount + 1;
            maxBlacklistedDamage = 100;
        } else {
            maxBlacklistedDamage = maxBlacklistedDamageConfig;
        }

        // Set Booleans
        configToggles.put(ConfigToggles.ONLY_NERF_HOSTILES, nerfHostilesBoolean);
        configToggles.put(ConfigToggles.ALLOW_SKELETON_CREEPER_DAMAGE, skeletonsDamageCreepersBoolean);
        configToggles.put(ConfigToggles.ALLOW_WITHER_DAMAGE, withersDamageEntitiesBoolean);
        configToggles.put(ConfigToggles.REQUIRE_PATH, requirePathBoolean);
        configToggles.put(ConfigToggles.REQUIRE_LINE_OF_SIGHT, requireLineOfSightBoolean);
        configToggles.put(ConfigToggles.ALLOW_PROJECTILE_DAMAGE, allowProjectileDamageBoolean);
    }

    public static Set<Material> getStandOnBlackList() {
        return Collections.unmodifiableSet(standOnBlacklist);
    }

    public static Set<Material> getInsideBlackList() {
        return Collections.unmodifiableSet(insideBlacklist);
    }

    public static Set<EntityType> getBypassList() {
        return Collections.unmodifiableSet(bypassList);
    }

    public static Set<CreatureSpawnEvent.SpawnReason> getSpawnReasonList() {
        return Collections.unmodifiableSet(spawnReasonList);
    }

    public static Set<EntityDamageEvent.DamageCause> getblacklistedDamageTypesSet() {
        return Collections.unmodifiableSet(blacklistedDamageTypes);
    }

    /**
     * Returns the modType setting, defining if EXP and/or Drops should be cleared.
     *
     * @return ModType enum, defaults to NEITHER
     */
    @NotNull
    public static ModType getModType() {
        return modType;
    }

    public static int getMaxDistance() {
        return maxDistance;
    }

    public static int getErrorCount() {
        return errorCount;
    }

    public static Map<ConfigToggles, Boolean> getConfigToggles() {
        return Collections.unmodifiableMap(configToggles);
    }

    public static int getMaxBlacklistedDamage() {
        return maxBlacklistedDamage;
    }

    public static boolean isDebug() {
        return debug;
    }

}
