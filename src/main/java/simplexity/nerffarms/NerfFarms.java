package simplexity.nerffarms;

import simplexity.nerffarms.command.CommandHandler;
import simplexity.nerffarms.config.NerfFarmsConfig;
import simplexity.nerffarms.listener.ItemPickupListener;
import simplexity.nerffarms.listener.damagehandling.DamageListener;
import simplexity.nerffarms.listener.MobDeathListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NerfFarms extends JavaPlugin {
    //TODO: Add config for a message that is sent to the player when the mob they kill is nerfed
    //TODO: Add a toggle command for that message
    //TODO: Add a config option for allowing non-player kills (checkDamager method in MobDamageListener)
    private static NerfFarms instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        instance = this;
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            Class.forName("com.destroystokyo.paper.entity.Pathfinder");
        } catch (ClassNotFoundException exception) {
            this.getLogger().severe("NerfFarms relies on methods in classes not present on your server. Disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        CommandHandler.registerCommands();
        //TODO BSTATS (previously was a maven project, need to figure out gradle import)
        //Metrics metrics = new Metrics(this, 16509);
        this.saveDefaultConfig();
        NerfFarmsConfig.validateConfig();
        this.getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        this.getServer().getPluginManager().registerEvents(new DamageListener(), this);
        this.getServer().getPluginManager().registerEvents(new ItemPickupListener(), this);
        registerCommand(this.getCommand("nerffarms"), new CommandHandler());
    }

    private static void registerCommand(PluginCommand command, CommandExecutor executor) {
        if (command != null) {
            command.setExecutor(executor);
        }
    }

    public static NerfFarms getInstance() {
        return instance;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
