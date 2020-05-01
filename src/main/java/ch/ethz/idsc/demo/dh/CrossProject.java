// code by jph
package ch.ethz.idsc.demo.dh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.idsc.edelweis.mvn.JavaFile;
import ch.ethz.idsc.edelweis.mvn.MavenCrossing;
import ch.ethz.idsc.edelweis.mvn.MavenRepoStructure;
import ch.ethz.idsc.edelweis.mvn.ProjectWrap;
import ch.ethz.idsc.edelweis.util.MapComparator;

/* package */ class CrossProject {
  private final MavenCrossing mavenCrossing;
  private final ProjectWrap projectWrap;

  CrossProject(MavenRepoStructure mavenRepoStructure) throws FileNotFoundException, IOException {
    mavenCrossing = new MavenCrossing(mavenRepoStructure.projects(), mavenRepoStructure.repos());
    projectWrap = new ProjectWrap(mavenRepoStructure.projects());
    // ---
    Map<String, List<String>> map = new HashMap<>();
    for (String self : mavenRepoStructure.projects()) {
      System.out.println("=============");
      System.out.println(self);
      Map<String, Long> imports = mavenCrossing.files(self).stream() //
          // .filter(JavaFile::isMain) //
          .map(JavaFile::imports) //
          .flatMap(List::stream) //
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      List<String> list = imports.keySet().stream() //
          .sorted(MapComparator.decreasing(imports)) //
          .collect(Collectors.toList());
      for (String project : mavenRepoStructure.projects())
        if (!Dependencies.dependsOn(self, project)) {
          // System.err.println(project);
          for (String classId : list) {
            Optional<String> optional = projectWrap.identifyProject(classId);
            if (optional.isPresent() && //
                optional.get().equals(project)) {
              long count = imports.get(classId);
              // if (1 < count)
              System.out.println(String.format("%5d %s", count, classId));
            }
          }
        }
      List<String> dependencies = new LinkedList<>();
      for (String classId : list) {
        Optional<String> optional = projectWrap.identifyProject(classId);
        if (optional.isPresent())
          dependencies.add(classId);
      }
      map.put(self, dependencies);
      // ---
      Map<String, Long> map2 = dependencies.stream() //
          .map(projectWrap::identifyProject) //
          .map(Optional::get) //
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      // System.out.println(map2);
      for (String project : mavenRepoStructure.projects()) {
        long count = map2.containsKey(project) //
            ? map2.get(project)
            : 0;
        if (0 < count) { //
          String name = project.substring(project.lastIndexOf('.') + 1);
          String format = String.format("%7s %d", name, count);
          if (Dependencies.dependsOn(self, project))
            System.out.println(format);
          else
            System.err.println(format);
        }
      }
    }
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    MavenRepoStructure mavenRepoStructure = DatahakiProjects.GOKART;
    // mavenRepoStructure.repos().forEach(RepoStatus::print); // requires git status to be clean
    new CrossProject(mavenRepoStructure);
  }
}
