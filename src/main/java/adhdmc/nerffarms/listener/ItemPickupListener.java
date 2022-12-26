package adhdmc.nerffarms.listener;

import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ItemPickupListener implements Listener {
    private final NamespacedKey pickedUp = Util.pickedUpItem;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent pickupEvent) {
        if (!(pickupEvent.getEntity() instanceof Mob mob)) return;
        ItemStack item = pickupEvent.getItem().getItemStack();
        byte t = 1;
        item.getItemMeta().getPersistentDataContainer().set(pickedUp, PersistentDataType.BYTE, t);
    }
}
