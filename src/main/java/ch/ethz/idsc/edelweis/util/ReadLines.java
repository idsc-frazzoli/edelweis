// code by jph
package ch.ethz.idsc.edelweis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ReadLine.html">ReadLine</a> */
public enum ReadLines {
  ;
  public static List<String> of(File file) {
    try (InputStream inputStream = new FileInputStream(file)) {
      return lines(inputStream);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  public static List<String> lines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());
  }
}
