package tc.oc.occ.idly;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class IdlyConfig {

  private boolean enabled;
  private boolean verbose;
  private boolean kickMode;
  private int participantDelay;
  private int observerDelay;
  private int enforcementDelay;
  private int warningDuration;
  private int warningFrequency;
  private boolean kickObservers;
  private boolean bypassEnabled;
  private String kickMessage;
  private boolean autoVanish;
  private String autoVanishPermission;
  private int autoVanishDelay;
  private int autoVanishRefresh;

  public IdlyConfig(Configuration config) {
    reload(config);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isVerbose() {
    return verbose;
  }

  public boolean isKickMode() {
    return kickMode;
  }

  public int getParticipantDelay() {
    return participantDelay;
  }

  public int getObserverDelay() {
    return observerDelay;
  }

  public int getEnforcementDelay() {
    return enforcementDelay;
  }

  public int getWarningDuration() {
    return warningDuration;
  }

  public int getWarningFrequency() {
    return warningFrequency;
  }

  public boolean isKickObservers() {
    return kickObservers;
  }

  public boolean isBypassEnabled() {
    return bypassEnabled;
  }

  public String getKickMessage() {
    return ChatColor.translateAlternateColorCodes('&', kickMessage);
  }

  public boolean isAutoVanish() {
    return autoVanish;
  }

  public String getAutoVanishPermission() {
    return autoVanishPermission;
  }

  public int getAutoVanishDelay() {
    return autoVanishDelay;
  }

  public int getAutoVanishRefresh() {
    return autoVanishRefresh;
  }

  public void reload(Configuration config) {
    this.enabled = config.getBoolean("enabled", true);
    this.verbose = config.getBoolean("verbose", false);
    this.kickMode = config.getBoolean("kick-mode", false);
    this.kickObservers = config.getBoolean("kick-observers", false);
    this.participantDelay = config.getInt("participant-delay", 90);
    this.observerDelay = config.getInt("observer-delay", 120);
    this.enforcementDelay = config.getInt("enforcement-delay", 4);
    this.warningDuration = config.getInt("warning-duration", 15);
    this.warningFrequency = config.getInt("warning-frequency", 5);
    this.bypassEnabled = config.getBoolean("bypass-enabled", true);
    this.kickMessage =
        config.getString("kick-message", "&c&lYou were kicked for being idle too long!");
    this.autoVanish = config.getBoolean("auto-vanish.enabled", true);
    this.autoVanishPermission = config.getString("auto-vanish.permission-node", "idly.auto-vanish");
    this.autoVanishDelay = config.getInt("auto-vanish.delay", 600);
    this.autoVanishRefresh = config.getInt("auto-vanish.refresh", 60);
  }
}
