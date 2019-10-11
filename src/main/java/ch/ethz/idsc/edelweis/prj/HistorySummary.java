// code by jph
package ch.ethz.idsc.edelweis.prj;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.edelweis.Session;
import ch.ethz.idsc.edelweis.git.Git;
import ch.ethz.idsc.sophus.util.plot.ListPlot;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.io.UserName;

enum HistorySummary {
  ;
  // private static final Dimension _16_9_1024 = new Dimension(1024, 576);
  private static final Dimension _16_9_1280 = new Dimension(1280, 720);

  public static void main(String[] args) throws IOException {
    args = new String[] { "test" };
    Session session = new Session(0 < args.length ? args[0] : UserName.get());
    final File export = new File(session.exportFolder(), "history");
    export.mkdir();
    // ---
    System.out.println("checking all clean...");
    for (String project : new TreeSet<>(session.history.stringPropertyNames())) {
      Git.requireClean(new File(session.history.getProperty(project)));
    }
    // ---
    VisualSet lines = new VisualSet();
    VisualSet files = new VisualSet();
    VisualSet ratio = new VisualSet();
    VisualSet todos = new VisualSet();
    for (String project : new TreeSet<>(session.history.stringPropertyNames())) {
      System.out.println("project=" + project);
      File root = new File(session.history.getProperty(project));
      ProjectHistory projectHistory = new ProjectHistory(root, project, session.ignore, session.cutoff(project));
      lines.add(projectHistory.lineCount()).setLabel(project);
      files.add(projectHistory.fileCount()).setLabel(project);
      ratio.add(projectHistory.ratios()).setLabel(project);
      todos.add(projectHistory.todos()).setLabel(project);
    }
    // ---
    Dimension dimension = _16_9_1280;
    {
      lines.setPlotLabel("Lines of Code");
      lines.setAxesLabelX("days ago");
      lines.setAxesLabelY("line count");
      JFreeChart jFreeChart = ListPlot.of(lines);
      ChartUtils.saveChartAsPNG(new File(export, "lines.png"), jFreeChart, dimension.width, dimension.height);
    }
    {
      files.setPlotLabel("Source Files");
      files.setAxesLabelX("days ago");
      files.setAxesLabelY("line count");
      JFreeChart jFreeChart = ListPlot.of(files);
      ChartUtils.saveChartAsPNG(new File(export, "files.png"), jFreeChart, dimension.width, dimension.height);
    }
    {
      ratio.setPlotLabel("Ratio lines/files");
      ratio.setAxesLabelX("days ago");
      ratio.setAxesLabelY("ratios");
      JFreeChart jFreeChart = ListPlot.of(ratio);
      ChartUtils.saveChartAsPNG(new File(export, "ratio.png"), jFreeChart, dimension.width, dimension.height);
    }
    {
      todos.setPlotLabel("Todos");
      todos.setAxesLabelX("days ago");
      todos.setAxesLabelY("todos");
      JFreeChart jFreeChart = ListPlot.of(todos);
      ChartUtils.saveChartAsPNG(new File(export, "todos.png"), jFreeChart, dimension.width, dimension.height);
    }
  }
}
