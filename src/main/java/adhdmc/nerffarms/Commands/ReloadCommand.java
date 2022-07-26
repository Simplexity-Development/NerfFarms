package adhdmc.nerffarms.Commands;

import adhdmc.nerffarms.ConfigParser;
import adhdmc.nerffarms.NerfFarms;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static adhdmc.nerffarms.ConfigParser.errorCount;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(){
        super("reload", "Reloads NerfFarms Plugin", "/nerffarms reload");
    }

    @Override
    public void doThing(CommandSender sender, String[] args) {
        if(sender instanceof Player && !sender.hasPermission(CommandHandler.commandsPermission)){
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize(CommandHandler.noPermission));
            return;
        }
        if (!(sender instanceof Player)|| sender.hasPermission(CommandHandler.commandsPermission)) {

            NerfFarms.plugin.reloadConfig();
            NerfFarms.plugin.saveConfig();
            ConfigParser.validateConfig();
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
