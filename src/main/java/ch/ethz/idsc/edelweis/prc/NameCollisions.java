// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.lang.ParserJava;

public class NameCollisions {
  /** import count */
  private final Map<String, List<ParserJava>> map;

  public NameCollisions(Collection<BulkParser> bulkParsers) {
    map = bulkParsers.stream() //
        .filter(BulkParser::nonTest) //
        .map(BulkParser::codes) //
        .flatMap(List::stream) //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .filter(ParserJava::isPublic) //
        .collect(Collectors.groupingBy(ParserJava::fileName));
  }

  public Stream<String> duplicates(BulkParser bulkParser) {
    return bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .filter(ParserJava::isPublic) //
        .map(ParserJava::fileName) //
        .filter(map::containsKey) //
        .filter(fileName -> 1 < map.get(fileName).size()) //
        .distinct() //
        .sorted();
  }
}
