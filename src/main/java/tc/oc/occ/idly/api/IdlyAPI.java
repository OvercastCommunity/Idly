package tc.oc.occ.idly.api;

import org.bukkit.entity.Player;

public interface IdlyAPI {

  boolean isMatchRunning(Player player);

  boolean isPlaying(Player player);
}
