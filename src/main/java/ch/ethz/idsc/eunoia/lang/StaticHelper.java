// code by jph
package ch.ethz.idsc.eunoia.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

enum StaticHelper {
  ;
  static List<String> lines(File file) {
    try (InputStream inputStream = new FileInputStream(file)) {
      return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }
}
