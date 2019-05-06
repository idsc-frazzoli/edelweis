// code by jph
package ch.ethz.idsc.edelweis.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum LeadingWhitespace {
  ;
  private static final Pattern PATTERN = Pattern.compile("\\s*");

  /** @param string
   * @return number of leading whitespace characters in given string */
  public static int of(String string) {
    Matcher matcher = PATTERN.matcher(string);
    matcher.find();
    return matcher.group().length();
  }
}
