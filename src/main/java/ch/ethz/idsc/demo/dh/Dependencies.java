// code by jph
package ch.ethz.idsc.demo.dh;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* package */ enum Dependencies {
  ;
  private static final Map<String, Set<String>> PERMITTED = new HashMap<>();
  static {
    PERMITTED.put("ch.ethz.idsc.demo",
        new HashSet<>(Arrays.asList( //
            "ch.ethz.idsc.gokart", //
            "ch.ethz.idsc.retina", //
            "ch.ethz.idsc.owl", //
            "ch.ethz.idsc.sophus", //
            "ch.ethz.idsc.subare", //
            "ch.ethz.idsc.tensor" //
        )));
    PERMITTED.put("ch.ethz.idsc.gokart",
        new HashSet<>(Arrays.asList( //
            "ch.ethz.idsc.retina", //
            "ch.ethz.idsc.owl", //
            "ch.ethz.idsc.sophus", //
            "ch.ethz.idsc.subare", //
            "ch.ethz.idsc.tensor" //
        )));
    PERMITTED.put("ch.ethz.idsc.retina",
        new HashSet<>(Arrays.asList( //
            "ch.ethz.idsc.sophus", //
            "ch.ethz.idsc.tensor" //
        )));
    PERMITTED.put("ch.ethz.idsc.owl",
        new HashSet<>(Arrays.asList( //
            "ch.ethz.idsc.sophus", //
            "ch.ethz.idsc.subare", //
            "ch.ethz.idsc.tensor" //
        )));
    PERMITTED.put("ch.ethz.idsc.sophus", new HashSet<>(Arrays.asList( //
        "ch.ethz.idsc.tensor" //
    )));
    PERMITTED.put("ch.ethz.idsc.subare", new HashSet<>(Arrays.asList( //
        "ch.ethz.idsc.tensor" //
    )));
  }

  private static Set<String> getProject(String project) {
    Set<String> set = PERMITTED.get(project);
    return Objects.isNull(set) //
        ? Collections.emptySet() : Collections.unmodifiableSet(set);
  }

  public static boolean dependsOn(String project, String library) {
    return project.equals(library) //
        || getProject(project).contains(library);
  }
}
