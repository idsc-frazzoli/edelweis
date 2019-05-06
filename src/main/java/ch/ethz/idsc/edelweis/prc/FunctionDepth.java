// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.io.File;

import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.util.LeadingWhitespace;

public class FunctionDepth implements Comparable<FunctionDepth> {
  private final File file;
  private final int max;

  public FunctionDepth(ParserJava parserJava) {
    file = parserJava.file();
    max = parserJava.lines().mapToInt(LeadingWhitespace::of).max().orElse(0);
  }

  @Override
  public int compareTo(FunctionDepth functionDepth) {
    return -Integer.compare(max, functionDepth.max);
  }

  @Override
  public String toString() {
    return String.format("%5d %s", max, file.getName());
  }
}
