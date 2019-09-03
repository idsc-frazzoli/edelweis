// code by jph
package ch.ethz.idsc.edelweis;

import java.util.Map;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.util.plot.Histogram;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Tally;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Increment;

public enum ParserImages {
  ;
  public static JFreeChart lines(BulkParser bulkParser, int resolution) {
    Scalar res = RealScalar.of(resolution);
    Tensor tensor = bulkParser.allLineCounts();
    tensor = tensor.map(Min.function(RealScalar.of(200)));
    Tensor ceil = Floor.of(tensor.divide(res));
    Scalar max = ceil.stream().reduce(Max::of).get().Get();
    Tensor domain = Range.of(0, max.number().intValue() + 1);
    Tensor values = Tensors.empty();
    Map<Tensor, Long> map = Tally.of(ceil);
    for (Tensor _x : domain)
      values.append(RealScalar.of(map.containsKey(_x) ? map.get(_x) : 0));
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel(bulkParser.name() + " line count histogram");
    visualSet.setAxesLabelX("lines");
    visualSet.setAxesLabelY("file count");
    visualSet.add(domain.map(Increment.ONE).multiply(res), values);
    return Histogram.of(visualSet);
  }
}
