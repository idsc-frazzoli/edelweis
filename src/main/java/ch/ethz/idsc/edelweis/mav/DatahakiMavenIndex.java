// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**/ enum DatahakiMavenIndex {
  ;
  static MavenCrossing gokart() {
    MavenCrossing mavenCrossing = new MavenCrossing();
    mavenCrossing.addProject("ch.ethz.idsc.tensor");
    mavenCrossing.addProject("ch.ethz.idsc.subare");
    mavenCrossing.addProject("ch.ethz.idsc.owl");
    mavenCrossing.addProject("ch.ethz.idsc.sophus");
    mavenCrossing.addProject("ch.ethz.idsc.retina");
    mavenCrossing.addProject("ch.ethz.idsc.gokart");
    mavenCrossing.addProject("ch.ethz.idsc.demo");
    // ---
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/tensor"));
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/subare"));
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/owl"));
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/retina"));
    return mavenCrossing;
  }

  static MavenCrossing amodeus() {
    MavenCrossing mavenCrossing = new MavenCrossing();
    mavenCrossing.addProject("amod");
    mavenCrossing.addProject("ch.ethz.idsc.amodeus");
    // ---
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/amod"));
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/amodeus"));
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/amodidsc"));
    return mavenCrossing;
  }

  public static MavenCrossing nocturne() {
    MavenCrossing mavenCrossing = new MavenCrossing();
    mavenCrossing.addProject("ch.ethz.idsc.nocturne");
    // ---
    mavenCrossing.addRepo(new File("/home/datahaki/Projects/nocturne"));
    return mavenCrossing;
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    // 116521
    MavenCrossing mavenCrossing = gokart();
    mavenCrossing.compile();
    // amodeus();
    // nocturne();
  }
}
