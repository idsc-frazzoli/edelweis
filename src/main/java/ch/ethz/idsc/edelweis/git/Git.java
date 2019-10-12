// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeMap;

import ch.ethz.idsc.edelweis.util.Run;

/** configuration required prior use
 * 
 * <pre>
 * git config --global user.name "John Smith"
 * git config --global user.email john@example.com
 * git config --global core.autocrlf false
 * </pre> */
public class Git {
  private static String getExecutable() {
    return "/usr/bin/git";
  }

  public static String version() {
    try {
      return Run.of(new ProcessBuilder(getExecutable(), "--version")).get(0);
    } catch (Exception myException) {
      myException.printStackTrace();
    }
    return null;
  }

  /** @param directory
   * @return
   * @throws Exception */
  public static Git requireClean(File directory) {
    Git git = new Git(directory);
    if (git.isClean())
      return git;
    throw new IllegalStateException("git status");
  }

  // ---
  private final File directory;

  private Git(File directory) {
    this.directory = Objects.requireNonNull(directory);
  }

  public boolean isClean() {
    return run("status", "-s").isEmpty();
  }

  /** @return navigable map that associates date to sha1 */
  public NavigableMap<Date, String> logSha1() {
    NavigableMap<Date, String> navigableMap = new TreeMap<>();
    for (String string : run("log", "--no-merges", "--pretty=format:%ad %H", "--date=unix")) {
      StringTokenizer stringTokenizer = new StringTokenizer(string);
      // the milliseconds since January 1, 1970, 00:00:00 GMT.
      long unix_ms = Long.parseLong(stringTokenizer.nextToken()) * 1000;
      navigableMap.put(new Date(unix_ms), stringTokenizer.nextToken());
    }
    return navigableMap;
  }

  /** Example:
   * 2019-03-21 c2b6ee0 organize imports, code format
   * 
   * @return */
  public List<String> log() {
    return run("log", "--no-merges", "--pretty=format:%ad %h %s", "--date=short");
  }

  public String branch() {
    return run("rev-parse", "--abbrev-ref", "HEAD").get(0);
  }

  public String currentCommitSha1() {
    return run("rev-parse", "HEAD").get(0);
  }

  public void checkout(String string) {
    run("checkout", string);
  }

  // private void init() throws Exception {
  // process(new ProcessBuilder(getExecutable(), "init"));
  // }
  // private void add_A() throws Exception {
  // process(new ProcessBuilder(getExecutable(), "add", "-A"));
  // }
  //
  // private void commit() throws Exception {
  // process(new ProcessBuilder(getExecutable(), "commit", "-a", "-m", "noname"));
  // }
  // public String revParse() throws Exception {
  // String myString = process(new ProcessBuilder(getExecutable(), "rev-parse", "HEAD"));
  // return myString.trim();
  // }
  // public List<String> revList() throws Exception {
  // String myString = process(new ProcessBuilder(getExecutable(), "rev-list", "--all"));
  // List<String> myList = new ArrayList<>();
  // StringTokenizer myStringTokenizer = new StringTokenizer(myString);
  // while (myStringTokenizer.hasMoreTokens())
  // myList.add(myStringTokenizer.nextToken());
  // return myList;
  // }
  // public void manifest() throws Exception {
  // String myPrev = revParse();
  // add_A();
  // commit();
  // String myNext = revParse();
  // if (!myManager.myExecute.contains(myNext)) {
  // int index = myManager.myExecute.indexOf(myPrev);
  // myManager.myExecute.add(Math.max(0, index), myNext);
  // myManager.manifest();
  // }
  // }
  private List<String> run(String... strings) {
    List<String> list = new ArrayList<>();
    list.add(getExecutable());
    list.addAll(Arrays.asList(strings));
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(list);
      processBuilder.directory(directory);
      return Run.of(processBuilder);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    throw new RuntimeException();
  }
}
