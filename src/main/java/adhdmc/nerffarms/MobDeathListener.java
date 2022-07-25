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

public class MobDeathListener implements Listener {

    @EventHandler
    public void onMobDeath(EntityDeathEvent deathEvent){
        Entity deadMob = deathEvent.getEntity();
        if (!(deadMob instanceof Mob mob)) return;
        if (ConfigParser.onlyNerfHostiles && !(deadMob instanceof Monster)) return;
        if (!ConfigParser.spawnReasonList.contains(deadMob.getEntitySpawnReason())) return;
        if (!ConfigParser.bypassList.isEmpty() && ConfigParser.bypassList.contains(deadMob.getType())) return;
        Location mobLocation = deadMob.getLocation();
        Location mobStandingOnLocation = deadMob.getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        Material entityStandingIn = mobLocation.getBlock().getType();
        Entity targetedEntity = deathEvent.getEntity().getTargetEntity(ConfigParser.maxDistance, false);
        Entity killer = deathEvent.getEntity().getKiller();

        if(mob.getKiller() == null){
            clearDrops(deathEvent);
            return;
        }
        if(deadMob instanceof Monster && ConfigParser.requireTargetting && (targetedEntity == null || targetedEntity != killer)){
            clearDrops(deathEvent);
            return;
        }
        if(ConfigParser.standOnBlacklist.contains(entityStandingOn)){
            clearDrops(deathEvent);
            return;
        }
        if(ConfigParser.insideBlacklist.contains(entityStandingIn)){
            clearDrops(deathEvent);
        }

    }
    private void clearDrops(EntityDeathEvent e){
        String configMod = ConfigParser.modType;
        if(configMod == null || configMod.equalsIgnoreCase("")) return;
        if(configMod.equalsIgnoreCase("both")){
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        if(configMod.equalsIgnoreCase("exp")){
            e.setDroppedExp(0);
        }
        if(configMod.equalsIgnoreCase("drops")){
            e.getDrops().clear();
        }
    }
}
