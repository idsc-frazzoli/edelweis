// code by jph
package ch.ethz.idsc.edelweis.prc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.lang.ParserJava;

public enum CommentCount {
  ;
  public static Stream<String> of(BulkParser bulkParser) {
    // compute number of comments per java file
    Map<ParserJava, Long> result = bulkParser.codes().stream() //
        .filter(ParserJava.class::isInstance) //
        .map(ParserJava.class::cast) //
        .filter(ParserJava::isPublic) //
        .collect(Collectors.toMap(pj -> pj, pj -> pj.comments().count()));
    // return Strings composed of number of comment lines and fileTitle
    List<ParserJava> pairs = new ArrayList<>(result.keySet());
    Collections.sort(pairs, new Comparator<ParserJava>() {
      @Override
      public int compare(ParserJava o1, ParserJava o2) {
        return Long.compare(result.get(o1), result.get(o2));
      }
    });
    return pairs.stream().map(pair -> String.format("%5d %s", result.get(pair), pair.fileTitle()));
  }
}
