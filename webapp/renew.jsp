<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Renew a Book</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/styles.css" />
</head>
<body>
<div class="container">
        <%@ include file="/WEB-INF/includes/header.jspf" %>

    <div class="panel">
        <h1 class="heading">Renew a Book</h1>
        <form class="form" action="<%= request.getContextPath() %>/renew" method="post">
            <div class="form-grid">
                <label>
                    <span>Book ID</span>
                    <input type="number" name="bookId" required min="1" placeholder="Enter Book ID" />
                </label>
                <label>
                    <span>Student Roll No</span>
                    <input type="text" name="studentRollNo" required placeholder="e.g., 22WH1A05XX" />
                </label>
                <label>
                    <span>Extend By (days)</span>
                    <input type="number" name="days" required min="1" max="60" value="7" />
                </label>
            </div>
            <button class="btn primary" type="submit">Renew</button>
        </form>
        <p class="subtle">Provide your roll number and the Book ID to extend the due date.</p>
    </div>

        <%@ include file="/WEB-INF/includes/footer.jspf" %>
</div>
</body>
</html>
