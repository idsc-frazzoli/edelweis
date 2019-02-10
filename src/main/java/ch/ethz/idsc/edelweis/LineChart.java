// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.Collection;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Log;

public enum LineChart {
  ;
  public static void of(Collection<BulkParser> bulkParsers, File directory) {
    VisualSet visualSet = new VisualSet();
    for (BulkParser bulkParser : bulkParsers) {
      Tensor lineCounts = bulkParser.allLineCounts();
      if (1 < lineCounts.length()) {
        Tensor domain = Subdivide.of(0, 1, lineCounts.length() - 1);
        Tensor values = Log.of(Sort.of(lineCounts.map(Max.function(RealScalar.ONE))));
        visualSet.add(domain, values).setLabel(bulkParser.name());
      }
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    try {
      ChartUtils.saveChartAsPNG(new File(directory, "lines.png"), jFreeChart, 1024, 768);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
