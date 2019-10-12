// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.Lists;

public class GitCollection implements AutoCloseable {
  private final Map<File, GitHistory> map = new LinkedHashMap<>();

  public GitCollection(List<File> repos) {
    for (File directory : repos)
      map.put(directory, new GitHistory(directory));
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

  public Date commonFirst() {
    List<Date> list = map.values().stream() //
        .map(GitHistory::earliest) //
        .sorted() // TODO is there a way to use max() ?
        .collect(Collectors.toList());
    return Lists.getLast(list);
  }
}
