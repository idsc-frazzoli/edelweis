// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class DependencyGlobal {
  /** import count */
  private final Map<String, Integer> dependency = new HashMap<>();

  public DependencyGlobal(Collection<BulkParser> bulkParsers) {
    bulkParsers.stream() //
        .filter(BulkParser::nonTest) //
        .map(BulkParser::codes) //
        .flatMap(List::stream) //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .map(ParserJava::identifier) //
        .filter(Optional::isPresent) //
        .map(Optional::get) //
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

  public Stream<String> publicUnref(BulkParser bulkParser) {
    return bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .filter(ParserJava::isPublic) //
        .map(ParserJava::identifier) //
        .filter(Optional::isPresent) //
        .map(Optional::get) //
        .filter(dependency::containsKey) //
        .filter(identifier -> 0 == dependency.get(identifier));
  }

  private Stream<ParserJava> stream(BulkParser bulkParser) {
    Map<ParserJava, Integer> map = new HashMap<>();
    bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        // .filter(pj -> 0 < map.get(pj.identifier())) //
        // .filter(Optional::isPresent) //
        // .map(Optional::get) //
        .filter(pj -> pj.identifier().isPresent()) //
        .forEach(parserJava -> map.put(parserJava, dependency.get(parserJava.identifier().get())));
    return map.entrySet().stream() //
        .sorted(EntrySort.INSTANCE) //
        .map(Entry::getKey) //
        .filter(ParserJava::isPublic);
  }

  public Stream<String> print(BulkParser bulkParser) {
    return stream(bulkParser).map(ParserJava::identifier) //
        .filter(Optional::isPresent) //
        .map(Optional::get) //
        .map(string -> String.format("%5d %s", dependency.get(string), string));
  }

  public Tensor getRefs(BulkParser bulkParser) {
    return Tensor.of(stream(bulkParser).map(ParserJava::identifier) //
        .filter(Optional::isPresent) //
        .map(Optional::get) //
        .map(dependency::get).map(RealScalar::of));
  }
}
