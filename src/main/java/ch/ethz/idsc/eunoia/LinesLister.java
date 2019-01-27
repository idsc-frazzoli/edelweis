// code by jph
package ch.ethz.idsc.eunoia;

import java.util.Comparator;
import java.util.stream.Stream;

import ch.ethz.idsc.eunoia.lang.ParserCode;

public enum LinesLister {
  ;
  private static final Comparator<ParserCode> COMPARATOR = new Comparator<ParserCode>() {
    @Override
    public int compare(ParserCode o1, ParserCode o2) {
      return Integer.compare(o2.lineCount(), o1.lineCount()); // largest first
    }
  };

  public static Stream<String> of(BulkParser bulkParser) {
    return bulkParser.codes().stream() //
        .sorted(COMPARATOR) //
        .map(parserCode -> {
          String name = bulkParser.name(parserCode.file());
          return String.format("%5d %s", parserCode.lineCount(), name);
        });
  }
}
