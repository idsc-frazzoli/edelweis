// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.util.function.Predicate;

import ch.ethz.idsc.edelweis.lang.ParserJava;

public enum JavaPredicates {
  ;
  public static final Predicate<String> RELEVANT_CODE = _string -> {
    final String string = _string.trim();
    return !string.isEmpty() //
        && !string.equals(";") //
        && !string.startsWith(ParserJava.IMPORT) //
        && !string.startsWith(ParserJava.PACKAGE) //
        && !string.startsWith("@Override") //
        && !string.startsWith("//") //
        && !string.startsWith("/**") // does not apply to /* package */
        && !string.startsWith("*") //
        && !string.startsWith("{") //
        && !string.startsWith("}"); // ignores "} else ... "
  };
}
