package simplexity.nerffarms.events;

import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import simplexity.nerffarms.util.NFConfig;
import simplexity.nerffarms.util.NFToggles;


public class DamageValidationEvent extends Event implements Cancellable {
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();
    private final org.bukkit.event.entity.EntityDamageEvent entityDamageEvent;
    private org.bukkit.event.entity.EntityDamageByEntityEvent entityDamageByEntityEvent;
    private org.bukkit.entity.LivingEntity bukkitDamagedLivingEntity;
    private net.minecraft.world.entity.LivingEntity nmsDamagedLivingEntity;
    private net.minecraft.world.entity.LivingEntity nmsAttackerLivingEntity;
    private net.minecraft.world.entity.projectile.Projectile nmsProjectile;


    public DamageValidationEvent(org.bukkit.event.entity.EntityDamageEvent entityDamageEvent){
        this.entityDamageEvent = entityDamageEvent;
    }

    public void runPreValidationChecks() {
        if (!isEntityDamageByEntityEvent()) {
            setCancelled(true);
            return;
        }
        if (getAttackerFromEntityDamageByEntityEvent() == null) {
            setCancelled(true);
        }
    }



    public org.bukkit.persistence.PersistentDataContainer getMobPDC() {
        return getBukkitDamagedLivingEntity().getPersistentDataContainer();
    }

    public org.bukkit.entity.Entity getDamagedEntity() {
        return getEntityDamageEvent().getEntity();
    }

    public double getFinalDamageAmount() {
        return getEntityDamageEvent().getFinalDamage();
    }

    public boolean isEntityDamageByEntityEvent() {
        if (getEntityDamageEvent() instanceof org.bukkit.event.entity.EntityDamageByEntityEvent entityDamageByEntity) {
            setEntityDamageByEntityEvent(entityDamageByEntity);
            setNmsDamagedLivingEntity(((CraftLivingEntity)entityDamageByEntity.getEntity()).getHandle());
            return true;
        }
        return false;
    }

    public org.bukkit.entity.Entity getAttackerFromEntityDamageByEntityEvent() {
        if (getEntityDamageByEntityEvent() != null) {
            setNmsAttackerLivingEntity(getNmsDamagedLivingEntity().getLastAttacker());
            return getEntityDamageByEntityEvent().getDamager();
        }
        return null;
    }

    public net.minecraft.world.damagesource.DamageSource getDamageSource() {
        return getNmsDamagedLivingEntity().getLastDamageSource();
    }

    public boolean isDamagerAllowed() {
        net.minecraft.world.entity.LivingEntity damager =  getNmsAttackerLivingEntity();
        if (damager == null) {
            return false;
        }
        if (damager instanceof net.minecraft.world.entity.player.Player) {
            return true;
        }
        if (((damager instanceof net.minecraft.world.entity.monster.AbstractSkeleton) && (getDamagedEntity() instanceof net.minecraft.world.entity.monster.Creeper)) && NFToggles.ALLOW_SKELETON_CREEPER_DAMAGE.isEnabled()) {
            return true;
        }
        if ((damager instanceof net.minecraft.world.entity.boss.wither.WitherBoss) && NFToggles.ALLOW_WITHER_DAMAGE.isEnabled()) {
            return true;
        }
        if ((damager instanceof net.minecraft.world.entity.animal.frog.Frog) && (getDamagedEntity() instanceof net.minecraft.world.entity.monster.Slime slime)) {
            if (slime instanceof net.minecraft.world.entity.monster.MagmaCube && NFToggles.ALLOW_FROG_MAGMA_CUBE_DAMAGE.isEnabled()) {
                return true;
            } else if (slime instanceof net.minecraft.world.entity.monster.MagmaCube) {
                return false;
            }
            return NFToggles.ALLOW_FROG_SLIME_DAMAGE.isEnabled();
        }
        if ((damager instanceof net.minecraft.world.entity.animal.IronGolem) && NFToggles.ALLOW_IRON_GOLEM_DAMAGE.isEnabled()) {
            return true;
        }
        return false;
    }

    public boolean withinDistance() {
        net.minecraft.world.entity.LivingEntity attacker = getNmsAttackerLivingEntity();
        net.minecraft.world.entity.LivingEntity damaged = getNmsDamagedLivingEntity();
        return attacker.closerThan(damaged, NFConfig.getMaxHeightDifference(), NFConfig.getMaxDistance());
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

    public org.bukkit.event.entity.EntityDamageEvent getEntityDamageEvent() {
        return entityDamageEvent;
    }

    public net.minecraft.world.entity.LivingEntity getNmsDamagedLivingEntity() {
        return nmsDamagedLivingEntity;
    }

    public void setNmsDamagedLivingEntity(net.minecraft.world.entity.LivingEntity nmsDamagedLivingEntity) {
        this.nmsDamagedLivingEntity = nmsDamagedLivingEntity;
    }

    public net.minecraft.world.entity.LivingEntity getNmsAttackerLivingEntity() {
        return nmsAttackerLivingEntity;
    }

    public void setNmsAttackerLivingEntity(net.minecraft.world.entity.LivingEntity nmsAttackerLivingEntity) {
        this.nmsAttackerLivingEntity = nmsAttackerLivingEntity;
    }

    public net.minecraft.world.entity.projectile.Projectile getNmsProjectile() {
        return nmsProjectile;
    }

    public void setNmsProjectile(net.minecraft.world.entity.projectile.Projectile nmsProjectile) {
        this.nmsProjectile = nmsProjectile;
    }

    public org.bukkit.entity.LivingEntity getBukkitDamagedLivingEntity() {
        return bukkitDamagedLivingEntity;
    }

    public void setBukkitDamagedLivingEntity(org.bukkit.entity.LivingEntity bukkitDamagedLivingEntity) {
        this.bukkitDamagedLivingEntity = bukkitDamagedLivingEntity;
    }

    public void setEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        this.entityDamageByEntityEvent = entityDamageByEntityEvent;
    }

    public org.bukkit.event.entity.EntityDamageByEntityEvent getEntityDamageByEntityEvent() {
        return entityDamageByEntityEvent;
    }
}
