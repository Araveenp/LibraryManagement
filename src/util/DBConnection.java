package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Prefer env vars in cloud; default to embedded H2 file DB for free hosting
    private static final String DEFAULT_H2_URL = "jdbc:h2:file:./data/library_db;DB_CLOSE_DELAY=-1;MODE=MySQL;AUTO_SERVER=TRUE";

    private static final String URL = System.getenv("DB_URL") != null && !System.getenv("DB_URL").isEmpty()
            ? System.getenv("DB_URL")
            : DEFAULT_H2_URL; // default for online demo without external DB

    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "sa";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";

    private static String resolveDriver(String url) {
        if (url.startsWith("jdbc:mysql")) return "com.mysql.cj.jdbc.Driver";
        if (url.startsWith("jdbc:postgresql")) return "org.postgresql.Driver";
        if (url.startsWith("jdbc:h2")) return "org.h2.Driver";
        return "com.mysql.cj.jdbc.Driver"; // sane default
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(resolveDriver(URL));
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully to: " + URL);
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found for URL: " + URL);
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed! URL: " + URL);
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
