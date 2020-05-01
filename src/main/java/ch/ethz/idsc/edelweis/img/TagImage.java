// code by jph
package ch.ethz.idsc.edelweis.img;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

/** image based on line count */
public enum TagImage {
  ;
  private static final Color LINECOLOR = new Color(230, 230, 230, 255);
  private static final int ICON = 60;
  private static final int SEP_X = 200;
  private static final int WIDTH = SEP_X + ICON + 5;
  private static final int HEIGHT = ICON + 4;
  private static final File GET_ICONS = new File("get", "icons");

  public static BufferedImage of(String name, Tensor allLineCounts) {
    String icon = name.endsWith("-test") //
        ? name.substring(0, name.length() - 5)
        : name;
    File file = new File(GET_ICONS, icon + ".png");
    try {
      BufferedImage iconImage = file.isFile() //
          ? ImageIO.read(file)
          : new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
      Properties properties = Import.properties(new File("get", "descriptions.properties"));
      BufferedImage bufferedImage = //
          new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      GraphicsUtil.setQualityHigh(graphics);
      final int pix = 2 + ICON + 1;
      int piy = 0;
      graphics.setColor(LINECOLOR);
      graphics.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
      String desc = properties.getProperty(name);
      if (Objects.isNull(desc))
        desc = "<no description>";
      graphics.drawImage(iconImage, 2, 2, ICON, ICON, null);
      graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
      graphics.setColor(Color.BLACK);
      graphics.drawString(name, pix, piy + 14);
      graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
      graphics.setColor(Color.GRAY);
      graphics.drawString(desc, pix, piy + 27);
      piy += 30;
      if (0 < allLineCounts.length()) {
        BufferedImage tag = ImageLineCount.generate(allLineCounts, SEP_X);
        graphics.drawImage(tag, pix, piy, new JLabel());
      }
      return bufferedImage;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }
}
