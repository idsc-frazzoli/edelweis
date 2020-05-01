package ch.ethz.idsc.edelweis.lang;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/* package */ enum SortedTodos {
  ;
  private static final Random random = new Random();
  static final String UNKNOWN_IDENT = "unknown developer";

  public static Map<String, Map<String, String>> of(ParserText parserText) {
    Map<String, Map<String, String>> sortedTodos = new HashMap<>();
    parserText.todosPrint().forEach(todoLine -> {
      String category = Integer.toString(random.nextInt(4));
      // add map if not contained
      if (!sortedTodos.containsKey(category))
        sortedTodos.put(category, new LinkedHashMap<String, String>());
      sortedTodos.get(category).put(parserText.file().toString(), todoLine);
    });
    return sortedTodos;
  }

  static String getDeveloper(String todoLine) {
    System.out.println("line in:" + todoLine);
    // no '@' --> no developer
    if (!todoLine.contains("@"))
      return UNKNOWN_IDENT;
    // contains developer, extract, find position of identifier    
    int identPos = todoLine.indexOf('@');

 
    
    int endPos = identPos;
    // advance to first non-space character
    while (!todoLine.substring(endPos, endPos + 1).equals(" ") && endPos < todoLine.length()) {
      System.out.println(todoLine.substring(endPos, endPos + 1));
      ++endPos;
    }
    // extract developer name
    String subString = todoLine.substring(identPos + 1, endPos);
    System.out.println(subString);
    return subString;
    // return todoLine;
  }


}
