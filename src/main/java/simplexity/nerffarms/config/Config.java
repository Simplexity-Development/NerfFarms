package simplexity.nerffarms.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final List<EntityDamageEvent.DamageCause> blacklistedDamageReasons = new ArrayList<>();
    
    public static void reloadConfig(FileConfiguration config){
        List<String> configDamageList = config.getStringList("blacklisted-damage-cause");
        blacklistedDamageReasons.clear();
        for (String string : configDamageList) {
            EntityDamageEvent.DamageCause damageCause = null;
            try {
                damageCause = EntityDamageEvent.DamageCause.valueOf(string);
            } catch (IllegalArgumentException e) {
                //todo LOG ERROR
                continue;
            }
            blacklistedDamageReasons.add(damageCause);
        }
    }
    
    public static List<EntityDamageEvent.DamageCause> getBlacklistedDamageReasons() {
        return blacklistedDamageReasons;
    }
}
