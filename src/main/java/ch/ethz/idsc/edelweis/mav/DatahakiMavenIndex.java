// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/* package */ enum DatahakiMavenIndex implements ProjectStructure {
  GOKART() {
    @Override
    public List<File> repos() {
      return Arrays.asList( //
          new File("/home/datahaki/Projects/tensor"), //
          new File("/home/datahaki/Projects/subare"), //
          new File("/home/datahaki/Projects/owl"), //
          new File("/home/datahaki/Projects/retina"));
    }

    @Override
    public List<String> projects() {
      return Arrays.asList( //
          "ch.ethz.idsc.tensor", //
          "ch.ethz.idsc.subare", //
          "ch.ethz.idsc.owl", //
          "ch.ethz.idsc.sophus", //
          "ch.ethz.idsc.retina", //
          "ch.ethz.idsc.gokart", //
          "ch.ethz.idsc.demo");
    }
  }, //
  NOCTURNE() {
    @Override
    public List<File> repos() {
      return Arrays.asList( //
          new File("/home/datahaki/Projects/nocturne"));
    }

    @Override
    public List<String> projects() {
      return Arrays.asList("ch.ethz.idsc.nocturne");
    }
  };
  // ---
  public final MavenCrossing mavenCrossing() {
    return new MavenCrossing(projects());
  }
  // static MavenCrossing amodeus() {
  // MavenCrossing mavenCrossing = new MavenCrossing();
  // mavenCrossing.addProject("amod");
  // mavenCrossing.addProject("ch.ethz.idsc.amodeus");
  // // // ---
  // // List<File> list = Arrays.asList( //
  // // new File("/home/datahaki/Projects/amod"), //
  // // new File("/home/datahaki/Projects/amodeus"), //
  // // new File("/home/datahaki/Projects/amodidsc"));
  // return mavenCrossing;
  // }
  //
  // public static MavenCrossing nocturne() {
  // MavenCrossing mavenCrossing = new MavenCrossing();
  // mavenCrossing.addProject();
  // return mavenCrossing;
  // }
  //
  // public static void main(String[] args) throws FileNotFoundException, IOException {
  // // 116521
  // List<File> list = Arrays.asList( //
  // new File("/home/datahaki/Projects/nocturne"));
  // MavenCrossing mavenCrossing = gokart();
  // mavenCrossing.compile(list);
  // mavenCrossing.print();
  // // amodeus();
  // // nocturne();
  // }
}
