package adhdmc.nerffarms.listener;


import adhdmc.nerffarms.util.*;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffectType;

public class MobDeathListener implements Listener {
    private final NamespacedKey nerfMob = NFKey.NERF_MOB.getKey();
    private final NamespacedKey pickedUp = NFKey.PICKED_UP_ITEM.getKey();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDeath(EntityDeathEvent deathEvent) {
        LivingEntity deadMob = deathEvent.getEntity();
        PersistentDataContainer mobPDC = deadMob.getPersistentDataContainer();
        if ((deadMob.customName() != null) && NFToggles.WHITELIST_NAMED.isEnabled()) {
            Util.debugLvl2("Mob is named, and named mobs have been whitelisted. Returning without nerfing, regardless of nerf status");
            return;
        }
        if (deadMob.isInsideVehicle() && NFToggles.WHITELIST_VEHICLES.isEnabled()) {
            Util.debugLvl2("Mob is in a vehicle and mobs in vehicles are whitelisted. Returning without nerfing, regardlist of nerf status");
            return;
        }
        if (deadMob.hasPotionEffect(PotionEffectType.WEAKNESS) && NFToggles.WHITELIST_WEAKNESS.isEnabled()) {
            Util.debugLvl2("Mob has weakness and mobs with weakness are whitelisted. Returning without nerfing regardless of nerf status");
            return;
        }
        if (deadMob.isLeashed() && NFToggles.WHITELIST_LEASHED.isEnabled()) {
            Util.debugLvl2("Mob is leashed and leashed mobs are whitelisted. Returning without nerfing regardless of nerf status");
            return;
        }
        if (deadMob.getKiller() != null && deadMob.getKiller().hasPermission(NFPerm.NF_BYPASS.getPerm())) {
            Util.debugLvl2("Player has bypass permission, and so mobs will not be nerfed. Returning without nerfing regardless of nerf status");
            return;
        }
        if (mobPDC.has(nerfMob)) {
            Util.debugLvl3("Running clearDrops");
            clearDrops(deathEvent);
        }

    }

    private void clearDrops(EntityDeathEvent deathEvent) {
        ModType configMod = ModType.getModType();
        if (configMod == ModType.EXP || configMod == ModType.BOTH) {
            Util.debugLvl3("configMod Setting clears EXP.");
            deathEvent.setDroppedExp(0);
        }
        if (configMod == ModType.DROPS || configMod == ModType.BOTH) {
            Util.debugLvl3("configMod Setting clears Drops.");
            deathEvent.getDrops().clear();
        }
    }
}
