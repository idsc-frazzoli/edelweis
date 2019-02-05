// code by jph
package ch.ethz.idsc.edelweis.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.util.ReadLines;

public class ParserText extends ParserBase {
  private static final Predicate<String> UNFINISHED = new Predicate<String>() {
    @Override
    public boolean test(String string) {
      return string.contains("TODO") //
          || string.contains("XXX") //
          || string.contains("LONGTERM") //
          || string.contains("EXPERIMENTAL") //
          || string.contains("FIXME");
    }
  };
  // ---
  private List<String> todos = new ArrayList<>();

  public ParserText(File file) {
    super(file);
    try {
      todos = ReadLines.stream(file).filter(UNFINISHED).collect(Collectors.toList());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public int lineCount() {
    throw new RuntimeException();
  }

  public List<String> todos() {
    return Collections.unmodifiableList(todos);
  }

  public Stream<String> todosNoXml() {
    return todos.stream() //
        .map(String::trim) //
        .map(ParserText::removeXml);
  }

  static String removeXml(String string) {
    return string.replace("<!--", "").replace("-->", "");
  }
}
