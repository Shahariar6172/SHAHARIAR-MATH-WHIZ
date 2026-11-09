<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Screen 2: Game Page (ধাপ ২) — প্রতিটি প্রশ্নে ৩টা option, player একটা select করবে --%>
<c:if test="${empty sessionScope.questions}">
    <c:redirect url="/index.jsp"/>
</c:if>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Answer – SHAHARIAR – MATH WHIZ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="card">
    <h1>SHAHARIAR – MATH WHIZ</h1>
    <p class="sub">Player: <b><c:out value="${sessionScope.playerName}"/></b> &nbsp;|&nbsp; Difficulty: <b>NORMAL</b></p>
    <p class="hint">Step 2 of 2: Choose the correct answer for each question.</p>

    <form action="${pageContext.request.contextPath}/submit" method="post">
        <%-- প্রতিটি প্রশ্নের জন্য loop; qs.index = 0,1,2,3 --%>
        <c:forEach var="q" items="${sessionScope.questions}" varStatus="qs">
            <div class="question">
                <p class="qtext">Q${qs.count}. ${q.text} <span class="marks">[${q.marks} marks]</span></p>
                <div class="options">
                    <%-- ৩টা option, radio button: একটাই select করা যায় --%>
                    <c:forEach var="opt" items="${q.options}" varStatus="os">
                        <label class="option">
                            <input type="radio" name="answer${qs.index}" value="${opt}" ${os.first ? 'required' : ''}>
                            ${opt}
                        </label>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
        <button type="submit">Submit Answers</button>
    </form>
</div>
</body>
</html>
