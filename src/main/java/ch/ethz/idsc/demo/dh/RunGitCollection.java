// code by jph
package ch.ethz.idsc.demo.dh;

import java.util.Date;

import ch.ethz.idsc.edelweis.git.GitCollection;
import ch.ethz.idsc.edelweis.mvn.JavaFile;
import ch.ethz.idsc.edelweis.mvn.JavaPredicates;
import ch.ethz.idsc.edelweis.mvn.MavenCrossing;
import ch.ethz.idsc.edelweis.mvn.MavenRepoStructure;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ enum RunGitCollection {
  ;
  public static void run(String name, MavenRepoStructure projectStructure) throws Exception {
    try (GitCollection gitCollection = new GitCollection(projectStructure.repos())) {
      Date commonFirst = gitCollection.commonFirst();
      System.out.println(commonFirst);
      long currentTimeMillis = 1570762236630L; // System.currentTimeMillis();
      long delta = 7 * 24 * 60 * 60 * 1000;
      int weeksago = 0;
      TableBuilder tableBuilder = new TableBuilder();
      while (gitCollection.checkout(currentTimeMillis) //
          && -(4 * 12) < weeksago) {
        MavenCrossing mavenCrossing = new MavenCrossing( //
            projectStructure.projects(), //
            projectStructure.repos());
        tableBuilder.appendRow(Tensors.vector( //
            weeksago, //
            currentTimeMillis / 1000, //
            mavenCrossing.fileCount(JavaFile.MAIN), //
            mavenCrossing.lineCount(JavaFile.MAIN, JavaPredicates.CODE), //
            mavenCrossing.fileCount(JavaFile.TEST), //
            mavenCrossing.lineCount(JavaFile.TEST, JavaPredicates.CODE) //
        ));
        --weeksago;
        currentTimeMillis -= delta;
      }
      Export.of(HomeDirectory.Documents(name + ".csv"), tableBuilder.getTable());
    }
  }

  public static void main(String[] args) throws Exception {
    run("gokart", DatahakiProjects.GOKART);
  }
}
