package adhdmc.nerffarms.listener;

import adhdmc.nerffarms.util.NFConfig;
import adhdmc.nerffarms.util.NFKey;
import adhdmc.nerffarms.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
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
    HashSet<EntityType> pickupBlacklist = NFConfig.getBlacklistedPickupMobs();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent pickupEvent) {
        if (pickupBlacklist.contains(pickupEvent.getEntityType())) {
            pickupEvent.setCancelled(true);
        }
    }
}
