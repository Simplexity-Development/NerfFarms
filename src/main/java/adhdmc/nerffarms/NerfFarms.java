package adhdmc.nerffarms;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class NerfFarms extends JavaPlugin {
    public static NerfFarms plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        configDefaults();
        this.saveDefaultConfig();
    }

    private void configDefaults() {
        FileConfiguration config = getConfig();
        this.saveDefaultConfig();
        config.addDefault("Nerf Hostiles Only", true);
        config.addDefault("Bypass", List.of(""));
        config.addDefault("Modification Type", "EXPERIENCE");
        config.addDefault("Spawns to modify", List.of("SPAWNER"));
        config.addDefault("Nerf non-player kills", false);
        config.addDefault("Blacklisted blocks mob can stand on", Arrays.asList("MAGMA_BLOCK", "HONEY_BLOCK", "LAVA"));
        config.addDefault("Blacklisted blocks mob can be in", Arrays.asList("WATER", "LAVA", "BUBBLE_COLUMN"));
        config.addDefault("Max distance mob can be from player", 20);
    }
}
