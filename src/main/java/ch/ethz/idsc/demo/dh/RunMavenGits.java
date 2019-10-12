// code by jph
package ch.ethz.idsc.demo.dh;

import ch.ethz.idsc.edelweis.mvn.MavenCrossing;
import ch.ethz.idsc.edelweis.mvn.MavenGits;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ enum RunMavenGits {
  ;
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
