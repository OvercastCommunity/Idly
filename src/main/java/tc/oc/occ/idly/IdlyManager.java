package tc.oc.occ.idly;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.text;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.util.bukkit.OnlinePlayerMapAdapter;

public class IdlyManager {

  private final Idly plugin;
  private final IdlyConfig config;
  private final OnlinePlayerMapAdapter<Instant> playerMovementCache;
  private final OnlinePlayerMapAdapter<Instant> playerAfkCache;

  public IdlyManager(Idly plugin) {
    this.plugin = plugin;
    this.config = plugin.getIdlyConfig();
    this.playerMovementCache = new OnlinePlayerMapAdapter<Instant>(plugin);
    this.playerAfkCache = new OnlinePlayerMapAdapter<Instant>(plugin);
    plugin
        .getServer()
        .getScheduler()
        .scheduleSyncRepeatingTask(plugin, this::checkPlayers, 20L, 20L);
  }

  public void reset() {
    this.playerMovementCache.clear();
    this.playerAfkCache.clear();
  }

  public Instant getLastMovement(Player player) {
    return playerMovementCache.get(player);
  }

  public void logMovement(Player player) {
    playerMovementCache.put(player, Instant.now());
    removeAFK(player);
  }

  public void resetMovement(Player player) {
    playerMovementCache.remove(player);
  }

  public boolean isAFK(Player player) {
    return playerAfkCache.containsKey(player);
  }

  public void setAFK(Player player) {
    this.playerAfkCache.put(player, Instant.now());
  }

  public void removeAFK(Player player) {
    this.playerAfkCache.remove(player);
  }

  private Instant getAFKSince(Player player) {
    return this.playerAfkCache.get(player);
  }

  private boolean checkAFK(MatchPlayer player) {
    Match match = player.getMatch();
    Audience viewer = plugin.getViewer(player.getBukkit());

    if (!isAFK(player.getBukkit())) return false;
    if (!match.isRunning()) return false;

    Instant afkTime = getAFKSince(player.getBukkit());
    Duration timeSinceAfk = Duration.between(afkTime, Instant.now());
    long secondsLeft = (config.getKickDelay() - timeSinceAfk.getSeconds()) + 1;
    Component observers = text("Observers", NamedTextColor.AQUA);

    if (secondsLeft > 0) {
      sendWarning(
          viewer,
          text()
              .append(text("You will be moved to "))
              .append(observers)
              .append(text(" due to inactivity in "))
              .append(text(secondsLeft, NamedTextColor.YELLOW))
              .append(text(" second" + (secondsLeft != 1 ? "s" : "")))
              .color(NamedTextColor.RED)
              .build(),
          COUNTDOWN);
    } else {
      match.setParty(player, match.getDefaultParty());
      sendWarning(
          viewer,
          text()
              .append(text("Moved to "))
              .append(observers)
              .append(text(" due to inactivity"))
              .color(NamedTextColor.RED)
              .build(),
          KICK);
      this.resetMovement(player.getBukkit());
      this.removeAFK(player.getBukkit());
    }

    return true;
  }

  private void checkPlayers() {
    if (!config.isEnabled()) return;
    Match match = getMatch();
    if (match != null) {
      // Only check participating players
      for (MatchPlayer player : match.getParticipants()) {
        // Ignore those with permission bypass
        if (player.getBukkit().hasPermission(IdlyPermissions.BYPASS)) continue;

        Instant lastMovement = getLastMovement(player.getBukkit());
        if (lastMovement == null) {
          // May be null if just joined match
          // so we log an initial movement
          logMovement(player.getBukkit());
        } else {
          Duration timeElasped = Duration.between(lastMovement, Instant.now());
          if (!checkAFK(player) && timeElasped.getSeconds() > config.getAfkDelay()) {
            setAFK(player.getBukkit());
          }
        }
      }
    }
  }

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

  private Match getMatch() {
    return PGM.get().getMatchManager().getMatches().hasNext()
        ? PGM.get().getMatchManager().getMatches().next()
        : null;
  }
}
