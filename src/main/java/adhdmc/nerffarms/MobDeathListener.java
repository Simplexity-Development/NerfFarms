package adhdmc.nerffarms;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import adhdmc.nerffarms.ConfigParser.ModType;

import java.util.logging.Logger;

public class MobDeathListener implements Listener {
    NamespacedKey nerfMob = new NamespacedKey(NerfFarms.plugin, "nerfMob");
    byte f = 0;
    byte t = 1;

    @EventHandler
    public void onMobDeath(EntityDeathEvent deathEvent) {
        Entity deadMob = deathEvent.getEntity();
        PersistentDataContainer mobPDC = deadMob.getPersistentDataContainer();
        if (mobPDC.has(nerfMob)) {
            clearDrops(deathEvent);
        }
    }

    private void clearDrops(EntityDeathEvent e) {
        boolean d = ConfigParser.isDebug();
        Logger l = NerfFarms.plugin.getLogger();
        ModType configMod = ConfigParser.getModType();
        if (configMod == ModType.EXP || configMod == ModType.BOTH) {
            if (d) {
                l.info("configMod Setting clears EXP.");
            }
            e.setDroppedExp(0);
        }
        if (configMod == ModType.DROPS || configMod == ModType.BOTH) {
            if (d) {
                l.info("configMod Setting clears Drops.");
            }
            e.getDrops().clear();
        }
    }
}
