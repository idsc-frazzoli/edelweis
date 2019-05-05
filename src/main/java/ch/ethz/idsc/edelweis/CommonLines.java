// code by jph
package ch.ethz.idsc.edelweis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.util.Sets;

public class CommonLines {
  private final Map<ParserJava, Set<String>> map;

  public CommonLines(Collection<BulkParser> bulkParsers) {
    map = bulkParsers.stream() //
        .filter(BulkParser::nonTest) //
        .map(BulkParser::codes) //
        .flatMap(List::stream) //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .collect(Collectors.toMap(j -> j, j -> j.lines().map(String::trim).collect(Collectors.toSet())));
  }

  public Stream<String> matrix() {
    List<ParserJava> list = new ArrayList<>(map.keySet());
    Map<String, Integer> result = new HashMap<>();
    for (int c0 = 0; c0 < list.size() - 1; ++c0)
      for (int c1 = c0 + 1; c1 < list.size(); ++c1) {
        Set<String> set = Sets.intersect(map.get(list.get(c0)), map.get(list.get(c1)));
        if (2 < set.size())
          result.put(list.get(c0).fileTitle() + " " + list.get(c1).fileTitle(), set.size());
      }
    List<String> list2 = new ArrayList<>(result.keySet());
    Collections.sort(list2, new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return -Integer.compare(result.get(o1), result.get(o2));
      }
    });
    // for (String line : list2.subList(0, 200))
    // System.out.println(String.format("%5d %s", result.get(line), line));
    return list2.stream().limit(400).map(line -> String.format("%5d %s", result.get(line), line));
  }
}