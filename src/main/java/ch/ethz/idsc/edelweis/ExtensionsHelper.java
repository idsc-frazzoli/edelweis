// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import ch.ethz.idsc.tensor.io.Import;

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
