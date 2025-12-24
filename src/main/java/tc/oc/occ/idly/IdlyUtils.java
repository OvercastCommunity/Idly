package tc.oc.occ.idly;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.player.MatchPlayer;

public class IdlyUtils {

  @Nullable
  public static MatchPlayer getMatchPlayer(Player player) {
    return PGM.get().getMatchManager().getPlayer(player);
  }
}
