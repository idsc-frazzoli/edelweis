// code by jph
package ch.ethz.idsc.edelweis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ReadLine.html">ReadLine</a> */
public enum ReadLines {
  ;
  private static Stream<String> stream(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream)).lines();
  }

  /** @param inputStream
   * @return */
  public static List<String> of(InputStream inputStream) {
    return stream(inputStream).collect(Collectors.toList());
  }

  /** @param file
   * @return
   * @throws FileNotFoundException
   * @throws IOException */
  public static List<String> of(File file) throws FileNotFoundException, IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      return of(inputStream);
    }
  }

  public static Stream<String> stream(File file) throws FileNotFoundException, IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      return of(inputStream).stream();
    }
  }
}
