package tc.oc.occ.idly;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.text;
import static tc.oc.occ.idly.Idly.log;

import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.integration.Integration;
import tc.oc.pgm.util.Audience;

public class IdlyAutoVanishManager {

  private final int PRE_AFK_DELAY = 5; // SECONDS
  private final Sound WARN_SOUND = sound(key("note.pling"), Sound.Source.MASTER, 1f, 0.5f);

  private final IdlyConfig config;
  private final IdlyManager manager;

  public IdlyAutoVanishManager(IdlyConfig config, IdlyManager manager) {
    this.config = config;
    this.manager = manager;

    Idly.get()
        .getServer()
        .getScheduler()
        .scheduleSyncRepeatingTask(
            Idly.get(), this::check, 0L, 20L * config.getAutoVanishRefresh());
  }

  public void check() {
    if (Bukkit.getOnlinePlayers().isEmpty()) return;

    logMessage("=== Checking for AFK players to auto-vanish ===");
    logMessage("- Delay: %d seconds", config.getAutoVanishDelay());
    logMessage("- Refresh: %d seconds", config.getAutoVanishRefresh());

    List<Player> staff =
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission(config.getAutoVanishPermission()))
            .filter(p -> !Integration.isVanished(p) && Integration.getNick(p) == null)
            .filter(p -> manager.isIdle(p, config.getAutoVanishDelay()))
            .collect(Collectors.toList());

    logMessage("- Found %d AFK player(s) to vanish!", staff.size());

    for (Player player : staff) {
      final Audience viewer = Audience.get(player);
      viewer.sendMessage(
          text()
              .append(text("You will be auto-vanished in "))
              .append(text(PRE_AFK_DELAY + " seconds", NamedTextColor.YELLOW))
              .color(NamedTextColor.RED));
      viewer.sendMessage(
          text()
              .append(text("If you're not AFK, move around or chat to cancel!"))
              .color(NamedTextColor.RED)
              .build());
      viewer.playSound(WARN_SOUND);

      Idly.get()
          .getServer()
          .getScheduler()
          .runTaskLater(
              Idly.get(),
              () -> {
                if (manager.isIdle(player, config.getAutoVanishDelay())) {
                  Bukkit.dispatchCommand(player, "vanish");
                  logMessage(
                      "- [AUTO-VANISH] %s has been auto-vanished for being AFK", player.getName());
                } else {
                  logMessage(
                      "- [FALSE-ALARM] %s is not AFK (recent activity found)", player.getName());
                }
              },
              20L * PRE_AFK_DELAY);
    }
  }

  private void logMessage(String message, Object... args) {
    if (config.isVerbose()) {
      log(message, args);
    }
  }
}
