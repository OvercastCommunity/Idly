package tc.oc.occ.idly;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;

public class IdlyListener implements Listener {

  private IdlyManager manager;

  public IdlyListener(IdlyManager plugin) {
    this.manager = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMoveEvent(PlayerMoveEvent event) {
    this.manager.logMovement(event.getPlayer());
  }

  @EventHandler
  public void onMatchStatusChange(MatchPhaseChangeEvent event) {
    this.manager.reset();
  }
}
