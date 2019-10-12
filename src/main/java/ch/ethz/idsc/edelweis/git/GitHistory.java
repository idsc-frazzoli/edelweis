// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.File;
import java.util.Date;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

public class GitHistory implements AutoCloseable {
  private final Git git;
  /** branch when git object is instantiated */
  private final String branch;
  private final NavigableMap<Date, String> logSha1;

  public GitHistory(File directory) {
    git = Git.requireClean(directory);
    branch = git.branch();
    logSha1 = git.logSha1();
  }

  /** "less than or equal to the given" date
   * 
   * @param date
   * @return checkout status */
  public boolean checkout(Date date) {
    Entry<Date, String> entry = logSha1.floorEntry(date);
    boolean success = Objects.nonNull(entry);
    if (success) {
      String sha1 = entry.getValue();
      git.checkout(sha1);
    }
    return success;
  }

  /** @return date of first commit */
  public Date earliest() {
    return logSha1.firstKey();
  }

  @Override // from AutoCloseable
  public void close() throws Exception {
    git.checkout(branch);
  }
}
