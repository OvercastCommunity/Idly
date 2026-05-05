package tc.oc.occ.idly;

import static net.kyori.adventure.text.Component.text;
import static tc.oc.pgm.util.bukkit.Sounds.sound;

import java.time.Duration;
import java.time.Instant;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.util.Audience;
import tc.oc.pgm.util.bukkit.OnlinePlayerUUIDMapAdapter;
import tc.oc.pgm.util.listener.AfkTracker;

@NullMarked
public class IdlyManager {

  private static final int TICK_FREQUENCY = 10;
  private static final Duration CHECK_FREQUENCY = Duration.ofMillis(TICK_FREQUENCY * 50L);

  private final Idly plugin;
  private final IdlyConfig config;
  private final OnlinePlayerUUIDMapAdapter<Instant> lastActivity;

  private Instant now = Instant.now();

  public IdlyManager(Idly plugin) {
    this.plugin = plugin;
    this.config = plugin.getIdlyConfig();
    this.lastActivity = new OnlinePlayerUUIDMapAdapter<>(plugin);

    plugin
        .getServer()
        .getScheduler()
        .scheduleSyncRepeatingTask(plugin, this::checkPlayers, 0L, TICK_FREQUENCY);
  }

  public void logActivity(Player player) {
    now = Instant.now();

    lastActivity.put(player.getUniqueId(), now);
  }

  public void applyEnforcementDelay(Player player) {
    now = Instant.now();

    Duration timeout = Duration.ofSeconds(config.getParticipantDelay());
    Duration cooldown = Duration.ofSeconds(config.getEnforcementDelay());
    Duration grace = cooldown.compareTo(timeout) > 0 ? timeout : cooldown;
    Duration inactivity = getInactivityDuration(player);

    if (inactivity.compareTo(timeout.minus(grace)) > 0) {
      lastActivity.put(player.getUniqueId(), now.minus(timeout.minus(grace)));
    }
  }

  public boolean isIdle(Player player, int timeoutSeconds) {
    now = Instant.now();

    Duration timeout = Duration.ofSeconds(timeoutSeconds);
    return getInactivityDuration(player).compareTo(timeout) > 0;
  }

  private Duration getInactivityDuration(Player player) {
    // Use PGM's activity tracker, but if Idly has separately tracked
    // more recent activity, we will use that timestamp instead
    AfkTracker.Activity activity = PGM.get().getAfkTracker().getActivity(player);

    Instant lastLogged = lastActivity.get(player.getUniqueId());
    if (lastLogged == null || lastLogged.isBefore(activity.getLastActive())) {
      return activity.getAfkDuration();
    }

    return Duration.between(lastLogged, now);
  }

  private void checkPlayers() {
    now = Instant.now();

    if (!config.isEnabled()) return;

    for (Player player : plugin.getServer().getOnlinePlayers()) {
      checkPlayer(player);
    }
  }

  private void checkPlayer(Player player) {
    boolean isPlaying = plugin.getAPI().isPlaying(player);

    // Only check observers if kicking observers is enabled as well as kick mode
    if (!isPlaying && (!config.isKickMode() || !config.isKickObservers())) return;

    // Ignore those with the bypass permission
    if (config.isBypassEnabled() && player.hasPermission(IdlyPermissions.BYPASS)) return;

    Duration timeout = getIdleTimeout(isPlaying);
    Duration remaining = timeout.minus(getInactivityDuration(player));

    if (remaining.isNegative()) {
      kick(player);
    } else if (remaining.compareTo(getWarningDuration()) <= 0
        && getWarningRemainder(remaining).compareTo(CHECK_FREQUENCY) < 0) {
      sendWarningCountdown(player, remaining);
    }
  }

  private Duration getIdleTimeout(boolean isPlaying) {
    return Duration.ofSeconds(isPlaying ? config.getParticipantDelay() : config.getObserverDelay());
  }

  private Duration getWarningDuration() {
    return Duration.ofSeconds(config.getWarningDuration());
  }

  private Duration getWarningRemainder(Duration remaining) {
    Duration frequency = Duration.ofSeconds(config.getWarningFrequency());
    return remaining.minus(frequency.multipliedBy(remaining.dividedBy(frequency)));
  }

  private void kick(Player player) {
    if (config.isKickMode()) {
      kickFromServer(player);
    } else {
      kickFromMatch(player);
    }
  }

  private void kickFromServer(Player player) {
    player.kickPlayer(config.getKickMessage());
  }

  private void kickFromMatch(Player player) {
    MatchPlayer mp = IdlyUtils.getMatchPlayer(player);
    if (mp == null) return;

    Match match = mp.getMatch();
    var viewer = Audience.get(player);

    match.setParty(mp, match.getDefaultParty());
    sendWarning(
        viewer,
        text()
            .append(text("Moved to "))
            .append(OBSERVERS)
            .append(text(" due to inactivity"))
            .color(NamedTextColor.RED)
            .build(),
        KICK);
  }

  private void sendWarningCountdown(Player player, Duration remaining) {
    long time = Math.max(1, remaining.toSeconds());
    var viewer = Audience.get(player);
    sendWarning(
        viewer,
        text()
            .append(text("You will be "))
            .append(
                config.isKickMode()
                    ? text("kicked", NamedTextColor.YELLOW)
                    : text().append(text("moved to ")).append(OBSERVERS))
            .append(text(" due to inactivity in "))
            .append(text(time, NamedTextColor.YELLOW))
            .append(text(" second" + (time != 1 ? "s" : "")))
            .color(NamedTextColor.RED)
            .build(),
        COUNTDOWN);
    viewer.playSound(COUNTDOWN);
  }

  private static final Component OBSERVERS = text("Observers", NamedTextColor.AQUA);
  private static final Component WARNING = text(" \u26a0 ", NamedTextColor.YELLOW);
  private static final Sound COUNTDOWN = sound("ITEM_BREAK", "ENTITY_ITEM_BREAK", 1f, 1.15f);
  private static final Sound KICK =
      sound("ZOMBIE_WOODBREAK", "ENTITY_ZOMBIE_BREAK_WOODEN_DOOR", 1f, 1.2f);

  private void sendWarning(Audience viewer, Component message, Sound sound) {
    viewer.sendMessage(text().append(WARNING).append(message));
    viewer.playSound(sound);
  }
}
