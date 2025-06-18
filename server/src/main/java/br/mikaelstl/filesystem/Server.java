package br.mikaelstl.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
  private int port;

  private Logger logger;

  Server() {
    this.port = 6061;
    this.logger = Logger.getLogger("SERVER");
  }

  @Override
  public void run() {
    super.run();
    this.logger.info("SERVER LISTEN ON PORT: "+this.port);

    try (
      ServerSocket server = new ServerSocket(this.port);
      Socket connection = server.accept();
      DataInputStream input = new DataInputStream(connection.getInputStream());  
      DataOutputStream output = new DataOutputStream(connection.getOutputStream())
    ) {

      String message = input.readUTF();
      this.logger.log(Level.INFO, "MESSAGE ON: "+connection.getInetAddress());
      this.logger.log(Level.INFO, message);

      output.writeUTF("RECEBIDO PELO SERVIDOR");

    } catch (Exception e) {
      this.logger.warning("ERROR ON SERVER:");
      this.logger.warning(e.getLocalizedMessage());
    }
  }
}
