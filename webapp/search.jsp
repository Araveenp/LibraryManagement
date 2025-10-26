<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Search Books</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/styles.css" />
</head>
<body>
<div class="container">
        <%@ include file="/WEB-INF/includes/header.jspf" %>

    <div class="panel">
        <h1 class="heading">Search for Books</h1>
        <form action="search" method="get">
            <input type="text" name="query" placeholder="Enter title or author" />
            <input type="submit" value="Search" />
        </form>

        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            if (books != null) {
        %>
            <div style="height:12px"></div>
            <table class="table">
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Available</th>
                </tr>
                    <%
                        List<Book> books = (List<Book>) request.getAttribute("books");
                        Boolean featured = (Boolean) request.getAttribute("featured");
                        String q = (String) request.getAttribute("q");
                        if (books != null) {
                    %>
                        <div style="height:8px"></div>
                        <p class="subtle">
                            <%= (featured != null && featured) ? "Showing some random books" : (q != null ? ("Results for '" + q + "'") : "") %>
                        </p>
                        <td><%= book.getAuthor() %></td>
                        <td><%= book.isAvailable() %></td>
                    </tr>
                <% } %>
            </table>
        <% } %>
    </div>

        <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
