package tc.oc.occ.idly;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.text;

import com.google.common.base.Objects;
import javax.annotation.Nullable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.util.bukkit.OnlinePlayerMapAdapter;

public class IdlyManager {

  private static final int TICK_FREQUENCY = 10;
  private static final int TICKS_PER_SECOND = 20;

  private final Idly plugin;
  private final IdlyConfig config;
  private final OnlinePlayerMapAdapter<Integer> playerInactivityTickCache;

  public IdlyManager(Idly plugin) {
    this.plugin = plugin;
    this.config = plugin.getIdlyConfig();
    this.playerInactivityTickCache = new OnlinePlayerMapAdapter<Integer>(plugin);

    plugin
        .getServer()
        .getScheduler()
        .scheduleSyncRepeatingTask(plugin, this::checkPlayers, 0L, TICK_FREQUENCY);
  }

  public void logMovement(Player player) {
    playerInactivityTickCache.put(player, 0);
  }

  private void checkPlayers() {
    if (!config.isEnabled()) return;
    Match match = IdlyUtils.getMatch();
    if (match != null) {
      if (config.isRequireMatchRunning() && !plugin.getAPI().isMatchRunning()) return;

      for (Player player : this.playerInactivityTickCache.keySet()) {
        checkPlayer(match.getPlayer(player), plugin.getAPI().isPlaying(player));
      }
    }
  }

  private void checkPlayer(MatchPlayer player, boolean isPlaying) {
    int inactivity =
        playerInactivityTickCache.compute(
            player.getBukkit(), (p, t) -> Objects.firstNonNull(t, 0) + TICK_FREQUENCY);

    // Don't track observers when kick mode is disabled
    if (!config.isKickMode() && !isPlaying) return;

    // Ignore those with the bypass permission
    if (player.getBukkit().hasPermission(IdlyPermissions.BYPASS)) return;

    int duration = (isPlaying ? config.getParticipantDelay() : config.getObserverDelay());
    float remaining = duration - inactivity;
    if (remaining <= 0) {
      kick(player);
    } else if (remaining <= config.getWarningDuration()
        && (remaining % config.getWarningFrequency()) < TICK_FREQUENCY) {
      sendWarningCountdown(player.getBukkit(), remaining);
    }
  }

  private void kick(MatchPlayer player) {
    if (config.isKickMode()) {
      kickFromServer(player);
    } else {
      kickFromMatch(player);
    }
  }

  private void kickFromServer(MatchPlayer player) {
    player.getBukkit().kickPlayer(config.getKickMessage());
  }

  private void kickFromMatch(MatchPlayer player) {
    Match match = player.getMatch();
    Audience viewer = plugin.getViewer(player.getBukkit());

    match.setParty(player, match.getDefaultParty());
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

  private void sendWarningCountdown(Player player, float tickTime) {
    int time = Math.round(tickTime / TICKS_PER_SECOND);
    Audience viewer = plugin.getViewer(player);
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
  private static final Sound COUNTDOWN = sound(key("random.break"), Sound.Source.MASTER, 1f, 1.15f);
  private static final Sound KICK =
      sound(key("mob.zombie.woodbreak"), Sound.Source.MASTER, 1f, 1.20f);

  private void sendWarning(Audience viewer, Component message, @Nullable Sound sound) {
    viewer.sendMessage(text().append(WARNING).append(message));
    if (sound != null) {
      viewer.playSound(sound);
    }
  }
}
