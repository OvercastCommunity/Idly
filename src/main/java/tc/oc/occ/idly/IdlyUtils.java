package tc.oc.occ.idly;

import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;

public class IdlyUtils {

  public static Match getMatch() {
    return PGM.get().getMatchManager().getMatches().hasNext()
        ? PGM.get().getMatchManager().getMatches().next()
        : null;
  }
}
