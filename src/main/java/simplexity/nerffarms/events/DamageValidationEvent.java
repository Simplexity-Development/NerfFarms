package simplexity.nerffarms.events;


import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import simplexity.nerffarms.util.NFToggles;

public class DamageValidationEvent extends Event implements Cancellable {
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();
    private final EntityDamageEvent entityDamageEvent;


    public DamageValidationEvent(EntityDamageEvent entityDamageEvent){
        this.entityDamageEvent = entityDamageEvent;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return entityDamageEvent.getCause();
    }
    public Entity getDamagedEntity() {
        return entityDamageEvent.getEntity();
    }

    public double getFinalDamageAmount() {
        return entityDamageEvent.getFinalDamage();
    }

    public EntityDamageByEntityEvent getEntityDamageByEntityEvent() {
        if (entityDamageEvent instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            return entityDamageByEntityEvent;
        }
        return null;
    }

    public Entity getDamagerFromEntityDamageByEntityEvent() {
        if (getEntityDamageByEntityEvent() != null) {
            return getEntityDamageByEntityEvent().getDamager();
        }
        return null;
    }

    public LivingEntity getOriginalSourceOfDamage() {
        Entity damager = getDamagerFromEntityDamageByEntityEvent();
        if (damager instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        if (damager instanceof Projectile projectile) {
            ProjectileSource projectileSource = projectile.getShooter();
            if (projectileSource instanceof LivingEntity livingProjectileSource) {
                return livingProjectileSource;
            }
        }
        return null;
    }

    public boolean isDamagerAllowed() {
        Entity damager = getDamagerFromEntityDamageByEntityEvent();
        if (damager == null) {
            return false;
        }
        if (((damager instanceof Skeleton) && (getDamagedEntity() instanceof Creeper)) && NFToggles.ALLOW_SKELETON_CREEPER_DAMAGE.isEnabled()) {
            return true;
        }
        if ((damager instanceof Wither) && NFToggles.ALLOW_WITHER_DAMAGE.isEnabled()) {
            return true;
        }
        if (((damager instanceof Frog) && (getDamagedEntity() instanceof Slime))) {
            return true;
        }
        if ((damager instanceof IronGolem) && NFToggles.ALLOW_IRON_GOLEM_DAMAGE.isEnabled()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EntityDamageEvent getEntityDamageEvent() {
        return entityDamageEvent;
    }
}
