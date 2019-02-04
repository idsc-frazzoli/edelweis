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
  final int count;

  public ParserC(File file) {
    super(file);
    count = (int) ReadLines.of(file).stream().filter(RELEVANT).count();
  }

  @Override // from ParserCode
  public int lineCount() {
    return count;
  }
}
