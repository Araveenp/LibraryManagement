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
            <input type="text" name="query" placeholder="Enter title or author (ISBN also supported)" />
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
                <%
                    if (q != null && !q.trim().isEmpty()) {
                %>
                    Results for '<%= q %>'
                <%
                    } else if (featured != null && featured) {
                %>
                    Featured books
                <%
                    }
                %>
            </p>
            <div class="features">
                <% for (Book book : books) { %>
                    <div class="card">
                        <% if (book.getCoverUrl() != null && !book.getCoverUrl().trim().isEmpty()) { %>
                            <img src="<%= book.getCoverUrl() %>" alt="<%= book.getTitle() %>" style="width:100%;height:160px;object-fit:cover;border-radius:8px"/>
                        <% } %>
                        <h3 style="margin-top:8px"><%= book.getTitle() %></h3>
                        <p class="subtle"><%= book.getAuthor() == null ? "" : book.getAuthor() %></p>
                        <p class="subtle">Available: <strong><%= (book.getAvailableCopies() != null ? book.getAvailableCopies() : (book.isAvailable() ? 1 : 0)) %></strong></p>
                        <% if (book.isAvailable()) { %>
                        <form action="<%= request.getContextPath() %>/borrow" method="post">
                            <input type="hidden" name="bookId" value="<%= book.getId() %>" />
                            <input type="text" name="studentRollNo" placeholder="Roll No" required style="width:100%;margin-bottom:6px"/>
                            <input type="number" name="quantity" min="1" value="1" max="<%= (book.getAvailableCopies()!=null?book.getAvailableCopies(): (book.isAvailable()?1:0)) %>" style="width:100%;margin-bottom:6px" />
                            <button type="submit" class="btn" style="width:100%">Borrow</button>
                        </form>
                        <% } else { %>
                            <span class="subtle">Not available</span>
                        <% } %>
                    </div>
                <% } %>
            </div>
        <%
            }
        %>
    </div>

        <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
