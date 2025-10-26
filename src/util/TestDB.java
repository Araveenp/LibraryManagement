package util;
import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/library_db", "root", "yourpassword");
            System.out.println("Connected!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
