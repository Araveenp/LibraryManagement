package servlet;

import dao.BookDAO;
import model.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchBookServlet extends HttpServlet {
    private BookDAO bookDAO;

    public void init() {
        bookDAO = new BookDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        if (query != null && !query.trim().isEmpty()) {
            List<Book> books = bookDAO.searchBooks(query);
            request.setAttribute("books", books);
            request.setAttribute("featured", false);
            request.setAttribute("q", query);
        } else {
            // Show some random featured books when there's no query
            List<Book> featuredBooks = bookDAO.getRandomBooks(12);
            request.setAttribute("books", featuredBooks);
            request.setAttribute("featured", true);
            request.setAttribute("q", null);
        }
        request.getRequestDispatcher("search.jsp").forward(request, response);
    }
}

