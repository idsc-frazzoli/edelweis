// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.edelweis.img.TagImage;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunTagImages {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    ProjectStructure projectStructure = ProjectDatahaki.GOKART;
    projectStructure.repos().forEach(RepoStatus::print);
    MavenCrossing mavenCrossing = new MavenCrossing(projectStructure.projects(), projectStructure.repos());
    for (String project : projectStructure.projects()) {
      Tensor allLineCounts = Tensor.of(mavenCrossing.files(project).stream() //
          .filter(JavaFile::isMain) //
          .map(javaFile -> javaFile.count(JavaPredicates.RELEVANT_CODE)) //
          .map(RealScalar::of));
      String name = project.substring(project.lastIndexOf('.') + 1);
      BufferedImage bufferedImage = TagImage.of(name, allLineCounts);
      ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures("tag_" + name + ".png"));
    }
  }
}
