package br.mikaelstl.filesystem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements Runnable {
  private final Logger logger;

  Client() {
    this.logger = LoggerFactory.getLogger(Client.class);
  }

  @Override
  public void run() {
    try (
      Socket connection = new Socket("192.168.0.5", 1234);
      DataInputStream input = new DataInputStream(connection.getInputStream());  
      DataOutputStream output = new DataOutputStream(connection.getOutputStream());
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))
    ) {
      String command;
      while (
        !(command = reader.readLine()).contains("LEAVE")
      ) {
        
        output.writeUTF(command);

        String response = input.readUTF();
        logger.info(response);
        
      }
      
    } catch (Exception e) {
      this.logger.error(e.toString());
    }
  }
}
