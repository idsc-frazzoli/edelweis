// code by jph
package ch.ethz.idsc.edelweis.prj;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.edelweis.BulkParser;
import ch.ethz.idsc.edelweis.git.Git;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.TableBuilder;

public class ProjectHistory {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
  /** today's date */
  private static final Date DATE = new Date();
  /** 2 weeks interval */
  private static final int INTERVAL = 14;
  private static final int AGE_MIN = -728;
  // ---
  private final Git git;
  private final String currentBranch;
  private final NavigableMap<Integer, String> navigableMap = new TreeMap<>();
  private final NavigableMap<Integer, BulkParser> bulkParsers = new TreeMap<>();

  public ProjectHistory(File root, String name, Properties ignore, String cutoff) {
    git = new Git(root);
    if (!git.isClean())
      throw new RuntimeException();
    // TODO style
    currentBranch = Objects.requireNonNull(git.branch());
    System.out.println("currentBranch=" + currentBranch);
    for (String line : git.logSha1()) {
      StringTokenizer stringTokenizer = new StringTokenizer(line);
      String date = stringTokenizer.nextToken();
      String sha1 = stringTokenizer.nextToken();
      if (cutoff.compareTo(date) < 0)
        try {
          int days = (int) TimeUnit.DAYS.convert( //
              DATE_FORMAT.parse(date).getTime() - DATE.getTime(), //
              TimeUnit.MILLISECONDS);
          if (!navigableMap.containsKey(days))
            navigableMap.put(days, sha1);
        } catch (Exception exception) {
          new RuntimeException();
        }
    }
    System.out.println("start days ago: " + navigableMap.firstKey());
    int ago = 0;
    while (AGE_MIN <= ago) {
      Entry<Integer, String> floorEntry = navigableMap.floorEntry(ago);
      if (Objects.isNull(floorEntry))
        break;
      String sha1 = floorEntry.getValue();
      try {
        // System.out.println("checkout " + sha1);
        git.checkout(sha1);
        bulkParsers.put(floorEntry.getKey(), new BulkParser(root, name, ignore));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      // ---
      ago -= INTERVAL;
    }
    // System.out.println("checkout branch " + currentBranch);
    try {
      git.checkout(currentBranch);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    System.out.println(navigableMap.size());
  }

  public Tensor fileCount() {
    TableBuilder tableBuilder = new TableBuilder();
    for (Entry<Integer, BulkParser> entry : bulkParsers.entrySet()) {
      BulkParser bulkParser = entry.getValue();
      tableBuilder.appendRow(Tensors.vector(entry.getKey(), bulkParser.codes().size()));
    }
    return tableBuilder.toTable();
  }

  public Tensor lineCount() {
    TableBuilder tableBuilder = new TableBuilder();
    for (Entry<Integer, BulkParser> entry : bulkParsers.entrySet()) {
      BulkParser bulkParser = entry.getValue();
      tableBuilder.appendRow(Tensors.vector(entry.getKey(), bulkParser.totalLineCount()));
    }
    return tableBuilder.toTable();
  }

  public Tensor ratios() {
    TableBuilder tableBuilder = new TableBuilder();
    for (Entry<Integer, BulkParser> entry : bulkParsers.entrySet()) {
      BulkParser bulkParser = entry.getValue();
      Scalar ratio = RationalScalar.of(bulkParser.totalLineCount(), Math.max(1, bulkParser.codes().size()));
      tableBuilder.appendRow(RealScalar.of(entry.getKey()), ratio);
    }
    return tableBuilder.toTable();
  }

  public Tensor todos() {
    TableBuilder tableBuilder = new TableBuilder();
    for (Entry<Integer, BulkParser> entry : bulkParsers.entrySet()) {
      BulkParser bulkParser = entry.getValue();
      int todos = (int) bulkParser.texts().stream().flatMap(parserText -> parserText.todos().stream()).count();
      tableBuilder.appendRow(Tensors.vector(entry.getKey(), todos));
    }
    return tableBuilder.toTable();
  }
}
