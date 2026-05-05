package tc.oc.occ.idly;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;
import tc.oc.pgm.api.match.event.MatchStartEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.events.PlayerPartyChangeEvent;

@NullMarked
public class IdlyListener implements Listener {

  private final IdlyManager manager;

  public IdlyListener(IdlyManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerPartyChange(PlayerPartyChangeEvent event) {
    if (event.getNewParty() != null && event.getNewParty() instanceof Competitor) {
      this.manager.logActivity(event.getPlayer().getBukkit());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchStart(MatchStartEvent event) {
    // Apply enforcement delay when match starts (to avoid instant kicking)
    event
        .getMatch()
        .getParticipants()
        .forEach(p -> this.manager.applyEnforcementDelay(p.getBukkit()));
  }
}
