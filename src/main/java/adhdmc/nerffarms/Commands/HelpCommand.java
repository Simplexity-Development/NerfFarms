package adhdmc.nerffarms.Commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends SubCommand {

    public HelpCommand(){
        super ("help","Villager Info help", "/vill help");
    }

    @Override
    public void doThing(CommandSender sender, String[] args) {
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
