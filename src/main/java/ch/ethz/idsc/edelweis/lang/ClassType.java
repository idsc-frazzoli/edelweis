// code by jph
package ch.ethz.idsc.edelweis.lang;

import java.util.function.Predicate;
import java.util.stream.Stream;

public enum ClassType {
  INTERFACE, //
  CLASS, //
  ENUM, //
  UNKNOWN, //
  ;
  // ---
  private final String lowercaseId = name().toLowerCase() + " ";

  static Predicate<String> definition(String name) {
    return string -> string.contains("class " + name) //
        || string.contains("enum " + name) //
        || string.contains("interface " + name);
  }

  public static ClassType in(String string) {
    return Stream.of(values()).filter(ct -> string.contains(ct.lowercaseId)).findFirst().get();
  }
}
