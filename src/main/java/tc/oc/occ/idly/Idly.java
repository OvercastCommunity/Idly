package tc.oc.occ.idly;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.occ.idly.api.BaseIdlyAPI;
import tc.oc.occ.idly.api.IdlyAPI;

public class Idly extends JavaPlugin {

  private static Idly plugin;
  private PaperCommandManager commands;
  private IdlyConfig config;
  private IdlyManager manager;
  private IdlyAutoVanishManager vanishManager;
  private IdlyAPI api;

  @Override
  public void onEnable() {
    plugin = this;
    this.saveDefaultConfig();
    this.reloadConfig();
    this.config = new IdlyConfig(this.getConfig());
    this.commands = new PaperCommandManager(this);
    this.api = new BaseIdlyAPI();
    this.manager = new IdlyManager(this);
    this.vanishManager = new IdlyAutoVanishManager(config, manager);

    this.commands.registerDependency(IdlyConfig.class, config);
    this.commands.registerCommand(new IdlyCommand());

    this.getServer().getPluginManager().registerEvents(new IdlyListener(manager), this);
  }

  public static Idly get() {
    return plugin;
  }

  public void setAPI(IdlyAPI api) {
    this.api = api;
  }

  public IdlyAPI getAPI() {
    return api;
  }

  public IdlyManager getIdlyManager() {
    return manager;
  }

  public IdlyConfig getIdlyConfig() {
    return config;
  }

  public static void log(String message, Object... args) {
    plugin.getLogger().info(String.format(message, args));
  }
}
