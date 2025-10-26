<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Book" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Admin Panel</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/styles.css" />
    </head>
<body>
<div class="container">
    <%@ include file="/WEB-INF/includes/header.jspf" %>

    <div class="panel">
        <h1 class="heading">Admin Panel</h1>
        <%
            String message = (String) request.getAttribute("message");
            if (message != null) {
        %>
            <div class="alert ok"><%= message %></div>
        <% } %>

        <h3 class="subtle">Add a New Book</h3>
        <form action="admin" method="post">
            <input type="hidden" name="action" value="add">
            <div class="row">
                <input type="text" name="title" placeholder="Title" required>
                <input type="text" name="author" placeholder="Author" required>
            </div>
            <div class="row">
                <input type="text" name="isbn" placeholder="ISBN">
                <input type="text" name="genre" placeholder="Genre">
            </div>
            <div class="row">
                <input type="text" name="publisher" placeholder="Publisher">
                <input type="text" name="publishedYear" placeholder="Published Year">
            </div>
            <div class="row">
                <input type="text" name="pages" placeholder="Pages">
                <input type="text" name="location" placeholder="Location (shelf)">
            </div>
            <input type="text" name="coverUrl" placeholder="Cover Image URL">
            <input type="text" name="description" placeholder="Description">
            <input type="submit" value="Add Book">
        </form>

        <div style="height:16px"></div>

        <h3 class="subtle">Update a Book</h3>
        <form action="admin" method="post">
            <input type="hidden" name="action" value="update">
            <div class="row">
                <input type="text" name="id" placeholder="Book ID" required>
                <select name="available">
                    <option value="true">Available</option>
                    <option value="false">Not Available</option>
                </select>
            </div>
            <div class="row">
                <input type="text" name="title" placeholder="New Title (optional)">
                <input type="text" name="author" placeholder="New Author (optional)">
            </div>
            <div class="row">
                <input type="text" name="isbn" placeholder="ISBN (optional)">
                <input type="text" name="genre" placeholder="Genre (optional)">
            </div>
            <div class="row">
                <input type="text" name="publisher" placeholder="Publisher (optional)">
                <input type="text" name="publishedYear" placeholder="Published Year (optional)">
            </div>
            <div class="row">
                <input type="text" name="pages" placeholder="Pages (optional)">
                <input type="text" name="location" placeholder="Location (optional)">
            </div>
            <input type="text" name="coverUrl" placeholder="Cover Image URL (optional)">
            <input type="text" name="description" placeholder="Description (optional)">
            <input type="submit" value="Update Book">
        </form>

        <div style="height:16px"></div>

        <h3 class="subtle">Delete a Book</h3>
        <form action="admin" method="post">
            <input type="hidden" name="action" value="delete">
            <input type="text" name="id" placeholder="Book ID" required>
            <input type="submit" value="Delete Book">
        </form>
    </div>

    <div style="height:16px"></div>
    <div class="panel">
        <h2 class="heading">All Books</h2>
        <%
            List<Book> books = (List<Book>) request.getAttribute("books");
            if (books != null && !books.isEmpty()) {
        %>
            <table class="table">
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>ISBN</th>
                    <th>Year</th>
                    <th>Genre</th>
                    <th>Available</th>
                    <th>Actions</th>
                </tr>
                <% for (Book b : books) { %>
                    <tr>
                        <td><%= b.getId() %></td>
                        <td><%= b.getTitle() %></td>
                        <td><%= b.getAuthor() %></td>
                        <td><%= b.getIsbn() == null ? "" : b.getIsbn() %></td>
                        <td><%= b.getPublishedYear() == null ? "" : b.getPublishedYear() %></td>
                        <td><%= b.getGenre() == null ? "" : b.getGenre() %></td>
                        <td><%= b.isAvailable() %></td>
                        <td>
                            <form action="admin" method="post" style="display:inline">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<%= b.getId() %>">
                                <input type="submit" value="Delete Permanently">
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } else { %>
            <p class="subtle">No books available.</p>
        <% } %>
    </div>

    <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
