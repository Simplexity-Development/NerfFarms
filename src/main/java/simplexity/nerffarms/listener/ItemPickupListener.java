package simplexity.nerffarms.listener;

import simplexity.nerffarms.config.NerfFarmsConfig;
import simplexity.nerffarms.util.NerfFarmsNamespacedKey;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.HashSet;

public class ItemPickupListener implements Listener {
    private final NamespacedKey pickedUp = NerfFarmsNamespacedKey.PICKED_UP_ITEM.getKey();
    HashSet<EntityType> pickupBlacklist = NerfFarmsConfig.getBlacklistedPickupMobs();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent pickupEvent) {
        if (pickupBlacklist.contains(pickupEvent.getEntityType())) {
            pickupEvent.setCancelled(true);
        }
    }
}
