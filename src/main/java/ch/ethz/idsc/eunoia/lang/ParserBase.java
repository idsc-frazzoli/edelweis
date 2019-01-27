// code by jph
package ch.ethz.idsc.eunoia.lang;

import java.io.File;

public abstract class ParserBase implements ParserCode {
  private final File file;

  public ParserBase(File file) {
    this.file = file;
  }

  @Override
  public final File file() {
    return file;
  }
}
