// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/* package */ class CrossProject {
  public static void main(String[] args) throws FileNotFoundException, IOException {
    ProjectStructure projectStructure = ProjectDatahaki.GOKART;
    projectStructure.repos().forEach(RepoStatus::print);
    MavenCrossing mavenCrossing = new MavenCrossing(projectStructure.projects(), projectStructure.repos());
    ProjectWrap projectWrap = new ProjectWrap(projectStructure.projects());
    // ---
    Map<String, List<String>> map = new HashMap<>();
    for (String self : projectStructure.projects()) {
      System.out.println(self);
      Map<String, Long> collect = mavenCrossing.files(self).stream() //
          .filter(JavaFile::isMain) //
          .map(JavaFile::imports) //
          .flatMap(List::stream) //
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      List<String> list = collect.keySet().stream() //
          .sorted((s1, s2) -> Long.compare(collect.get(s2), collect.get(s1))) //
          .collect(Collectors.toList());
      List<String> dependencies = new LinkedList<>();
      for (String classId : list) {
        Optional<String> optional = projectWrap.identify(classId);
        if (optional.isPresent())
          dependencies.add(classId);
      }
      map.put(self, dependencies);
      Map<String, Long> map2 = dependencies.stream() //
          .map(projectWrap::identify) //
          .map(Optional::get) //
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      // System.out.println(map2);
      for (String project : projectStructure.projects()) {
        long count = map2.containsKey(project) ? map2.get(project) : 0;
        if (0 < count) { //
          String format = String.format("%7s %d", project.substring(project.lastIndexOf('.') + 1), count);
          if (self.equals(project) || //
              Dependencies.getProject(self).contains(project)) {
            System.out.println(format);
          } else
            System.err.println(format);
        }
      }
    }
  }
}
