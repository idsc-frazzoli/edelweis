// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.edelweis.git.Git;
import ch.ethz.idsc.edelweis.lang.ParserC;
import ch.ethz.idsc.edelweis.lang.ParserCode;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.lang.ParserMatlab;
import ch.ethz.idsc.edelweis.lang.ParserPy;
import ch.ethz.idsc.edelweis.lang.ParserText;
import ch.ethz.idsc.edelweis.util.Filename;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class BulkParser {
  private static final String CATKIN_IGNORE = "CATKIN_IGNORE";
  private static final String PACKAGE_INFO = "package-info";
  // ---
  private final File root;
  private final String name;
  private final boolean test;
  private final String branch;
  private final Properties ignore;
  private final Git git;
  private final List<ParserCode> codes = new ArrayList<>();
  private final List<ParserText> texts = new ArrayList<>();
  private final Set<String> extensions = new TreeSet<>();

  public BulkParser(File root, String name, Properties ignore) {
    this.root = root;
    this.name = name;
    test = name.endsWith("-test");
    this.ignore = ignore;
    git = new Git(root);
    branch = git.branch();
    visit(root);
  }

  public String name() {
    return name;
  }

  public String branch() {
    return branch;
  }

  public List<String> log() {
    return git.log();
  }

  public boolean isClean() {
    return git.isClean();
  }

  private void visit(File directory) {
    if (!directory.isDirectory()) {
      System.err.println("miss: " + directory);
      return;
    }
    List<File> collect = Stream.of(directory.listFiles()).sorted().collect(Collectors.toList());
    for (final File file : collect) {
      if (ignore.containsKey(file.toString())) {
        System.err.println("ignore: " + file);
        continue;
      }
      if (file.isDirectory()) {
        if (file.getName().equals(".git"))
          continue;
        File catkin_ignore = new File(file, CATKIN_IGNORE);
        if (!catkin_ignore.isFile())
          visit(file);
        else
          System.out.println("skip " + file);
      } else //
      if (file.isFile())
        try {
          Filename filename = new Filename(file);
          if (ExtensionsHelper.isIgnored(filename.extension.toLowerCase()))
            continue;
          texts.add(new ParserText(file));
          if (filename.hasExtension("cs"))
            System.err.println("cs: " + file);
          else //
          if (filename.hasExtension("java") && !filename.title.equals(PACKAGE_INFO)) {
            if (filename.title.endsWith("Test")) {
              // FailInTest.replaceAll(file);
              // System.out.println(file);
            }
            // replaceAll(file);
            codes.add(new ParserJava(file));
          } else //
          if (filename.hasExtension("m"))
            codes.add(new ParserMatlab(file));
          else //
          if (filename.hasExtension("cpp") || //
              filename.hasExtension("cc") || //
              filename.hasExtension("c") || //
              filename.hasExtension("hpp") || //
              filename.hasExtension("h")) {
            // System.out.println("c: " + file);
            codes.add(new ParserC(file));
          } else //
          if (filename.hasExtension("py")) { //
            if (!filename.title.equalsIgnoreCase("__init__"))
              codes.add(new ParserPy(file));
          } else //
          {
            extensions.add(filename.extension.toLowerCase());
          }
        } catch (Exception exception) {
          System.err.println(exception.getMessage());
        }
    }
  }

  public File root() {
    return root;
  }

  public boolean nonTest() {
    return !test;
  }

  public String name(File file) {
    String head = root.toString();
    String name = file.toString();
    if (!name.startsWith(head))
      throw new RuntimeException();
    return name.substring(head.length() + 1);
  }

  public List<ParserCode> codes() {
    return Collections.unmodifiableList(codes);
  }

  public List<ParserText> texts() {
    return Collections.unmodifiableList(texts);
  }

  public Tensor allLineCounts() {
    return Tensor.of(codes.stream() //
        .map(ParserCode::lineCount) //
        .map(RealScalar::of));
  }

  public Map<String, ParserCode> parserCodeIndex() {
    return codes.stream().collect(Collectors.toMap(this::relative, Function.identity(), (e, r) -> e, TreeMap::new));
  }

  public String relative(ParserCode parserCode) {
    return parserCode.file().toString().substring(root.toString().length() + 1);
  }

  private static final long DAY_MS = 86400_000;

  public Tensor allAges() {
    final long finow = System.currentTimeMillis();
    return Tensor.of(codes.stream() //
        .map(ParserCode::file) //
        .map(File::lastModified) //
        .map(l -> (l - finow) / DAY_MS) //
        .map(RealScalar::of));
  }

  public Tensor allRefs() {
    final long finow = System.currentTimeMillis();
    return Tensor.of(codes.stream() //
        .map(ParserCode::file) //
        .map(File::lastModified) //
        .map(l -> (l - finow) / DAY_MS) //
        .map(RealScalar::of));
  }

  public Set<String> unknownExtensions() {
    return Collections.unmodifiableSet(extensions);
  }

  public int totalLineCount() {
    return codes.stream().mapToInt(ParserCode::lineCount).sum();
  }
}
