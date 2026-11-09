<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Screen 1: Welcome Page — নাম নেওয়া ও Start Game --%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SHAHARIAR – MATH WHIZ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="card">
    <h1>SHAHARIAR – MATH WHIZ</h1>
    <p class="sub">Prime · Modulus · Trigonometry &nbsp;|&nbsp; Difficulty: NORMAL</p>

    <%-- error থাকলে দেখাও (c:out HTML escape করে) --%>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/start" method="post">
        <label for="playerName">Player Name</label>
        <input type="text" id="playerName" name="playerName" maxlength="100" required>
        <button type="submit">Start Game</button>
    </form>

    <a class="link" href="${pageContext.request.contextPath}/leaderboard">View Leaderboard</a>
</div>
</body>
</html>
