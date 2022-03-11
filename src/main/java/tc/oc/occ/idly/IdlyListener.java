package tc.oc.occ.idly;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.events.PlayerPartyChangeEvent;

public class IdlyListener implements Listener {

  private IdlyConfig config;
  private IdlyManager manager;

  public IdlyListener(IdlyManager manager, IdlyConfig config) {
    this.manager = manager;
    this.config = config;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.manager.logMovement(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerPartyChange(PlayerPartyChangeEvent event) {
    if (event.getNewParty() != null && event.getNewParty() instanceof Competitor) {
      this.manager.logMovement(event.getPlayer().getBukkit());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
    if (config.isChatCheck()) {
      this.manager.logMovement(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveEvent(PlayerMoveEvent event) {
    if (config.isMovementCheck()) {
      boolean wasPitchYawMoved =
          event.getFrom().getYaw() != event.getTo().getYaw()
              || event.getFrom().getPitch() != event.getTo().getPitch();

      if (config.isPreciseMovement() && !wasPitchYawMoved) return;

      this.manager.logMovement(event.getPlayer());
    }
  }
}
