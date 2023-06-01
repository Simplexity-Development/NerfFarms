package simplexity.nerffarms.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import simplexity.nerffarms.listener.damagehandling.DamageListener;
import simplexity.nerffarms.util.ConfigToggle;
import simplexity.nerffarms.util.Debug;
import simplexity.nerffarms.util.NerfFarmsConfig;

import java.util.Objects;

public class AttackerValidationEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final EntityDamageByEntityEvent entityDamageByEntityEvent;
    private LivingEntity bukkitDamagedLivingEntity;
    private Entity bukkitAttackingEntity;
    private net.minecraft.world.entity.LivingEntity nmsDamagedLivingEntity;
    private net.minecraft.world.entity.LivingEntity nmsAttackerLivingEntity;
    public AttackerValidationEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        this.entityDamageByEntityEvent = entityDamageByEntityEvent;
    }
    
    public void runAttackerValidation() {
        populateAttackerEntity();
        if (getBukkitAttackingEntity() == null) {
            setCancelled(true);
            return;
        }
        populateDamagedEntity();
        if (getBukkitAttackingEntity() instanceof Mob && !isMobAllowedToFightThatMob()) {
            addPDCDamage();
            return;
        }
        if (getProjectile() != null) {
            if (getProjectileShooter() == null) {
                return;
            }
            if (getProjectileShooter() instanceof Mob && !isMobAllowedToFightThatMob()) {
                addPDCDamage();
                return;
            }
            setBukkitAttackingEntity(getProjectileShooter());
        }
        if (!isAttackerWithinConfiguredDistance()) {
            addPDCDamage();
        }
    }

    public void populateDamagedEntity() {
        if (getEntityDamageByEntityEvent().getEntity() instanceof LivingEntity livingEntity) {
            setBukkitDamagedLivingEntity(livingEntity);
            setNmsDamagedLivingEntity(((CraftLivingEntity)livingEntity).getHandle());
        }
    }

    public void populateAttackerEntity() {
        setBukkitAttackingEntity(getEntityDamageByEntityEvent().getDamager());
        setNmsAttackerLivingEntity(getNmsDamagedLivingEntity().getLastAttacker());
    }

    public double getFinalDamage() {
        return getEntityDamageByEntityEvent().getFinalDamage();
    }

    public Projectile getProjectile() {
        if (getBukkitAttackingEntity() instanceof Projectile projectile) {
            return projectile;
        }
        return null;
    }

    public LivingEntity getProjectileShooter() {
        if (getProjectile() != null) {
            ProjectileSource shooter = getProjectile().getShooter();
            if (shooter instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }
        return null;
    }

    public boolean isAttackerWithinConfiguredDistance() {
        return getNmsDamagedLivingEntity().closerThan(getNmsAttackerLivingEntity(),
                NerfFarmsConfig.getMaxHeightDifference(), NerfFarmsConfig.getMaxDistance());
    }

    public boolean isMobAllowedToFightThatMob() {
        if (!(getBukkitAttackingEntity() instanceof Mob mob)) return false;
        if (ConfigToggle.IGNORE_MOBS_ATTACKING_MOBS.isEnabled()) return true;
        if ((mob instanceof IronGolem) && ConfigToggle.ALLOW_IRON_GOLEM_DAMAGE.isEnabled()) return true;
        if ((mob instanceof Wither) && ConfigToggle.ALLOW_WITHER_DAMAGE.isEnabled()) return true;
        if (((mob instanceof AbstractSkeleton) && (getBukkitDamagedLivingEntity() instanceof Creeper)) && ConfigToggle.ALLOW_SKELETON_CREEPER_DAMAGE.isEnabled()) return true;
        if ((mob instanceof Frog) && (getBukkitDamagedLivingEntity() instanceof Slime slime)) {
            if ((slime instanceof MagmaCube) && ConfigToggle.ALLOW_FROG_MAGMA_CUBE_DAMAGE.isEnabled()) return true;
            if (slime instanceof MagmaCube) return false;
            return ConfigToggle.ALLOW_FROG_SLIME_DAMAGE.isEnabled();
        }
        return false;
    }

    public PersistentDataContainer getMobPDC() {
        return getBukkitDamagedLivingEntity().getPersistentDataContainer();
    }

    /**
     * Adds nerfed damage to the mob's Persistent data container.
     */
    public void addPDCDamage() {
        LivingEntity livingEntity = getBukkitDamagedLivingEntity();
        double totalHealth = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        double currentHealth = livingEntity.getHealth();
        double damageTotal = getMobPDC().getOrDefault(DamageListener.blacklistedDamage, PersistentDataType.DOUBLE, 0.0);
        if (damageTotal + getFinalDamage() > totalHealth) {
            damageTotal += currentHealth;
            Debug.debugLvl3("damageTotal + damage was greater than total health, adding current health to PDC, which is: " + currentHealth
                    + ". Total damage is now " + damageTotal);
        } else {
            damageTotal += getFinalDamage();
            Debug.debugLvl3("added " + getFinalDamage() + " to mob's PDC. Total damage is now " + damageTotal);
        }
        getMobPDC().set(DamageListener.blacklistedDamage, PersistentDataType.DOUBLE, damageTotal);
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


    public EntityDamageByEntityEvent getEntityDamageByEntityEvent() {
        return entityDamageByEntityEvent;
    }

    public LivingEntity getBukkitDamagedLivingEntity() {
        return bukkitDamagedLivingEntity;
    }

    public void setBukkitDamagedLivingEntity(LivingEntity bukkitDamagedLivingEntity) {
        this.bukkitDamagedLivingEntity = bukkitDamagedLivingEntity;
    }

    public Entity getBukkitAttackingEntity() {
        return bukkitAttackingEntity;
    }

    public void setBukkitAttackingEntity(Entity bukkitAttackingEntity) {
        this.bukkitAttackingEntity = bukkitAttackingEntity;
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

    public net.minecraft.world.entity.LivingEntity getNmsDamagedLivingEntity() {
        return nmsDamagedLivingEntity;
    }
}
