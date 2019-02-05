// code by jph
package ch.ethz.idsc.edelweis.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Run.html">Run</a> */
public enum Run {
  ;
  /** @param processBuilder
   * @return list of lines output by the process
   * @throws IOException
   * @throws InterruptedException */
  public static List<String> of(ProcessBuilder processBuilder) throws IOException, InterruptedException {
    Process process = processBuilder.start();
    try (InputStream inputStream = process.getInputStream()) {
      return ReadLines.of(inputStream);
    } finally {
      process.waitFor();
    }
  }
}
