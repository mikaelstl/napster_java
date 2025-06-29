package br.mikaelstl.filesystem.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFileTranfer implements Runnable {
  
  private final Socket socket;

  private final Logger logger;

  private final Map<String, Consumer<String[]>> commands = new HashMap<>();

  public ClientFileTranfer(Socket socket) {
    this.socket = socket;
    this.logger = LoggerFactory.getLogger(ClientFileTranfer.class);
    commands.put("GET", this::getFile);
  }

  @Override
  public void run() {
    try (
      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
    ) {
      logger.info("CONNECTED WITH: " + socket.getInetAddress());
        
      while (true) {
        String[] parts = input.readUTF().split(" ");
        logger.info("REQUEST >>>> " + Arrays.toString(parts));
        
        String command = parts[0].toUpperCase();
        
        var action = commands.get(command);
        if (action != null) {
          action.accept(parts);
        } else {
          output.writeUTF("ERROR Unknown command");
        }
      }
    } catch (EOFException eofException) {
      logger.info("Download finished... Client disconnected");
    } catch (Exception e) {
      logger.error(e.toString() + ": " + e.getMessage());
    }
  }

  public void getFile(String[] args) {
    logger.info(Arrays.toString(args));
    try {
      String ip = args[1];
      if (ip.isEmpty()) {
        logger.info("ERROR Missing ip address");
        return;
      }
      String filename = args[2];
      if (filename.isEmpty()) {
        logger.info("ERROR Missing filename");
        return;
      }

      

      logger.info("ERROR Ip address not founded");
      return;
    } catch (Exception e) {
      logger.error("ERROR: ", e);
      return;
    }
  }
}