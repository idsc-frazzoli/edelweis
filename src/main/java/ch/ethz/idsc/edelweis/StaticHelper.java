// code by jph
package ch.ethz.idsc.edelweis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.edelweis.img.ImageAge;
import ch.ethz.idsc.edelweis.img.TagImage;

/* package */ enum StaticHelper {
  ;
  public static BufferedImage of(BulkParser bulkParser) {
    return TagImage.of(bulkParser.name(), bulkParser.allLineCounts());
  }

  public static void generate(File directory, BulkParser bulkParser) throws IOException {
    ImageAge.generate(directory, bulkParser.name(), bulkParser.allAges());
  }
}
