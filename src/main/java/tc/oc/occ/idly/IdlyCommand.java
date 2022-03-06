package tc.oc.occ.idly;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
    sender.sendMessage(ChatColor.GREEN + "Idly config has been reloaded");
  }
}
