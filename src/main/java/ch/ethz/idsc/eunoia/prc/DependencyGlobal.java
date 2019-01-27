// code by jph
package ch.ethz.idsc.eunoia.prc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import ch.ethz.idsc.eunoia.BulkParser;
import ch.ethz.idsc.eunoia.lang.ParserJava;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class DependencyGlobal {
  /** import count */
  private final Map<String, Integer> dependency = new HashMap<>();

  public DependencyGlobal(List<BulkParser> bulkParsers) {
    bulkParsers.stream() //
        .filter(BulkParser::nonTest) //
        .map(BulkParser::codes) //
        .flatMap(List::stream) //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .map(ParserJava::identifier) //
        .forEach(identifier -> dependency.put(identifier, 0));
    // ---
    // global usage
    bulkParsers.stream() //
        .filter(BulkParser::nonTest) //
        .map(BulkParser::codes) //
        .flatMap(List::stream) //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .map(ParserJava::imports) //
        .flatMap(Set::stream) //
        .filter(dependency::containsKey) //
        .forEach(_import -> dependency.put(_import, dependency.get(_import) + 1));
  }

  public Stream<ParserJava> publicUnref(BulkParser bulkParser) {
    return bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .filter(ParserJava::isPublic) //
        .filter(pj -> 0 == dependency.get(pj.identifier())); //
  }

  private Stream<ParserJava> stream(BulkParser bulkParser) {
    Map<ParserJava, Integer> map = new HashMap<>();
    bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        // .filter(pj -> 0 < map.get(pj.identifier())) //
        .forEach(parserJava -> map.put(parserJava, dependency.get(parserJava.identifier())));
    return map.entrySet().stream() //
        .sorted(EntrySort.INSTANCE) //
        .map(Entry::getKey) //
        .filter(ParserJava::isPublic);
  }

  public Stream<String> print(BulkParser bulkParser) {
    return stream(bulkParser).map(ParserJava::identifier) //
        .map(string -> String.format("%5d %s", dependency.get(string), string));
  }

  public Tensor getRefs(BulkParser bulkParser) {
    return Tensor.of(stream(bulkParser).map(ParserJava::identifier) //
        .map(dependency::get).map(RealScalar::of));
  }
}
