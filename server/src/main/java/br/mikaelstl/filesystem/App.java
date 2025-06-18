package br.mikaelstl.filesystem;

public class App 
{
  public static void main( String[] args )
  {
    Server server = new Server();
    while (true) {
      server.run();
    }
  }
}
