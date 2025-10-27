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

        <div class="features">
            <div class="card">
                <h3>Search</h3>
                <p>Find books by title or author.</p>
                <a class="btn" href="<%= request.getContextPath() %>/search">Open Search</a>
            </div>
            <div class="card">
                <h3>Borrow</h3>
                <p>Borrow an available book using ID and roll no.</p>
                <a class="btn" href="<%= request.getContextPath() %>/borrow.jsp">Borrow Book</a>
            </div>
            <div class="card">
                <h3>Return</h3>
                <p>Return a borrowed book by ID.</p>
                <a class="btn" href="<%= request.getContextPath() %>/return.jsp">Return Book</a>
            </div>
            <div class="card">
                <h3>Renew</h3>
                <p>Extend your borrowing period.</p>
                <a class="btn" href="<%= request.getContextPath() %>/renew.jsp">Renew Book</a>
            </div>
        </div>

        <div class="panel" style="margin-top:16px">
            <h2 class="heading">Admin quick actions</h2>
            <div class="features">
                <div class="card">
                    <h3>Admin Panel</h3>
                    <p>Full access to manage books and settings.</p>
                    <a class="btn" href="<%= request.getContextPath() %>/admin.jsp">Open Admin</a>
                </div>
                <div class="card">
                    <h3>Loans</h3>
                    <p>Search active loans by student roll number.</p>
                    <a class="btn" href="<%= request.getContextPath() %>/admin/loans">Manage Loans</a>
                </div>
                <div class="card">
                    <h3>Add Book</h3>
                    <p>Quickly add a new title to the library.</p>
                    <a class="btn" href="<%= request.getContextPath() %>/admin.jsp#add">Add New</a>
                </div>
            </div>
        </div>
    </div>

        <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
