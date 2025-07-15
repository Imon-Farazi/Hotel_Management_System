package hotel_management_system;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    public static Connection connectDb() {
        try {
            // Use correct driver (modern one for MySQL 8+)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Proper JDBC URL format
            String url = "jdbc:mysql://localhost:3306/hotel_data";
            String user = "root";
            String password = "";  // If no password

            Connection connect = DriverManager.getConnection(url, user, password);
            return connect;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
