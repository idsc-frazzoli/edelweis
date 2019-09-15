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

  static boolean lineHasDefinition(String string) {
    boolean value = false;
    value |= string.startsWith("class ");
    value |= string.startsWith("enum ");
    value |= string.startsWith("interface ");
    value |= string.startsWith("/* package */ class ");
    value |= string.startsWith("/* package */ enum ");
    value |= string.startsWith("/* package */ interface ");
    return value;
  }
}
