package tc.oc.occ.idly;

import co.aikar.commands.BukkitCommandManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Idly extends JavaPlugin {

  private BukkitAudiences adventure;
  private BukkitCommandManager commands;
  private IdlyConfig config;
  private IdlyManager manager;

  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    this.reloadConfig();
    this.config = new IdlyConfig(this.getConfig());
    this.commands = new BukkitCommandManager(this);
    this.adventure = BukkitAudiences.create(this);
    this.manager = new IdlyManager(this);

    this.commands.registerDependency(IdlyConfig.class, config);
    this.commands.registerCommand(new IdlyCommand());

    this.getServer().getPluginManager().registerEvents(new IdlyListener(manager), this);
  }

  public IdlyConfig getIdlyConfig() {
    return config;
  }

  public Audience getViewer(CommandSender sender) {
    return adventure.sender(sender);
  }

  // TODO: command to view last movement for players
  // TODO: ignore permission bypass
  // TODO: eee

}
