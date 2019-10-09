package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class MavenCrossing {
  private final List<String> projects = new LinkedList<>();
  private final List<File> repos = new LinkedList<>();
  private final Map<String, List<JavaFile>> map = new HashMap<>();

  public void addProject(String project) {
    projects.add(project);
    map.put(project, new LinkedList<>());
  }

  public void addRepo(File repo) {
    repos.add(repo);
  }

  public void run() throws FileNotFoundException, IOException {
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
    int lineCount = 0;
    for (Entry<String, List<JavaFile>> entry : map.entrySet()) {
      String project = entry.getKey();
      List<JavaFile> list = entry.getValue();
      System.out.println(project + " " + list.size());
      int sum = list.stream().mapToInt(jf -> jf.count(JavaPredicates.RELEVANT_CODE)).sum();
      System.out.println(sum);
      lineCount += sum;
    }
    int fileCount = map.values().stream().mapToInt(List::size).sum();
    System.out.println("fileCount=" + fileCount);
    System.out.println("lineCount=" + lineCount);
  }
}
