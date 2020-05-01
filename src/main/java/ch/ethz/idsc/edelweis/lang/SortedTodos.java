package ch.ethz.idsc.edelweis.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* package */ enum SortedTodos {
  ;
  public static Map<String, List<String>> of(List<String> todos) {
    Map<String, List<String>> map = new HashMap<>();
    for (int i = 0; i < todos.size(); ++i) {
      map.put(Integer.toString(i), Arrays.asList(todos.get(i)));
    }
    return map;
  }
}
