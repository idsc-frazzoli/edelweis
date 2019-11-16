// code by jph
package ch.ethz.idsc.edelweis.mvn;

public enum Imports {
  ;
  public static String getPackage(String className) {
    int index = className.lastIndexOf('.');
    return className.substring(0, index);
  }
}
