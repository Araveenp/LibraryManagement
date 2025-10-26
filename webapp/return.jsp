<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Return a Book</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/styles.css" />
</head>
<body>
<div class="container">
        <%@ include file="/WEB-INF/includes/header.jspf" %>

    <div class="panel">
        <h1 class="heading">Return a Book</h1>
        <form action="<%= request.getContextPath() %>/return" method="post">
            <div class="row">
                <input type="text" name="bookId" placeholder="Enter Book ID" required>
                <input type="text" name="studentRollNo" placeholder="Student Roll No" required>
            </div>
            <div class="row">
                <input type="number" name="quantity" min="1" value="1" placeholder="Quantity to return" required>
            </div>
            <input type="submit" value="Return">
        </form>
    </div>

        <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
