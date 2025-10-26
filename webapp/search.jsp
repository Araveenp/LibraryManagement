<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>

<html>
<head>
    <title>Search Books</title>
</head>
<body>
    <h1>Search for Books</h1>
    <form action="search" method="get">
        <input type="text" name="query" placeholder="Enter title or author">
        <input type="submit" value="Search">
    </form>

    <hr>

    <%
        List<Book> books = (List<Book>) request.getAttribute("books");
        if (books != null) {
    %>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Author</th>
            <th>Available</th>
        </tr>
        <% for (Book book : books) { %>
        <tr>
            <td><%= book.getId() %></td>
            <td><%= book.getTitle() %></td>
            <td><%= book.getAuthor() %></td>
            <td><%= book.isAvailable() %></td>
        </tr>
        <% } %>
    </table>
    <% } %>
</body>
</html>
