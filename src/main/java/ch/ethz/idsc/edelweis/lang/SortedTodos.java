package ch.ethz.idsc.edelweis.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/* package */ enum SortedTodos {
  ;
  
  private static final Random random = new Random();
  
  public static Map<String, Map<String,String>> of(ParserText parserText) {
    Map<String, Map<String,String>> map = new HashMap<>();
    parserText.todosPrint().forEach(s->{
      String category = Integer.toString(random.nextInt(4));
      // add map if not contained
      if(!map.containsKey(category))
        map.put(category, new LinkedHashMap<String, String>());
      
      map.get(category).put(parserText.file().toString(),s);
      
      
    });
    return map;
//    
//    for (int i = 0; i < todos.size(); ++i) {
//      map.put(Integer.toString(i), Arrays.asList(todos.get(i)));
//    }
//    return map;
  }
}
