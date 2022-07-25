package adhdmc.nerffarms;

import adhdmc.nerffarms.Commands.CommandHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class NerfFarms extends JavaPlugin {
    public static NerfFarms plugin;
    public final MiniMessage miniMessage = MiniMessage.miniMessage();
    public final double version = 0.0;

    @Override
    public void onEnable() {
        plugin = this;
        configDefaults();
        ConfigParser.checkInsideMaterials(getConfig().getStringList("blacklisted-in"));
        ConfigParser.checkStandMaterials(getConfig().getStringList("blacklisted-below"));
        ConfigParser.checkEntityList(getConfig().getStringList("bypass"));
        ConfigParser.checkSpawnReason(getConfig().getStringList("spawn-types"));
        ConfigParser.checkModificationType(getConfig().getString("modification-type"));
        ConfigParser.checkDistance(getConfig().getInt("max-mob-distance"));
        CommandHandler.registerCommands();
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        this.getCommand("nerffarms").setExecutor(new CommandHandler());
    }

    private void configDefaults() {
        FileConfiguration config = getConfig();
        config.addDefault("only-nerf-hostiles", true);
        config.addDefault("bypass", List.of(""));
        config.addDefault("modification-type", "EXP");
        config.addDefault("spawn-types", List.of("SPAWNER", "NATURAL"));
        config.addDefault("blacklisted-below", Arrays.asList("MAGMA_BLOCK", "HONEY_BLOCK", "LAVA"));
        config.addDefault("blacklisted-in", Arrays.asList("WATER", "LAVA", "BUBBLE_COLUMN"));
        config.addDefault("max-mob-distance", 15);
    }
}
