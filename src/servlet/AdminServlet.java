package servlet;

import dao.BookDAO;
import model.Book;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
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
            newBook.setIsbn(emptyToNull(request.getParameter("isbn")));
            newBook.setGenre(emptyToNull(request.getParameter("genre")));
            newBook.setPublisher(emptyToNull(request.getParameter("publisher")));
            newBook.setPublishedYear(parseInteger(request.getParameter("publishedYear")));
            newBook.setPages(parseInteger(request.getParameter("pages")));
            newBook.setDescription(emptyToNull(request.getParameter("description")));
            newBook.setCoverUrl(emptyToNull(request.getParameter("coverUrl")));
            newBook.setLocation(emptyToNull(request.getParameter("location")));
            bookDAO.addBook(newBook);
            request.setAttribute("message", "Book added successfully!");
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Book existing = bookDAO.getBookById(id);
            if (existing == null) {
                request.setAttribute("message", "Book not found: ID " + id);
            } else {
                String title = request.getParameter("title");
                String author = request.getParameter("author");
                String isbn = request.getParameter("isbn");
                String genre = request.getParameter("genre");
                String publisher = request.getParameter("publisher");
                String publishedYear = request.getParameter("publishedYear");
                String pages = request.getParameter("pages");
                String description = request.getParameter("description");
                String coverUrl = request.getParameter("coverUrl");
                String location = request.getParameter("location");
                boolean available = Boolean.parseBoolean(request.getParameter("available"));

                existing.setTitle(isEmpty(title) ? existing.getTitle() : title);
                existing.setAuthor(isEmpty(author) ? existing.getAuthor() : author);
                existing.setIsbn(isEmpty(isbn) ? existing.getIsbn() : isbn);
                existing.setGenre(isEmpty(genre) ? existing.getGenre() : genre);
                existing.setPublisher(isEmpty(publisher) ? existing.getPublisher() : publisher);
                existing.setPublishedYear(isEmpty(publishedYear) ? existing.getPublishedYear() : parseInteger(publishedYear));
                existing.setPages(isEmpty(pages) ? existing.getPages() : parseInteger(pages));
                existing.setDescription(isEmpty(description) ? existing.getDescription() : description);
                existing.setCoverUrl(isEmpty(coverUrl) ? existing.getCoverUrl() : coverUrl);
                existing.setLocation(isEmpty(location) ? existing.getLocation() : location);
                existing.setAvailable(available);

                bookDAO.updateBook(existing);
                request.setAttribute("message", "Book updated successfully!");
            }
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            bookDAO.deleteBook(id);
            request.setAttribute("message", "Book deleted successfully!");
        }

        // After handling POST, show the admin page with updated list
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("books", bookDAO.getAllBooks());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin.jsp");
        dispatcher.forward(request, response);
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String emptyToNull(String s) {
        return isEmpty(s) ? null : s;
    }

    private static Integer parseInteger(String s) {
        try {
            if (isEmpty(s)) return null;
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

