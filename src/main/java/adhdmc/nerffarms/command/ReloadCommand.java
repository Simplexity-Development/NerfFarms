package adhdmc.nerffarms.command;

import adhdmc.nerffarms.config.ConfigParser;
import adhdmc.nerffarms.NerfFarms;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super("reload", "Reloads NerfFarms Plugin", "/nerffarms reload");
    }

    @Override
    public void doThing(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission(CommandHandler.commandsPermission)) {
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize(CommandHandler.noPermission));
            return;
        }
        if (!(sender instanceof Player) || sender.hasPermission(CommandHandler.commandsPermission)) {
            NerfFarms.plugin.reloadConfig();
            NerfFarms.plugin.saveConfig();
            ConfigParser.validateConfig();
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<gold>NerfFarms config has been reloaded!"));
            if (ConfigParser.getErrorCount() > 0) {
                sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<red>Your config had <errors> error(s). Check your console for details.", Placeholder.unparsed("errors", String.valueOf(ConfigParser.getErrorCount()))));
            }
            if (ConfigParser.getModType() == ConfigParser.ModType.NEITHER) {
                sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<red>Your config does not modify mob drops or exp, this can be due to an error with the modification-type setting or it was set to neither!"));
                sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<gold>This plugin will do nothing in this state."));
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
