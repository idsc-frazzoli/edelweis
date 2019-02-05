// code by jph
package ch.ethz.idsc.edelweis.lang;

import java.io.File;
import java.util.function.Predicate;

import ch.ethz.idsc.edelweis.util.ReadLines;

public class ParserC extends ParserBase {
  private static final Predicate<String> RELEVANT = new Predicate<String>() {
    @Override
    public boolean test(String _string) {
      final String string = _string.trim();
      return !string.isEmpty() //
          && !string.startsWith("#") //
          && !string.startsWith("//") //
          && !string.startsWith("/*") // does not apply to /* package */
          && !string.startsWith("*") //
          && !string.startsWith("using namespace ") //
      ;
    }
  };
  private int count;

  public ParserC(File file) {
    super(file);
    try {
      count = (int) ReadLines.stream(file).filter(RELEVANT).count();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override // from ParserCode
  public int lineCount() {
    return count;
  }
}
