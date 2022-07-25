package adhdmc.nerffarms.Commands;

import adhdmc.nerffarms.ConfigParser;
import adhdmc.nerffarms.NerfFarms;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(){
        super("reload", "Reloads NerfFarms Plugin", "/nerfmobs reload");
    }
    public static int errorCount = 0;


    @Override
    public void doThing(CommandSender sender, String[] args) {
        errorCount = 0;
        FileConfiguration config = NerfFarms.plugin.getConfig();
        if(sender instanceof Player && !sender.hasPermission(CommandHandler.commandsPermission)){
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize(CommandHandler.noPermission));
            return;
        }
        if (!(sender instanceof Player)|| sender.hasPermission(CommandHandler.commandsPermission)) {
        NerfFarms.plugin.reloadConfig();
        NerfFarms.plugin.saveConfig();
        List<String> standList = config.getStringList("blacklisted-below");
        ConfigParser.checkStandMaterials(standList);
        List<String> inList = config.getStringList("blacklisted-in");
        ConfigParser.checkInsideMaterials(inList);
        List<String> bypassList = config.getStringList("bypass");
        ConfigParser.checkEntityList(bypassList);
        List<String> spawnReasonList = config.getStringList("spawn-types");
        ConfigParser.checkSpawnReason(spawnReasonList);
        String modificationType = config.getString("modification-type");
        ConfigParser.checkModificationType(modificationType);
        int maxDistance = config.getInt("max-mob-distance");
        ConfigParser.checkDistance(maxDistance);
        sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<gold>NerfFarms config has been reloaded!"));
        if(errorCount > 0){
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<red>Your config had <errors> error(s). Check your console for details.", Placeholder.unparsed("errors", String.valueOf(errorCount))));
        }
        if(ConfigParser.modType.equalsIgnoreCase("")){
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<dark_red>'modification-type' setting in config is invalid. Plugin will not be able to function properly until this is fixed."));
        }
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
