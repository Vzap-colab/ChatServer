package Client.dao;

import Client.DBConfig;
import Client.Model.User;

import java.io.*;
import java.sql.*;
import java.net.Socket;
import java.nio.file.Files;

public class UserRepoImpl extends User implements UserRepo {
    Connection con;
    @Override
    public void createUser(String username, String password) {
        try {
            con = DBConfig.getCon();
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            System.out.println("User successfully created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userLogin(String username, String password) {
        try {
            con = DBConfig.getCon();
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("User successfully logged in");
            } else {
                System.out.println("Your username or password is invalid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectToServer(String username, String password) {
        try {
            con = DBConfig.getCon();

            Socket clientSocket = new Socket("192.168.8.170", 3060);
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            writer.println(username);
            writer.println(password);
            writer.flush();

            int senderID = getUserID(username);

            String response = reader.readLine();
            System.out.println(response);

            new Thread(() -> {
                String message;
                try {
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                        if (message.startsWith("f:")) {
                            String fileName = message.substring(3);
                            byte[] fileBytes = transfer(clientSocket);
                            storeFile(senderID, fileName, fileBytes);
                        } else {
                            storeMessage(senderID, message);
                        }
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }).start();

            System.out.println("Please enter your message OR");
            System.out.println("Please enter 'f:' followed by the name of your file if you wish to send a file OR");
            System.out.println("Please enter 'quit' to quit messaging : ");

            String message2;

            BufferedReader userMessage = new BufferedReader(new InputStreamReader(System.in));
            while ((message2 = userMessage.readLine()) != null) {
                writer.println(username + " : " + message2);
                writer.flush();
                if (message2.equalsIgnoreCase("quit")) {
                    break;
                }
                if (message2.startsWith("f:")) {
                    String fileName = message2.substring(3);
                    byte[] fileBytes = readFile(fileName);
                    transfer(clientSocket, fileBytes);
                }
            }
            writer.close();
            reader.close();
            clientSocket.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getUserID(String username) throws SQLException {
        Connection con = DBConfig.getCon();
        String query = "SELECT userID FROM users WHERE username = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("userID");
        }
        return -1;
    }

    @Override
    public byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        byte[] fileBytes;
        fileBytes = Files.readAllBytes(file.toPath());
        return fileBytes;
    }

    @Override
    public byte[] transfer(Socket clientSocket) throws IOException {
        InputStream is = clientSocket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void transfer(Socket clientSocket, byte[] fileBytes) throws IOException {
        OutputStream os = clientSocket.getOutputStream();
        os.write(fileBytes);
        os.close();
    }

    @Override
    public void storeFile(int senderID, String fileName, byte[] fileBytes) throws SQLException {
        Connection con = DBConfig.getCon();
        String query = "INSERT INTO files (userID, fileName, file) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, senderID);
        ps.setString(2, fileName);
        ps.setBytes(3, fileBytes);
        ps.executeUpdate();

        ps.close();
        con.close();
    }

    @Override
    public void storeMessage(int senderID, String message) throws SQLException {
        con = DBConfig.getCon();
        String query = "INSERT INTO messages (message, userID) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, message);
        ps.setInt(2, senderID);
        ps.executeUpdate();

        ps.close();
        con.close();
    }

    @Override
    public void viewMessages() {
        try {
            Connection con = DBConfig.getCon();
            String query = "SELECT message, timeStamp FROM messages";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("Viewing messages : ");
            while (rs.next()) {
                String message = rs.getString("message");
                Timestamp timeStamp = rs.getTimestamp("timeStamp");
                System.out.println(message + " - " + timeStamp);
            }

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}