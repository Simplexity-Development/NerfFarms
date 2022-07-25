package adhdmc.nerffarms.Commands;

import adhdmc.nerffarms.NerfFarms;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabExecutor {

    public static final String helpCommand = "/nerffarms help";
    public static final String reloadCommand = "/nerffarms reload";
    public static final String commandsPermission = "nerffarms.commands";
    public static final String noPermission = "<red>You do not have permission to run this command!";

    private static final HashMap<String, SubCommand> subcommandList = new HashMap<String, SubCommand>();

    public static void registerCommands() {
        CommandHandler.subcommandList.put("help", new HelpCommand());
        CommandHandler.subcommandList.put("reload", new ReloadCommand());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> subCommands1 = new ArrayList<String>(Arrays.asList("help", "reload"));
        if (args.length == 1 && sender.hasPermission(commandsPermission)) {
            return subCommands1;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && !sender.hasPermission(commandsPermission)) {
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize(noPermission));
            return false;}
        if (args.length == 0){
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<click:open_url:'https://github.com/illogicalsong/NerfFarms'><green><hover:show_text:'<aqua>Click to visit the GitHub repository'>NerfFarms | Version: <version>\nAuthor: _Rhythmic</hover></click>", Placeholder.unparsed("version", String.valueOf(NerfFarms.plugin.version))));
            return true;
        }
        String command = args[0].toLowerCase();
        if (subcommandList.containsKey(command)) {
            subcommandList.get(command).doThing(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.sendMessage(NerfFarms.plugin.miniMessage.deserialize("<red><hover:show_text:'<gray>/nerffarms help'><click:suggest_command:'/nerffarms help'>Sorry! You input the command incorrectly. Please use /nerffarms help to see all commands.</click></hover>"));
        }
        return true;
    }
}

