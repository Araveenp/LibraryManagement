<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Borrow a Book</title>
</head>
<body>
    <h1>Borrow a Book</h1>
    <form action="borrow" method="post">
        <input type="text" name="bookId" placeholder="Enter Book ID" required>
        <input type="submit" value="Borrow">
    </form>
</body>
</html>
