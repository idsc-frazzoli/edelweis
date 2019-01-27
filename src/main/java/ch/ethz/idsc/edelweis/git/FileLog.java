// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitConvert;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

//%H Commit hash
//%h Abbreviated commit hash
//%T Tree hash
//%t Abbreviated tree hash
//%P Parent hashes
//%p Abbreviated parent hashes
//%an Author name
//%ae Author email
//%ad Author date (format respects the --date=option)
//%ar Author date, relative
//%cn Committer name
//%ce Committer email
//%cd Committer date
//%cr Committer date, relative
//%s Subject
public class FileLog {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final long MILLIS = System.currentTimeMillis();

  public static FileLog getDetailed(File file) {
    ProcessBuilder processBuilder = new ProcessBuilder( //
        "git", "log", "--no-merges", "--pretty=format:%ad %ce %h %s", "--date=short", "--", file.toString());
    processBuilder.directory(file.getParentFile());
    try {
      String output = StaticHelper.static_process(processBuilder);
      try (BufferedReader bufferedReader = new BufferedReader(new StringReader(output))) {
        return new FileLog(bufferedReader.lines());
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  // ---
  // maps daysAgo to #edits at that day
  public final NavigableSet<Scalar> edits = new TreeSet<>();
  public final NavigableMap<String, Integer> committer = new TreeMap<>();
  private int totalEdits;

  public FileLog(Stream<String> stream) {
    stream.forEach(this::parseLine);
  }

  private void parseLine(final String line) {
    StringTokenizer stringTokenizer = new StringTokenizer(line);
    try { // date
      String token = stringTokenizer.nextToken();
      Date date = DATE_FORMAT.parse(token);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      Scalar diff = Quantity.of(MILLIS - calendar.getTimeInMillis(), "ms");
      boolean added = edits.add(diff);
      if (added)
        ++totalEdits;
      // else
      // System.err.println("skip " + diff);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    // ---
    try { // committer name
      String token = stringTokenizer.nextToken();
      token = token.split("@")[0];
      if (!committer.containsKey(token))
        committer.put(token, 0);
      committer.put(token, committer.get(token) + 1);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public NavigableMap<Integer, Integer> edits(String string) {
    NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
    ScalarUnaryOperator scalarUnaryOperator = UnitConvert.SI().to(string);
    for (Scalar millis : edits) {
      int floor = scalarUnaryOperator.apply(millis).map(Floor.FUNCTION).Get().number().intValue();
      navigableMap.put(floor, navigableMap.containsKey(floor) //
          ? navigableMap.get(floor) + 1
          : 1);
    }
    return navigableMap;
  }

  public Tensor asTensor(String string, int length) {
    NavigableMap<Integer, Integer> navigableMap = edits(string);
    return Tensors.vector(i -> RealScalar.of(navigableMap.containsKey(i) ? navigableMap.get(i) : 0), 50);
  }

  public int getTotalEdits() {
    return totalEdits;
  }

  public int getCommitsBy(String author) {
    return committer.containsKey(author) //
        ? committer.get(author)
        : 0;
  }

  public static void main(String[] args) {
    File directory = HomeDirectory.file("Projects/retina");
    Git git = new Git(directory);
    File file = new File(directory, "src/main/java/ch/ethz/idsc/gokart/core/AutoboxSocket.java");
    FileLog fileLog = FileLog.getDetailed(file);
    System.out.println(fileLog.committer);
    System.out.println(fileLog.totalEdits);
    System.out.println("days =" + fileLog.edits("days"));
    System.out.println("weeks=" + fileLog.edits("wk"));
    Tensor vector = fileLog.asTensor("wk", 50);
    System.out.println(vector);
  }
}
