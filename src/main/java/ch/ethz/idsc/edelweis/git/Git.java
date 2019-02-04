// code by jph
package ch.ethz.idsc.edelweis.git;

import java.io.File;
import java.util.List;

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
      return StaticHelper.static_process(new ProcessBuilder(getExecutable(), "--version"));
    } catch (Exception myException) {
      myException.printStackTrace();
    }
    return null;
  }

  // ---
  private final File directory;

  public Git(File directory) {
    this.directory = directory;
    // if (!new File(directory, ".git").exists())
    // System.err.println("warning: .git directory does not exist in given directory");
    // ---
    // File gitIgnore = new File(myDirectory, ".gitignore");
    // if (!gitIgnore.exists()) {
    // PrintStream myPrintStream = new PrintStream(gitIgnore);
    // myPrintStream.println(".gitignore");
    // myPrintStream.println("sha.properties");
    // myPrintStream.close();
    // }
    // myManager = new Manager(new File(myDirectory, "sha.properties"));
  }

  public List<String> log() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder( //
          "git", "log", "--no-merges", "--pretty=format:%ad %h %s", "--date=short");
      processBuilder.directory(directory);
      return StaticHelper.static_process_lines(processBuilder);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public List<String> logSha1() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder( //
          "git", "log", "--no-merges", "--pretty=format:%ad %H", "--date=short");
      processBuilder.directory(directory);
      return StaticHelper.static_process_lines(processBuilder);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public String branch() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(getExecutable(), //
          "rev-parse", "--abbrev-ref", "HEAD");
      processBuilder.directory(directory);
      return StaticHelper.static_process(processBuilder).trim();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public String currentCommitSha1() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(getExecutable(), "rev-parse", "HEAD");
      processBuilder.directory(directory);
      return StaticHelper.static_process(processBuilder).trim();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public boolean isClean() {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(getExecutable(), "status", "-s");
      processBuilder.directory(directory);
      return StaticHelper.static_process(processBuilder).trim().isEmpty();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return false;
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
  synchronized String process(ProcessBuilder processBuilder) throws Exception {
    processBuilder.directory(directory);
    return StaticHelper.static_process(processBuilder);
  }

  public void checkout(String string) throws Exception {
    process(new ProcessBuilder(getExecutable(), "checkout", string));
  }

  public void tryCheckout(String string) {
    try {
      checkout(string);
    } catch (Exception myException) {
      myException.printStackTrace();
    }
  }
}
