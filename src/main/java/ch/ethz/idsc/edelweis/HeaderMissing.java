// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.edelweis.lang.ParserCode;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.subare.util.HtmlUtf8;

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

  public void writeHtml(File dir) {
    if (!list.isEmpty())
      try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "headermiss.htm"))) {
        htmlUtf8.appendln("<h3>Missing Headers</h3>");
        htmlUtf8.appendln("<pre>");
        list.stream() //
            .map(ParserJava::identifier) //
            .filter(Optional::isPresent) //
            .map(Optional::get) //
            .forEach(htmlUtf8::appendln);
        htmlUtf8.appendln("</pre>");
      }
  }
}
