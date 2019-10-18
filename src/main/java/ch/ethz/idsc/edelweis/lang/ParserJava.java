// code by jph
package ch.ethz.idsc.edelweis.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.mvn.JavaPredicates;
import ch.ethz.idsc.edelweis.util.Filename;
import ch.ethz.idsc.edelweis.util.ReadLines;

public class ParserJava extends ParserBase {
  public static final String PACKAGE = "package ";
  public static final String IMPORT = "import ";
  // ---
  private final Predicate<String> relevant;
  private final int count;
  private final String identifier;
  private final String fileTitle;
  private final Set<String> imports = new HashSet<>();
  private final ClassType classType;
  private final boolean isPublic;
  private final boolean isAbstract;
  private final boolean hasHeader;

  public ParserJava(File file, Predicate<String> relevant) {
    super(file);
    this.relevant = relevant;
    fileTitle = new Filename(file).title;
    List<String> lines = new ArrayList<>();
    try {
      lines = ReadLines.of(file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    // final List<String> lines = ReadLines.of(file);
    hasHeader = !lines.isEmpty() && JavaPredicates.COMMENT.test(lines.get(0));
    count = (int) lines.stream().filter(relevant).count();
    // if (file.getName().equals("CsvFormat.java")) {
    // lines.stream().filter(RELEVANT_JAVA).forEach(System.out::println);
    // }
    // ---
    String fname = file.getName();
    String name = fname.substring(0, fname.indexOf('.'));
    // ---
    {
      long defns = lines.stream() //
          .filter(ClassType::lineHasDefinition) //
          .count();
      if (1 < defns) {
        System.out.println(defns + " " + file);
      }
    }
    {
      Optional<String> optional = lines.stream() //
          .filter(ClassType.definition(name)) //
          .findFirst();
      classType = optional.map(ClassType::in).orElse(ClassType.UNKNOWN);
      if (optional.isPresent()) {
        String line = optional.get();
        isPublic = line.startsWith("public ");
        isAbstract = line.contains("abstract ");
      } else {
        isPublic = true;
        isAbstract = false;
      }
    }
    // build identifier, for instance ch.ethz.idsc.subare.core.DiscountFunction
    Optional<String> optional = lines.stream() //
        .filter(string -> string.startsWith(PACKAGE)) //
        .map(string -> string.substring(PACKAGE.length(), string.indexOf(';'))) //
        .findFirst();
    if (optional.isPresent()) {
      String string = optional.get();
      identifier = string + "." + name;
    } else {
      identifier = "UNKNOWN." + name;
    }
    // ---
    try {
      lines.stream() //
          .filter(string -> string.startsWith(IMPORT)) //
          .map(string -> string.substring(IMPORT.length(), string.indexOf(';'))) //
          .forEach(imports::add);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public Stream<String> lines() {
    try {
      return ReadLines.of(file()).stream().filter(relevant);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  public Stream<String> comments() {
    try {
      return ReadLines.of(file()).stream().filter(JavaPredicates.DOCUMENTATION);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  @Override // from ParserCode
  public int lineCount() {
    return count;
  }

  /** @return string of the form "ch.ethz.idsc.tensor.Tensor" */
  public Optional<String> identifier() {
    return Optional.ofNullable(identifier);
  }

  /** @return string of the form "Tensor" */
  public String fileTitle() {
    return fileTitle;
  }

  public ClassType classType() {
    return classType;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public boolean hasHeader() {
    return hasHeader;
  }

  public Set<String> imports() {
    return Collections.unmodifiableSet(imports);
  }
}
