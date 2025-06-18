package br.mikaelstl.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {
  private Logger logger;

  Client() {
    this.logger = Logger.getLogger("CLIENT");
  }

  @Override
  public void run() {
    try (
      Socket connection = new Socket("127.0.0.1", 6061);
      DataInputStream input = new DataInputStream(connection.getInputStream());  
      DataOutputStream output = new DataOutputStream(connection.getOutputStream())
    ) {

      output.writeUTF("CLIENTE ACESSANDO");

      logger.log(Level.INFO, input.readUTF());
      
    } catch (Exception e) {
      this.logger.warning(e.getLocalizedMessage());
    }
  }
}
