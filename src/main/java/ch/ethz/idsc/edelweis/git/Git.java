// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  // ---
  private final File directory;

  public Git(File directory) {
    this.directory = directory;
  }

  public boolean hint() {
    if (!new File(directory, ".git").exists())
      System.err.println("warning: .git directory does not exist in given directory");
    // ---
    File gitIgnore = new File(directory, ".gitignore");
    if (!gitIgnore.exists()) {
      // PrintStream myPrintStream = new PrintStream(gitIgnore);
      // myPrintStream.println(".gitignore");
      // myPrintStream.println("sha.properties");
      // myPrintStream.close();
    }
    // myManager = new Manager(new File(myDirectory, "sha.properties"));
    return false;
  }

  public List<String> log() {
    return git("log", "--no-merges", "--pretty=format:%ad %h %s", "--date=short");
  }

  public List<String> logSha1() {
    return git("log", "--no-merges", "--pretty=format:%ad %H", "--date=short");
  }

  public String branch() {
    return git("rev-parse", "--abbrev-ref", "HEAD").get(0);
  }

  public String currentCommitSha1() {
    return git("rev-parse", "HEAD").get(0);
  }

  public boolean isClean() {
    return git("status", "-s").isEmpty();
  }

  public void checkout(String string) throws Exception {
    process(new ProcessBuilder(getExecutable(), "checkout", string));
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
  private List<String> git(String... strings) {
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

  private void process(ProcessBuilder processBuilder) throws Exception {
    processBuilder.directory(directory);
    Run.of(processBuilder);
  }
}
