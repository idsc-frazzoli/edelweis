// code by jph
package ch.ethz.idsc.edelweis.img;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.pdf.CDF;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.HistogramDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** image based on line count */
public enum ImageAge {
  ;
  private static final Tensor AXES = Tensors.vector(-90, -60, -30);
  private static final Clip LINES_CLIP = Clips.interval(-30 * 4, 0);
  private static final int WIDTH = 256;
  private static final ScalarTensorFunction COLOR_DATA_GRADIENT = ColorDataGradients.DENSITY;

  public static void generate(File directory, String name, Tensor allAges) throws IOException {
    Distribution distribution = HistogramDistribution.of(LINES_CLIP.of(allAges), RealScalar.ONE);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Tensor array = Subdivide.of(0, 1.0, WIDTH - 1).map(inverseCDF::quantile).map(LINES_CLIP::rescale);
    Tensor image = Tensors.of(array).map(COLOR_DATA_GRADIENT);
    image = ImageResize.nearest(image, 16 + 16, 1);
    CDF cdf = CDF.of(distribution);
    for (Tensor val : AXES) {
      Scalar p100 = cdf.p_lessThan(val.Get()).multiply(RealScalar.of(WIDTH));
      Tensor COLOR = Tensors.vector(255, 255, 255, 255);
      COLOR = Tensors.vector(0, 0, 0, 255);
      for (int row = 16 + 12; row < 16 + 16; ++row) {
        int piy = p100.number().intValue();
        if (piy < 256)
          image.set(COLOR, row, piy);
      }
    }
    BufferedImage bufferedImage = ImageFormat.of(image);
    Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    // {
    // graphics.setColor(Color.WHITE);
    // Tensor total = Round.of(Total.of(bulkParser.all_lines()).divide(RealScalar.of(1000)));
    // graphics.drawString(String.format("%d files, %sk lines", bulkParser.all_lines().length(), total), 2, 13);
    // }
    // {
    // graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
    // graphics.setColor(Color.LIGHT_GRAY);
    // Scalar median = Median.of(bulkParser.all_lines()).Get();
    // graphics.drawString(String.format("median: %s lines per file", Round.of(median)), 2, 30);
    // }
    {
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
      graphics.setColor(Color.BLACK);
      for (Tensor val : AXES) {
        Scalar p100 = cdf.p_lessThan(val.Get()).multiply(RealScalar.of(WIDTH));
        String str = "" + val;
        int len = graphics.getFontMetrics().stringWidth(str);
        int pix = p100.number().intValue() - len;
        // if (128 < pix)
        graphics.drawString(str, pix, 27);
      }
    }
    image = ImageFormat.from(bufferedImage);
    // System.out.println(Dimensions.of(img));
    Export.of(new File(directory, name + ".png"), image);
  }
}
