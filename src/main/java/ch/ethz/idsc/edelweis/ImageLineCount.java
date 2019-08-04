// code by jph
package ch.ethz.idsc.edelweis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.pdf.CDF;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.HistogramDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.red.Median;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** image based on line count */
public enum ImageLineCount {
  ;
  private static final Clip LINES_CLIP = Clips.interval(0, 180);
  static final int WIDTH = 256;
  private static final ScalarTensorFunction COLOR_DATA_GRADIENT = ColorDataGradients.STARRYNIGHT;

  public static BufferedImage generate(BulkParser bulkParser, int width) {
    Distribution distribution = HistogramDistribution.of(LINES_CLIP.of(bulkParser.allLineCounts()), RealScalar.ONE);
    Tensor array = Subdivide.of(0, 1.0, width - 1) //
        .map(InverseCDF.of(distribution)::quantile) //
        .map(LINES_CLIP::rescale);
    Tensor image = Tensors.of(array).map(COLOR_DATA_GRADIENT);
    image = ImageResize.nearest(image, 32, 1);
    CDF cdf = CDF.of(distribution);
    for (Tensor val : Tensors.vector(50, 100, 150)) {
      Scalar p100 = cdf.p_lessThan(val.Get()).multiply(RealScalar.of(width));
      Tensor color = Tensors.vector(0, 0, 0, 255);
      for (int row = 28; row < 32; ++row) {
        int piy = p100.number().intValue();
        if (piy < width)
          image.set(color, row, piy);
      }
    }
    BufferedImage bufferedImage = ImageFormat.of(image);
    Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    {
      graphics.setColor(Color.WHITE);
      Scalar total = Round.FUNCTION.apply(Total.of(bulkParser.allLineCounts()).divide(RealScalar.of(1000)).Get());
      graphics.drawString(String.format("%d files, %sk lines", bulkParser.allLineCounts().length(), total), 2, 13);
    }
    {
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
      graphics.setColor(Color.LIGHT_GRAY);
      Scalar median = Median.of(bulkParser.allLineCounts()).Get();
      graphics.drawString(String.format("median: %s lines per file", Round.of(median)), 2, 30);
    }
    {
      graphics.setColor(Color.BLACK);
      for (Tensor val : Tensors.vector(50, 100)) {
        Scalar p100 = cdf.p_lessThan(val.Get()).multiply(RealScalar.of(width));
        String str = "" + val;
        int len = graphics.getFontMetrics().stringWidth(str);
        int pix = p100.number().intValue() - len;
        if (128 < pix)
          graphics.drawString(str, pix, 27);
      }
    }
    return bufferedImage;
  }
}
