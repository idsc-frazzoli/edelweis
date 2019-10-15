// code by jph
package ch.ethz.idsc.edelweis.mvn;

import java.util.List;
import java.util.Optional;

public class ProjectWrap {
  private final List<String> projects;

  public ProjectWrap(List<String> projects) {
    this.projects = projects;
  }

  /** Examples of classId:
   * "java.nio.ByteBuffer"
   * "ch.ethz.idsc.gokart.lcm.OfflineLogPlayer"
   * 
   * @param classId
   * @return */
  public Optional<String> identifyProject(String classId) {
    return projects.stream() //
        .filter(project -> classId.startsWith(project + ".")) //
        .findFirst();
  }
}
