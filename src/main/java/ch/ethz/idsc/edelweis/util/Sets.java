// code by jph
package ch.ethz.idsc.edelweis.util;

import java.util.Set;
import java.util.stream.Collectors;

public enum Sets {
  ;
  public static <T> Set<T> intersect(Set<T> a, Set<T> b) {
    if (a.size() < b.size())
      return a.stream().filter(b::contains).collect(Collectors.toSet());
    return b.stream().filter(a::contains).collect(Collectors.toSet());
  }
}
