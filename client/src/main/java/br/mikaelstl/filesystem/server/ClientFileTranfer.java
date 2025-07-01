package br.mikaelstl.filesystem.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.mikaelstl.filesystem.env.Enviroment;

public class ClientFileTranfer implements Runnable {
  
  private final Socket socket;

  private final Logger logger;


  public ClientFileTranfer(Socket socket/* , String filename */) {
    this.socket = socket;
    this.logger = LoggerFactory.getLogger(ClientFileTranfer.class);
    // commands.put("GET", this::getFile);
  }

  @Override
  public void run() {
    try (
      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
    ) {
      logger.info("CONNECTED WITH: " + socket.getInetAddress());
      output.writeUTF("CONNECTED WITH: "+socket.getLocalAddress());

      String filename = input.readUTF();

      Path filePath = Enviroment.SHARED_FOLDER.resolve(filename);
      if (!Files.exists(filePath)) {
        output.writeUTF("ERROR: File not found");
        return;
      }
      var fileSize = Files.size(filePath);
      output.writeLong(fileSize);

      try (InputStream fileIn = Files.newInputStream(filePath)) {
        byte[] buffer = new byte[4096];

        int bytesRead;
        while ((bytesRead = fileIn.read(buffer)) != -1) {
          output.write(buffer, 0, bytesRead);
        }
      } catch (Exception e) {
        logger.error(e.toString()+": "+e.getMessage());
        output.writeUTF("ERROR "+e.toString());
      }
      
    } catch (EOFException eofException) {
      logger.info("Download finished... Client disconnected");
    } catch (Exception e) {
      logger.error(e.toString() + ": " + e.getMessage());
    }
  }
}