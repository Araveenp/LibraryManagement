package servlet;

import util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/renew")
public class RenewBookServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookIdStr = request.getParameter("bookId");
        String roll = request.getParameter("studentRollNo");
        String daysStr = request.getParameter("days");
        try {
            int bookId = Integer.parseInt(bookIdStr);
            int days = 7;
            try { if (daysStr != null) days = Math.max(1, Integer.parseInt(daysStr)); } catch (NumberFormatException ignore) {}

            // Find active loan and current due date
            Integer loanId = null;
            LocalDate dueDate = null;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement sel = conn.prepareStatement(
                         "SELECT borrow_id, due_date FROM borrowed_books WHERE book_id=? AND student_roll_no=? AND return_date IS NULL ORDER BY borrow_date DESC LIMIT 1")) {
                sel.setInt(1, bookId);
                sel.setString(2, roll);
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) {
                        loanId = rs.getInt("borrow_id");
                        java.sql.Date d = rs.getDate("due_date");
                        if (d != null) dueDate = d.toLocalDate();
                    }
                }
            }

            if (loanId == null || dueDate == null) {
                response.getWriter().println("No active loan found to renew for this Book ID and Roll No.");
                return;
            }

            LocalDate newDue = dueDate.plusDays(days);

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement upd = conn.prepareStatement(
                         "UPDATE borrowed_books SET due_date=? WHERE borrow_id=?")) {
                upd.setDate(1, java.sql.Date.valueOf(newDue));
                upd.setInt(2, loanId);
                upd.executeUpdate();
            }

            response.getWriter().println("Renewed successfully. New due date: " + newDue + ".");
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid input.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

