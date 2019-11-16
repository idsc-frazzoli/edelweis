// code by jph
package ch.ethz.idsc.edelweis.mvn;

import java.util.function.Predicate;

import ch.ethz.idsc.edelweis.lang.ParserJava;

public enum JavaPredicates {
  ;
  /** lines of effective source code */
  public static final Predicate<String> CODE = _string -> {
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
  /** documentation (not comments in code) */
  public static final Predicate<String> DOCUMENTATION = _string -> {
    final String string = _string.trim();
    return string.startsWith("/** ") //
        || string.startsWith("* "); //
  };
  public static final Predicate<String> COMMENT = _string -> {
    final String string = _string.trim();
    return string.startsWith("/*") //
        || string.startsWith("* ") //
        || string.startsWith("//"); //
  };
  /** future tasks marked in the code as unfinished */
  public static final Predicate<String> UNFINISHED = string -> {
    return string.contains("TODO") //
        || string.contains("XXX") //
        || string.contains("LONGTERM") //
        || string.contains("EXPERIMENTAL") //
        || string.contains("FIXME");
  };
  public static final Predicate<String> RELEVANT_TEX = _string -> {
    final String string = _string.trim();
    return !string.isEmpty() //
        && !string.equals(";") //
        && !string.startsWith(ParserJava.IMPORT) //
        && !string.startsWith(ParserJava.PACKAGE) //
        && !string.startsWith("@Override") //
        && !string.startsWith("//") //
        && !string.startsWith("/**") // does not apply to /* package */
        && !string.startsWith("*");
  };
}
