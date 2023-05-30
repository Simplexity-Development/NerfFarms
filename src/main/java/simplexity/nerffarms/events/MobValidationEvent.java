package simplexity.nerffarms.events;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Monster;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import simplexity.nerffarms.listener.damagehandling.DamageListener;
import simplexity.nerffarms.util.Debug;
import simplexity.nerffarms.util.NFConfig;
import simplexity.nerffarms.util.NFKey;
import simplexity.nerffarms.util.NFToggles;

/**
 * This class checks for anything that should end the checks early
 */
public class MobValidationEvent extends Event implements Cancellable {
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();
    private final NamespacedKey nerfOnDeath = NFKey.NERF_MOB.getKey();
    private final NamespacedKey bypassChecks = NFKey.BYPASS_MOB.getKey();
    private final org.bukkit.entity.LivingEntity bukkitLivingEntity;

    public MobValidationEvent(org.bukkit.entity.LivingEntity bukkitLivingEntity){
        this.bukkitLivingEntity = bukkitLivingEntity;
    }

    public void runChecks() {
        if (isNerfed()) {
            setCancelled(true);
            return;
        }
        if (isBypassed()) {
            setCancelled(true);
            return;
        }
        if (!isHostile() && NFToggles.ONLY_NERF_HOSTILES.isEnabled()) {
            setCancelled(true);
            return;
        }
        if (isWhitelistedMob()) {
            setCancelled(true);
            return;
        }
        if (isWhitelistedSpawnReason()) {
            setCancelled(true);
            return;
        }
        if (isBlacklistedMob()) {
            setCancelled(true);
            return;
        }
        if (isBlacklistedSpawnReason()) {
            setCancelled(true);
        }
    }

    /**
     * Checks if the mob has already been marked for nerfing, returns true if it has
     * @return boolean
     */
    public boolean isNerfed() {
        Debug.debugLvl1("Performing isNerfed() on " + bukkitLivingEntity.getName());
        if (getLivingEntityPDC().has(nerfOnDeath)) {
            Debug.debugLvl2(bukkitLivingEntity.getName() + " is already nerfed, ignoring, and returning true");
            return true;
        }
        return false;
    }

    /**
     * Checks if the mob has already been marked to bypass checks, returns true if it has
     * @return boolean
     */
    public boolean isBypassed() {
        Debug.debugLvl1("Performing isBypassed() on" + bukkitLivingEntity.getName());
        if (getLivingEntityPDC().has(bypassChecks)) {
            Debug.debugLvl2(bukkitLivingEntity.getName() + " is bypassed, ignoring, and returning true");
            return true;
        }
        return false;
    }

    /**
     * Checks if a mob is hostile. Returns true if it is, returns false if it is not
     * @return boolean
     */
    public boolean isHostile() {
        Debug.debugLvl1("Performing isHostile on " + bukkitLivingEntity.getName());
        if (bukkitLivingEntity instanceof Monster) {
            Debug.debugLvl2("LivingEntity is instanceof monster, returning true");
            return true;
        }
        Debug.debugLvl2("Cleared all 'isHostile' checks. Returning false");
        return false;
    }

    /**
     * Checks mob type, sets mob to bypass future checks and returns true if the mob is on the configured whitelist
     * @return boolean
     */
    public boolean isWhitelistedMob() {
        Debug.debugLvl1("Performing isWhitelistedMob on " + bukkitLivingEntity.getName());
        if (NFConfig.getWhitelistedMobList().contains(bukkitLivingEntity.getType())) {
            Debug.debugLvl2("Ignoring onMobDamage because " + bukkitLivingEntity.getName() + " is on the Whitelisted Mob list as "
                    + bukkitLivingEntity.getType() + ". Setting mob to bypass future checks. Returning true");
            getLivingEntityPDC().set(bypassChecks, PersistentDataType.BYTE, (byte) 1);
            return true;
        }
        Debug.debugLvl2("Cleared all 'isWhitelistedMob' checks. Returning false");
        return false;
    }

    /**
     * Checks mob spawn reason, returns true if the SpawnReason is on the configured whitelist
     * @return boolean
     */
    public boolean isWhitelistedSpawnReason() {
        Debug.debugLvl1("Performing isWhitelistedSpawnReason on " + bukkitLivingEntity.getName());
        if (NFConfig.getWhitelistedSpawnReasonList().contains(bukkitLivingEntity.getEntitySpawnReason())) {
            Debug.debugLvl2("Ignoring onMobDamage because " + bukkitLivingEntity.getName() + " spawned from "
                    + bukkitLivingEntity.getEntitySpawnReason() + " which is whitelisted. Marking to skip future checks and returning true");
            getLivingEntityPDC().set(bypassChecks, PersistentDataType.BYTE, (byte) 1);
            return true;
        }
        Debug.debugLvl2("Cleared all 'isWhitelistedSpawnReason' checks. Returning false");
        return false;
    }

    /**
     * Checks mob type, nerfs and returns true if the mob is on the configured blacklist
     * @return boolean
     */
    public boolean isBlacklistedMob() {
        Debug.debugLvl1("Performing isBlacklistedMob on " + bukkitLivingEntity.getName());
        if (NFConfig.getBlacklistedMobList().contains(bukkitLivingEntity.getType())) {
            bukkitLivingEntity.getPersistentDataContainer().set(nerfOnDeath, PersistentDataType.BYTE, DamageListener.t);
            Debug.debugLvl2("Nerfing " + bukkitLivingEntity.getName() + " Because they are on the blacklisted mob types as "
                    + bukkitLivingEntity.getType() + ". Nerfing and returning true");
            return true;
        }
        Debug.debugLvl2("Cleared all 'isBlacklistedMob' checks. Returning false");
        return false;
    }

    /**
     * Checks mob spawn reason, nerfs and returns true if the SpawnReason is on the configured blacklist
     * @return boolean
     */
    public boolean isBlacklistedSpawnReason() {
        Debug.debugLvl1("Performing isBlacklistedSpawnReason on " + bukkitLivingEntity.getName());
        if (NFConfig.getBlacklistedSpawnReasonList().contains(bukkitLivingEntity.getEntitySpawnReason())) {
            bukkitLivingEntity.getPersistentDataContainer().set(nerfOnDeath, PersistentDataType.BYTE, (byte) 1);
            Debug.debugLvl2("Nerfing " + bukkitLivingEntity.getName() + " because they spawned with the spawn reason "
                    + bukkitLivingEntity.getEntitySpawnReason() + " which is blacklisted. Setting mob as nerfed. Returning true");
            return true;
        }
        Debug.debugLvl2("Cleared all 'isBlacklistedSpawnReason' checks. Returning false");
        return false;
    }

    /**
     * Gets the persistent data container of the living entity that was damaged and is being checked
     * @return PersistentDataContainer
     */
    public PersistentDataContainer getLivingEntityPDC() {
        return bukkitLivingEntity.getPersistentDataContainer();
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
}
