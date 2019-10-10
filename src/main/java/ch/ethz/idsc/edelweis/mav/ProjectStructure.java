// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.util.List;

public interface ProjectStructure {
  List<File> repos();

  List<String> projects();
}
