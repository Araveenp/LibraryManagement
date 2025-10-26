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
        try {
            int bookId = Integer.parseInt(bookIdStr);
            Book book = bookDAO.getBookById(bookId);
            if (book != null && !book.isAvailable()) {
                // Update borrow record to set return date
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE borrowed_books SET return_date=? WHERE book_id=? AND student_roll_no=? AND return_date IS NULL")) {
                    LocalDate today = LocalDate.now();
                    ps.setDate(1, java.sql.Date.valueOf(today));
                    ps.setInt(2, bookId);
                    ps.setString(3, roll);
                    int updated = ps.executeUpdate();
                    if (updated == 0) {
                        response.getWriter().println("No active loan found for this Book ID and Roll No.");
                        return;
                    }
                }

                // Increment available_copies (up to total_copies) and update availability
                Integer avail = book.getAvailableCopies();
                Integer total = book.getTotalCopies();
                if (avail == null) avail = 0;
                if (total == null) total = Math.max(1, avail + 1);
                avail = Math.min(total, avail + 1);
                book.setAvailableCopies(avail);
                book.setAvailable(avail > 0);
                bookDAO.updateBook(book);
                response.getWriter().println("Book returned successfully for Roll No: " + (roll != null ? roll : "N/A") + "!");
            } else {
                response.getWriter().println("Book not borrowed or does not exist.");
            }
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid Book ID.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

