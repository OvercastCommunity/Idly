package tc.oc.occ.idly.api;

import org.bukkit.entity.Player;
import tc.oc.occ.idly.IdlyUtils;
import tc.oc.pgm.api.player.MatchPlayer;

public class BaseIdlyAPI implements IdlyAPI {

  @Override
  public boolean isMatchRunning(Player player) {
    MatchPlayer mp = IdlyUtils.getMatchPlayer(player);
    return mp != null && mp.getMatch().isRunning();
  }

  @Override
  public boolean isPlaying(Player player) {
    MatchPlayer mp = IdlyUtils.getMatchPlayer(player);
    return mp != null && mp.isParticipating();
  }
}
