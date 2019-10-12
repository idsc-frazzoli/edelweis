// code by jph
package ch.ethz.idsc.edelweis.mvn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.ethz.idsc.edelweis.util.ReadLines;

public class JavaFile {
  private static final String PACKAGE = "package ";
  private static final String IMPORT = "import ";
  // ---
  private final File file;
  private final boolean main;
  private final List<String> lines;

  public JavaFile(File file, boolean main) throws FileNotFoundException, IOException {
    this.file = file;
    this.main = main;
    lines = ReadLines.of(file);
  }

  /** @return package name or null */
  public String getPackage() {
    Optional<String> optional = lines.stream() //
        .map(String::trim) //
        .filter(string -> string.startsWith(PACKAGE)) //
        .findFirst();
    if (optional.isPresent()) {
      String string = optional.get();
      return string.substring(8, string.length() - 1);
    }
    return null;
  }

  public int count(Predicate<String> predicate) {
    return (int) lines.stream().filter(predicate).count();
  }

  public List<String> imports() {
    return lines.stream() //
        .filter(string -> string.startsWith(IMPORT)) //
        .map(String::trim) //
        .map(string -> string.substring(7, string.length() - 1)) //
        .collect(Collectors.toList());
  }

  public File getFile() {
    return file;
  }

  public boolean isMain() {
    return main;
  }
}
