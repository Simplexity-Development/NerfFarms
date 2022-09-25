package adhdmc.nerffarms;

import adhdmc.nerffarms.command.CommandHandler;
import adhdmc.nerffarms.config.ConfigParser;
import adhdmc.nerffarms.listener.MobDamageListener;
import adhdmc.nerffarms.listener.MobDeathListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class NerfFarms extends JavaPlugin {
    public static NerfFarms plugin;
    public final MiniMessage miniMessage = MiniMessage.miniMessage();
    public final String version = "0.0.10";

    @Override
    public void onEnable() {
        plugin = this;
        configDefaults();
        ConfigParser.validateConfig();
        CommandHandler.registerCommands();
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        this.getServer().getPluginManager().registerEvents(new MobDamageListener(), this);
        registerCommand(this.getCommand("nerffarms"), new CommandHandler());
    }

    private static void registerCommand(PluginCommand command, CommandExecutor executor) {
        if (command != null) {
            command.setExecutor(executor);
        }
    }

    private void configDefaults() {
        FileConfiguration config = getConfig();
        config.addDefault("debug", false);
        config.addDefault("only-nerf-hostiles", true);
        config.addDefault("bypass", List.of(""));
        config.addDefault("modification-type", "EXP");
        config.addDefault("blacklisted-spawn-types", List.of("CUSTOM"));
        config.addDefault("blacklisted-below", Arrays.asList("MAGMA_BLOCK", "HONEY_BLOCK", "LAVA"));
        config.addDefault("blacklisted-in", Arrays.asList("HONEY_BLOCK", "LAVA", "BUBBLE_COLUMN"));
        config.addDefault("allow-projectile-damage", true);
        config.addDefault("require-path", false);
        config.addDefault("require-line-of-sight", false);
        config.addDefault("skeletons-can-damage-creepers", true);
        config.addDefault("withers-can-damage-entities", true);
        config.addDefault("max-distance", 15);
        config.addDefault("disallowed-damage-types", Arrays.asList("FALL", "FALLING_BLOCK", "LAVA", "DROWNING"));
        config.addDefault("max-disallowed-damage-percent", 75);
    }
}
