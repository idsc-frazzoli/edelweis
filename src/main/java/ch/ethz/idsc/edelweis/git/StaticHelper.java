// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.InputStream;

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
}
