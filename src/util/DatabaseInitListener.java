package util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class DatabaseInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement()) {
            String url = conn.getMetaData().getURL();
            boolean isMySQLorH2 = url.startsWith("jdbc:mysql") || url.startsWith("jdbc:h2");

            if (isMySQLorH2) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS books (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(255), " +
                        "author VARCHAR(255), " +
                        "available BOOLEAN DEFAULT TRUE)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) UNIQUE, " +
                        "password VARCHAR(255), " +
                        "full_name VARCHAR(100), " +
                        "email VARCHAR(100) UNIQUE, " +
                        "role VARCHAR(20) DEFAULT 'user')");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS borrowed_books (" +
                        "borrow_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "book_id INT NOT NULL, " +
                        "user_id INT NOT NULL, " +
                        "borrow_date DATE NOT NULL, " +
                        "due_date DATE NOT NULL, " +
                        "return_date DATE)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
