// code by jph
package ch.ethz.idsc.eunoia.lang;

import java.io.File;

public interface ParserCode {
  /** @return */
  File file();

  /** @return */
  int lineCount();
}
