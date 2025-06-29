package br.mikaelstl.filesystem.server;

import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientServer {
  private final Logger logger;

  private final int CLIENT_PORT;

  public ClientServer() {
    this.logger = LoggerFactory.getLogger(ClientServer.class);
    CLIENT_PORT = 1235;
  }

  public Socket start() {
    try (
      ServerSocket server = new ServerSocket(CLIENT_PORT);
    ) {
      while (true) {
        Socket connection = server.accept();

        // new Thread(new ClientFileTranfer(connection)).start();

        return connection;
      }
    } catch (Exception e) {
      logger.error(e.toString()+": "+e.getLocalizedMessage());
      return null;
    }
  }
}
