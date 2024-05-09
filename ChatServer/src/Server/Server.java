package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    public static CopyOnWriteArrayList<PrintWriter> clientOutputStreams = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(3060);
            System.out.println("Waiting for users");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                ClientService clientService = new ClientService(clientSocket, writer);
                Thread clientThread = new Thread(clientService);
                clientThread.start();
                System.out.println("User connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
