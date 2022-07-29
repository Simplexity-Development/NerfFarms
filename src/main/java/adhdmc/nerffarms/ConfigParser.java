package adhdmc.nerffarms;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfigParser {
    public static final ArrayList<Material> standOnBlacklist = new ArrayList<>();
    public static final ArrayList<Material> insideBlacklist = new ArrayList<>();
    public static final ArrayList<EntityType> bypassList = new ArrayList<>();
    public static final ArrayList<CreatureSpawnEvent.SpawnReason> spawnReasonList = new ArrayList<>();
    public static final ArrayList<EntityDamageEvent.DamageCause> damageCauseWhitelist = new ArrayList<>();
    public static String modType = null;
    public static int maxDistance = 0;
    public static int errorCount = 0;
    public static boolean onlyNerfHostiles = true;
    public static boolean requireTargetting = false;
    public static boolean debug = false;

    public static void validateConfig(){
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
        onlyNerfHostiles = true;
        requireTargetting = false;
        debug = false;
        FileConfiguration config = NerfFarms.plugin.getConfig();
        List<String> standStringList = config.getStringList("blacklisted-below");
        List<String> inStringList = config.getStringList("blacklisted-in");
        List<String> bypassStringList = config.getStringList("bypass");
        List<String> spawnReasonStringList = config.getStringList("spawn-types");
        List<String> damageWhitelist = config.getStringList("whitelisted-damage-types");
        String modificationTypeString = config.getString("modification-type");
        int maxDistanceInt = config.getInt("max-mob-distance");
        boolean nerfHostilesBoolean = config.getBoolean("only-nerf-hostiles");
        boolean requireTargettingBoolean = config.getBoolean("require-targetting");
        boolean debugSetting = config.getBoolean("debug");
        for (String type : standStringList)
        {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock())
            {
                standOnBlacklist.add(materialType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid block for mobs to stand on, please choose another.");
                errorCount = errorCount + 1;
            }
        }
        for (String type : inStringList)
        {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock())
            {
                insideBlacklist.add(materialType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid block for mobs to be inside, please choose another.");
                errorCount = errorCount + 1;
            }
        }
        for (String type : bypassStringList)
        {
            if (type == null || type.equalsIgnoreCase("")) return;
            try {EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                errorCount = errorCount + 1;
                break;
            }
            EntityType entityType  = EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            if (entityType.isAlive())
            {
                bypassList.add(entityType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid entity for bypass. Please choose another.");
                errorCount = errorCount + 1;
            }
        }
        for (String type : spawnReasonStringList)
        {
            try {
                CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH));
                    } catch (IllegalArgumentException e) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid spawn reason. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            spawnReasonList.add(CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH)));
        }
        for (String type : damageWhitelist)
        {
            try {
                EntityDamageEvent.DamageCause.valueOf(type);
            } catch (IllegalArgumentException e) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid damage type. Please check that you have entered this correctly.");
                errorCount = errorCount + 1;
                continue;
            }
            damageCauseWhitelist.add(EntityDamageEvent.DamageCause.valueOf(type));
        }
        if (modificationTypeString == null || modificationTypeString.equalsIgnoreCase("") || (!(modificationTypeString.equalsIgnoreCase("exp") || modificationTypeString.equalsIgnoreCase("drops") || modificationTypeString.equalsIgnoreCase("both")))){
            NerfFarms.plugin.getLogger().severe(modificationTypeString + " is not a valid modification type. Plugin will not function properly until this is fixed.");
            modType = "";
            errorCount = errorCount + 1;
        } else {
        modType = modificationTypeString;
        }
        if (!(maxDistanceInt>1 && maxDistanceInt<120)){
            NerfFarms.plugin.getLogger().warning("Max player distance must be between 1 and 120, setting distance to 20");
            errorCount = errorCount + 1;
            maxDistance = 20;
        } else {
        maxDistance = maxDistanceInt;
        }
        onlyNerfHostiles = nerfHostilesBoolean;
        requireTargetting = requireTargettingBoolean;
        debug = debugSetting;
    }
}
