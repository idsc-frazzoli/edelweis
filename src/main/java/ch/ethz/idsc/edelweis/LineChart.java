// code by jph
package ch.ethz.idsc.edelweis;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import ch.ethz.idsc.subare.plot.ListPlotBuilder;
import ch.ethz.idsc.subare.plot.XYDatasets;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Log;

public enum LineChart {
  ;
  public static void of(Collection<BulkParser> bulkParsers, File directory) {
    Map<String, Tensor> map = new LinkedHashMap<>();
    for (BulkParser bulkParser : bulkParsers) {
      Tensor lineCounts = bulkParser.allLineCounts();
      Tensor tensor = Transpose.of(Tensors.of(Log.of(Range.of(1, lineCounts.length() + 1)), Log.of(Sort.of(lineCounts.map(Max.function(RealScalar.ONE))))));
      map.put(bulkParser.name(), tensor);
    }
    XYDataset xyDataset = XYDatasets.create(map);
    ListPlotBuilder listPlotBuilder = new ListPlotBuilder("lines", "file", "lines", xyDataset);
    JFreeChart jFreeChart = listPlotBuilder.getJFreeChart();
    try {
      ChartUtils.saveChartAsPNG(new File(directory, "lines.png"), jFreeChart, 1024, 768);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
