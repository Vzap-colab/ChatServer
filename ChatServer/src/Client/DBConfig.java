package Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    private static final String url = "jdbc:mysql://localhost:3306/chatserver";
    private static final String username = "root";
    private static final String password = "root";

    private static Connection con;

    public static Connection getCon() {
        if (con == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(url, username, password);
                System.out.println("Successfully connected to database");
            } catch (SQLException | ClassNotFoundException e) {
                System.out.println("Error establishing connection");
            }
        }
        return con;
    }

    public static void main(String[] args) {
        getCon();
    }
}