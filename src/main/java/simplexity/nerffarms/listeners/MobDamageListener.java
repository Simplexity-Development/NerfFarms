package simplexity.nerffarms.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import simplexity.nerffarms.config.Config;

import java.util.List;

public class MobDamageListener implements Listener {
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDamage(EntityDamageEvent damageEvent) {
        if (damageEvent instanceof EntityDamageByEntityEvent) return;
        if (!(damageEvent.getEntity() instanceof LivingEntity)) return;
        EntityDamageEvent.DamageCause damageCause = damageEvent.getCause();
        List<EntityDamageEvent.DamageCause> blacklistedDamageReasons = Config.getBlacklistedDamageReasons();
        if (!blacklistedDamageReasons.contains(damageCause)) return;
        //TODO Nerf logic
    }

}
