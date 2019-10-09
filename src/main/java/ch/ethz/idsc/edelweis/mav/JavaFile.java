// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import ch.ethz.idsc.edelweis.util.ReadLines;

class JavaFile {
  private static final String PACKAGE = "package ";
  // ---
  private final File file;
  private final boolean main;
  private final List<String> lines;

  public JavaFile(File file, boolean main) throws FileNotFoundException, IOException {
    this.file = file;
    this.main = main;
    lines = ReadLines.of(file);
  }

  public String getPackage() {
    Optional<String> optional = lines.stream() //
        .map(String::trim) //
        .filter(string -> string.startsWith(PACKAGE)) //
        .findFirst();
    if (optional.isPresent()) {
      String string = optional.get();
      return string.substring(8, string.length() - 1);
    }
    System.err.println(file);
    return null;
  }

  public int count(Predicate<String> predicate) {
    // lines.stream().filter(predicate).forEach(System.out::println);
    return (int) lines.stream().filter(predicate).count();
  }

  public File getFile() {
    return file;
  }

  public boolean isMain() {
    return main;
  }
}
