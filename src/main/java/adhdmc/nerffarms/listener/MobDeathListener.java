package adhdmc.nerffarms.listener;


import adhdmc.nerffarms.config.ConfigToggle;
import adhdmc.nerffarms.config.ModType;
import adhdmc.nerffarms.util.NFKey;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MobDeathListener implements Listener {
    private final NamespacedKey nerfMob = NFKey.NERF_MOB.getKey();
    private final NamespacedKey pickedUp = NFKey.PICKED_UP_ITEM.getKey();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDeath(EntityDeathEvent deathEvent) {
        Entity deadMob = deathEvent.getEntity();
        PersistentDataContainer mobPDC = deadMob.getPersistentDataContainer();
        if (mobPDC.has(nerfMob)) {
            Util.debugLvl3("Running clearDrops");
            clearDrops(deathEvent);
        }
    }

    private void clearDrops(EntityDeathEvent deathEvent) {
        ModType configMod = ModType.getModType();
        if (configMod == ModType.EXP || configMod == ModType.BOTH) {
            Util.debugLvl3("configMod Setting clears EXP.");
            deathEvent.setDroppedExp(0);
        }
        if (configMod == ModType.DROPS || configMod == ModType.BOTH) {
            Util.debugLvl3("configMod Setting clears Drops.");
            if (ConfigToggle.DROP_PLAYER_ITEMS.isEnabled()) {
                dropPlayerItems(deathEvent);
            } else {
                deathEvent.getDrops().clear();
            }
        }
    }

    private void dropPlayerItems(EntityDeathEvent deathEvent) {
        List<ItemStack> drops = deathEvent.getDrops();
        List<ItemStack> pickedUpItems = new ArrayList<>();
        for (ItemStack item : drops) {
            Util.debugLvl3("Item in inventory: " + item.toString() + "\n");
            ItemMeta itemMeta = item.getItemMeta();
            PersistentDataContainer itemPDC = itemMeta.getPersistentDataContainer();
            Util.debugLvl3("ItemPDC output: " + itemPDC.get(pickedUp, PersistentDataType.BYTE));
            if (itemPDC.get(pickedUp, PersistentDataType.BYTE) != null) {
                pickedUpItems.add(item);
                Util.debugLvl3("Adding item to items that should drop.");
            }
        }
        deathEvent.getDrops().clear();
        Util.debugLvl3("Clearing normal drops");
        for (ItemStack item : pickedUpItems) {
            Util.debugLvl3("Adding " + item.toString() + " to drops");
            deathEvent.getDrops().add(item);
        }
    }
}
