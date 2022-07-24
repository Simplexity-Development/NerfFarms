package adhdmc.nerffarms.Commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ToggleCommand extends SubCommand {

    public ToggleCommand() {
        super("toggle", "Toggles villager info on and off","/vill toggle");
    }

    @Override
    public void doThing(CommandSender sender, String[] args) {
        }



    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
