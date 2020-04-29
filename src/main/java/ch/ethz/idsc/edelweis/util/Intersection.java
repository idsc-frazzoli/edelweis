// code by jph
package ch.ethz.idsc.edelweis.util;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Intersection.html">Intersection</a> */
public enum Intersection {
  ;
  /** @param a
   * @param b
   * @return */
  public static <T> Set<T> of(Collection<T> a, Collection<T> b) {
    return (a.size() < b.size() //
        ? a.stream().filter(b::contains) : b.stream().filter(a::contains)).collect(Collectors.toSet());
  }
}
