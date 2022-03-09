package tc.oc.occ.idly.api;

import org.bukkit.entity.Player;
import tc.oc.occ.idly.IdlyUtils;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;

public class BaseIdlyAPI implements IdlyAPI {

  @Override
  public boolean isMatchRunning() {
    return IdlyUtils.getMatch().isRunning();
  }

  @Override
  public boolean isPlaying(Player player) {
    Match match = IdlyUtils.getMatch();
    MatchPlayer mp = match.getPlayer(player);
    return mp != null && mp.isParticipating();
  }
}
