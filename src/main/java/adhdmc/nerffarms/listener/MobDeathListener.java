package adhdmc.nerffarms.listener;


import adhdmc.nerffarms.NerfFarms;
import adhdmc.nerffarms.config.ModType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class MobDeathListener implements Listener {
    NamespacedKey nerfMob = MobDamageListener.nerfMob;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDeath(EntityDeathEvent deathEvent) {
        Entity deadMob = deathEvent.getEntity();
        PersistentDataContainer mobPDC = deadMob.getPersistentDataContainer();
        if (mobPDC.has(nerfMob)) {
            NerfFarms.debugLvl3("Running clearDrops");
            clearDrops(deathEvent);
        }
    }

    private void clearDrops(EntityDeathEvent deathEvent) {
        ModType configMod = ModType.getModType();
        if (configMod == ModType.EXP || configMod == ModType.BOTH) {
            NerfFarms.debugLvl3("configMod Setting clears EXP.");
            deathEvent.setDroppedExp(0);
        }
        if (configMod == ModType.DROPS || configMod == ModType.BOTH) {
            NerfFarms.debugLvl3("configMod Setting clears Drops.");
            deathEvent.getDrops().clear();
        }
    }
}
