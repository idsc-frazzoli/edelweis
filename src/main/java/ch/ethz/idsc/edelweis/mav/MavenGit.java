// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.util.Date;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.edelweis.git.Git;

public class MavenGit implements AutoCloseable {
  private final Git git;
  private final String branch;
  private final NavigableMap<Date, String> logSha1;

  public MavenGit(File directory) {
    git = Git.requireClean(directory);
    branch = git.branch();
    System.out.println("branch=" + branch);
    logSha1 = git.logSha1();
  }

  public boolean checkout(long millis) {
    Entry<Date, String> entry = logSha1.floorEntry(new Date(millis));
    boolean success = Objects.nonNull(entry);
    if (success) {
      String sha1 = entry.getValue();
      System.out.println(sha1);
      git.checkout(sha1);
    }
    return success;
  }

  @Override // from AutoCloseable
  public void close() throws Exception {
    System.out.println("checkout " + branch);
    git.checkout(branch);
  }
}
