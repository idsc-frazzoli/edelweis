package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**/ enum RunMavenIndex {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    List<String> projects = new LinkedList<>();
    projects.add("ch.ethz.idsc.tensor");
    projects.add("ch.ethz.idsc.subare");
    projects.add("ch.ethz.idsc.owl");
    projects.add("ch.ethz.idsc.sophus");
    projects.add("ch.ethz.idsc.retina");
    projects.add("ch.ethz.idsc.gokart");
    projects.add("ch.ethz.idsc.demo");
    // ---
    Map<String, List<JavaFile>> map = new HashMap<>();
    projects.stream().forEach(project -> map.put(project, new LinkedList<>()));
    // ---
    List<File> repos = new LinkedList<>();
    repos.add(new File("/home/datahaki/Projects/tensor"));
    repos.add(new File("/home/datahaki/Projects/subare"));
    repos.add(new File("/home/datahaki/Projects/owl"));
    repos.add(new File("/home/datahaki/Projects/retina"));
    // ---
    for (File repo : repos) {
      MavenPackageIndex mavenPackageIndex = MavenPackageIndex.of(repo);
      for (JavaFile javaFile : mavenPackageIndex.javaFiles()) {
        String package_name = javaFile.getPackage();
        Optional<String> optional = projects.stream() //
            .filter(package_name::startsWith) //
            .findFirst();
        if (optional.isPresent()) {
          String project = optional.get();
          map.get(project).add(javaFile);
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
