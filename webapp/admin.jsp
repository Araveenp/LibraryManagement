<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Panel</title>
</head>
<body>
    <h1>Admin Panel</h1>

    <h3>Add a New Book</h3>
    <form action="admin" method="post">
        <input type="hidden" name="action" value="add">
        <input type="text" name="title" placeholder="Title" required>
        <input type="text" name="author" placeholder="Author" required>
        <input type="submit" value="Add Book">
    </form>

    <hr>

    <h3>Update a Book</h3>
    <form action="admin" method="post">
        <input type="hidden" name="action" value="update">
        <input type="text" name="id" placeholder="Book ID" required>
        <input type="text" name="title" placeholder="New Title">
        <input type="text" name="author" placeholder="New Author">
        <select name="available">
            <option value="true">Available</option>
            <option value="false">Not Available</option>
        </select>
        <input type="submit" value="Update Book">
    </form>

    <hr>

    <h3>Delete a Book</h3>
    <form action="admin" method="post">
        <input type="hidden" name="action" value="delete">
        <input type="text" name="id" placeholder="Book ID" required>
        <input type="submit" value="Delete Book">
    </form>
</body>
</html>
