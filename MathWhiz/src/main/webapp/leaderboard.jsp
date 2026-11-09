<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- Screen 4: Leaderboard — Top 10 --%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Leaderboard – SHAHARIAR – MATH WHIZ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="card">
    <h1>SHAHARIAR – MATH WHIZ</h1>
    <h2>Top 10 Leaderboard</h2>

    <c:if test="${not empty dbError}">
        <p class="error"><c:out value="${dbError}"/></p>
    </c:if>

    <table>
        <tr><th>Rank</th><th>Player</th><th>Score</th><th>Difficulty</th><th>Date / Time</th></tr>
        <c:forEach var="s" items="${scores}" varStatus="st">
            <tr>
                <td>${st.count}</td>
                <td><c:out value="${s.playerName}"/></td>
                <td>${s.score}</td>
                <td>${s.difficulty}</td>
                <td><fmt:formatDate value="${s.playedAt}" pattern="dd-MM-yyyy hh:mm a"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty scores and empty dbError}">
            <tr><td colspan="5">No scores yet.</td></tr>
        </c:if>
    </table>

    <a class="link" href="${pageContext.request.contextPath}/index.jsp">Back to Home</a>
</div>
</body>
</html>
