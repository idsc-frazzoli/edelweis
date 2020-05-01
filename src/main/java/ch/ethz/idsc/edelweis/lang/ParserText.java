// code by jph
package ch.ethz.idsc.edelweis.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.mvn.JavaPredicates;
import ch.ethz.idsc.edelweis.util.ReadLines;

public class ParserText extends ParserBase {
  private List<String> todos = new ArrayList<>();
  private Map<String, List<String>> sortedTodos = new HashMap<>();

  public ParserText(File file) {
    super(file);
    try {
      todos = ReadLines.of(file).stream().filter(JavaPredicates.UNFINISHED).collect(Collectors.toList());
      sortedTodos = SortedTodos.of(todos);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public int lineCount() {
    throw new RuntimeException();
  }

  public List<String> todoLines() {
    return Collections.unmodifiableList(todos);
  }

  public Stream<String> todosPrint() {
    return todos.stream() //
        .map(String::trim);
  }

  public Map<String, List<String>> sortedTodos() {
    return Collections.unmodifiableMap(sortedTodos);
  }
  // -- deprecated old functions below... --------------------------------------------------------

  @Deprecated // clruch checked if this is used often and found few instances that
  // did not make a lot of sense, e.g., people using --> to indicate something was
  // removed, so clruch temporarily removed usage of this function.
  // TODO @datahaki confirm and delete or change back, todosNoXml was only used
  // in Edelweis line 227 once.
  public Stream<String> todosNoXml() {
    return todos.stream() //
        .map(String::trim) //
        .map(ParserText::removeXml);
  }

  @Deprecated // see comment above
  private static String removeXml(String string) {
    String before = string;
    String after = string.replace("<!--", "").replace("-->", "");
    if (!before.equals(after)) {
      System.out.println("before: ");
      System.out.println(before);
      System.out.println("after: ");
      System.out.println(after);
    }
    return string.replace("<!--", "").replace("-->", "");
  }
}
