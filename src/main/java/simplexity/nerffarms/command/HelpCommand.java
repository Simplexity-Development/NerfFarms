package simplexity.nerffarms.command;

import simplexity.nerffarms.config.Message;
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
            player.sendMessage(Message.HELP.getMessage());
            return;
        }
        if (sender instanceof Player player) {
            player.sendRichMessage(Message.NO_PERMISSION.getMessage());
            return;
        }
        sender.sendRichMessage(Message.HELP.getMessage());
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
