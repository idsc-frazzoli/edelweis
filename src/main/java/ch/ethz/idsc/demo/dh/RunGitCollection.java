// code by jph
package ch.ethz.idsc.demo.dh;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.edelweis.git.GitCollection;
import ch.ethz.idsc.edelweis.mvn.JavaFile;
import ch.ethz.idsc.edelweis.mvn.JavaPredicates;
import ch.ethz.idsc.edelweis.mvn.MavenCrossing;
import ch.ethz.idsc.edelweis.mvn.MavenRepoStructure;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.StackedTablePlot;
import ch.ethz.idsc.tensor.fig.VisualRow;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.io.DeleteDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ enum RunGitCollection {
  ;
  private static final Scalar THOUSANDTH = RationalScalar.of(1, 1000);
  private static final int WIDTH = 640;
  private static final int HEIGHT = 480;

  private static void write(File root, String name, VisualSet visualSet) throws IOException {
    {
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      ChartUtils.saveChartAsPNG(new File(root, name + "_listPlot.png"), jFreeChart, WIDTH, HEIGHT);
    }
    {
      JFreeChart jFreeChart = StackedTablePlot.of(visualSet);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      ChartUtils.saveChartAsPNG(new File(root, name + "_stackedTable.png"), jFreeChart, WIDTH, HEIGHT);
    }
  }

  public static void run(String name, MavenRepoStructure projectStructure) throws Exception {
    File root = HomeDirectory.Documents("edelweis_mvn", name);
    if (root.exists())
      DeleteDirectory.of(root, 2, 30);
    root.mkdirs();
    TableBuilder tb_mainLines = new TableBuilder();
    TableBuilder tb_testLines = new TableBuilder();
    TableBuilder tb_mainDocs = new TableBuilder();
    TableBuilder tb_testDocs = new TableBuilder();
    TableBuilder tb_mainTodos = new TableBuilder();
    TableBuilder tb_testTodos = new TableBuilder();
    TableBuilder tb_mainFiles = new TableBuilder();
    TableBuilder tb_testFiles = new TableBuilder();
    try (GitCollection gitCollection = new GitCollection(projectStructure.repos())) {
      Date commonFirst = gitCollection.commonFirst();
      System.out.println("max common first: " + commonFirst);
      long currentTimeMillis = System.currentTimeMillis();
      // currentTimeMillis = 1570762236630L;
      long delta = 7 * 24 * 60 * 60 * 1000;
      int weeksago = 0;
      final int min_weeksago = -280;
      TableBuilder tableBuilder = new TableBuilder();
      while (gitCollection.checkout(new Date(currentTimeMillis)) //
          && min_weeksago < weeksago) {
        System.out.println("weeksago=" + weeksago);
        MavenCrossing mavenCrossing = new MavenCrossing( //
            projectStructure.projects(), //
            projectStructure.repos());
        final int main_lines = mavenCrossing.lineCount(JavaFile.MAIN, JavaPredicates.CODE);
        final int test_lines = mavenCrossing.lineCount(JavaFile.TEST, JavaPredicates.CODE);
        final int main_docs = mavenCrossing.lineCount(JavaFile.MAIN, JavaPredicates.DOCUMENTATION);
        final int test_docs = mavenCrossing.lineCount(JavaFile.TEST, JavaPredicates.DOCUMENTATION);
        final int main_todos = mavenCrossing.lineCount(JavaFile.MAIN, JavaPredicates.UNFINISHED);
        final int test_todos = mavenCrossing.lineCount(JavaFile.TEST, JavaPredicates.UNFINISHED);
        final int main_files = mavenCrossing.fileCount(JavaFile.MAIN);
        final int test_files = mavenCrossing.fileCount(JavaFile.TEST);
        tb_mainLines.appendRow(Tensors.vector(weeksago, main_lines));
        tb_testLines.appendRow(Tensors.vector(weeksago, test_lines));
        tb_mainTodos.appendRow(Tensors.vector(weeksago, main_todos));
        tb_testTodos.appendRow(Tensors.vector(weeksago, test_todos));
        tb_mainDocs.appendRow(Tensors.vector(weeksago, main_docs));
        tb_testDocs.appendRow(Tensors.vector(weeksago, test_docs));
        tb_mainFiles.appendRow(Tensors.vector(weeksago, main_files));
        tb_testFiles.appendRow(Tensors.vector(weeksago, test_files));
        tableBuilder.appendRow(Tensors.vector( //
            weeksago, //
            currentTimeMillis / 1000, //
            main_files, //
            main_lines, //
            test_files, //
            test_lines //
        ));
        --weeksago;
        currentTimeMillis -= delta;
      }
      Tensor table = tableBuilder.getTable();
      Export.of(new File(root, name + ".csv"), table);
      // ---
      {
        Tensor tp_mainLines = Reverse.of(tb_mainLines.getTable().copy());
        tp_mainLines.set(THOUSANDTH::multiply, Tensor.ALL, 1);
        Tensor tp_testLines = Reverse.of(tb_testLines.getTable().copy());
        tp_testLines.set(THOUSANDTH::multiply, Tensor.ALL, 1);
        {
          VisualSet visualSet = new VisualSet();
          visualSet.setPlotLabel("Project: " + name);
          visualSet.setAxesLabelX("weeks ago");
          visualSet.setAxesLabelY("lines of code [k]");
          {
            VisualRow visualRow = visualSet.add(tp_mainLines);
            visualRow.setLabel("main");
          }
          {
            VisualRow visualRow = visualSet.add(tp_testLines);
            visualRow.setLabel("test");
          }
          write(root, "lines", visualSet);
        }
      }
      // ---
      {
        Tensor tp_mainDocs = Reverse.of(tb_mainDocs.getTable().copy());
        tp_mainDocs.set(THOUSANDTH::multiply, Tensor.ALL, 1);
        Tensor tp_testDocs = Reverse.of(tb_testDocs.getTable().copy());
        tp_testDocs.set(THOUSANDTH::multiply, Tensor.ALL, 1);
        {
          VisualSet visualSet = new VisualSet();
          visualSet.setPlotLabel("Project: " + name);
          visualSet.setAxesLabelX("weeks ago");
          visualSet.setAxesLabelY("lines of documentation [k]");
          {
            VisualRow visualRow = visualSet.add(tp_mainDocs);
            visualRow.setLabel("main");
          }
          {
            VisualRow visualRow = visualSet.add(tp_testDocs);
            visualRow.setLabel("test");
          }
          write(root, "docs", visualSet);
        }
      }
      // ---
      {
        Tensor tp_mainTodos = Reverse.of(tb_mainTodos.getTable().copy());
        Tensor tp_testTodos = Reverse.of(tb_testTodos.getTable().copy());
        {
          VisualSet visualSet = new VisualSet();
          visualSet.setPlotLabel("Project: " + name);
          visualSet.setAxesLabelX("weeks ago");
          visualSet.setAxesLabelY("open tasks");
          {
            VisualRow visualRow = visualSet.add(tp_mainTodos);
            visualRow.setLabel("main");
          }
          {
            VisualRow visualRow = visualSet.add(tp_testTodos);
            visualRow.setLabel("test");
          }
          write(root, "tasks", visualSet);
        }
      }
      // ---
      {
        Tensor tp_mainFiles = Reverse.of(tb_mainFiles.getTable().copy());
        Tensor tp_testFiles = Reverse.of(tb_testFiles.getTable().copy());
        {
          VisualSet visualSet = new VisualSet();
          visualSet.setPlotLabel("Project: " + name);
          visualSet.setAxesLabelX("weeks ago");
          visualSet.setAxesLabelY("source files");
          {
            VisualRow visualRow = visualSet.add(tp_mainFiles);
            visualRow.setLabel("main");
          }
          {
            VisualRow visualRow = visualSet.add(tp_testFiles);
            visualRow.setLabel("test");
          }
          write(root, "files", visualSet);
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    run("gokart", DatahakiProjects.GOKART);
  }
}
