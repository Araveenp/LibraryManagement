<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Library Management System</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/styles.css" />
    </head>
<body>
<div class="container">
        <%@ include file="/WEB-INF/includes/header.jspf" %>

    <div class="panel">
        <h1 class="heading">Welcome to the Library Management System</h1>
        <p class="subtle">Search, borrow, return, and manage books with a clean, modern interface.</p>

        <%
            User user = (User) session.getAttribute("user");
            if (user != null) {
        %>
            <p class="subtle">Hello, <strong><%= user.getFullName() %></strong>!</p>
        <% } else { %>
            <p class="subtle">New here? <a href="register.jsp">Create an account</a>. Already have one? <a href="login.jsp">Login</a>.</p>
        <% } %>

        <div style="height:8px"></div>
        <ul>
            <li><a href="search.jsp">Search for a Book</a></li>
            <li><a href="borrow.jsp">Borrow a Book</a></li>
            <li><a href="return.jsp">Return a Book</a></li>
            <li><a href="renew.jsp">Renew a Book</a></li>
        </ul>

        <%
            if (user != null && "admin".equals(user.getRole())) {
        %>
            <p><a href="admin.jsp">Go to Admin Panel</a></p>
        <% } %>
    </div>

        <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
