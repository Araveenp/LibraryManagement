package servlet;

import dao.UserDAO;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = safeTrim(request.getParameter("username"));
        String password = safeTrim(request.getParameter("password"));
        String fullName = safeTrim(request.getParameter("fullName"));
        String email = safeTrim(request.getParameter("email"));

        User newUser = new User(0, username, password, fullName, email, "user");
        userDAO.addUser(newUser);

        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }
}
