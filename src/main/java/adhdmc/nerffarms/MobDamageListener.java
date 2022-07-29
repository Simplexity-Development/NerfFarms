package adhdmc.nerffarms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
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
    public void onMobDamage(EntityDamageByEntityEvent damageEvent){
        boolean d = ConfigParser.debug;
        Logger l  = NerfFarms.plugin.getLogger();
        Entity damagedEntity = damageEvent.getEntity();
        if (!(damagedEntity instanceof Mob mob)) {
            if (d) {
                l.info("(!(damagedEntity instanceof Mob mob)) return;");
            }
            return;
        }
        double entityHealth = ((Mob) damagedEntity).getHealth();
        double hitDamage = damageEvent.getFinalDamage();
        if (!(entityHealth - hitDamage <= 0)) {
            l.info(String.valueOf((entityHealth - hitDamage)));
            return;
        }
        if (ConfigParser.onlyNerfHostiles && !(damagedEntity instanceof Monster)) {
            if (d) {
                l.info("ConfigParser.onlyNerfHostiles && !(damagedEntity instanceof Monster)");
            }
            return;
        }
        if (!ConfigParser.spawnReasonList.contains(damagedEntity.getEntitySpawnReason())) {
            if (d) {
                l.info("!ConfigParser.spawnReasonList.contains(damagedEntity.getEntitySpawnReason())");
            }
            return;
        }
        if (!ConfigParser.bypassList.isEmpty() && ConfigParser.bypassList.contains(damagedEntity.getType())) {
            if (d) {
                l.info("!ConfigParser.bypassList.isEmpty() && ConfigParser.bypassList.contains(damagedEntity.getType())");
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
        if (!ConfigParser.damageCauseWhitelist.contains(damageType)) {
            if (d) {
                l.info("!ConfigParser.damageCauseWhitelist.contains(damageEvent.getCause()");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return;
        }
        if(!(entityDamager instanceof Player player)){
            if (d) {
                l.info("Killer was not a player, nerfing");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return;
        }
        if(ConfigParser.standOnBlacklist.contains(entityStandingOn)){
            if (d) {
                l.info("ConfigParser.standOnBlacklist.contains(entityStandingOn)");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
            return;
        }
        if(ConfigParser.insideBlacklist.contains(entityStandingIn)){
            if (d) {
                l.info("ConfigParser.standOnBlacklist.contains(entityStandingOn)");
            }
            mobPDC.set(nerfMob, PersistentDataType.BYTE, t);
        }
    }
}
