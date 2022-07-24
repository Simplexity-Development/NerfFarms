package adhdmc.nerffarms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.Locale;

public class MobDeathListener implements Listener {
    FileConfiguration config = NerfFarms.plugin.getConfig();

    @EventHandler
    public void onMobDeath(EntityDeathEvent deathEvent){
        if (!(deathEvent.getEntity() instanceof Mob mob)) return;
        if (config.getBoolean("Nerf Hostiles Only") && !(deathEvent.getEntity() instanceof Monster)) return;
        if (config.getList("Bypass") != null && config.getList("Bypass").contains(deathEvent.getEntity().getType())) return;
        Location mobLocation = deathEvent.getEntity().getLocation();
        Location mobStandingOnLocation = deathEvent.getEntity().getLocation().subtract(0, 1, 0);
        Material entityStandingOn = mobStandingOnLocation.getBlock().getType();
        Material entityStandingIn = mobLocation.getBlock().getType();
        Entity targetedEntity = deathEvent.getEntity().getTargetEntity(config.getInt("Max distance mob can be from player"), false);
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
            return;
        }

    }
    private void clearDrops(EntityDeathEvent e){
        if(config.getString("Modification Type").equalsIgnoreCase("BOTH")){
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        if(config.getString("Modification Type").equalsIgnoreCase("EXPERIENCE")){
            e.setDroppedExp(0);
        }
        if(config.getString("Modification Type").equalsIgnoreCase("DROPS")){
            e.getDrops().clear();
        }
    }
}
