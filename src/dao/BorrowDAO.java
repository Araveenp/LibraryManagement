package dao;

import model.BorrowRecord;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    public List<BorrowRecord> getActiveLoansByRoll(String roll) {
        List<BorrowRecord> list = new ArrayList<>();
    String sql = "SELECT b.borrow_id, b.book_id, bk.title, bk.author, b.quantity, b.student_roll_no, b.user_id, u.username, b.borrow_date, b.due_date, b.return_date " +
                "FROM borrowed_books b " +
                "LEFT JOIN books bk ON bk.id = b.book_id " +
                "LEFT JOIN users u ON u.user_id = b.user_id " +
                "WHERE b.student_roll_no = ? AND b.return_date IS NULL " +
                "ORDER BY b.borrow_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roll);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BorrowRecord r = new BorrowRecord();
                r.setBorrowId(rs.getInt("borrow_id"));
                r.setBookId(rs.getInt("book_id"));
                r.setTitle(rs.getString("title"));
                r.setAuthor(rs.getString("author"));
                r.setQuantity((Integer) rs.getObject("quantity"));
                r.setStudentRollNo(rs.getString("student_roll_no"));
                r.setUserId((Integer) rs.getObject("user_id"));
                r.setUsername(rs.getString("username"));
                r.setBorrowDate(rs.getDate("borrow_date"));
                r.setDueDate(rs.getDate("due_date"));
                r.setReturnDate(rs.getDate("return_date"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
