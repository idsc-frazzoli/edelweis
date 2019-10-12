// code by jph
package ch.ethz.idsc.edelweis.mvn;

import java.io.File;
import java.util.List;

public interface MavenRepoStructure {
  /** Example:
   * {new File("/home/datahaki/Projects/tensor"), new File("/home/datahaki/Projects/subare")}
   * 
   * @return */
  List<File> repos();

  /** Example:
   * {"ch.ethz.idsc.tensor", "ch.ethz.idsc.subare"}
   * 
   * @return */
  List<String> projects();
}
