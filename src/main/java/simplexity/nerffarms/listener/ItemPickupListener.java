package simplexity.nerffarms.listener;

import simplexity.nerffarms.util.NFConfig;
import simplexity.nerffarms.util.NFKey;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

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
