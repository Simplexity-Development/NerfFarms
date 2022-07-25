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
    FileConfiguration config = NerfFarms.plugin.getConfig();

    @EventHandler
    public void onMobDeath(EntityDeathEvent deathEvent){
        if (!(deathEvent.getEntity() instanceof Mob mob)) return;
        if (config.getBoolean("only-nerf-hostiles") && !(deathEvent.getEntity() instanceof Monster)) return;
        if (!ConfigParser.spawnReasonList.contains(deathEvent.getEntity().getEntitySpawnReason())) return;
        if (!ConfigParser.bypassList.isEmpty() && ConfigParser.bypassList.contains(deathEvent.getEntity().getType())) return;
        Location mobLocation = deathEvent.getEntity().getLocation();
        Location mobStandingOnLocation = deathEvent.getEntity().getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        Material entityStandingIn = mobLocation.getBlock().getType();
        Entity targetedEntity = deathEvent.getEntity().getTargetEntity(ConfigParser.maxDistance, false);
        Entity killer = deathEvent.getEntity().getKiller();

        if(mob.getKiller() == null){
            clearDrops(deathEvent);
            return;
        }
        if(targetedEntity == null || targetedEntity != killer){
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
