// code by jph
package ch.ethz.idsc.edelweis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import ch.ethz.idsc.edelweis.htm.HtmlUtf8;
import ch.ethz.idsc.edelweis.lang.ClassType;
import ch.ethz.idsc.edelweis.lang.ParserCode;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.lang.ParserText;
import ch.ethz.idsc.edelweis.prc.DependencyGlobal;
import ch.ethz.idsc.edelweis.prc.ExtDependencies;
import ch.ethz.idsc.edelweis.prc.NameCollisions;
import ch.ethz.idsc.edelweis.util.FileDelete;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.red.Total;

public class Edelweis {
  static String smallgray(Object text) {
    return "<small><font color='#a0a0a0'>" + text + "</font></small>";
  }

  public static void main(String[] args) throws IOException {
    args = new String[] { "test" };
    Session session = new Session(0 < args.length ? args[0] : UserName.get());
    final File root = HomeDirectory.Documents("edelweis", session.user);
    if (root.isDirectory())
      FileDelete.of(root, 3, 500).printNotification();
    root.mkdirs();
    // File ICONS_OVERVIEW = new File(OUTPUT_ROOT, "icons_overview");
    final File pages = new File(root, "pages");
    final File tagimage = new File(root, "tagimage");
    tagimage.mkdir();
    final File linechart = new File(root, "linechart");
    linechart.mkdir();
    // ---
    generateTallImages(tagimage, session);
    // ---
    {
      List<BulkParser> list = session.bulkParsers().stream().filter(BulkParser::nonTest).collect(Collectors.toList());
      LineChart.of(list, root);
    }
    // ---
    DependencyGlobal dependencyGlobal = new DependencyGlobal(session.bulkParsers());
    // ---
    NameCollisions nameCollisions = new NameCollisions(session.bulkParsers());
    // ---
    HtmlUtf8.index(new File(root, "index.html"), "Edelweis " + session.user, "cols=\"250,*\"", "projects.htm", "menu", "lines.png", "project");
    try (HtmlUtf8 menu = HtmlUtf8.page(new File(root, "projects.htm"), false)) {
      pages.mkdir();
      menu.appendln("<h3>" + session.user + "</h3>");
      menu.appendln("<table>");
      for (BulkParser bulkParser : session.bulkParsers()) {
        String name = bulkParser.name();
        String link = name + ".htm";
        int size = bulkParser.codes().size();
        menu.appendln(
            "<tr><td><a href='" + pages.getName() + "/" + link + "' target='project'>" + name + "</a> <td align='right'>" + smallgray(size) + "</tr>");
        {
          HtmlUtf8.index(new File(pages, link), "", "cols=\"300,*\"", name + "/menu.htm", "item", name + "/lines.htm", "content");
          File dir = new File(pages, name);
          dir.mkdir();
          HeaderMissing headerMissing = new HeaderMissing(bulkParser);
          List<String> duplicates = nameCollisions.duplicates(bulkParser).collect(Collectors.toList());
          if (!headerMissing.list.isEmpty())
            try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "headermiss.htm"), false)) {
              htmlUtf8.appendln("<h3>Missing Headers</h3>");
              htmlUtf8.appendln("<pre>");
              headerMissing.list.stream().map(ParserJava::identifier).forEach(htmlUtf8::appendln);
              htmlUtf8.appendln("</pre>");
            }
          try (HtmlUtf8 submenu = HtmlUtf8.page(new File(dir, "menu.htm"), false)) {
            submenu.appendln("<img src='../../tagimage/" + name + ".png'>");
            submenu.appendln("<br/><br/><small>branch</small> <b>" + bulkParser.branch() + "</b><br/><br/>");
            submenu.appendln("<table>");
            submenu.appendln("<tr><td><a href='lines.htm' target='content'>Lines</a> " + smallgray(Total.of(bulkParser.allLineCounts())));
            submenu.appendln("<tr><td><a href='dependencies.htm' target='content'>Dependencies</a>");
            // submenu.appendln("<tr><td><a href='../../linechart/" + name + ".png' target='content'>Chart</a>");
            if (0 < dependencyGlobal.publicUnref(bulkParser).count())
              submenu.appendln("<tr><td><a href='ghost.htm' target='content'>Unused</a><br/>");
            if (!duplicates.isEmpty())
              submenu.appendln("<tr><td><a href='names.htm' target='content'>Duplicate Names</a><br/>");
            if (0 < bulkParser.texts().stream().flatMap(parserText -> parserText.todos().stream()).count())
              submenu.appendln("<tr><td><a href='todos.htm' target='content'>Todos</a><br/>");
            // htmlUtf8.append("<a href='edits.htm' target='content'>Edits</a><br/>\n");
            if (session.edelweisConfig.missingHeaders)
              if (!headerMissing.list.isEmpty())
                submenu.append("<tr><td><a href='headermiss.htm' target='content'>Headermiss</a><br/>\n");
            submenu.appendln("<tr><td><a href='commits.htm' target='content'>Commits</a>");
            submenu.appendln("</table>");
          }
          // ---
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "lines.htm"), false)) {
            htmlUtf8.appendln("<h3>Lines</h3>");
            htmlUtf8.appendln("<pre>");
            LinesLister.of(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.appendln("</pre>");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "ghost.htm"), false)) {
            htmlUtf8.appendln("<h3>Unused</h3>");
            htmlUtf8.appendln("<pre>");
            dependencyGlobal.publicUnref(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.appendln("</pre>");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "names.htm"), false)) {
            htmlUtf8.appendln("<h3>Duplicate Names</h3>");
            htmlUtf8.appendln("<pre>");
            for (String key : duplicates) {
              htmlUtf8.appendln("<b>" + key + "</b>");
              nameCollisions.flatMap(key).forEach(htmlUtf8::appendln);
              htmlUtf8.appendln();
            }
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "todos.htm"), false)) {
            htmlUtf8.append("<h3>Todos</h3>\n");
            htmlUtf8.append("<pre>\n");
            for (ParserText parserText : bulkParser.texts())
              if (!parserText.todos().isEmpty()) {
                htmlUtf8.append("<b>" + parserText.file() + "</b>\n");
                parserText.todosNoXml().forEach(htmlUtf8::appendln);
                htmlUtf8.appendln();
              }
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "edits.htm"), false)) {
            htmlUtf8.append("<h3>Edits</h3>\n");
            htmlUtf8.append("<pre>\n");
            // new EditCount(bulkParser).listing().forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "dependencies.htm"), false)) {
            ExtDependencies extDependencies = new ExtDependencies(bulkParser);
            Map<String, Long> set = extDependencies.getAll();
            htmlUtf8.append("<h3>Dependencies</h3>\n");
            htmlUtf8.append("<pre>\n");
            set.entrySet().forEach(entry -> htmlUtf8.append(String.format("%5d %s", entry.getValue(), entry.getKey()) + "\n"));
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "commits.htm"), false)) {
            htmlUtf8.append("<h3>Commits</h3>\n");
            htmlUtf8.append("<pre>\n");
            htmlUtf8.append(bulkParser.log());
            htmlUtf8.append("</pre>\n");
          }
        }
      }
      menu.appendln("</table>");
      menu.appendln("<hr/>");
      menu.appendln("<a href='lines.png' target='project'>lines</a>");
      menu.appendln("<hr/>");
      menu.appendln(new Date());
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
