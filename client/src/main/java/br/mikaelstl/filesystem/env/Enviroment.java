package br.mikaelstl.filesystem.env;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Enviroment {
  public static final int CLIENT_PORT = 1234;
  public static final int SERVER_PORT = 1235;
  
  private static final String USER_HOME = System.getProperty("user.home");
  public static final Path SHARED_FOLDER = Paths.get(USER_HOME, "napster", "public");

}
