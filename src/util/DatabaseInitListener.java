package util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

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
            "isbn VARCHAR(20), " +
            "genre VARCHAR(100), " +
            "publisher VARCHAR(100), " +
            "published_year INT, " +
            "pages INT, " +
            "description TEXT, " +
            "cover_url VARCHAR(500), " +
            "location VARCHAR(100), " +
            "total_copies INT DEFAULT 10, " +
            "available_copies INT DEFAULT 10, " +
            "available BOOLEAN DEFAULT TRUE)");
                // Ensure columns exist even if the table was created before these fields were added

                // Ensure columns exist even if the table was created before these fields were added
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS isbn VARCHAR(20)");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS genre VARCHAR(100)");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS publisher VARCHAR(100)");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS published_year INT");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS pages INT");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS location VARCHAR(100)");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS cover_url VARCHAR(500)");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS total_copies INT DEFAULT 10");
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS available_copies INT DEFAULT 10");
                // Seed data when table is empty
                int count = 0;
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {
                    if (rs.next()) count = rs.getInt(1);
                }

                if (count == 0) {
                    // If a CSV file exists at repo root, import it.
                    Path csvPath = Paths.get("books.csv");
                    if (Files.exists(csvPath)) {
                        String header = "title,author,isbn,genre,publisher,published_year,pages,description,cover_url,location,available";
                        String csvInsert = "INSERT INTO books (title, author, isbn, genre, publisher, published_year, pages, description, cover_url, location, available) " +
                                "SELECT title, author, isbn, genre, publisher, CAST(published_year AS INT), CAST(pages AS INT), description, cover_url, location, " +
                                "CASE WHEN LOWER(available)='true' THEN TRUE ELSE FALSE END FROM CSVREAD('" + csvPath.toString().replace("\\", "/") + "', '" + header + "')";
                        try {
                            st.executeUpdate(csvInsert);
                        } catch (SQLException ignore) {
                            // CSVREAD failed; continue without synthetic fallback to keep data real-only
                        }
                    }
                }
                st.executeUpdate("ALTER TABLE books ADD COLUMN IF NOT EXISTS location VARCHAR(100)");
                // Backfill copies columns for existing rows without values
                try (Statement st2 = conn.createStatement()) {
                    st2.executeUpdate("UPDATE books SET total_copies = COALESCE(total_copies, 10)");
                    st2.executeUpdate("UPDATE books SET available_copies = CASE WHEN available_copies IS NULL THEN (CASE WHEN available=TRUE THEN total_copies ELSE 0 END) ELSE available_copies END");
                    st2.executeUpdate("UPDATE books SET available = CASE WHEN available_copies > 0 THEN TRUE ELSE FALSE END");
                }

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
            "student_roll_no VARCHAR(50), " +
            "quantity INT DEFAULT 1, " +
            "borrow_date DATE NOT NULL, " +
            "due_date DATE NOT NULL, " +
            "return_date DATE)");
        st.executeUpdate("ALTER TABLE borrowed_books ADD COLUMN IF NOT EXISTS student_roll_no VARCHAR(50)");
        st.executeUpdate("ALTER TABLE borrowed_books ADD COLUMN IF NOT EXISTS quantity INT DEFAULT 1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String makeTitle(String genre, int i) {
        switch (genre) {
            case "Engineering":
                return "Engineering Handbook Vol " + i;
            case "Medicine":
                return "MBBS Essentials " + i;
            case "Story":
                return "Story Collection " + i;
            case "Action":
                return "Action Chronicles " + i;
            case "Fantasy":
                return "Fantasy Realms " + i;
            case "Technology":
                return "Tech Insights " + i;
            case "Computer Science":
                return "Computer Science Guide " + i;
            case "History":
                return "Historical Perspectives " + i;
            case "Psychology":
                return "Psychology Insights " + i;
            case "Business":
                return "Business Strategies " + i;
            default:
                return genre + " Volume " + i;
        }
    }

    private static String slug(String s) {
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
