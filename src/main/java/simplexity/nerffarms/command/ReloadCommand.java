package simplexity.nerffarms.command;

import simplexity.nerffarms.config.NerfFarmsConfig;
import simplexity.nerffarms.NerfFarms;
import simplexity.nerffarms.util.ModType;
import simplexity.nerffarms.config.Message;
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
            sender.sendRichMessage(Message.NO_PERMISSION.getMessage());
            return;
        }
        if (!(sender instanceof Player) || sender.hasPermission(NFPerm.NF_RELOAD.getPerm())) {
            NerfFarms.getInstance().reloadConfig();
            NerfFarms.getInstance().saveConfig();
            NerfFarmsConfig.validateConfig();
            sender.sendRichMessage(Message.PLUGIN_RELOADED.getMessage());
            if (NerfFarmsConfig.getErrorCount() > 0) {
                sender.sendMessage(NerfFarms.getMiniMessage().deserialize(Message.PLUGIN_RELOADED_WITH_ERRORS.getMessage(), Placeholder.unparsed("errors", String.valueOf(NerfFarmsConfig.getErrorCount()))));
            }
            if (ModType.getModType() == ModType.NEITHER) {
                sender.sendRichMessage(Message.PLUGIN_USELESS.getMessage());
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
