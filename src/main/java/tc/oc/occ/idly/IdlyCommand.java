package tc.oc.occ.idly;

import static net.kyori.adventure.text.Component.text;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.util.Audience;

@CommandAlias("idle|idly")
@CommandPermission(IdlyPermissions.COMMAND)
public class IdlyCommand extends BaseCommand {

  @Dependency private Idly plugin;
  @Dependency private IdlyConfig config;

  @Subcommand("reload")
  @CommandPermission(IdlyPermissions.RELOAD)
  public void reload(CommandSender sender) {
    plugin.reloadConfig();
    config.reload(plugin.getConfig());
    Audience.get(sender).sendMessage(text("Idly config has been reloaded", NamedTextColor.GREEN));
  }
}
