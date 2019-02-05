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
import ch.ethz.idsc.edelweis.util.XYSeriesCollectionBuilder;
import ch.ethz.idsc.subare.plot.ListPlotBuilder;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.UserName;

enum HistorySummary {
  ;
  // private static final Dimension _16_9_1024 = new Dimension(1024, 576);
  private static final Dimension _16_9_1280 = new Dimension(1280, 720);

  public static void main(String[] args) throws IOException {
    // args = new String[] { "stpbase" };
    Session session = new Session(0 < args.length ? args[0] : UserName.get());
    // ---
    for (String project : new TreeSet<>(session.history.stringPropertyNames())) {
      System.out.println("project=" + project);
      File root = new File(session.history.getProperty(project));
      Git git = new Git(root);
      if (!git.isClean())
        throw new RuntimeException();
    }
    XYSeriesCollectionBuilder lines = new XYSeriesCollectionBuilder();
    XYSeriesCollectionBuilder files = new XYSeriesCollectionBuilder();
    XYSeriesCollectionBuilder ratio = new XYSeriesCollectionBuilder();
    for (String project : new TreeSet<>(session.history.stringPropertyNames())) {
      System.out.println("project=" + project);
      File root = new File(session.history.getProperty(project));
      ProjectHistory projectHistory = new ProjectHistory(root, project, session.ignore);
      lines.add(project, projectHistory.lineCount());
      files.add(project, projectHistory.fileCount());
      ratio.add(project, projectHistory.ratios());
    }
    // ---
    Dimension dimension = _16_9_1280;
    {
      ListPlotBuilder listPlotBuilder = new ListPlotBuilder("Lines of Code", "days ago", "line count", lines);
      JFreeChart jFreeChart = listPlotBuilder.getJFreeChart();
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("lines.png"), jFreeChart, dimension.width, dimension.height);
    }
    {
      ListPlotBuilder listPlotBuilder = new ListPlotBuilder("Source Files", "days ago", "file count", files);
      JFreeChart jFreeChart = listPlotBuilder.getJFreeChart();
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("files.png"), jFreeChart, dimension.width, dimension.height);
    }
    {
      ListPlotBuilder listPlotBuilder = new ListPlotBuilder("Ratio lines/files", "days ago", "ratio", ratio);
      JFreeChart jFreeChart = listPlotBuilder.getJFreeChart();
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("ratio.png"), jFreeChart, dimension.width, dimension.height);
    }
  }
}