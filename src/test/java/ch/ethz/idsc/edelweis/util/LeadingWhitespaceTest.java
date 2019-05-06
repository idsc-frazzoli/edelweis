// code by jph
package ch.ethz.idsc.edelweis.util;

import junit.framework.TestCase;

public class LeadingWhitespaceTest extends TestCase {
  public void testNone() {
    assertEquals(LeadingWhitespace.of(""), 0);
    assertEquals(LeadingWhitespace.of("data"), 0);
    assertEquals(LeadingWhitespace.of("data   "), 0);
    assertEquals(LeadingWhitespace.of("data  haki"), 0);
  }

  public void testLeading() {
    assertEquals(LeadingWhitespace.of("  "), 2);
    assertEquals(LeadingWhitespace.of("  data"), 2);
    assertEquals(LeadingWhitespace.of("  data   "), 2);
    assertEquals(LeadingWhitespace.of("  data   haki"), 2);
  }
}
