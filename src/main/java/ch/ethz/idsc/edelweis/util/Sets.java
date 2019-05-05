// code by jph
package ch.ethz.idsc.edelweis.util;

import java.util.Set;
import java.util.stream.Collectors;

public enum Sets {
  ;
  public static <T> Set<T> intersect(Set<T> a, Set<T> b) {
    return (a.size() < b.size() //
        ? a.stream().filter(b::contains)
        : b.stream().filter(a::contains)).collect(Collectors.toSet());
  }
}
