// code by jph
package ch.ethz.idsc.edelweis.mvn;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
