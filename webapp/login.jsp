<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <h1>Login</h1>
    <form action="login" method="post">
        <input type="text" name="username" placeholder="Username" required>
        <input type="password" name="password" placeholder="Password" required>
        <input type="submit" value="Login">
    </form>
    <p>Don't have an account? <a href="register.jsp">Register here</a></p>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
            out.println("<p style='color:red'>" + errorMessage + "</p>");
        }
    %>
</body>
</html>
