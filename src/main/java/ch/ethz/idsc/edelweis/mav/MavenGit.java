// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.util.Date;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.edelweis.git.Git;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class MavenGit {
  public static void main(String[] args) throws Exception {
    File directory = new File("/home/datahaki/Projects/nocturne");
    Git git = Git.requireClean(directory);
    String branch = git.branch();
    System.out.println("branch=" + branch);
    NavigableMap<Date, String> logSha1 = git.logSha1();
    long currentTimeMillis = System.currentTimeMillis();
    long delta = 7 * 24 * 60 * 60 * 1000;
    int weeksago = 0;
    TableBuilder tableBuilder = new TableBuilder();
    while (true) {
      Entry<Date, String> entry = logSha1.floorEntry(new Date(currentTimeMillis));
      if (Objects.isNull(entry))
        break;
      Date date = entry.getKey();
      String sha1 = entry.getValue();
      System.out.println(sha1);
      git.checkout(sha1);
      {
        MavenCrossing mavenCrossing = DatahakiMavenIndex.nocturne();
        mavenCrossing.compile();
        tableBuilder.appendRow(Tensors.vector( //
            weeksago, //
            currentTimeMillis / 1000, //
            mavenCrossing.fileCount(true), //
            mavenCrossing.lineCount(true), //
            mavenCrossing.fileCount(false), //
            mavenCrossing.lineCount(false) //
            ));
      }
      --weeksago;
      currentTimeMillis -= delta;
    }
    git.checkout(branch);
    // ---
    Export.of(HomeDirectory.Documents("nocturne.csv"), tableBuilder.getTable());
  }
}
