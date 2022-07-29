package adhdmc.nerffarms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Logger;

public class MobDeathListener implements Listener {
    NamespacedKey nerfMob = new NamespacedKey(NerfFarms.plugin, "nerfMob");
    byte f = 0;
    byte t = 1;

    @EventHandler
    public void onMobDeath(EntityDeathEvent deathEvent){
        Entity deadMob = deathEvent.getEntity();
        PersistentDataContainer mobPDC = deadMob.getPersistentDataContainer();
        if (mobPDC.get(nerfMob, PersistentDataType.BYTE) != null && mobPDC.get(nerfMob, PersistentDataType.BYTE).equals(t)){
            clearDrops(deathEvent);
        }
    }
    private void clearDrops(EntityDeathEvent e){
        boolean d = ConfigParser.debug;
        Logger l  = NerfFarms.plugin.getLogger();
        String configMod = ConfigParser.modType;
        if(configMod == null || configMod.equalsIgnoreCase("")) {
            if (d) {
                l.info("configMod == null || configMod.equalsIgnoreCase(\"\")");
            }
            return;
        }
        if(configMod.equalsIgnoreCase("both")){
            if (d) {
                l.info("configMod.equalsIgnoreCase(\"both\"");
            }
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        if(configMod.equalsIgnoreCase("exp")){
            if (d) {
                l.info("configMod.equalsIgnoreCase(\"exp\")");
            }
            e.setDroppedExp(0);
        }
        if(configMod.equalsIgnoreCase("drops")){
            if (d) {
                l.info("configMod.equalsIgnoreCase(\"drops\")");
            }
            e.getDrops().clear();
        }
    }
}
