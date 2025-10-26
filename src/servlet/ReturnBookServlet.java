package servlet;

import dao.BookDAO;
import model.Book;
import util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDate;

@WebServlet("/return")
public class ReturnBookServlet extends HttpServlet {
    private BookDAO bookDAO;

    public void init() {
        bookDAO = new BookDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookIdStr = request.getParameter("bookId");
        String roll = request.getParameter("studentRollNo");
        String qtyStr = request.getParameter("quantity");
        try {
            int bookId = Integer.parseInt(bookIdStr);
            int qty = 1;
            try {
                if (qtyStr != null) qty = Math.max(1, Integer.parseInt(qtyStr));
            } catch (NumberFormatException ignore) { qty = 1; }

            Book book = bookDAO.getBookById(bookId);
            if (book == null) {
                response.getWriter().println("Book not found.");
                return;
            }

            // Find the most recent active loan for this book and roll
            Integer loanId = null;
            Integer loanQty = null;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement sel = conn.prepareStatement(
                         "SELECT borrow_id, quantity FROM borrowed_books WHERE book_id=? AND student_roll_no=? AND return_date IS NULL ORDER BY borrow_date DESC LIMIT 1")) {
                sel.setInt(1, bookId);
                sel.setString(2, roll);
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) {
                        loanId = rs.getInt("borrow_id");
                        loanQty = rs.getInt("quantity");
                    }
                }
            }

            if (loanId == null || loanQty == null) {
                response.getWriter().println("No active loan found for this Book ID and Roll No.");
                return;
            }

            int returnQty = Math.min(qty, loanQty);

            // Update loan: partial return reduces quantity, full return sets return_date
            LocalDate today = LocalDate.now();
            try (Connection conn = DBConnection.getConnection()) {
                if (returnQty < loanQty) {
                    try (PreparedStatement upd = conn.prepareStatement(
                            "UPDATE borrowed_books SET quantity = quantity - ? WHERE borrow_id=?")) {
                        upd.setInt(1, returnQty);
                        upd.setInt(2, loanId);
                        upd.executeUpdate();
                    }
                } else {
                    try (PreparedStatement upd = conn.prepareStatement(
                            "UPDATE borrowed_books SET return_date=? WHERE borrow_id=?")) {
                        upd.setDate(1, java.sql.Date.valueOf(today));
                        upd.setInt(2, loanId);
                        upd.executeUpdate();
                    }
                }
            }

            // Increment available_copies (up to total_copies) and update availability
            Integer avail = book.getAvailableCopies();
            Integer total = book.getTotalCopies();
            if (avail == null) avail = 0;
            if (total == null) total = Math.max(1, avail + returnQty);
            avail = Math.min(total, avail + returnQty);
            book.setAvailableCopies(avail);
            book.setAvailable(avail > 0);
            bookDAO.updateBook(book);

            response.getWriter().println("Returned " + returnQty + " copy(ies) for Roll No: " + (roll != null ? roll : "N/A") + ".");
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid Book ID.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

