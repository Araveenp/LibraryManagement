package servlet;

import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length());

        // Public endpoints and assets
    boolean isAsset = path.startsWith("/assets/");
    boolean isAuth = path.equals("/login") || path.equals("/register");
    boolean isAuthPage = path.equals("/login.jsp") || path.equals("/register.jsp");
    boolean isRoot = path.equals("") || path.equals("/");
    boolean isHome = path.equals("/index.jsp");

        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (isAsset || isAuth || isAuthPage || isRoot || isHome) {
            chain.doFilter(request, response);
            return;
        }

        if (user == null) {
            resp.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}
