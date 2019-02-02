// code by jph
package ch.ethz.idsc.edelweis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.edelweis.htm.HtmlUtf8;
import ch.ethz.idsc.edelweis.lang.ClassType;
import ch.ethz.idsc.edelweis.lang.ParserCode;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.lang.ParserText;
import ch.ethz.idsc.edelweis.prc.DependencyGlobal;
import ch.ethz.idsc.edelweis.prc.ExtDependencies;
import ch.ethz.idsc.edelweis.prc.NameCollisions;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.red.Total;

class Main {
  static String smallgray(Object text) {
    return "<small><font color='#a0a0a0'>" + text + "</font></small>";
  }

  public static void main(String[] args) {
    Session session = new Session(0 < args.length ? args[0] : UserName.get());
    // FileDelete.of(OUTPUT_ROOT, 2, 500).printNotification();
    File OUTPUT_ROOT = HomeDirectory.Documents("edelweis", session.user);
    OUTPUT_ROOT.mkdirs();
    // File ICONS_OVERVIEW = new File(OUTPUT_ROOT, "icons_overview");
    File PAGES_ROOT = new File(OUTPUT_ROOT, "pages");
    File TAGIMAGE = new File(OUTPUT_ROOT, "tagimage");
    TAGIMAGE.mkdir();
    // ---
    generateTallImages(TAGIMAGE, session);
    // ---
    DependencyGlobal dependencyGlobal = new DependencyGlobal(session.bulkParsers());
    // ---
    NameCollisions nameCollisions = new NameCollisions(session.bulkParsers());
    // ---
    HtmlUtf8.index(new File(OUTPUT_ROOT, "index.html"), "Edelweis", "cols=\"200,*\"", "projects.htm", "menu", "some.htm", "project");
    try (HtmlUtf8 menu = HtmlUtf8.page(new File(OUTPUT_ROOT, "projects.htm"), false)) {
      PAGES_ROOT.mkdir();
      menu.appendln("<h3>" + session.user + "</h3>");
      for (BulkParser bulkParser : session.bulkParsers()) {
        String name = bulkParser.name();
        String link = name + ".htm";
        int size = bulkParser.codes().size();
        menu.append("<a href='" + PAGES_ROOT.getName() + "/" + link + "' target='project'>" + name + "</a> " + smallgray(size) + "<br/>\n");
        {
          HtmlUtf8.index(new File(PAGES_ROOT, link), "", "cols=\"200,*\"", name + "/menu.htm", "item", name + "/lines.htm", "content");
          File dir = new File(PAGES_ROOT, name);
          dir.mkdir();
          HeaderMissing headerMissing = new HeaderMissing(bulkParser);
          if (!headerMissing.list.isEmpty())
            try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "headermiss.htm"), false)) {
              htmlUtf8.append("<h3>Missing Headers</h3>\n");
              htmlUtf8.append("<pre>\n");
              headerMissing.list.stream().map(ParserJava::identifier).forEach(htmlUtf8::appendln);
              htmlUtf8.append("</pre>\n");
            }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "menu.htm"), false)) {
            htmlUtf8.append("<h3>" + name + "</h3>\n");
            htmlUtf8.append("<a href='external.htm' target='content'>External</a><br/>\n");
            htmlUtf8.append("<a href='lines.htm' target='content'>Lines</a> " + smallgray(Total.of(bulkParser.allLineCounts())) + "<br/>\n");
            htmlUtf8.append("<a href='ghost.htm' target='content'>Unused</a><br/>\n");
            htmlUtf8.append("<a href='names.htm' target='content'>Duplicate Names</a><br/>\n");
            htmlUtf8.append("<a href='todos.htm' target='content'>Todos</a><br/>\n");
            // htmlUtf8.append("<a href='edits.htm' target='content'>Edits</a><br/>\n");
            htmlUtf8.append("<a href='../../tagimage/" + name + ".png' target='content'>Tagimage</a><br/>\n");
            if (!headerMissing.list.isEmpty())
              htmlUtf8.append("<a href='headermiss.htm' target='content'>Headermiss</a><br/>\n");
          }
          // ---
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "lines.htm"), false)) {
            htmlUtf8.append("<h3>Lines</h3>\n");
            htmlUtf8.append("<pre>\n");
            LinesLister.of(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "ghost.htm"), false)) {
            htmlUtf8.append("<h3>Unused</h3>\n");
            htmlUtf8.append("<pre>\n");
            dependencyGlobal.publicUnref(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "names.htm"), false)) {
            htmlUtf8.append("<h3>Duplicate Names</h3>\n");
            htmlUtf8.append("<pre>\n");
            nameCollisions.duplicates(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "todos.htm"), false)) {
            htmlUtf8.append("<h3>Todos</h3>\n");
            htmlUtf8.append("<pre>\n");
            for (ParserText parserText : bulkParser.texts())
              if (!parserText.todos().isEmpty()) {
                htmlUtf8.append("<b>" + parserText.file() + "</b>\n");
                parserText.todosNoXml().forEach(htmlUtf8::appendln);
              }
            // new EditCount(bulkParser).listing().forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "edits.htm"), false)) {
            htmlUtf8.append("<h3>Edits</h3>\n");
            htmlUtf8.append("<pre>\n");
            // new EditCount(bulkParser).listing().forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "external.htm"), false)) {
            ExtDependencies extDependencies = new ExtDependencies(bulkParser);
            Map<String, Long> set = extDependencies.getAll();
            htmlUtf8.append("<h3>External</h3>\n");
            htmlUtf8.append("<pre>\n");
            set.entrySet().forEach(entry -> htmlUtf8.append(String.format("%5d %s", entry.getValue(), entry.getKey()) + "\n"));
            htmlUtf8.append("</pre>\n");
          }
        }
      }
    }
    // bulkParser.nonTest();
    // TODO show @Override methods that are not final
    // missingHeaders(session);
    // unassociatedTests(session);
  }

  static void missingTests(Session session) {
    System.out.println(session.testPairs().size());
    for (BulkParserPair bulkParserPair : session.testPairs()) {
      boolean hasOutput = false;
      Map<String, ParserCode> testIndex = bulkParserPair.bp2.parserCodeIndex();
      for (Entry<String, ParserCode> entry : bulkParserPair.bp1.parserCodeIndex().entrySet()) {
        String key = entry.getKey();
        ParserCode parserCode = entry.getValue();
        if (parserCode instanceof ParserJava) {
          ParserJava parserJava = (ParserJava) parserCode;
          if (parserJava.isPublic() && //
              !parserJava.isAbstract() && //
              !ClassType.INTERFACE.equals(parserJava.classType())) {
            String testKey = key.substring(0, key.length() - 5) + "Test.java";
            if (!testIndex.containsKey(testKey)) {
              if (!hasOutput) {
                System.out.println(" --- " + bulkParserPair.bp1.name() + " --- ");
                hasOutput = true;
              }
              System.out.println(key);
            }
          }
        }
      }
    }
  }

  static void unassociatedTests(Session session) {
    System.out.println(session.testPairs().size());
    for (BulkParserPair bulkParserPair : session.testPairs()) {
      boolean hasOutput = false;
      Map<String, ParserCode> testIndex = bulkParserPair.bp1.parserCodeIndex();
      for (Entry<String, ParserCode> entry : bulkParserPair.bp2.parserCodeIndex().entrySet()) {
        String key = entry.getKey();
        ParserCode parserCode = entry.getValue();
        if (parserCode instanceof ParserJava) {
          ParserJava parserJava = (ParserJava) parserCode;
          if (parserJava.isPublic() && //
              !parserJava.isAbstract() && //
              !ClassType.INTERFACE.equals(parserJava.classType())) {
            // System.out.println();
            String testKey = key.substring(0, key.length() - 9) + ".java";
            if (!testIndex.containsKey(testKey)) {
              if (!hasOutput) {
                System.out.println(" --- " + bulkParserPair.bp1.name() + " --- ");
                hasOutput = true;
              }
              System.out.println(key);
            }
          }
        }
      }
    }
  }

  static void generateTallImages(File TAGIMAGE, Session session) {
    for (BulkParser bulkParser : session.bulkParsers()) {
      BufferedImage bufferedImage = TagImage.of(bulkParser);
      if (Objects.nonNull(bufferedImage))
        try {
          ImageIO.write(bufferedImage, "png", new File(TAGIMAGE, bulkParser.name() + ".png"));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }
}
