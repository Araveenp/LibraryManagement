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
        <form action="<%= request.getContextPath() %>/search" method="get">
            <input type="text" name="query" placeholder="Enter title or author" />
            <input type="submit" value="Search" />
        </form>

        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            Boolean featured = (Boolean) request.getAttribute("featured");
            String q = (String) request.getAttribute("q");
            if (books != null) {
        %>
            <div style="height:12px"></div>
            <p class="subtle">
                <%= (q != null && !q.trim().isEmpty()) ? ("Results for '" + q + "'") : "" %>
            </p>
            <table class="table">
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Available (count)</th>
                    <th>Actions</th>
                </tr>
                <%
                    for (Book book : books) {
                %>
                    <tr>
                        <td><%= book.getId() %></td>
                        <td><%= book.getTitle() %></td>
                        <td><%= book.getAuthor() %></td>
                        <td><%= (book.getAvailableCopies() != null ? book.getAvailableCopies() : (book.isAvailable() ? 1 : 0)) %></td>
                        <td>
                            <% if (book.isAvailable()) { %>
                                <form action="<%= request.getContextPath() %>/borrow" method="post" class="inline">
                                    <input type="hidden" name="bookId" value="<%= book.getId() %>" />
                                    <input type="text" name="studentRollNo" placeholder="Student Roll No" required style="width:160px"/>
                                    <input type="number" name="quantity" min="1" value="1" max="<%= (book.getAvailableCopies()!=null?book.getAvailableCopies(): (book.isAvailable()?1:0)) %>" style="width:90px" />
                                    <button type="submit" class="btn">Borrow</button>
                                </form>
                            <% } else { %>
                                <span class="subtle">Not available</span>
                            <% } %>
                        </td>
                    </tr>
                <%
                    }
                %>
            </table>
        <%
            }
        %>
    </div>

        <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
