// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.Properties;

import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.UserName;

class UserProperties {
  private final String user;

  public UserProperties(String user) {
    this.user = user;
  }

  public UserProperties() {
    this(UserName.get());
  }

  public File file(String name) {
    File directory = new File("get/user", user);
    return new File(directory, name + ".properties");
  }

  public Properties load(String name) {
    File file = file(name);
    try {
      return Import.properties(file);
    } catch (Exception exception) {
      System.err.println("not found: " + file);
    }
    return new Properties();
  }
}
