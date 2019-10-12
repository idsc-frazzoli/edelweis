// code by jph
package ch.ethz.idsc.demo.dh;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.edelweis.mvn.ProjectStructure;

/* package */ enum ProjectDatahaki implements ProjectStructure {
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
          "ch.ethz.idsc.sophus", //
          "ch.ethz.idsc.owl", //
          "ch.ethz.idsc.retina", //
          "ch.ethz.idsc.gokart", //
          "ch.ethz.idsc.demo");
    }
  }, //
  AMODEUS() {
    @Override
    public List<File> repos() {
      return Arrays.asList( //
          new File("/home/datahaki/Projects/amod"), //
          new File("/home/datahaki/Projects/amodeus"));
    }

    @Override
    public List<String> projects() {
      return Arrays.asList( //
          "amod", //
          "ch.ethz.idsc.amodeus");
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
  }, //
  ;
}
