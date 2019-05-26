// code by jph
package ch.ethz.idsc.edelweis.util;

import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;

public class IntersectionTest extends TestCase {
  public void testSimple() {
    Set<Integer> set = Intersection.of(Arrays.asList(2, 3, 4), Arrays.asList(1, 2, 3));
    assertEquals(set.size(), 2);
    assertTrue(set.contains(2));
    assertTrue(set.contains(3));
  }

  public void testSimple1() {
    Set<Integer> set = Intersection.of(Arrays.asList(2, 4), Arrays.asList(1, 2, 3));
    assertEquals(set.size(), 1);
    assertTrue(set.contains(2));
  }

  public void testSimple2() {
    Set<Integer> set = Intersection.of(Arrays.asList(2, 3, 4), Arrays.asList(1, 3));
    assertEquals(set.size(), 1);
    assertTrue(set.contains(3));
  }
}
