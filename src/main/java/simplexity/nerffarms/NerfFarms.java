package simplexity.nerffarms;

import org.bukkit.plugin.java.JavaPlugin;
import simplexity.nerffarms.listeners.MobDamageByEntityListener;
import simplexity.nerffarms.listeners.MobDamageListener;

public final class NerfFarms extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new MobDamageByEntityListener(), this);
        this.getServer().getPluginManager().registerEvents(new MobDamageListener(), this);
        this.saveDefaultConfig();
    }
}
