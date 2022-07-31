package adhdmc.nerffarms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Logger;

public class MobDamageListener implements Listener {
    NamespacedKey nerfMob = new NamespacedKey(NerfFarms.plugin, "nerfMob");
    byte f = 0;
    byte t = 1;

    @EventHandler
    public void onMobDamage(EntityDamageByEntityEvent damageEvent) {
        boolean d = ConfigParser.isDebug();
        Logger l = NerfFarms.plugin.getLogger();
        Entity damagedEntity = damageEvent.getEntity();
        if (!(damagedEntity instanceof Mob)) {
            if (d) {
                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " is not a mob.");
            }
            return;
        }
        double entityHealth = ((Mob) damagedEntity).getHealth();
        double hitDamage = damageEvent.getFinalDamage();
        if (!(entityHealth - hitDamage <= 0)) {
            l.info(String.valueOf((entityHealth - hitDamage)));
            return;
        }
        if (ConfigParser.isNerfHostilesOnly() && !(damagedEntity instanceof Monster)) {
            if (d) {
                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " is not a Monster and Nerf Hostiles Only is True.");
            }
            return;
        }
        if (!ConfigParser.getSpawnReasonList().contains(damagedEntity.getEntitySpawnReason())) {
            if (d) {
                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " spawned from " + damagedEntity.getEntitySpawnReason() + " which isn't nerfed.");
            }
            return;
        }
        if (ConfigParser.getBypassList().contains(damagedEntity.getType())) {
            if (d) {
                l.info("Ignoring onMobDamage because " + damagedEntity.getName() + " is on the bypass list as " + damagedEntity.getType());
            }
            return;
        }
        Location mobLocation = damagedEntity.getLocation();
        Location mobStandingOnLocation = damagedEntity.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        Material entityStandingIn = mobLocation.getBlock().getType();
        EntityDamageEvent.DamageCause damageType = damageEvent.getCause();
        Entity entityDamager = damageEvent.getDamager();
        PersistentDataContainer mobPDC = damagedEntity.getPersistentDataContainer();
        if (!ConfigParser.getDamageCauseWhitelist().contains(damageType)) {
            if (d) {
                l.info("Nerfing " + damagedEntity.getName() + " due to " + damageType);
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return;
        }
        if (!(entityDamager instanceof Player)) {
            if (d) {
                l.info("Nerfing " + damagedEntity.getName() + " because killer is not a player");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return;
        }
        if (ConfigParser.getStandOnBlackList().contains(entityStandingOn)) {
            if (d) {
                l.info("Nerfing " + damagedEntity.getName() + " since they are standing on " + entityStandingOn);
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return;
        }
        if (ConfigParser.getInsideBlackList().contains(entityStandingIn)) {
            if (d) {
                l.info("Nerfing " + damagedEntity.getName() + " since they are standing in " + entityStandingIn);
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
        }
    }
}
