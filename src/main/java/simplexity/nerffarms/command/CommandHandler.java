package simplexity.nerffarms.command;

import simplexity.nerffarms.NerfFarms;
import simplexity.nerffarms.config.Message;
import simplexity.nerffarms.util.NFPerm;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabExecutor {

    private static final HashMap<String, SubCommand> subcommandList = new HashMap<>();
    MiniMessage miniMessage = NerfFarms.getMiniMessage();

    public static void registerCommands() {
        CommandHandler.subcommandList.put("help", new HelpCommand());
        CommandHandler.subcommandList.put("reload", new ReloadCommand());
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        ArrayList<String> subCommands1 = new ArrayList<>(Arrays.asList("help", "reload"));
        if (args.length == 1 && sender.hasPermission(NFPerm.NF_COMMANDS.getPerm())) {
            return subCommands1;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission(NFPerm.NF_COMMANDS.getPerm())) {
            sender.sendRichMessage(Message.NO_PERMISSION.getMessage());
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(Message.PLUGIN_INFO.getMessage(),
                    Placeholder.unparsed("author",  NerfFarms.getInstance().getDescription().getAuthors().toString()),
                    Placeholder.unparsed("version", NerfFarms.getInstance().getDescription().getVersion()),
                    Placeholder.unparsed("desc", NerfFarms.getInstance().getDescription().getDescription())));
            return true;
        }
        String command = args[0].toLowerCase();
        if (subcommandList.containsKey(command)) {
            subcommandList.get(command).execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.sendRichMessage(Message.INCORRECT_INPUT.getMessage());
        }
        return true;
    }
}

