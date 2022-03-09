package tc.oc.occ.idly;

import java.time.Duration;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class IdlyConfig {

  private boolean enabled;
  private boolean kickMode;
  private int participantDelay;
  private int observerDelay;
  private int warningDuration;
  private int warningFrequency;
  private boolean requireMatchRunning;
  private boolean preciseMovement;
  private boolean movementCheck;
  private boolean chatCheck;
  private String kickMessage;

  public IdlyConfig(Configuration config) {
    reload(config);
  }

  public boolean isEnabled() {
    return enabled;
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

  public boolean isRequireMatchRunning() {
    return requireMatchRunning;
  }

  public boolean isPreciseMovement() {
    return preciseMovement;
  }

  public boolean isMovementCheck() {
    return movementCheck;
  }

  public boolean isChatCheck() {
    return chatCheck;
  }

  public String getKickMessage() {
    return ChatColor.translateAlternateColorCodes('&', kickMessage);
  }

  public void reload(Configuration config) {
    this.enabled = config.getBoolean("enabled");
    this.kickMode = config.getBoolean("kick-mode");
    this.participantDelay = config.getInt("participant-delay");
    this.observerDelay = config.getInt("observer-delay");
    this.warningDuration = config.getInt("warning-duration");
    this.warningFrequency = config.getInt("warning-frequency");
    this.requireMatchRunning = config.getBoolean("require-match-running");
    this.preciseMovement = config.getBoolean("precise-movement");
    this.movementCheck = config.getBoolean("checks.movement");
    this.chatCheck = config.getBoolean("checks.chat");
    this.kickMessage = config.getString("kick-message");

    this.participantDelay = convertSettingTime(participantDelay);
    this.observerDelay = convertSettingTime(observerDelay);
    this.warningFrequency = convertSettingTime(warningFrequency);
    this.warningDuration = convertSettingTime(warningDuration);
  }

  private int convertSettingTime(int value) {
    return (int) Duration.ofSeconds(value).toMillis() / 50;
  }
}
