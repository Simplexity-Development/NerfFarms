package net.neednot.farmnerf;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Config {
    public enum ModType {EXP, DROPS, BOTH, NEITHER}

    private static final HashSet<Material> standOnBlacklist = new HashSet<>();
    private static final HashSet<Material> insideBlacklist = new HashSet<>();
    private static final HashSet<EntityType> bypassList = new HashSet<>();
    private static final HashSet<CreatureSpawnEvent.SpawnReason> spawnReasonList = new HashSet<>();
    private static final HashSet<EntityDamageEvent.DamageCause> damageCauseWhitelist = new HashSet<>();
    private static final HashSet<EntityDamageEvent.DamageCause> environmentalDamage = new HashSet<>();
    private static ModType modType;
    private static int maxDistance;
    private static int errorCount;
    private static float dropRate;
    private static float xpRate;
    private static int percentFromEnvironment;
    private static boolean nerfHostilesOnly;
    private static boolean requireTargeting;
    private static boolean debug;

    public static void validateConfig(FarmNerf plugin) {
        //you're doing the best you can, config.
        //clear any set stuff.
        standOnBlacklist.clear();
        insideBlacklist.clear();
        bypassList.clear();
        spawnReasonList.clear();
        damageCauseWhitelist.clear();
        modType = null;
        maxDistance = 0;
        errorCount = 0;
        nerfHostilesOnly = true;
        requireTargeting = false;
        debug = false;
        FileConfiguration config = plugin.getConfig();
        List<String> standStringList = config.getStringList("blacklisted-below");
        List<String> inStringList = config.getStringList("blacklisted-in");
        List<String> bypassStringList = config.getStringList("bypass");
        List<String> spawnReasonStringList = config.getStringList("spawn-types");
        List<String> damageWhitelist = config.getStringList("whitelisted-damage-types");
        List<String> environmentalDamageList = config.getStringList("environmental-damage-types");
        String modificationTypeString = config.getString("modification-type");
        int maxDistanceInt = config.getInt("max-mob-distance");
        int percentFromEnvironmentInt = config.getInt("percent-from-environment");
        dropRate = (float) config.getInt("drop-rate")/100;
        xpRate = (float) config.getInt("xp-rate")/100;
        boolean nerfHostilesBoolean = config.getBoolean("only-nerf-hostiles");
        boolean requireTargetingBoolean = config.getBoolean("require-targeting");
        boolean debugSetting = config.getBoolean("debug");

        // Assemble the Stand On BlackList
        for (String type : standStringList) {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock()) {
                standOnBlacklist.add(materialType);
            } else {
                plugin.getLogger().warning(type + " is not a valid block for mobs to stand on, please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Assemble the Inside BlackList
        for (String type : inStringList) {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock()) {
                insideBlacklist.add(materialType);
            } else {
                plugin.getLogger().warning(type + " is not a valid block for mobs to be inside, please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Mob Bypass
        for (String type : bypassStringList) {
            if (type == null || type.equalsIgnoreCase("")) break;
            try {
                EntityType.valueOf(type.toUpperCase(Locale.ENGLISH.ENGLISH));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                errorCount = errorCount + 1;
                continue;
            }
            EntityType entityType = EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            if (entityType.isAlive()) {
                bypassList.add(entityType);
            } else {
                plugin.getLogger().warning(type + " is not a valid entity for bypass. Please choose another.");
                errorCount = errorCount + 1;
            }
        }

        // Generate Spawn Reasons
        for (String type : spawnReasonStringList) {
            try {
                CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(type + " is not a valid spawn reason. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            spawnReasonList.add(CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH)));
        }

        // Generate Damage Causes
        for (String type : damageWhitelist) {
            try {
                EntityDamageEvent.DamageCause.valueOf(type);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(type + " is not a valid damage type. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            damageCauseWhitelist.add(EntityDamageEvent.DamageCause.valueOf(type));
        }

        // Generate Environmental Causes
        for (String type : environmentalDamageList) {
            try {
                EntityDamageEvent.DamageCause.valueOf(type);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(type + " is not a valid damage type. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            environmentalDamage.add(EntityDamageEvent.DamageCause.valueOf(type));
        }

        // Determine modType
        try {
            modType = ModType.valueOf(modificationTypeString);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe(modificationTypeString + " is not a valid modification type. Plugin will not function properly until this is fixed.");
            modType = ModType.NEITHER;
        }

        // Determine Distance
        maxDistance = Utils.clamp(maxDistanceInt, 1, 120);

        percentFromEnvironment = Utils.clamp(percentFromEnvironmentInt, 1, 100);

        // Set Booleans
        nerfHostilesOnly = nerfHostilesBoolean;
        requireTargeting = requireTargetingBoolean;
        debug = debugSetting;
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

    public static Set<EntityDamageEvent.DamageCause> getDamageCauseWhitelist() {
        return Collections.unmodifiableSet(damageCauseWhitelist);
    }

    public static Set<EntityDamageEvent.DamageCause> getEnvironmentalDamageSet() {
        return Collections.unmodifiableSet(environmentalDamage);
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
    public static float getDropRate() {return dropRate;}
    public static float getXpRate() {return xpRate;}

    public static boolean nerfHostilesOnly() {
        return nerfHostilesOnly;
    }

    public static boolean requireTargeting() {
        return requireTargeting;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static int getPercentFromEnvironment() {
        return percentFromEnvironment;
    }


}