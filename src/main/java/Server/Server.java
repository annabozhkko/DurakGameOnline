package Server;

import Controller.ServerController;
import Model.GameModel;
import View.View;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT = 8080;

    public Server() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        GameModel gameModel = new GameModel();
        View view = new View(gameModel);
        Socket socket = serverSocket.accept();
        ServerController controller = new ServerController(gameModel, view, serverSocket, socket);
    }
}
