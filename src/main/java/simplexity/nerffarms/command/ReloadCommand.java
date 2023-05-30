package simplexity.nerffarms.command;

import simplexity.nerffarms.util.NFConfig;
import simplexity.nerffarms.NerfFarms;
import simplexity.nerffarms.util.ModType;
import simplexity.nerffarms.util.NFMessage;
import simplexity.nerffarms.util.NFPerm;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super("reload", "Reloads NerfFarms Plugin", "/nerffarms reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission(NFPerm.NF_RELOAD.getPerm())) {
            sender.sendRichMessage(NFMessage.NO_PERMISSION.getMessage());
            return;
        }
        if (!(sender instanceof Player) || sender.hasPermission(NFPerm.NF_RELOAD.getPerm())) {
            NerfFarms.getInstance().reloadConfig();
            NerfFarms.getInstance().saveConfig();
            NFConfig.validateConfig();
            sender.sendRichMessage(NFMessage.PLUGIN_RELOADED.getMessage());
            if (NFConfig.getErrorCount() > 0) {
                sender.sendMessage(NerfFarms.getMiniMessage().deserialize(NFMessage.PLUGIN_RELOADED_WITH_ERRORS.getMessage(), Placeholder.unparsed("errors", String.valueOf(NFConfig.getErrorCount()))));
            }
            if (ModType.getModType() == ModType.NEITHER) {
                sender.sendRichMessage(NFMessage.PLUGIN_USELESS.getMessage());
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
