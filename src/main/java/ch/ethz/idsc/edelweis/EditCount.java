// code by jph
package ch.ethz.idsc.edelweis;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.git.FileLog;
import ch.ethz.idsc.edelweis.lang.ParserCode;
import ch.ethz.idsc.edelweis.lang.ParserJava;

public class EditCount {
  private final Map<String, Integer> map = new HashMap<>();

  public EditCount(BulkParser bulkParser) {
    for (ParserCode parserCode : bulkParser.codes()) {
      if (parserCode instanceof ParserJava) {
        ParserJava parserJava = (ParserJava) parserCode;
        FileLog fileLog = FileLog.getDetailed(parserJava.file());
        map.put(parserJava.identifier(), fileLog.getTotalEdits());
        // System.out.println(fileLog.getTotalEdits() + " " + parserJava.identifier());
      }
    }
  }

  public Stream<String> listing() {
    return map.entrySet().stream() //
        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) //
        .map(entry -> String.format("%5d %s", entry.getValue(), entry.getKey()));
  }
}
