// code by clruch
package ch.ethz.idsc.edelweis.lang;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** This class is used to sort TODOs and FIXMEs according to the developer that
 * is assigned to resolve them.
 * @author clruch */
/* package */ enum SortedTodos {
  ;
  // private static final Random random = new Random();
  static final String UNKNOWN_IDENT = "unknown developer";

  public static Map<String, Map<String, String>> of(ParserText parserText) {
    Map<String, Map<String, String>> sortedTodos = new HashMap<>();
    parserText.todosPrint().forEach(todoLine -> {
      String category = getDeveloper(todoLine);// Integer.toString(random.nextInt(4));
      // add map if not contained
      if (!sortedTodos.containsKey(category))
        sortedTodos.put(category, new LinkedHashMap<String, String>());
      sortedTodos.get(category).put(parserText.file().toString(), todoLine);
    });
    return sortedTodos;
  }

  /** @return name of the developer assigned to solve the issue mentioned
   * in the {@link String} @param todoLineIn, the convention is that it is the first
   * word after the '@' sign placed after the TODOs or FIXMEs identifier. */
  static String getDeveloper(String todoLineIn) {
    String todoLine = preConditionLine(todoLineIn);
    // no '@' --> no developer
    if (!todoLine.contains("@"))
      return UNKNOWN_IDENT;
    // contains developer, extract, find position of identifier
    int identPos = todoLine.indexOf('@') + 1;
    // remove any spaces before idetifier
    while (todoLine.substring(identPos, identPos + 1).equals(" ") && identPos < todoLine.length()) {
      ++identPos;
    }
    // first word after identifier is developer
    String restOfLine = todoLine.substring(identPos, todoLine.length());
    String developer = restOfLine.split(" ")[0];
    return developer.replace(",", "");
  }

  private static String preConditionLine(String todoLineIn) {
    String todoLine = todoLineIn.replace("{@link", "_");
    todoLine = todoLine.replace("@Deprecated", "_");
    // remove anything before the Todo identifier
    if (todoLine.contains("TODO"))
      return todoLine.substring(todoLine.indexOf("TODO") + 4, todoLine.length());
    if (todoLine.contains("FIXME"))
      return todoLine.substring(todoLine.indexOf("FIXME") + 4, todoLine.length());
    return todoLine;
  }
}