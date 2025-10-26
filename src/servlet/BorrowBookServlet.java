package servlet;

import dao.BookDAO;
import model.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/borrow")
public class BorrowBookServlet extends HttpServlet {
    private BookDAO bookDAO;

    public void init() {
        bookDAO = new BookDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        Book book = bookDAO.getBookById(bookId);
        if (book != null && book.isAvailable()) {
            book.setAvailable(false);
            bookDAO.updateBook(book);
            response.getWriter().println("Book borrowed successfully!");
        } else {
            response.getWriter().println("Book not available or does not exist.");
        }
    }
}

