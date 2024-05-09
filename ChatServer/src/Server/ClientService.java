package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientService implements Runnable {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private static CopyOnWriteArrayList<PrintWriter> clientOutputStreams;

    public ClientService(Socket clientSocket, PrintWriter writer) {
        this.socket = clientSocket;
        this.writer = writer;
        clientOutputStreams = Server.clientOutputStreams;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while((message = reader.readLine()) != null){
                for(PrintWriter clientOutputStream : clientOutputStreams){
                    clientOutputStream.println(message);
                    clientOutputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
                if(socket != null) {
                    socket.close();
                }
                if(writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
