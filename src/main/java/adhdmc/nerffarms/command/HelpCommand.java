package adhdmc.nerffarms.command;

import adhdmc.nerffarms.NerfFarms;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends SubCommand {


    public HelpCommand() {
        super("help", "NerfMobs help", CommandHandler.helpCommand);
    }

    @Override
    public void doThing(CommandSender sender, String[] args) {
        if (sender instanceof Player player && sender.hasPermission("nerffarms.commands")) {
            player.sendMessage(NerfFarms.plugin.miniMessage.deserialize(
                    "<grey>• <aqua><click:suggest_command:'<help>'>" +
                            "<hover:show_text:'<yellow><help>'>/nerffarms help" +
                            "</hover></click></aqua> - <dark_aqua>shows this list",
                    Placeholder.unparsed("help", CommandHandler.helpCommand)));
            player.sendMessage(NerfFarms.plugin.miniMessage.deserialize(
                    "<grey>• <aqua><click:suggest_command:'<reload>'>" +
                            "<hover:show_text:'<yellow><reload>'>/nerffarms reload" +
                            "</hover></click></aqua> - <dark_aqua>reloads the NerfFarms config",
                    Placeholder.unparsed("reload", CommandHandler.reloadCommand)));
            return;
        }
        if (sender instanceof Player player) {
            player.sendMessage(NerfFarms.plugin.miniMessage.deserialize(CommandHandler.noPermission));
            return;
        }
        sender.sendMessage(CommandHandler.helpCommand);
        sender.sendMessage(CommandHandler.reloadCommand);
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
