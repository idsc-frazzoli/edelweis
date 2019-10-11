// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class MavenCrossing {
  // private final List<String> projects;
  private final Map<String, List<JavaFile>> map = new HashMap<>();

  public MavenCrossing(List<String> projects, Collection<File> repos) throws FileNotFoundException, IOException {
    // this.projects = projects;
    projects.forEach(project -> map.put(project, new LinkedList<>()));
    // ---
    for (File repo : repos) {
      MavenPackageIndex mavenPackageIndex = MavenPackageIndex.of(repo);
      for (JavaFile javaFile : mavenPackageIndex.javaFiles()) {
        String package_name = javaFile.getPackage();
        if (Objects.nonNull(package_name)) {
          Predicate<String> isPackage = //
              string -> package_name.equals(string) || package_name.startsWith(string + ".");
          Optional<String> optional = projects.stream() //
              .filter(isPackage) //
              .findFirst();
          if (optional.isPresent()) {
            String project = optional.get();
            map.get(project).add(javaFile);
          }
        }
      }
    }
  }

  /** @return total file count of all projects */
  public int fileCount() {
    return map.values().stream().mapToInt(List::size).sum();
  }

  /** @return total line count of all projects */
  public int lineCount() {
    return map.values().stream() //
        .flatMap(List::stream) //
        .mapToInt(javaFile -> javaFile.count(JavaPredicates.RELEVANT_CODE)) //
        .sum();
  }

  /** @return total file count of all projects */
  public int fileCount(boolean isMain) {
    return (int) map.values().stream() //
        .flatMap(List::stream) //
        .filter(javaFile -> javaFile.isMain() == isMain) //
        .count();
  }

  /** @return total line count of all projects */
  public int lineCount(boolean isMain) {
    return map.values().stream() //
        .flatMap(List::stream) //
        .filter(javaFile -> javaFile.isMain() == isMain) //
        .mapToInt(javaFile -> javaFile.count(JavaPredicates.RELEVANT_CODE)) //
        .sum();
  }

  public List<JavaFile> files(String project) {
    return Collections.unmodifiableList(map.get(project));
  }

  public void print() {
    int lineCount = 0;
    for (Entry<String, List<JavaFile>> entry : map.entrySet()) {
      String project = entry.getKey();
      List<JavaFile> list = entry.getValue();
      int sum = list.stream().mapToInt(javaFile -> javaFile.count(JavaPredicates.RELEVANT_CODE)).sum();
      System.out.println(String.format("%20s %5d %6d", project, list.size(), sum));
      lineCount += sum;
    }
    System.out.println("fileCount=" + fileCount());
    System.out.println("lineCount=" + lineCount);
  }
}
