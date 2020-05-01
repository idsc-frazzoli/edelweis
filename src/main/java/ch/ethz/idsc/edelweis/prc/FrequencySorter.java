// code by claudio ruch
package ch.ethz.idsc.edelweis.prc;

import java.util.Comparator;
import java.util.Map.Entry;

/** Helper class used in {@link ExtDependencies}
 * @author clruch */
/* package */ enum FrequencySorter implements Comparator<Entry<String, Long>> {
  INSTANCE;

  @Override
  public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
    if (o1.getValue() < o2.getValue())
      return 1;
    if (o1.getValue() > o2.getValue())
      return -1;
    return 0;
  }
}
