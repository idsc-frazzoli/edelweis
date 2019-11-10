// code by jph
package ch.ethz.idsc.edelweis;

import java.util.Comparator;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.lang.ParserJava;

public enum ImportsLister {
  ;
  private static final Comparator<ParserJava> COMPARATOR = new Comparator<ParserJava>() {
    @Override
    public int compare(ParserJava o1, ParserJava o2) {
      return Integer.compare(o2.imports().size(), o1.imports().size()); // largest first
    }
  };

  public static Stream<String> html(BulkParser bulkParser) {
    return bulkParser.codes().stream() //
        .filter(parserCode -> parserCode instanceof ParserJava) //
        .map(ParserJava.class::cast) //
        .sorted(COMPARATOR) //
        .map(parserJava -> String.format("%5d %s", //
            parserJava.imports().size(), parserJava.identifier().get()));
  }
}
