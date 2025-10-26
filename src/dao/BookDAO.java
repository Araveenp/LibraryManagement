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
                String isbn = rs.getString("isbn");
                String genre = rs.getString("genre");
                String publisher = rs.getString("publisher");
                Integer publishedYear = (Integer) rs.getObject("published_year");
                Integer pages = (Integer) rs.getObject("pages");
                String description = rs.getString("description");
                String coverUrl = rs.getString("cover_url");
                String location = rs.getString("location");
                boolean available = rs.getBoolean("available");
                books.add(new Book(id, title, author, isbn, genre, publisher, publishedYear, pages, description, coverUrl, location, available));
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
                String isbn = rs.getString("isbn");
                String genre = rs.getString("genre");
                String publisher = rs.getString("publisher");
                Integer publishedYear = (Integer) rs.getObject("published_year");
                Integer pages = (Integer) rs.getObject("pages");
                String description = rs.getString("description");
                String coverUrl = rs.getString("cover_url");
                String location = rs.getString("location");
                boolean available = rs.getBoolean("available");
                book = new Book(id, title, author, isbn, genre, publisher, publishedYear, pages, description, coverUrl, location, available);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, genre, publisher, published_year, pages, description, cover_url, location, available) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setString(5, book.getPublisher());
            if (book.getPublishedYear() != null) pstmt.setInt(6, book.getPublishedYear()); else pstmt.setNull(6, java.sql.Types.INTEGER);
            if (book.getPages() != null) pstmt.setInt(7, book.getPages()); else pstmt.setNull(7, java.sql.Types.INTEGER);
            pstmt.setString(8, book.getDescription());
            pstmt.setString(9, book.getCoverUrl());
            pstmt.setString(10, book.getLocation());
            pstmt.setBoolean(11, book.isAvailable());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, genre = ?, publisher = ?, published_year = ?, pages = ?, description = ?, cover_url = ?, location = ?, available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setString(5, book.getPublisher());
            if (book.getPublishedYear() != null) pstmt.setInt(6, book.getPublishedYear()); else pstmt.setNull(6, java.sql.Types.INTEGER);
            if (book.getPages() != null) pstmt.setInt(7, book.getPages()); else pstmt.setNull(7, java.sql.Types.INTEGER);
            pstmt.setString(8, book.getDescription());
            pstmt.setString(9, book.getCoverUrl());
            pstmt.setString(10, book.getLocation());
            pstmt.setBoolean(11, book.isAvailable());
            pstmt.setInt(12, book.getId());
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
                String isbn = rs.getString("isbn");
                String genre = rs.getString("genre");
                String publisher = rs.getString("publisher");
                Integer publishedYear = (Integer) rs.getObject("published_year");
                Integer pages = (Integer) rs.getObject("pages");
                String description = rs.getString("description");
                String coverUrl = rs.getString("cover_url");
                String location = rs.getString("location");
                boolean available = rs.getBoolean("available");
                books.add(new Book(id, title, author, isbn, genre, publisher, publishedYear, pages, description, coverUrl, location, available));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getRandomBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY RAND() LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                String genre = rs.getString("genre");
                String publisher = rs.getString("publisher");
                Integer publishedYear = (Integer) rs.getObject("published_year");
                Integer pages = (Integer) rs.getObject("pages");
                String description = rs.getString("description");
                String coverUrl = rs.getString("cover_url");
                String location = rs.getString("location");
                boolean available = rs.getBoolean("available");
                books.add(new Book(id, title, author, isbn, genre, publisher, publishedYear, pages, description, coverUrl, location, available));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
