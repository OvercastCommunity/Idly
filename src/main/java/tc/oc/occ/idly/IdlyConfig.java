package tc.oc.occ.idly;

import org.bukkit.configuration.Configuration;

public class IdlyConfig {

  private boolean enabled;
  private int afkDelay;
  private int kickDelay;

  public IdlyConfig(Configuration config) {
    reload(config);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public int getAfkDelay() {
    return afkDelay;
  }

  public int getKickDelay() {
    return kickDelay;
  }

  public void reload(Configuration config) {
    this.enabled = config.getBoolean("enabled");
    this.afkDelay = config.getInt("afk-delay");
    this.kickDelay = config.getInt("kick-delay");
  }
}
