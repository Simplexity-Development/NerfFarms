package adhdmc.nerffarms;

import adhdmc.nerffarms.Commands.CommandHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class NerfFarms extends JavaPlugin {
    public static NerfFarms plugin;
    public final MiniMessage miniMessage = MiniMessage.miniMessage();
    public final String version = "0.0.6";

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
        config.addDefault("only-nerf-hostiles", true);
        config.addDefault("bypass", List.of(""));
        config.addDefault("modification-type", "EXP");
        config.addDefault("spawn-types", List.of("SPAWNER", "NATURAL", "DEFAULT"));
        config.addDefault("blacklisted-below", Arrays.asList("MAGMA_BLOCK", "HONEY_BLOCK", "LAVA"));
        config.addDefault("blacklisted-in", Arrays.asList("WATER", "LAVA", "BUBBLE_COLUMN"));
        config.addDefault("whitelisted-damage-types", Arrays.asList("PROJECTILE", "THORNS", "MAGIC", "ENTITY_ATTACK", "ENTITY_SWEEP_ATTACK"));
        config.addDefault("require-targetting", false);
        config.addDefault("debug", false);
        config.addDefault("max-mob-distance", 15);
        config.addDefault("environmental-damage-types", Arrays.asList("FALL", "FALLING_BLOCK", "LAVA", "DROWNING"));
        config.addDefault("percent-from-environment", 75);
    }
}
