package tc.oc.occ.idly;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class IdlyConfig {

  private boolean enabled;
  private boolean verbose;
  private boolean kickMode;
  private int participantDelay;
  private int observerDelay;
  private int warningDuration;
  private int warningFrequency;
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

  public int getWarningDuration() {
    return warningDuration;
  }

  public int getWarningFrequency() {
    return warningFrequency;
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
    this.enabled = config.getBoolean("enabled");
    this.verbose = config.getBoolean("verbose");
    this.kickMode = config.getBoolean("kick-mode");
    this.participantDelay = config.getInt("participant-delay");
    this.observerDelay = config.getInt("observer-delay");
    this.warningDuration = config.getInt("warning-duration");
    this.warningFrequency = config.getInt("warning-frequency");
    this.bypassEnabled = config.getBoolean("bypass-enabled", true);
    this.kickMessage = config.getString("kick-message");
    this.autoVanish = config.getBoolean("auto-vanish.enabled");
    this.autoVanishPermission = config.getString("auto-vanish.permission-node");
    this.autoVanishDelay = config.getInt("auto-vanish.delay");
    this.autoVanishRefresh = config.getInt("auto-vanish.refresh");
  }
}
