package br.mikaelstl.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private List<Client> clients = new LinkedList<>();
  private final Map<String, Function<String[], String>> commands = new HashMap<>();

  private int port;

  private final Logger logger;

  Server() {
    this.port = 1234;
    this.logger = LoggerFactory.getLogger(Server.class);
    this.setCommands();
  }

  public void start() {
    this.logger.info("SERVER LISTEN ON PORT " + this.port);

    try (
      ServerSocket server = new ServerSocket(this.port);
    ) {
    
      while (true) {
        Socket connection = server.accept();

        new Thread(new ClientHandler(connection)).start();
      }

    } catch (Exception e) {
      this.logger.error("ERROR ON SERVER: " + e);
    }
  }

  // JOIN { IP - ADDRESS }
  String handleJoin(String[] args) {
    try {
      String ip = args[1];
      if (ip.isEmpty()) {
        return "ERROR Missing your ip address";
      }

      var client = new Client(args[1], new LinkedList<>());

      clients.add(client);
      logger.info(clients.toString());

      return "CONFIRM JOIN";
    } catch (Exception e) {
      return "ERROR Missing your ip address";
    }
  }

  // COMMAND: LEAVE
  String handleLeave(String[] args) {
    try {
      String ip = args[1];
      if (ip.isEmpty()) {
        return "ERROR Missing your ip address";
      }

      boolean result = clients.removeIf(client -> client.ipAddress().equals(args[1]));

      return result
          ? "LEAVE"
          : "ERROR This ip address not founded";
    } catch (Exception e) {
      return "ERROR Missing your ip address";
    }
  }

  // SEARCH { PATTERN }
  String handleSearch(String[] args) {
    logger.info(Arrays.toString(args));
    try {
      String filename = args[1];
      if (filename.isEmpty()) {
        return "ERROR Missing filename";
      }

      String pattern = filename;

      for (Client client : clients) {
        for (File file : client.files()) {
          if (file.filename().contains(pattern)) {
            return "FILE { " + file.filename() + " } { " + client.ipAddress() + " } { " + file.size() + " }";
          }
        }
      }

      return "";
    } catch (Exception e) {
      logger.error(e.toString());
      return "ERROR Missing filename";
    }
  }

  // CREATEFILE { FILENAME1 } { SIZE IN BYTES }
  String createFile(String[] args) {
    logger.info(Arrays.toString(args));
    try {
      String ip = args[1];
      if (ip.isEmpty()) {
        return "ERROR Missing ip address";
      }
      String filename = args[2];
      String size = args[3];
      if (filename.isEmpty() || size.isEmpty()) {
        return "ERROR Missing filename / file size";
      }

      File file = new File(filename, new BigDecimal(size));

      Optional<Client> client = clients.stream().filter((c -> c.ipAddress().equals(ip))).findFirst();

      if (client.isPresent()) {
        client.get().files().add(file);
        return "CONFIRMCREATEFILE " + file.filename();
      }

      return "ERROR Ip address not founded";
    } catch (Exception e) {
      logger.error("ERROR: ", e);
      return "ERROR Missing filename / file size";
    }
  }

  void setCommands() {
    commands.put("JOIN", this::handleJoin);
    commands.put("LEAVE", this::handleLeave);
    commands.put("SEARCH", this::handleSearch);
    commands.put("CREATEFILE", this::createFile);
  }

  class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      ) {
        logger.info("MESSAGE ON: " + socket.getInetAddress());
        
        while (true) {
          String[] parts = input.readUTF().split(" ");

          logger.info("REQUEST >>>> " + Arrays.toString(parts));

          String command = parts[0].toUpperCase();

          Function<String[], String> action = commands.get(command);
          if (action != null) {
            String response = action.apply(parts);
            logger.info(response);

            output.writeUTF(response);
          } else {
            output.writeUTF("ERROR Unknown command");
          }
        }

      } catch (EOFException eofException) {
        logger.info("Client disconnected");
      } catch (Exception e) {
        logger.error(e.toString() + ": " + e.getMessage());
      }
    }
  }
}