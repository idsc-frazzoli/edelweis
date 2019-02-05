// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.lang.ParserJava;

public enum NoIdentifier {
  ;
  public static Stream<String> of(BulkParser bulkParser) {
    return bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .filter(pj -> !pj.identifier().isPresent()) //
        .map(pj -> pj.file().toString());
  }
}
