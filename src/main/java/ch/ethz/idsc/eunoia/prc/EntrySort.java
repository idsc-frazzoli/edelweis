// code by jph
package ch.ethz.idsc.eunoia.prc;

import java.util.Comparator;
import java.util.Map.Entry;

import ch.ethz.idsc.eunoia.lang.ParserJava;

/* package */ enum EntrySort implements Comparator<Entry<ParserJava, Integer>> {
  INSTANCE;
  // ---
  @Override
  public int compare(Entry<ParserJava, Integer> entry1, Entry<ParserJava, Integer> entry2) {
    int cmp = Integer.compare(entry2.getValue(), entry1.getValue());
    return cmp == 0 //
        ? entry1.getKey().identifier().compareTo(entry2.getKey().identifier())
        : cmp;
  }
}
