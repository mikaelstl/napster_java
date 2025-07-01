package br.mikaelstl.filesystem.server;

import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.mikaelstl.filesystem.env.Enviroment;

public class ClientServer {
  private final Logger logger;

  public ClientServer() {
    this.logger = LoggerFactory.getLogger(ClientServer.class);
  }

  public Socket start() {
    try (
      ServerSocket server = new ServerSocket(Enviroment.SERVER_PORT);
    ) {
      this.logger.info("SERVER LISTEN ON PORT " + Enviroment.SERVER_PORT);
      while (true) {
        Socket connection = server.accept();

        new Thread(new ClientFileTranfer(connection)).start();
      }
    } catch (Exception e) {
      logger.error(e.toString()+": "+e.getLocalizedMessage());
      return null;
    }
  }
}
