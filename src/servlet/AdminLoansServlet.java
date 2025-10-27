package servlet;

import dao.BorrowDAO;
import model.BorrowRecord;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/loans")
public class AdminLoansServlet extends HttpServlet {
    private BorrowDAO borrowDAO;

    @Override
    public void init() {
        borrowDAO = new BorrowDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Only require login; no separate admin role in this app setup
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        String roll = request.getParameter("roll");
        if (roll != null && !roll.trim().isEmpty()) {
            List<BorrowRecord> loans = borrowDAO.getActiveLoansByRoll(roll.trim());
            request.setAttribute("loans", loans);
            request.setAttribute("roll", roll.trim());
        }
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
}
