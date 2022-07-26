package adhdmc.nerffarms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.logging.Logger;

public class MobDeathListener implements Listener {

    @EventHandler
    public void onMobDeath(EntityDeathEvent deathEvent){
        boolean d = ConfigParser.debug;
        Logger l  = NerfFarms.plugin.getLogger();
        Entity deadMob = deathEvent.getEntity();
        if (!(deadMob instanceof Mob mob)) {
            if (d) {
                l.info("(!(deadMob instanceof Mob mob)) return;");
            }
            return;
        }
        if (ConfigParser.onlyNerfHostiles && !(deadMob instanceof Monster)) {
            if (d) {
                l.info("ConfigParser.onlyNerfHostiles && !(deadMob instanceof Monster)");
            }
            return;
        }
        if (!ConfigParser.spawnReasonList.contains(deadMob.getEntitySpawnReason())) {
            if (d) {
                l.info("!ConfigParser.spawnReasonList.contains(deadMob.getEntitySpawnReason())");
            }
            return;
        }
        if (!ConfigParser.bypassList.isEmpty() && ConfigParser.bypassList.contains(deadMob.getType())) {
            if (d) {
                l.info("!ConfigParser.bypassList.isEmpty() && ConfigParser.bypassList.contains(deadMob.getType())");
            }
            return;
        }
        Location mobLocation = deadMob.getLocation();
        Location mobStandingOnLocation = deadMob.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        Material entityStandingIn = mobLocation.getBlock().getType();
        Entity targetedEntity = deathEvent.getEntity().getTargetEntity(ConfigParser.maxDistance, false);
        Entity killer = deathEvent.getEntity().getKiller();

        if(mob.getKiller() == null){
            if (d) {
                l.info("Killer == null, nerfing drops");
            }
            clearDrops(deathEvent);
            return;
        }
        if(deadMob instanceof Monster && ConfigParser.requireTargetting && (targetedEntity == null || targetedEntity != killer)){
            if (d) {
                l.info("deadMob instanceof Monster && ConfigParser.requireTargetting && (targetedEntity == null || targetedEntity != killer), ");
                l.info("Killer: " + killer.toString());
            }
            clearDrops(deathEvent);
            return;
        }
        if(ConfigParser.standOnBlacklist.contains(entityStandingOn)){
            if (d) {
                l.info("ConfigParser.standOnBlacklist.contains(entityStandingOn)");
            }
            clearDrops(deathEvent);
            return;
        }
        if(ConfigParser.insideBlacklist.contains(entityStandingIn)){
            if (d) {
                l.info("ConfigParser.standOnBlacklist.contains(entityStandingOn)");
            }
            clearDrops(deathEvent);
        }

    }
    private void clearDrops(EntityDeathEvent e){
        boolean d = ConfigParser.debug;
        Logger l  = NerfFarms.plugin.getLogger();
        String configMod = ConfigParser.modType;
        if(configMod == null || configMod.equalsIgnoreCase("")) {
            if (d) {
                l.info("configMod == null || configMod.equalsIgnoreCase(\"\")");
            }
            return;
        }
        if(configMod.equalsIgnoreCase("both")){
            if (d) {
                l.info("configMod.equalsIgnoreCase(\"both\"");
            }
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        if(configMod.equalsIgnoreCase("exp")){
            if (d) {
                l.info("configMod.equalsIgnoreCase(\"exp\")");
            }
            e.setDroppedExp(0);
        }
        if(configMod.equalsIgnoreCase("drops")){
            if (d) {
                l.info("configMod.equalsIgnoreCase(\"drops\")");
            }
            e.getDrops().clear();
        }
    }
}
