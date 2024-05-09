package Client.dao;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public interface UserRepo {
    void createUser(String username, String password);
    void userLogin(String username, String password);
    void connectToServer(String username, String password);
    int getUserID(String username) throws SQLException;
    byte[] readFile(String fileName) throws IOException;
    byte[] transfer(Socket clientSocket) throws IOException;
    void transfer(Socket clientSocket, byte[] fileBytes) throws IOException;
    void storeFile(int senderID, String fileName, byte[] fileBytes) throws SQLException;
    void storeMessage(int senderID, String message) throws SQLException;
    void viewMessages();
}
