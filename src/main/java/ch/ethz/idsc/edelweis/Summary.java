// code by jph
package ch.ethz.idsc.edelweis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.swing.JLabel;

import ch.ethz.idsc.edelweis.util.GraphicsUtil;
import ch.ethz.idsc.tensor.io.Import;

public enum Summary {
  ;
  private static final Color LINECOLOR = new Color(230, 230, 230, 255);
  private static final int SEP_X = 200;

  static BufferedImage process(String title, List<String> PROJ, Map<String, BulkParser> map) throws IOException {
    System.out.println("PROCESS " + title);
    Properties properties = Import.properties(new File("get", "descriptions.properties"));
    final int width = SEP_X + ImageLineCount.WIDTH;
    BufferedImage bufferedImage = new BufferedImage(width, (32 + 3) * PROJ.size() + 3, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    GraphicsUtil.setQualityHigh(graphics);
    int piy = 1;
    graphics.setColor(LINECOLOR);
    graphics.drawLine(0, piy, width, piy);
    piy += 2;
    for (String proj : PROJ) {
      System.out.println("PROCESS " + title + " " + proj);
      String desc = properties.getProperty(proj);
      if (Objects.isNull(desc))
        desc = "<no description>";
      // System.out.println(desc);
      System.out.println(proj);
      BufferedImage tag = ImageLineCount.generate(map.get(proj), ImageLineCount.WIDTH);
      // ImageIO.read(new File(Main.ICONS_LINES, proj + ".png"));
      // System.out.println(tag);
      graphics.drawImage(tag, SEP_X, piy, new JLabel());
      graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
      graphics.setColor(Color.BLACK);
      graphics.drawString(proj, 0, piy + 15);
      graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
      graphics.setColor(Color.GRAY);
      graphics.drawString(desc, 0, piy + 30);
      piy += 32;
      piy += 1;
      graphics.setColor(LINECOLOR);
      graphics.drawLine(0, piy, width, piy);
      piy += 2;
    }
    // ImageIO.write(bufferedImage, "png", new File(Main.ICONS_OVERVIEW, title + ".png"));
    return bufferedImage;
  }
}
