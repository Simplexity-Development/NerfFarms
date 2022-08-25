package net.neednot.farmnerf;

import net.neednot.farmnerf.listeners.Listeners;
import net.neednot.farmnerf.listeners.commands.Commands;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class FarmNerf extends JavaPlugin {

    @Override
    public void onEnable() {
        configDefaults();
        Config.validateConfig(this);
        this.getCommand("reload").setExecutor(new Commands(this));
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void configDefaults() {
        FileConfiguration config = getConfig();
        config.addDefault("drop-rate", 0);
        config.addDefault("xp-rate", 0);
        config.addDefault("max-mob-distance", 15);
        config.addDefault("percent-from-environment", 75);
        config.addDefault("only-nerf-hostiles", true);
        config.addDefault("require-targeting", false);
        config.addDefault("debug", false);
        config.addDefault("bypass", List.of(""));
        config.addDefault("modification-type", "EXP");
        config.addDefault("spawn-types", List.of("SPAWNER", "NATURAL", "DEFAULT"));
        config.addDefault("blacklisted-below", Arrays.asList("MAGMA_BLOCK", "HONEY_BLOCK", "LAVA"));
        config.addDefault("blacklisted-in", Arrays.asList("WATER", "LAVA", "BUBBLE_COLUMN"));
        config.addDefault("whitelisted-damage-types", Arrays.asList("PROJECTILE", "THORNS", "MAGIC", "ENTITY_ATTACK", "ENTITY_SWEEP_ATTACK"));
        config.addDefault("environmental-damage-types", Arrays.asList("FALL", "FALLING_BLOCK", "LAVA", "DROWNING"));
        config.options().copyDefaults(true);
        saveConfig();
    }
}
