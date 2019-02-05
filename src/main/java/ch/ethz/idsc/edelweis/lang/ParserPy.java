// code by jph
package ch.ethz.idsc.edelweis.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.ethz.idsc.edelweis.util.ReadLines;

public class ParserPy extends ParserBase {
  private static final String QUOTES = "\"\"\"";
  private static final Predicate<String> RELEVANT = new Predicate<String>() {
    @Override
    public boolean test(String _string) {
      final String string = _string.trim();
      return !string.isEmpty() //
          && !string.startsWith("import ") //
          && !string.startsWith("from ") //
          && !string.startsWith("#") //
      ;
    }
  };
  private int count = 0;

  public ParserPy(File file) {
    super(file);
    List<String> list = new ArrayList<>();
    try {
      list = ReadLines.stream(file).filter(RELEVANT).collect(Collectors.toList());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    boolean isActive = true;
    for (String line : list) {
      isActive ^= line.startsWith(QUOTES);
      if (isActive)
        ++count;
    }
  }

  @Override // from ParserCode
  public int lineCount() {
    return count;
  }
}
