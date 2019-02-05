// code by jph
package ch.ethz.idsc.edelweis.util;

import java.io.InputStream;
import java.util.List;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Run.html">Run</a> */
public enum Run {
  ;
  /** @param processBuilder
   * @return
   * @throws Exception */
  public static List<String> of(ProcessBuilder processBuilder) throws Exception {
    Process process = processBuilder.start();
    try (InputStream inputStream = process.getInputStream()) {
      return ReadLines.of(inputStream);
    } finally {
      process.waitFor();
    }
  }
}
