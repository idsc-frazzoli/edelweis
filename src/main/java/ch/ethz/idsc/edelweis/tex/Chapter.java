// code by jph
package ch.ethz.idsc.edelweis.tex;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.Session;
import ch.ethz.idsc.edelweis.lang.ParserCode;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.util.WriteLines;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.red.Total;

public class Chapter {
  StringBuilder stringBuilder = new StringBuilder();

  public Chapter(BulkParser bulkParser) {
    List<String> list = new LinkedList<>();
    for (ParserCode parserCode : bulkParser.codes()) {
      if (parserCode instanceof ParserJava) {
        ParserJava parserJava = (ParserJava) parserCode;
        parserJava.lines().map(Chapter::format).forEach(list::add);
        list.add("");
      }
    }
    try {
      WriteLines.of(HomeDirectory.Documents("latex", bulkParser.name() + ".tex"), list);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String putSpaceBefCaps(String string) {
    return string.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
  }

  public static String format(String line) {
    line = putSpaceBefCaps(line);
    line = line.toLowerCase();
    line = line.replace("\\", " ");
    // line = line.replace("_", "\\_");
    line = line.replace("_", " ");
    line = line.replace("&", "\\&");
    line = line.replace("$", "\\$");
    // ---
    line = line.replace("||", "$\\Vert$");
    line = line.replace("{", "\\{");
    line = line.replace("}", "\\}");
    line = line.replace("#", "\\#");
    line = line.replace("<=", "$\\leq$");
    line = line.replace(">=", "$\\geq$");
    line = line.replace("->", "$\\rightarrow$");
    line = line.replace("<-", "$\\leftarrow$");
    line = line.replace("<", "\\textless{}");
    line = line.replace(">", "\\textgreater{}");
    line = line.replace("^", " ");
    line = line.replace(".", " ");
    return line;
  }

  public static void main(String[] args) {
    args = new String[] { "datahaki" };
    Session session = new Session(0 < args.length ? args[0] : UserName.get());
    // final File export = session.exportFolder();
    session.build();
    List<String> list = Arrays.asList( //
        "tensor", "tensor-test", "sophus", "sophus-test", //
        "subare", "subare-test", "owl", "owl-test", //
        "retina", "retina-test", "gokart", "gokart-test");
    list.stream().map(session::bulkParser).forEach(Chapter::new);
    int files = list.stream() //
        .map(session::bulkParser) //
        .mapToInt(bp -> bp.codes().size()) //
        .sum();
    int lines = list.stream() //
        .map(session::bulkParser) //
        .mapToInt(bp -> Total.ofVector(bp.allLineCounts()).number().intValue()) //
        .sum();
    System.out.println(files + " " + lines);
  }
}
