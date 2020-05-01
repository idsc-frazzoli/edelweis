package ch.ethz.idsc.edelweis.lang;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

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

  static String getDeveloper(String todoLine) {
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
    return developer;
  }
}