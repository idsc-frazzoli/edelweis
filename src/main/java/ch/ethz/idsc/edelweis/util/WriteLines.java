// code by jph
package ch.ethz.idsc.edelweis.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Stream;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/WriteLine.html">WriteLine</a> */
public enum WriteLines {
  ;
  // the use of BufferedOutputStream is motivated by
  // http://www.oracle.com/technetwork/articles/javase/perftuning-137844.html
  private static void lines(Stream<String> stream, OutputStream outputStream) {
    try (PrintWriter printWriter = new PrintWriter(new BufferedOutputStream(outputStream))) {
      stream.sequential().forEach(printWriter::println);
    }
  }

  public static void of(File file, List<String> list) throws FileNotFoundException, IOException {
    try (OutputStream outputStream = new FileOutputStream(file)) {
      lines(list.stream(), outputStream);
    }
  }
}
