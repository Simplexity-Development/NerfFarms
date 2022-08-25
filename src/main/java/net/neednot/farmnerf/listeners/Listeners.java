package net.neednot.farmnerf.listeners;


import com.sun.security.auth.login.ConfigFile;
import net.kyori.adventure.text.Component;
import net.neednot.farmnerf.Config;
import net.neednot.farmnerf.FarmNerf;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.persistence.PersistentDataType;

public class Listeners implements Listener {

    private FarmNerf plugin;

    public Listeners(FarmNerf plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //checks if non monsters are allowed and returns if the entity isn't a monster
        if (Config.nerfHostilesOnly() && !(event.getEntity() instanceof Monster)) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity.getKiller() != null) {
            Player killer = entity.getKiller();
            if (killer.hasPermission("farmnerf.bypass")) return;
            debug("we got the kill", killer);
            //whitelists
            if (Config.getBypassList().contains(entity.getType())) return;
            debug("not on the entity whitelist", killer);
            if (Config.getDamageCauseWhitelist().contains(entity.getLastDamageCause().getCause())) return;
            debug("not on the damage cause whitelist", killer);
            if (Config.getModType().equals(Config.ModType.NEITHER)) return;
            debug("xp or drops are modified", killer);
            //require reach
            boolean dirty = false;

            //separated for readability;
            if (Config.getStandOnBlackList().contains(entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()) ||
                    Config.getInsideBlackList().contains(entity.getLocation().getBlock().getType()) ||
                    Config.getSpawnReasonList().contains(entity.getEntitySpawnReason()) ||
                    Config.getMaxDistance() < entity.getEyeLocation().distance(killer.getEyeLocation())
            ) {
                dirty = true;
                debug("one of these", killer);
                debug(String.valueOf(Config.getStandOnBlackList().contains(entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType())) , killer);
                debug(String.valueOf(Config.getInsideBlackList().contains(entity.getLocation().getBlock().getType())), killer);
                debug(String.valueOf(Config.getSpawnReasonList().contains(entity.getEntitySpawnReason())), killer);
                debug(String.valueOf(Config.getMaxDistance() < entity.getEyeLocation().distance(killer.getEyeLocation())), killer);
            }
            debug(entity.getEntitySpawnReason().name(), killer);

            //checks if damage was environmental
            double envDmg = entity.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "env-dmg"), PersistentDataType.DOUBLE, 0d);
            if (Config.getPercentFromEnvironment() / 100f <= envDmg / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                dirty = true;
                debug("environment damage", killer);
            }
            debug(String.valueOf(envDmg), killer);
            debug(String.valueOf(envDmg/entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()), killer);

            //checks if entity could move from MLG-Fortress
            if (Config.requireTargeting()) {
                if (!entity.hasLineOfSight(killer)) {
                    dirty = true;
                    debug("line of sight", killer);
                }
                Location monster = entity.getEyeLocation();
                Location player = killer.getEyeLocation();

                Location[] locations = new Location[]{
                        new Location(monster.getWorld(), 0.2 * monster.getX() + 0.8 * player.getX(),
                                monster.getY(), 0.2 * monster.getZ() + 0.8 * player.getZ()),
                        new Location(monster.getWorld(), 0.5 * monster.getX() + 0.5 * player.getX(),
                                monster.getY(), 0.5 * monster.getZ() + 0.5 * player.getZ()),
                        new Location(monster.getWorld(), 0.8 * monster.getX() + 0.2 * player.getX(),
                                monster.getY(), 0.8 * monster.getZ() + 0.2 * player.getZ()),};

                for (Location middleLocation : locations) {
                    // monster doesn't have room above to hurdle a foot level block, unable to advance toward killer
                    Block bottom = middleLocation.getBlock().getRelative(BlockFace.DOWN);
                    Block top = middleLocation.getBlock().getRelative(BlockFace.UP);
                    debug("running now", killer);
                    if (top.getType() != Material.AIR &&
                            bottom.getType() != Material.AIR
                            //Since this feature seems to cause issues anyways, I'm gonna do a lazy check for fences and fence gates
                            || bottom.getType().name().contains("_FENCE")
                            || bottom.getType() == Material.COBBLESTONE_WALL) {
                        dirty = true;
                        debug("too low", killer);
                        break;
                    }
                }
            }
            //if the kill is farmed
            if (dirty) {
                Bukkit.broadcastMessage("mob has been nerfed");
                if (Config.getModType().equals(Config.ModType.EXP) || Config.getModType().equals(Config.ModType.BOTH)) {
                    event.setDroppedExp((int) (event.getDroppedExp() * Config.getXpRate()));
                }
                if (Config.getModType().equals(Config.ModType.DROPS) || Config.getModType().equals(Config.ModType.BOTH)) {
                    event.getDrops().forEach(itemStack -> {
                        itemStack.setAmount((int) (itemStack.getAmount() * Config.getDropRate()));
                    });
                }
            }
            else {
                plugin.getServer().broadcast(Component.text("mob not nerfed"));
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Config.getEnvironmentalDamageSet().contains(event.getCause())) {
            double data = event.getEntity().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "env-dmg"), PersistentDataType.DOUBLE, 0d);

            double dmg = event.getDamage() + data;
            event.getEntity().getPersistentDataContainer().set(new NamespacedKey(plugin, "env-dmg"), PersistentDataType.DOUBLE, dmg);
        }
    }
    @EventHandler
    public void onEntityRegen(EntityRegainHealthEvent event) {
        double data = event.getEntity().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "env-dmg"), PersistentDataType.DOUBLE, 0d);
        double dmg = Math.max(data-event.getAmount(), 0);
        event.getEntity().getPersistentDataContainer().set(new NamespacedKey(plugin, "env-dmg"), PersistentDataType.DOUBLE, dmg);
    }
    public static void debug(String text, Player player) {
        if (Config.isDebug() && player.hasPermission("farmnerf.debug") || player.isOp()) player.sendMessage(text);
    }
}
