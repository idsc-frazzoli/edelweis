// code by jph
package ch.ethz.idsc.edelweis;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.edelweis.lang.ParserCode;
import ch.ethz.idsc.edelweis.lang.ParserJava;

public class HeaderMissing {
  public final List<ParserJava> list = new LinkedList<>();

  public HeaderMissing(BulkParser bulkParser) {
    for (ParserCode parserCode : bulkParser.codes())
      if (parserCode instanceof ParserJava) {
        ParserJava parserJava = (ParserJava) parserCode;
        if (!parserJava.hasHeader())
          list.add(parserJava);
      }
  }
}
