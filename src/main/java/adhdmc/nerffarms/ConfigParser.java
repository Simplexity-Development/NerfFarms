package adhdmc.nerffarms;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfigParser {
    public static final ArrayList<Material> standOnBlacklist = new ArrayList<>();
    public static final ArrayList<Material> insideBlacklist = new ArrayList<>();

    public static void checkStandMaterials(List<Material> list){
        standOnBlacklist.clear();
        for (Material type : list)
        {
            if (type != null && type.isBlock())
            {
                standOnBlacklist.add(type);
            } else {
                NerfFarms.plugin.getLogger().warning(type.toString().toLowerCase(Locale.ENGLISH) + "is not a valid block for mobs to stand on, please choose another.");
            }
        }
    }

    public static void checkInsideMaterials(List<Material> list){
        insideBlacklist.clear();
        for (Material type : list)
        {
            if (type != null && type.isBlock())
            {
                insideBlacklist.add(type);
            } else {
                NerfFarms.plugin.getLogger().warning(type.toString().toLowerCase(Locale.ENGLISH) + "is not a valid block for mobs to be inside, please choose another.");
            }
        }
    }

}
