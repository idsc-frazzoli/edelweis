// code by jph
package ch.ethz.idsc.edelweis.prj;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import ch.ethz.idsc.edelweis.Session;
import ch.ethz.idsc.subare.plot.ListPlotBuilder;
import ch.ethz.idsc.subare.plot.XYDatasets;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Pretty;

enum HistorySummary {
  ;
  public static void main(String[] args) throws IOException {
    Session session = new Session("test");
    Map<String, Tensor> lines = new LinkedHashMap<>();
    Map<String, Tensor> files = new LinkedHashMap<>();
    for (String project : new TreeSet<>(session.projects.stringPropertyNames())) {
      System.out.println("project=" + project);
      File root = new File(session.projects.getProperty(project));
      ProjectHistory projectHistory = new ProjectHistory(root, project, session.ignore);
      lines.put(project, projectHistory.lineCount());
      files.put(project, projectHistory.fileCount());
      // System.out.println(Pretty.of());
      System.out.println(Pretty.of(projectHistory.lineCount()));
    }
    // ---
    {
      XYDataset xyDataset = XYDatasets.create(lines);
      ListPlotBuilder listPlotBuilder = new ListPlotBuilder("line count", "days ago", "lines", xyDataset);
      JFreeChart jFreeChart = listPlotBuilder.getJFreeChart();
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("lines.png"), jFreeChart, 1024, 768);
    }
    {
      XYDataset xyDataset = XYDatasets.create(files);
      ListPlotBuilder listPlotBuilder = new ListPlotBuilder("file count", "days ago", "files", xyDataset);
      JFreeChart jFreeChart = listPlotBuilder.getJFreeChart();
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("files.png"), jFreeChart, 1024, 768);
    }
  }
}
