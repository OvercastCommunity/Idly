package tc.oc.occ.idly;

import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.player.MatchPlayer;

public class IdlyUtils {

  @Nullable
  public static MatchPlayer getMatchPlayer(Player player) {
    return PGM.get().getMatchManager().getPlayer(player);
  }
}
