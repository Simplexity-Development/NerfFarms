package simplexity.nerffarms.events;

import org.bukkit.entity.Mob;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import simplexity.nerffarms.config.NerfFarmsConfig;

import java.util.Collections;
import java.util.Set;

public class DamageValidationEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final EntityDamageEvent entityDamageEvent;
    private final Mob damagedMob;
    public DamageValidationEvent (EntityDamageEvent entityDamageEvent, Mob damagedMob) {
        this.entityDamageEvent = entityDamageEvent;
        this.damagedMob = damagedMob;
    }

    public Set<EntityDamageEvent.DamageCause> getConfiguredDamageCauseBlacklist() {
        return NerfFarmsConfig.getBlacklistedDamageTypesSet();
    }





    /**
     * Checks whether this event has been cancelled or not
     * @return boolean
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether this event should be cancelled or not
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
     * Gets the handler list for this event
     * @return HandlerList
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    /**
     * Gets the handler list for this event
     * @return HandlerList
     */
    @SuppressWarnings("unused") //Required for plugin
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EntityDamageEvent getEntityDamageEvent() {
        return entityDamageEvent;
    }

    public Mob getDamagedMob() {
        return damagedMob;
    }
}
