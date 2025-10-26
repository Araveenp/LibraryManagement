<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<html>
<head>
    <title>Library Management System</title>
</head>
<body>
    <h1>Welcome to the Library Management System</h1>

    <%
        User user = (User) session.getAttribute("user");
        if (user != null) {
    %>
        <p>Hello, <%= user.getFullName() %>! (<a href="logout">Logout</a>)</p>
    <% } else { %>
        <p><a href="login.jsp">Login</a> | <a href="register.jsp">Register</a></p>
    <% } %>

    <h3><a href="search.jsp">Search for a Book</a></h3>
    <h3><a href="borrow.jsp">Borrow a Book</a></h3>
    <h3><a href="return.jsp">Return a Book</a></h3>
    <h3><a href="renew.jsp">Renew a Book</a></h3>

    <%
        if (user != null && "admin".equals(user.getRole())) {
    %>
        <h3><a href="admin.jsp">Admin Panel</a></h3>
    <% } %>
</body>
</html>
