// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.lang.ParserJava;

public class DependencyExtern {
  public DependencyExtern(List<BulkParser> bulkParsers) {
    for (BulkParser bulkParser : bulkParsers) {
      Map<String, Integer> map = new HashMap<>();
      bulkParser.codes().stream() //
          .filter(ParserJava.class::isInstance) //
          .map(ParserJava.class::cast) //
          .map(ParserJava::identifier) //
          .forEach(identifier -> map.put(identifier, 0));
      if (!map.isEmpty()) {
        System.out.println("---\n" + bulkParser.root());
        bulkParsers.stream() //
            .filter(bp -> !bp.equals(bulkParser)) //
            .filter(BulkParser::nonTest) //
            .map(BulkParser::codes) //
            .flatMap(List::stream) //
            .filter(ParserJava.class::isInstance) //
            .map(ParserJava.class::cast) //
            .map(ParserJava::imports) //
            .flatMap(Set::stream) //
            .filter(map::containsKey) //
            .forEach(_import -> map.put(_import, map.get(_import) + 1));
        Map<ParserJava, Integer> here = new HashMap<>();
        bulkParser.codes().stream() //
            .filter(ParserJava.class::isInstance) //
            .map(ParserJava.class::cast) //
            .filter(pj -> 0 < map.get(pj.identifier())) //
            .forEach(pj -> here.put(pj, map.get(pj.identifier())));
        List<ParserJava> list = here.entrySet().stream() //
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) //
            .map(Entry::getKey) //
            .collect(Collectors.toList());
        list.stream() //
            .map(ParserJava::identifier) //
            .forEach(s -> System.out.println(String.format("%5d %s", map.get(s), s)));
      }
    }
  }
}
