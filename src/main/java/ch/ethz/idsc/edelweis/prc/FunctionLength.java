// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.io.File;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.util.LeadingWhitespace;

public class FunctionLength implements Comparable<FunctionLength> {
  private final File file;
  private final int max;

  public FunctionLength(ParserJava parserJava) {
    file = parserJava.file();
    List<Integer> list = parserJava.lines().mapToInt(LeadingWhitespace::of) //
        .distinct() //
        .sorted() //
        .boxed() //
        .collect(Collectors.toList());
    if (1 < list.size()) {
      final int mark = list.get(1);
      int block = 0;
      NavigableSet<Integer> navigableSet = new TreeSet<>();
      navigableSet.add(0);
      for (String line : parserJava.lines().collect(Collectors.toList())) {
        int wht = LeadingWhitespace.of(line);
        if (wht <= mark) {
          navigableSet.add(block);
          block = 0;
        } else {
          ++block;
        }
      }
      max = navigableSet.last();
    } else
      max = 0;
  }

  @Override
  public int compareTo(FunctionLength functionDepth) {
    return -Integer.compare(max, functionDepth.max);
  }

  @Override
  public String toString() {
    return String.format("%5d %s", max, file.getName());
  }
}
