package servlet;

import dao.BookDAO;
import model.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private BookDAO bookDAO;

    public void init() {
        bookDAO = new BookDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            Book newBook = new Book(0, title, author, true);
            bookDAO.addBook(newBook);
            response.getWriter().println("Book added successfully!");
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            boolean available = Boolean.parseBoolean(request.getParameter("available"));
            Book bookToUpdate = new Book(id, title, author, available);
            bookDAO.updateBook(bookToUpdate);
            response.getWriter().println("Book updated successfully!");
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            bookDAO.deleteBook(id);
            response.getWriter().println("Book deleted successfully!");
        }
    }
}

