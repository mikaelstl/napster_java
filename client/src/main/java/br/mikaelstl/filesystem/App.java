package br.mikaelstl.filesystem;

import br.mikaelstl.filesystem.server.ClientServer;

public class App 
{
    public static void main( String[] args )
    {
        Client client = new Client();
        new Thread(() -> client.run()).start();

        new Thread(() -> {
            var clientConnection = new ClientServer().start();
            client.setClientConnection(clientConnection);
        }).start();
    }
}
