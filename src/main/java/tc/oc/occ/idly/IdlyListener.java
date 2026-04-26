package tc.oc.occ.idly;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.events.PlayerPartyChangeEvent;

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
  public void onMatchStart(MatchPhaseChangeEvent event) {
    // Log participant activity when match starts (to avoid instant kicking)
    if (event.getOldPhase() == MatchPhase.IDLE && event.getNewPhase() != MatchPhase.IDLE) {
      event.getMatch().getParticipants().forEach(p -> this.manager.logActivity(p.getBukkit()));
    }
  }
}
