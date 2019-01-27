// code by jph
package ch.ethz.idsc.edelweis.htm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/* package */ class FlushedHtmlUtf8 extends HtmlUtf8 {
  protected FlushedHtmlUtf8(File file) {
    super(file);
  }

  @Override
  protected void private_append(Object object) {
    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, true), CHARSET)) {
      outputStreamWriter.write(object.toString());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
