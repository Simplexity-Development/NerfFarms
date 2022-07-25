package adhdmc.nerffarms;

import adhdmc.nerffarms.Commands.ReloadCommand;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfigParser {
    public static final ArrayList<Material> standOnBlacklist = new ArrayList<>();
    public static final ArrayList<Material> insideBlacklist = new ArrayList<>();
    public static final ArrayList<EntityType> bypassList = new ArrayList<>();
    public static final ArrayList<CreatureSpawnEvent.SpawnReason> spawnReasonList = new ArrayList<>();
    public static String modType = null;
    public static int maxDistance = 0;

    public static void checkStandMaterials(List<String> list){
        standOnBlacklist.clear();
        for (String type : list)
        {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock())
            {
                standOnBlacklist.add(materialType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid block for mobs to stand on, please choose another.");
                ReloadCommand.errorCount = ReloadCommand.errorCount + 1;
            }
        }
    }

    public static void checkInsideMaterials(List<String> list){
        insideBlacklist.clear();
        for (String type : list)
        {
            Material materialType = Material.matchMaterial(type);
            if (materialType != null && materialType.isBlock())
            {
                insideBlacklist.add(materialType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid block for mobs to be inside, please choose another.");
                ReloadCommand.errorCount = ReloadCommand.errorCount + 1;
                return;
            }
        }
    }

    public static void checkEntityList(List<String> list){
        bypassList.clear();
        for (String type : list)
        {
            if (type == null || type.equalsIgnoreCase("")) return;
            try {EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid entity to blacklist. Please choose another.");
                ReloadCommand.errorCount = ReloadCommand.errorCount + 1;
                return;
            }
            EntityType entityType  = EntityType.valueOf(type.toUpperCase(Locale.ENGLISH));
            if (entityType.isAlive())
            {
                bypassList.add(entityType);
            } else {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid entity for bypass. Please choose another.");
                ReloadCommand.errorCount = ReloadCommand.errorCount + 1;
            }
        }
    }

    public static void checkSpawnReason(List<String> list){
        spawnReasonList.clear();
        for (String type : list)
        {
            try {
                CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH));
                    } catch (IllegalArgumentException e) {
                NerfFarms.plugin.getLogger().warning(type + " is not a valid spawn reason. Please check that you have entered this correctly.");
                ReloadCommand.errorCount = ReloadCommand.errorCount + 1;
                return;
            }
            spawnReasonList.add(CreatureSpawnEvent.SpawnReason.valueOf(type.toUpperCase(Locale.ENGLISH)));
        }
    }

    public static void checkModificationType(String s){
        modType = "";
        if (s == null || s.equalsIgnoreCase("") || (!(s.equalsIgnoreCase("exp") || s.equalsIgnoreCase("drops") || s.equalsIgnoreCase("both")))){
            NerfFarms.plugin.getLogger().severe(s + " is not a valid modification type. Plugin will not function properly until this is fixed.");
            ReloadCommand.errorCount = ReloadCommand.errorCount + 1;
            return;
        }
        modType = s;
    }

    public static void checkDistance(int distance){
        maxDistance = 0;
        if (!(distance>1 && distance<120)){
            NerfFarms.plugin.getLogger().warning("Max player distance must be between 1 and 120, setting distance to 20");
            ReloadCommand.errorCount = ReloadCommand.errorCount + 1;
            maxDistance = 20;
            return;
        }
        maxDistance = distance;
    }
}
