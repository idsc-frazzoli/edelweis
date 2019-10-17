// code by jph
package ch.ethz.idsc.edelweis.util;

import java.util.Comparator;
import java.util.Map;

public enum MapComparator {
  ;
  /** @param <T>
   * @param imports
   * @return */
  public static <T> Comparator<T> increasing(Map<T, Long> imports) {
    return (key1, key2) -> Long.compare(imports.get(key1), imports.get(key2));
  }

  /** @param <T>
   * @param imports
   * @return */
  public static <T> Comparator<T> decreasing(Map<T, Long> imports) {
    return (key1, key2) -> Long.compare(imports.get(key2), imports.get(key1));
  }
}
