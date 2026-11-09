<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Screen 3: Result Page — নাম ও final score --%>
<c:if test="${empty totalScore}">
    <c:redirect url="/index.jsp"/>
</c:if>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Result – SHAHARIAR – MATH WHIZ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="card">
    <h1>SHAHARIAR – MATH WHIZ</h1>

    <div class="score">
        <p><c:out value="${playerName}"/></p>
        <h2>${totalScore} / 10</h2>
        <p class="sub">Difficulty: ${difficulty}</p>
    </div>

    <c:if test="${not empty dbError}">
        <p class="error"><c:out value="${dbError}"/></p>
    </c:if>

    <table>
        <tr><th>#</th><th>Question</th><th>Your Answer</th><th>Correct</th><th>Marks</th></tr>
        <c:forEach var="q" items="${questions}" varStatus="s">
            <tr class="${q.correct ? 'ok' : 'bad'}">
                <td>${s.count}</td>
                <td>${q.text}</td>
                <td><c:out value="${empty q.userAnswer ? '-' : q.userAnswer}"/></td>
                <td>${q.correctAnswerText}</td>
                <td>${q.earnedMarks} / ${q.marks}</td>
            </tr>
        </c:forEach>
    </table>

    <a class="link" href="${pageContext.request.contextPath}/index.jsp">Play Again</a>
    <a class="link" href="${pageContext.request.contextPath}/leaderboard">View Leaderboard</a>
</div>
</body>
</html>
