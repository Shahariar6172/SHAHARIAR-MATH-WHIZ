<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Screen 2: Game Page — ৪টি প্রশ্ন ও উত্তরের ঘর --%>
<c:if test="${empty sessionScope.questions}">
    <c:redirect url="/index.jsp"/>
</c:if>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Game – SHAHARIAR – MATH WHIZ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="card">
    <h1>SHAHARIAR – MATH WHIZ</h1>
    <p class="sub">Player: <b><c:out value="${sessionScope.playerName}"/></b> &nbsp;|&nbsp; Difficulty: <b>NORMAL</b></p>

    <form action="${pageContext.request.contextPath}/submit" method="post">
        <%-- প্রতিটি প্রশ্নের জন্য loop; status.index = 0,1,2,3 --%>
        <c:forEach var="q" items="${sessionScope.questions}" varStatus="status">
            <div class="question">
                <label for="answer${status.index}">
                    Q${status.count}. ${q.text} <span class="marks">[${q.marks} marks]</span>
                </label>
                <input type="text" id="answer${status.index}" name="answer${status.index}"
                       placeholder="Your answer" autocomplete="off">
            </div>
        </c:forEach>
        <p class="hint">Trigonometry: decimal answer (e.g. 0.7071). Tolerance ±0.01.</p>
        <button type="submit">Submit Answers</button>
    </form>
</div>
</body>
</html>
