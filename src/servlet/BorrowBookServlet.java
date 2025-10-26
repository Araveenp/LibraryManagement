package servlet;

import dao.BookDAO;
import model.Book;
import model.User;
import util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/borrow")
public class BorrowBookServlet extends HttpServlet {
    private BookDAO bookDAO;

    public void init() {
        bookDAO = new BookDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    String bookIdStr = request.getParameter("bookId");
    String roll = request.getParameter("studentRollNo");
    String qtyStr = request.getParameter("quantity");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            int qty = 1;
            try { if (qtyStr != null) qty = Math.max(1, Integer.parseInt(qtyStr)); } catch (NumberFormatException ignore) {}
            Book book = bookDAO.getBookById(bookId);
            Integer availCopies = (book != null ? book.getAvailableCopies() : null);
            if (book != null && book.isAvailable() && availCopies != null && qty <= availCopies) {
        // Insert a borrow record
                try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO borrowed_books (book_id, user_id, student_roll_no, quantity, borrow_date, due_date) VALUES (?,?,?,?,?,?)")) {
                    LocalDate today = LocalDate.now();
                    LocalDate due = today.plusDays(14);
                    ps.setInt(1, bookId);
                    ps.setInt(2, user.getUserId());
                    ps.setString(3, roll);
                    ps.setInt(4, qty);
                    ps.setDate(5, java.sql.Date.valueOf(today));
                    ps.setDate(6, java.sql.Date.valueOf(due));
                    ps.executeUpdate();
                }

                // Decrement available_copies and recompute availability
                Integer avail = book.getAvailableCopies();
                if (avail == null) avail = 1;
                avail = Math.max(0, avail - qty);
                book.setAvailableCopies(avail);
                book.setAvailable(avail > 0);
                bookDAO.updateBook(book);

                response.getWriter().println("Borrowed " + qty + " copy(ies) for Roll No: " + (roll != null ? roll : "N/A") + ". Due in 14 days.");
            } else {
                response.getWriter().println("Book not available, does not exist, or requested quantity exceeds available copies.");
            }
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid Book ID.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

