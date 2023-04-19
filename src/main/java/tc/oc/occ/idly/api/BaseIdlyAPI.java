package tc.oc.occ.idly.api;

import org.bukkit.entity.Player;
import tc.oc.occ.idly.IdlyUtils;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;

public class BaseIdlyAPI implements IdlyAPI {

  @Override
  public boolean isPlaying(Player player) {
    MatchPlayer mp = IdlyUtils.getMatchPlayer(player);
    return mp != null
        && mp.getParty() instanceof Competitor
        && (mp.getMatch().getPhase().equals(MatchPhase.STARTING) || mp.getMatch().isRunning());
  }
}
