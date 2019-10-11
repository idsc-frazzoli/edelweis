// code by jph
package ch.ethz.idsc.edelweis.mav;

import java.io.File;

import ch.ethz.idsc.edelweis.git.Git;

public enum RepoStatus {
  ;
  public static void print(File directory) {
    Git git = Git.requireClean(directory);
    System.out.println("branch=" + git.branch());
  }
}
