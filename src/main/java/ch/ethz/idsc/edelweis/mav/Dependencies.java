// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/* package */ class Dependencies {
  public static void main(String[] args) throws FileNotFoundException, IOException {
    ProjectStructure projectStructure = ProjectDatahaki.GOKART;
    projectStructure.repos().forEach(RepoStatus::print);
    MavenCrossing mavenCrossing = new MavenCrossing(projectStructure.projects(), projectStructure.repos());
    Map<String, Long> collect = mavenCrossing.files("ch.ethz.idsc.tensor").stream() //
        .filter(JavaFile::isMain) //
        .map(JavaFile::imports) //
        .flatMap(List::stream) //
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    // System.out.println(collect);
    List<String> list = collect.keySet().stream() //
        .sorted((s1, s2) -> Long.compare(collect.get(s2), collect.get(s1))) //
        .collect(Collectors.toList());
    ProjectWrap projectWrap = new ProjectWrap(projectStructure.projects());
    for (String classId : list.subList(0, 50)) {
      Optional<String> optional = projectWrap.identify(classId);
      if (optional.isPresent())
        System.out.println(String.format("%5d %s", collect.get(classId), classId));
    }
  }
}
