package Client;

import Controller.ClientController;
import Model.*;
import Server.Server;
import View.View;

import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public Client() throws Exception {
        InetAddress addr = InetAddress.getByName(null);
        Socket socket = new Socket(addr, Server.PORT);
        GameModel gameModel = new GameModel();
        View view = new View(gameModel);
        ClientController controller = new ClientController(gameModel, view, socket);
    }
}
