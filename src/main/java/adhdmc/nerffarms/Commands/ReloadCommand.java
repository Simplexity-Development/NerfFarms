package adhdmc.nerffarms.Commands;

import adhdmc.nerffarms.Commands.SubCommand;
import adhdmc.nerffarms.ConfigParser;
import adhdmc.nerffarms.NerfFarms;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(){
        super("reload", "Reloads NerfFarms Plugin", "/nerfmobs reload");
    }


    @Override
    public void doThing(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)|| sender.hasPermission("nerfmobs.reload")) {
        NerfFarms.plugin.reloadConfig();
        List<Material> standList = (List<Material>) NerfFarms.plugin.getConfig().getList("Blacklisted blocks mob can stand on");
        ConfigParser.checkStandMaterials(standList);
        List<Material> inList = (List<Material>) NerfFarms.plugin.getConfig().getList("Blacklisted blocks mob can be in");
        ConfigParser.checkInsideMaterials(inList);

        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
