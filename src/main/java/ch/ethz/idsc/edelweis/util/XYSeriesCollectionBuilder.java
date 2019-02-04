// code by jph
package ch.ethz.idsc.edelweis.util;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ch.ethz.idsc.tensor.Tensor;

public class XYSeriesCollectionBuilder extends XYSeriesCollection {
  public void add(String string, Tensor points) {
    XYSeries xySeries = new XYSeries(string);
    for (Tensor row : points)
      xySeries.add(row.Get(0).number(), row.Get(1).number());
    addSeries(xySeries);
  }
}
