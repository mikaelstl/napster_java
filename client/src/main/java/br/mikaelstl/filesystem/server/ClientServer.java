package br.mikaelstl.filesystem.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.mikaelstl.filesystem.env.Enviroment;

public class ClientServer {
  private ServerSocket server;

  private final Logger logger;

  public ClientServer() {
    this.logger = LoggerFactory.getLogger(ClientServer.class);
  }

  public void start() {
    try {
      server = new ServerSocket(Enviroment.SERVER_PORT);
      this.logger.info("SERVER LISTEN ON PORT " + Enviroment.SERVER_PORT);
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Socket connection = server.accept();

          new Thread(new ClientFileTranfer(connection)).start();
        } catch (SocketException e) {
          logger.info("Server fechado");
        }
      }
    } catch (Exception e) {
      logger.error(e.toString()+": "+e.getLocalizedMessage());
    } finally {
      this.stop();
    }
  }

  public void stop() {
    try {
      if (server != null && !server.isClosed()) {
        server.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
