package dao;

import model.Book;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean available = rs.getBoolean("available");
                books.add(new Book(id, title, author, available));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookById(int id) {
        Book book = null;
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean available = rs.getBoolean("available");
                book = new Book(id, title, author, available);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, available) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBoolean(3, book.isAvailable());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBoolean(3, book.isAvailable());
            pstmt.setInt(4, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean available = rs.getBoolean("available");
                books.add(new Book(id, title, author, available));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
