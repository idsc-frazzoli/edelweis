// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.InputStream;
import java.util.List;

import ch.ethz.idsc.edelweis.util.ReadLines;

/* package */ enum StaticHelper {
  ;
  static String static_process(ProcessBuilder processBuilder) throws Exception {
    Process process = processBuilder.start();
    process.waitFor();
    try (InputStream inputStream = process.getInputStream()) {
      byte[] data = new byte[inputStream.available()];
      inputStream.read(data);
      return new String(data);
    }
  }

  static List<String> static_process_lines(ProcessBuilder processBuilder) throws Exception {
    Process process = processBuilder.start();
    process.waitFor();
    try (InputStream inputStream = process.getInputStream()) {
      return ReadLines.of(inputStream);
    }
  }
}
