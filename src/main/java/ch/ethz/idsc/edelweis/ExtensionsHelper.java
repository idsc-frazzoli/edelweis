// code by jph and clruch
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import ch.ethz.idsc.tensor.io.Import;

/** Put all file extensions (without the dot .) in the file
 * "ignore_extensions.properties" placed in the "get" directory, a typical file
 * could have the following lines to start with:
 * jar
 * bin */
enum ExtensionsHelper {
  ;
  static final Set<String> SET = new HashSet<>();
  static {
    try {
      Properties properties = Import.properties(new File("get", "ignore_extensions.properties"));
      for (String ext : properties.stringPropertyNames())
        SET.add(ext);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static boolean isIgnored(String extension) {
    return SET.contains(extension);
  }
}
