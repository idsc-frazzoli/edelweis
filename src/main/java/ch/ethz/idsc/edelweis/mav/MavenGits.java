// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class MavenGits implements AutoCloseable {
  private final Map<File, MavenGit> map = new HashMap<>();

  public MavenGits(List<File> repos) {
    for (File directory : repos)
      map.put(directory, new MavenGit(directory));
  }

  public boolean checkout(long millis) {
    return map.values().stream() //
        .allMatch(mavenGit -> mavenGit.checkout(millis));
  }

  @Override // from AutoCloseable
  public void close() throws Exception {
    map.values().stream() //
        .forEach(mavenGit -> {
          try {
            mavenGit.close();
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        });
  }

  public static void main(String[] args) throws Exception {
    ProjectDatahaki projectStructure = ProjectDatahaki.GOKART;
    try (MavenGits mavenGits = new MavenGits(projectStructure.repos())) {
      long currentTimeMillis = 1570762236630L; // System.currentTimeMillis();
      long delta = 7 * 24 * 60 * 60 * 1000;
      int weeksago = 0;
      TableBuilder tableBuilder = new TableBuilder();
      while (mavenGits.checkout(currentTimeMillis) //
          && -(4 * 12) < weeksago) {
        MavenCrossing mavenCrossing = new MavenCrossing( //
            projectStructure.projects(), //
            projectStructure.repos());
        tableBuilder.appendRow(Tensors.vector( //
            weeksago, //
            currentTimeMillis / 1000, //
            mavenCrossing.fileCount(true), //
            mavenCrossing.lineCount(true), //
            mavenCrossing.fileCount(false), //
            mavenCrossing.lineCount(false) //
        ));
        --weeksago;
        currentTimeMillis -= delta;
      }
      Export.of(HomeDirectory.Documents(projectStructure.name() + ".csv"), tableBuilder.getTable());
    }
  }
}
