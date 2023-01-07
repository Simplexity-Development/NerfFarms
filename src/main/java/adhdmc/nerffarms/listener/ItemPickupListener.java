package adhdmc.nerffarms.listener;

import adhdmc.nerffarms.config.ConfigParser;
import adhdmc.nerffarms.config.ConfigToggle;
import adhdmc.nerffarms.util.NFKey;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;

public class ItemPickupListener implements Listener {
    private final NamespacedKey pickedUp = NFKey.PICKED_UP_ITEM.getKey();
    HashSet<EntityType> pickupBlacklist = ConfigParser.getBlacklistedPickupMobs();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent pickupEvent) {
        byte t = 1;
        if (!(pickupBlacklist.contains(pickupEvent.getEntityType()))) return;
        pickupEvent.setCancelled(true);
        /*ItemStack item = pickupEvent.getItem().getItemStack();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemPDC = meta.getPersistentDataContainer();
        itemPDC.set(pickedUp, PersistentDataType.BYTE, t);
        item.setItemMeta(meta);
        pickupEvent.getItem().setItemStack(item);
        Util.debugLvl3("Item meta, item picked up: " + pickupEvent.getItem().getItemStack().getItemMeta().getPersistentDataContainer().get(pickedUp, PersistentDataType.BYTE));
        */
    }
}
