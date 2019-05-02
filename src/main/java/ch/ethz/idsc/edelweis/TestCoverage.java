// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.Properties;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.util.Filename;

public class TestCoverage {
  private final File main;
  private final File test;

  public TestCoverage(File root) {
    main = new File(root, "main");
    test = new File(root, "test");
    if (!main.isDirectory())
      throw new RuntimeException(main.toString());
    if (!test.isDirectory())
      throw new RuntimeException(test.toString());
    // visitMain(main);
    System.out.println("---");
    visitTest(test);
  }

  private void visitMain(File file) {
    if (file.isDirectory())
      Stream.of(file.listFiles()).sorted().forEach(this::visitMain);
    else {
      Filename filename = new Filename(file);
      if (filename.hasExtension("java")) {
        String string = file.toString();
        String substring = string.substring(main.toString().length() + 1, string.length() - 5);
        File testfile = new File(test, substring + "Test.java");
        if (!testfile.isFile())
          System.out.println(substring);
      }
    }
  }

  private void visitTest(File file) {
    if (file.isDirectory())
      Stream.of(file.listFiles()).sorted().forEach(this::visitTest);
    else {
      Filename filename = new Filename(file);
      if (filename.hasExtension("java") && filename.title.endsWith("Test")) {
        String string = file.toString();
        String substring = string.substring(test.toString().length() + 1, string.length() - 5 - 4);
        File testfile = new File(test, substring + ".java");
        File mainfile = new File(main, substring + ".java");
        if (!mainfile.isFile() && !testfile.isFile())
          System.out.println(substring);
      }
    }
  }

  public static void main(String[] args) {
    Properties properties = new UserProperties().load("unittest");
    // List<BulkParser> bulkParsers = new ArrayList<>();
    for (String project : properties.stringPropertyNames()) {
      String directory = properties.getProperty(project);
      // TODO filter out interfaces
      new TestCoverage(new File(directory));
      // BulkParser bulkParser = generate(new File(properties.getProperty(project)), project);
      // bulkParsers.add(bulkParser);
      // System.out.println(project);
    }
  }
}
