package adhdmc.nerffarms.listener;

import adhdmc.nerffarms.NerfFarms;
import adhdmc.nerffarms.config.ConfigParser;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import adhdmc.nerffarms.config.ConfigParser.ModType;

import java.util.logging.Logger;

public class MobDeathListener implements Listener {
    NamespacedKey nerfMob = MobDamageListener.nerfMob;
    boolean debug = ConfigParser.getConfigToggles().get(ConfigParser.ConfigToggles.DEBUG);
    Logger logger = NerfFarms.plugin.getLogger();

    @EventHandler
    public void onMobDeath(EntityDeathEvent deathEvent) {
        Entity deadMob = deathEvent.getEntity();
        PersistentDataContainer mobPDC = deadMob.getPersistentDataContainer();
        if (mobPDC.has(nerfMob)) {
            if (debug) {
                logger.info("Running clearDrops");
            }
            clearDrops(deathEvent);
        }
    }

    private void clearDrops(EntityDeathEvent entity) {
        ModType configMod = ConfigParser.getModType();
        if (configMod == ModType.EXP || configMod == ModType.BOTH) {
            if (debug) {
                logger.info("configMod Setting clears EXP.");
            }
            entity.setDroppedExp(0);
        }
        if (configMod == ModType.DROPS || configMod == ModType.BOTH) {
            if (debug) {
                logger.info("configMod Setting clears Drops.");
            }
            entity.getDrops().clear();
        }
    }
}
