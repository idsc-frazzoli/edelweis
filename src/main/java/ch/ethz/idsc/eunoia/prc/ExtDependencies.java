// code by jph
package ch.ethz.idsc.eunoia.prc;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.idsc.eunoia.BulkParser;
import ch.ethz.idsc.eunoia.lang.ParserJava;

public class ExtDependencies {
  public static boolean isRelevant(String string) {
    return !string.startsWith("java.") //
        && !string.startsWith("javax.");
  }

  private final Map<String, Long> collect;

  public ExtDependencies(BulkParser bulkParser) {
    // FIXME not generic!!!
    String st = "ch.ethz.idsc." + bulkParser.name();
    collect = bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .map(ParserJava::imports) //
        .flatMap(Set::stream) //
        .filter(ExtDependencies::isRelevant) //
        .filter(s -> !s.startsWith(st)) //
        .collect(Collectors.groupingBy(Function.identity(), () -> new TreeMap<>(), Collectors.counting()));
  }

  public Map<String, Long> getAll() {
    return Collections.unmodifiableMap(collect);
  }
}
