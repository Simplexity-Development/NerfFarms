package simplexity.nerffarms.command;

import simplexity.nerffarms.util.NFMessage;
import simplexity.nerffarms.util.NFPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends SubCommand {


    public HelpCommand() {
        super("help", "NerfMobs help", "/nerffarms help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player && sender.hasPermission(NFPerm.NF_COMMANDS.getPerm())) {
            player.sendMessage(NFMessage.HELP.getMessage());
            return;
        }
        if (sender instanceof Player player) {
            player.sendRichMessage(NFMessage.NO_PERMISSION.getMessage());
            return;
        }
        sender.sendRichMessage(NFMessage.HELP.getMessage());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
