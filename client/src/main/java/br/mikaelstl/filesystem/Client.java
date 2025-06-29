package br.mikaelstl.filesystem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.mikaelstl.filesystem.server.ClientFileTranfer;
import br.mikaelstl.filesystem.server.ClientServer;

public class Client implements Runnable {
  private final Logger logger;

  private final String FILES_PATH = "./napster_java/public";

  private final Map<String, Consumer<String[]>> commands = new HashMap<>();

  private final Socket clientConnection;

  public Client() {
    this.logger = LoggerFactory.getLogger(Client.class);
    commands.put("CONNECT", this::connectWithClient);
    
    clientConnection = new ClientServer().start();
  }

  public void createPath() {
    File dir = new File(FILES_PATH);
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }

  @Override
  public void run() {
    try (
      Socket connection = new Socket("192.168.0.2", 1234);
      DataInputStream input = new DataInputStream(connection.getInputStream());  
      DataOutputStream output = new DataOutputStream(connection.getOutputStream());
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))
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
          break;
        }
        
      }
      
    } catch (Exception e) {
      this.logger.error(e.toString());
    }
  }

  public void connectWithClient(String[] args) {
    String ip = args[1];
    if (ip.isEmpty()) {
      logger.error("ERROR Missing ip address");
      return;
    }
    
    try (
      Socket connection = new Socket(ip, 1235);
    ) {

      if (connection.isConnected()) {
        logger.info("Connected with client... Ready to download files.");

        new Thread(new ClientFileTranfer(clientConnection)).start();
      }
      
    } catch (Exception e) {
      logger.error(e.toString()+": "+e.getMessage());
    }
  }
}
