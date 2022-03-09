package tc.oc.occ.idly.api;

import org.bukkit.entity.Player;

public interface IdlyAPI {

  boolean isMatchRunning();

  boolean isPlaying(Player player);
}
