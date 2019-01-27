// code by jph
package ch.ethz.idsc.edelweis.htm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/* package */ class BufferedHtmlUtf8 extends HtmlUtf8 {
  private final StringBuilder stringBuilder = new StringBuilder();

  protected BufferedHtmlUtf8(File file) {
    super(file);
  }

  @Override
  protected void private_append(Object object) {
    stringBuilder.append(object);
  }

  @Override
  public void close() {
    super.close();
    try (OutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(stringBuilder.toString().getBytes(CHARSET));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
