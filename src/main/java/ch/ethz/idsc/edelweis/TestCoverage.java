// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.lang.ClassType;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.util.Filename;

public class TestCoverage {
  private final File main;
  private final File test;

  public TestCoverage(File main, File test) {
    this.main = main;
    this.test = test;
    if (!main.isDirectory())
      throw new RuntimeException(main.toString());
    if (!test.isDirectory())
      throw new RuntimeException(test.toString());
  }

  public List<String> visitMain() {
    List<String> list = new LinkedList<>();
    visitMain(main, list);
    return list;
  }

  private void visitMain(File file, List<String> list) {
    if (file.isDirectory())
      Stream.of(file.listFiles()).sorted().forEach(f -> visitMain(f, list));
    else {
      Filename filename = new Filename(file);
      if (filename.hasExtension("java")) {
        ParserJava parserJava = new ParserJava(file, ParserJava.RELEVANT_CODE);
        if (parserJava.classType().equals(ClassType.INTERFACE)) {
          // ---
        } else {
          String string = file.toString();
          String substring = string.substring(main.toString().length() + 1, string.length() - 5);
          File testfile = new File(test, substring + "Test.java");
          if (!testfile.isFile())
            list.add(substring);
        }
      }
    }
  }

  public List<String> visitTest() {
    List<String> list = new LinkedList<>();
    visitTest(test, list);
    return list;
  }

  private void visitTest(File file, List<String> list) {
    if (file.isDirectory())
      Stream.of(file.listFiles()).sorted().forEach(f -> visitTest(f, list));
    else {
      Filename filename = new Filename(file);
      if (filename.hasExtension("java") && filename.title.endsWith("Test")) {
        String string = file.toString();
        String substring = string.substring(test.toString().length() + 1, string.length() - 5 - 4);
        File testfile = new File(test, substring + ".java");
        File mainfile = new File(main, substring + ".java");
        if (!mainfile.isFile() && !testfile.isFile())
          list.add(substring);
      }
    }
  }

  public static void main(String[] args) {
    TestCoverage testCoverage = new TestCoverage( //
        new File("/home/datahaki/Projects/owl/src/main/java/ch/ethz/idsc/sophus"), //
        new File("/home/datahaki/Projects/owl/src/test/java/ch/ethz/idsc/sophus"));
    {
      List<String> list = testCoverage.visitMain();
      list.forEach(System.out::println);
    }
    System.out.println("===");
    {
      List<String> list = testCoverage.visitTest();
      list.forEach(System.out::println);
    }
  }
}
