package adhdmc.nerffarms.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class CommandHandler implements CommandExecutor, TabExecutor {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> subCommands1 = new ArrayList<String>(Arrays.asList("help", "disable", "reload", "enable"));
        if (args.length == 1) {
            return subCommands1;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //Checking for arguments
        if (args.length == 0){
                       return true;
        }
        //if has an argument, check to see if it's contained in the list of arguments
        String command = args[0].toLowerCase();

        return true;
    }
}

