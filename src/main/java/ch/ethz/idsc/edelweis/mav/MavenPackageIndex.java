// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** whereas edelweis treats projects of ANY language
 * mavenPackageIndex specializes on maven projects */
/* package */ class MavenPackageIndex {
  /** @param file for instance "/home/datahaki/Projects/retina"
   * @return
   * @throws FileNotFoundException
   * @throws IOException */
  public static MavenPackageIndex of(File file) throws FileNotFoundException, IOException {
    MavenPackageIndex mavenPackageIndex = new MavenPackageIndex();
    mavenPackageIndex.visit(new File(file, "src/main"), true);
    mavenPackageIndex.visit(new File(file, "src/test"), false);
    return mavenPackageIndex;
  }

  // ---
  private final List<JavaFile> javaFiles = new LinkedList<>();

  private void visit(File directory, boolean isMain) throws FileNotFoundException, IOException {
    if (!directory.isDirectory()) {
      System.err.println("miss: " + directory);
      return;
    }
    List<File> collect = Stream.of(directory.listFiles()) //
        .sorted() //
        .collect(Collectors.toList());
    for (final File file : collect) {
      if (file.isDirectory())
        visit(file, isMain);
      else //
      if (file.isFile() && //
          file.getName().endsWith(".java"))
        javaFiles.add(new JavaFile(file, isMain));
    }
  }

  public List<JavaFile> javaFiles() {
    return Collections.unmodifiableList(javaFiles);
  }
}
