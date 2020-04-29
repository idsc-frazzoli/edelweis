// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.lang.ParserJava;

/** This class is responsible to evalue the {@link BulkParser}
 * and create the information used in the tab "Dependencies" in the report. */
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

  /** @return {@link Map} with class names and number of detected dependencies
   * sorted with the highest number first. */
  public Map<String, Long> getAllFrequencySorted() {
    LinkedHashMap<String, Long> sorted = new LinkedHashMap<>();
    collect.entrySet().stream()//
        .sorted(FrequencySorter.INSTANCE)//
        .forEach(e -> sorted.put(e.getKey(), e.getValue()));
    return sorted;
  }
}
