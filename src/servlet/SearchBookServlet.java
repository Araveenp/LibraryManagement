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
            // Show some random featured books when there's no query; allow optional 'limit' param
            int limit = 12;
            String limitStr = request.getParameter("limit");
            try {
                if (limitStr != null) {
                    limit = Math.max(4, Math.min(36, Integer.parseInt(limitStr)));
                }
            } catch (NumberFormatException ignore) {}
            List<Book> featuredBooks = bookDAO.getRandomBooks(limit);
            request.setAttribute("books", featuredBooks);
            request.setAttribute("featured", true);
            request.setAttribute("q", null);
            request.setAttribute("limit", limit);
        }
        request.getRequestDispatcher("search.jsp").forward(request, response);
    }
}

