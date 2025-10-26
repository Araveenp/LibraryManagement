<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
    <h1>Register</h1>
    <form action="register" method="post">
        <input type="text" name="username" placeholder="Username" required>
        <input type="password" name="password" placeholder="Password" required>
        <input type="text" name="fullName" placeholder="Full Name" required>
        <input type="email" name="email" placeholder="Email" required>
        <input type="submit" value="Register">
    </form>
</body>
</html>
