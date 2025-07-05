package br.mikaelstl.filesystem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.mikaelstl.filesystem.env.Enviroment;
import br.mikaelstl.filesystem.server.ClientFileTranfer;
import br.mikaelstl.filesystem.server.ClientServer;

public class Client implements Runnable {
  private final ClientServer server;
  private Thread serverThread;

  private final Logger logger;

  private final Map<String, Consumer<String[]>> commands = new HashMap<>();

  public Client() {
    server = new ClientServer();
    this.logger = LoggerFactory.getLogger(Client.class);
    // commands.put("CONNECT", this::connectWithClient);
    commands.put("GET", this::getFile);
    
    createDirs();

    startFileServer();
  }

  public void createDirs() {
    File dir = Enviroment.SHARED_FOLDER.toFile();
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }

  @Override
  public void run() {
    try (
      Socket connection = new Socket("192.168.0.4", Enviroment.CLIENT_PORT);
      DataInputStream input = new DataInputStream(connection.getInputStream());  
      DataOutputStream output = new DataOutputStream(connection.getOutputStream());
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    ) {
      String command;
      while (true) {
        command = reader.readLine();

        String[] parts = command.split(" ");
        logger.info("REQUEST >>>> " + Arrays.toString(parts));
        
        var action = commands.get(parts[0]);
        if (action != null) {
          action.accept(parts);
        }

        output.writeUTF(command);
      
        String response = input.readUTF();
        logger.info(response);

        if (command.contains("LEAVE")) {
          closeFileServer();
          break;
        }
        
      }
      
    } catch (Exception e) {
      this.logger.error(e.toString());
    }
  }

  public void getFile(String[] args) {
    String ip = args[1];
    if (ip.isEmpty()) {
      logger.error("ERROR Missing ip address");
      return;
    }

    String filename = args[2];
    if (filename.isEmpty()) {
      logger.error("ERROR Missing ip address");
      return;
    }
    
    try (
      Socket socket = new Socket(ip, Enviroment.SERVER_PORT);
      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
    ) {

      output.writeUTF(filename);

      if (socket.isClosed()) {
        output.writeUTF("ERROR This client is offline");
        return;
      }
      
      Path destination = Enviroment.SHARED_FOLDER.resolve(filename);
      downloadFile(destination, input, output);
      
    } catch (Exception e) {
      logger.error(e.toString()+": "+e.getMessage());
    }
  }

  public void downloadFile(
    Path destination,
    DataInputStream input,
    DataOutputStream output
  ) {
    try {
      long fileSize = input.readLong();
      if (fileSize == -1) {
        logger.error("ERROR File not founded");
        return;
      }

      try (OutputStream outputFile = Files.newOutputStream(destination)) {
        byte[] buffer = new byte[4096];
        long totalRead = 0;
        int bytesRead;
      
        while (totalRead < fileSize && (bytesRead = input.read(buffer)) != -1) {
          outputFile.write(buffer, 0, bytesRead);
          totalRead += bytesRead;
        }
      
      } catch (Exception e) {
        // TODO: handle exception
      } 
    
    } catch (Exception e) {
      logger.error(e.toString()+": "+e.getMessage());
    }
  }

  public void startFileServer() {
    serverThread = new Thread(() -> server.start());
    serverThread.start();
  }

  public void closeFileServer() {
    server.stop();
    serverThread.interrupt();
  }

}
