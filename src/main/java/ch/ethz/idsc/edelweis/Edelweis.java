// code by jph
package ch.ethz.idsc.edelweis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.edelweis.img.ImageRedundancy;
import ch.ethz.idsc.edelweis.lang.ParserJava;
import ch.ethz.idsc.edelweis.lang.ParserText;
import ch.ethz.idsc.edelweis.prc.CommentCount;
import ch.ethz.idsc.edelweis.prc.CommonLines;
import ch.ethz.idsc.edelweis.prc.DependencyGlobal;
import ch.ethz.idsc.edelweis.prc.ExtDependencies;
import ch.ethz.idsc.edelweis.prc.FunctionDepth;
import ch.ethz.idsc.edelweis.prc.FunctionLength;
import ch.ethz.idsc.edelweis.prc.NameCollisions;
import ch.ethz.idsc.edelweis.prc.NoIdentifier;
import ch.ethz.idsc.subare.util.HtmlUtf8;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.red.Total;

public class Edelweis {
  static String smallgray(Object text) {
    return "<small><font color='#a0a0a0'>" + text + "</font></small>";
  }

  // TODO list duplicates in central page (not per project)
  // TODO find tabs in text files
  // TODO find non-unix line endings
  // FIXME proper documentation of the required input files and what the
  // lines in them mean.
  public static void main(String[] args) {
    // args = new String[] { "datahaki" };
    Session session = new Session(0 < args.length ? args[0] : UserName.get());
    final File export = session.exportFolder();
    session.build();
    // File ICONS_OVERVIEW = new File(OUTPUT_ROOT, "icons_overview");
    final File pages = new File(export, "pages");
    final File tagimage = new File(export, "tagimage");
    tagimage.mkdir();
    final File commonimage = new File(export, "commonimage");
    commonimage.mkdir();
    final File linechart = new File(export, "linechart");
    linechart.mkdir();
    // ---
    generateTallImages(tagimage, session);
    // ---
    {
      List<BulkParser> list = session.bulkParsers().stream().filter(BulkParser::nonTest).collect(Collectors.toList());
      LineChart.of(list, export);
    }
    // ---
    DependencyGlobal dependencyGlobal = new DependencyGlobal(session.bulkParsers());
    // ---
    NameCollisions nameCollisions = new NameCollisions(session.bulkParsers());
    // ---
    {
      CommonLines commonLines = new CommonLines(session.bulkParsers().stream().filter(BulkParser::nonTest));
      try (HtmlUtf8 page = HtmlUtf8.page(new File(export, "commons.htm"))) {
        page.appendln("<pre>");
        commonLines.matrix().forEach(page::appendln);
        page.appendln("</pre>");
      }
    }
    // ---
    HtmlUtf8.index(new File(export, "index.html"), "Edelweis " + session.user, "cols=\"250,*\"", "projects.htm", "menu", "lines.png", "project");
    try (HtmlUtf8 menu = HtmlUtf8.page(new File(export, "projects.htm"))) {
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
          final File dir = new File(pages, name);
          dir.mkdir();
          HeaderMissing headerMissing = new HeaderMissing(bulkParser);
          List<String> duplicates = nameCollisions.duplicates(bulkParser).collect(Collectors.toList());
          if (!headerMissing.list.isEmpty())
            try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "headermiss.htm"))) {
              htmlUtf8.appendln("<h3>Missing Headers</h3>");
              htmlUtf8.appendln("<pre>");
              headerMissing.list.stream() //
                  .map(ParserJava::identifier) //
                  .filter(Optional::isPresent) //
                  .map(Optional::get) //
                  .forEach(htmlUtf8::appendln);
              htmlUtf8.appendln("</pre>");
            }
          List<String> syncTestFail = session.syncTestFail(bulkParser);
          try (HtmlUtf8 submenu = HtmlUtf8.page(new File(dir, "menu.htm"))) {
            submenu.appendln("<img src='../../tagimage/" + name + ".png'>");
            // submenu.appendln("<img src='../../commonimage/" + name + ".png'>");
            // submenu.appendln("<br/><br/><small>branch</small> <b>" + bulkParser.branch() + "</b><br/><br/>");
            submenu.appendln("<table>");
            // ---
            submenu.appendln("<tr><td><a href='lines.htm' target='content'>Lines</a> " + smallgray(Total.of(bulkParser.allLineCounts())));
            if (!new ExtDependencies(bulkParser).getAll().isEmpty())
              submenu.appendln("<tr><td><a href='dependencies.htm' target='content'>Dependencies</a>");
            // submenu.appendln("<tr><td><a href='../../linechart/" + name + ".png' target='content'>Chart</a>");
            {
              long count = dependencyGlobal.publicUnref(bulkParser).count();
              if (0 < count)
                submenu.appendln("<tr><td><a href='ghost.htm' target='content'>Unused</a> " + smallgray(count));
            }
            submenu.appendln("<tr><td><a href='common.htm' target='content'>Redundancy</a><br/>");
            submenu.appendln("<tr><td><a href='depth.htm' target='content'>Depth</a><br/>");
            submenu.appendln("<tr><td><a href='function.htm' target='content'>Function</a><br/>");
            if (!duplicates.isEmpty())
              submenu.appendln("<tr><td><a href='names.htm' target='content'>Duplicate Names</a><br/>");
            if (!syncTestFail.isEmpty())
              submenu.appendln("<tr><td><a href='testsync.htm' target='content'>Test-Sync</a> " + smallgray(syncTestFail.size()));
            // ---
            {
              long count = bulkParser.texts().stream().flatMap(parserText -> parserText.todos().stream()).count();
              if (0 < count)
                submenu.appendln("<tr><td><a href='todos.htm' target='content'>Todos</a> " + smallgray(count));
            }
            // htmlUtf8.append("<a href='edits.htm' target='content'>Edits</a><br/>\n");
            {
              int count = headerMissing.list.size();
              if (0 < count)
                submenu.appendln("<tr><td><a href='headermiss.htm' target='content'>Headermiss</a> " + smallgray(count));
            }
            {
              long count = NoIdentifier.of(bulkParser).count();
              if (0 < count)
                submenu.appendln("<tr><td><a href='noid.htm' target='content'>No Identifier</a> " + smallgray(count));
            }
            submenu.appendln("<tr><td><a href='remcount.htm' target='content'>Remcount</a>");
            submenu.appendln("</table>");
          }
          // ---
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "lines.htm"))) {
            htmlUtf8.appendln("<h3>Lines</h3>");
            try {
              JFreeChart jFreeChart = ParserImages.lines(bulkParser, 10);
              ChartUtils.saveChartAsPNG(new File(dir, "histogram_lines.png"), jFreeChart, 640, 360);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
            htmlUtf8.appendln("<img src='histogram_lines.png' /><br/>");
            htmlUtf8.appendln("<pre>");
            LinesLister.html(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.appendln("</pre>");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "ghost.htm"))) {
            htmlUtf8.appendln("<h3>Unused</h3>");
            htmlUtf8.appendln("<pre>");
            dependencyGlobal.publicUnref(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.appendln("</pre>");
          }
          CommonLines commonLines = new CommonLines(Stream.of(bulkParser));
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "common.htm"))) {
            htmlUtf8.appendln("<h3>Redundancy</h3>");
            htmlUtf8.appendln("<pre>");
            commonLines.matrix().forEach(htmlUtf8::appendln);
            htmlUtf8.appendln("</pre>");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "testsync.htm"))) {
            htmlUtf8.appendln("<h3>Test-Sync</h3>");
            htmlUtf8.appendln("<pre>");
            syncTestFail.forEach(htmlUtf8::appendln);
            htmlUtf8.appendln("</pre>");
          }
          try {
            BufferedImage bufferedImage = ImageRedundancy.generate(commonLines.vector(), 180);
            ImageIO.write(bufferedImage, "png", new File(commonimage, name + ".png"));
          } catch (Exception exception) {
            exception.printStackTrace();
          }
          {
            List<FunctionDepth> list = bulkParser.codes().stream().filter(ParserJava.class::isInstance) //
                .map(ParserJava.class::cast).map(FunctionDepth::new) //
                .sorted().collect(Collectors.toList()); //
            try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "depth.htm"))) {
              htmlUtf8.appendln("<h3>Depth</h3>");
              htmlUtf8.appendln("<pre>");
              list.forEach(htmlUtf8::appendln);
              htmlUtf8.appendln("</pre>");
            }
          }
          {
            List<FunctionLength> list = bulkParser.codes().stream().filter(ParserJava.class::isInstance) //
                .map(ParserJava.class::cast).map(FunctionLength::new) //
                .sorted().collect(Collectors.toList()); //
            try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "function.htm"))) {
              htmlUtf8.appendln("<h3>Function</h3>");
              htmlUtf8.appendln("<pre>");
              list.forEach(htmlUtf8::appendln);
              htmlUtf8.appendln("</pre>");
            }
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "names.htm"))) {
            htmlUtf8.appendln("<h3>Duplicate Names</h3>");
            htmlUtf8.appendln("<pre>");
            for (String key : duplicates) {
              htmlUtf8.appendln("<b>" + key + "</b>");
              nameCollisions.flatMap(key).forEach(htmlUtf8::appendln);
              htmlUtf8.appendln();
            }
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "todos.htm"))) {
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
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "edits.htm"))) {
            htmlUtf8.append("<h3>Edits</h3>\n");
            htmlUtf8.append("<pre>\n");
            // new EditCount(bulkParser).listing().forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "dependencies.htm"))) {
            ExtDependencies extDependencies = new ExtDependencies(bulkParser);
            Map<String, Long> set = extDependencies.getAll();
            htmlUtf8.append("<h3>Dependencies</h3>\n");
            htmlUtf8.append("<pre>\n");
            set.entrySet().forEach(entry -> htmlUtf8.appendln(String.format("%5d %s", entry.getValue(), entry.getKey())));
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "noid.htm"))) {
            htmlUtf8.append("<h3>No Identifier</h3>\n");
            htmlUtf8.append("<pre>\n");
            NoIdentifier.of(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
          try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(dir, "remcount.htm"))) {
            htmlUtf8.append("<h3>Comment Count</h3>\n");
            htmlUtf8.append("<pre>\n");
            CommentCount.of(bulkParser).forEach(htmlUtf8::appendln);
            htmlUtf8.append("</pre>\n");
          }
        }
      }
      menu.appendln("</table>");
      menu.appendln("<hr/>");
      menu.appendln("<a href='commons.htm' target='project'>commons</a>");
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

  static void generateTallImages(File TAGIMAGE, Session session) {
    for (BulkParser bulkParser : session.bulkParsers()) {
      BufferedImage bufferedImage = StaticHelper.of(bulkParser);
      if (Objects.nonNull(bufferedImage))
        try {
          ImageIO.write(bufferedImage, "png", new File(TAGIMAGE, bulkParser.name() + ".png"));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }
}
