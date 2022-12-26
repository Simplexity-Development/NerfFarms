package adhdmc.nerffarms.listener;


import adhdmc.nerffarms.NerfFarms;
import adhdmc.nerffarms.config.ModType;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MobDeathListener implements Listener {
    private final NamespacedKey nerfMob = Util.nerfMob;
    private final NamespacedKey pickedUp = Util.pickedUpItem;

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
            List<ItemStack> drops = deathEvent.getDrops();
            List<ItemStack> pickedUpItems = new ArrayList<>();
            for (ItemStack item : drops) {
                if (item.getItemMeta().getPersistentDataContainer().get(pickedUp, PersistentDataType.BYTE) != null) {
                    pickedUpItems.add(item);
                }
            }
            deathEvent.getDrops().clear();
            for (ItemStack item : pickedUpItems) {
                deathEvent.getDrops().add(item);
            }
        }
    }
}
