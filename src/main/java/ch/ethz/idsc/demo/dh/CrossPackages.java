// code by jph
package ch.ethz.idsc.demo.dh;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import ch.ethz.idsc.edelweis.mvn.Imports;
import ch.ethz.idsc.edelweis.mvn.JavaFile;
import ch.ethz.idsc.edelweis.mvn.MavenCrossing;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum CrossPackages {
  ;
  private static final ScalarUnaryOperator CAP = Min.function(RealScalar.of(1));
  private static final ScalarUnaryOperator SEP = Max.function(RealScalar.of(0.5));

  private static BufferedImage run(DatahakiProjects mavenRepoStructure) throws IOException {
    MavenCrossing mavenCrossing = new MavenCrossing(mavenRepoStructure.projects(), mavenRepoStructure.repos());
    // determine packages in order
    Map<String, Integer> sep = new LinkedHashMap<>();
    Map<String, Integer> map = new LinkedHashMap<>();
    for (String project : mavenRepoStructure.projects()) {
      {
        final int key = map.size();
        int index = Math.max(0, project.lastIndexOf('.') + 1);
        sep.put(project.substring(index), key);
        map.put("separator_" + project, key);
      }
      for (JavaFile javaFile : mavenCrossing.files(project)) {
        String _package = javaFile.getPackage();
        if (!map.containsKey(_package))
          map.put(_package, map.size());
      }
    }
    {
      final int key = map.size();
      sep.put("separator_z", key);
      map.put("separator_z", key);
    }
    // ---
    Tensor tensor = Array.zeros(map.size(), map.size());
    {
      for (int index : sep.values()) {
        tensor.set(SEP, Tensor.ALL, index);
        tensor.set(SEP, index, Tensor.ALL);
      }
    }
    // map.entrySet().stream().forEach(System.out::println);
    // determine imports
    for (String project : mavenRepoStructure.projects())
      for (JavaFile javaFile : mavenCrossing.files(project)) {
        String _package = javaFile.getPackage();
        int from = map.get(_package);
        List<String> imports = javaFile.imports();
        for (String imp : imports) {
          String ref = Imports.getPackage(imp);
          if (map.containsKey(ref))
            tensor.set(RealScalar.ONE::add, map.get(ref), from);
        }
      }
    tensor = tensor.map(CAP);
    Tensor image = ArrayPlot.of(ImageResize.nearest(tensor, 4), ColorDataGradients.CLASSIC);
    BufferedImage bufferedImage = ImageFormat.of(image);
    {
      Graphics2D graphics = bufferedImage.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
      graphics.setColor(new Color(255, 255, 0, 192));
      for (Entry<String, Integer> entry : sep.entrySet()) {
        int pix = entry.getValue() * 4;
        graphics.drawString(entry.getKey(), pix + 4, pix + 20);
      }
    }
    return bufferedImage;
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    DatahakiProjects datahakiProjects = DatahakiProjects.GOKART;
    BufferedImage bufferedImage = run(datahakiProjects);
    ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures(datahakiProjects.name() + ".png"));
  }
}
