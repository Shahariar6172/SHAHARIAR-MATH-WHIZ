<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Screen 2: Game Page (ধাপ ১) — player নিজে ৪টি প্রশ্নের সংখ্যা দিবে --%>
<c:if test="${empty sessionScope.trigFuncs}">
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
    <p class="hint">Step 1 of 2: Enter your own whole numbers (integers) for each question.</p>

    <%-- validation error থাকলে দেখাও --%>
    <c:if test="${not empty errors}">
        <div class="error">
            <c:forEach var="e" items="${errors}"><div><c:out value="${e}"/></div></c:forEach>
        </div>
    </c:if>

    <%-- param.x = আগের বার দেওয়া মান, যাতে error হলে আবার লিখতে না হয় --%>
    <form action="${pageContext.request.contextPath}/options" method="post">
        <div class="question">
            <label for="primeNumber">Q1. Prime Number — enter a number to check <span class="marks">[2 marks]</span></label>
            <input type="number" step="1" id="primeNumber" name="primeNumber"
                   value="<c:out value='${param.primeNumber}'/>" placeholder="e.g. 29" required>
        </div>

        <div class="question">
            <label>Q2. Modulus — enter a and b to find a % b <span class="marks">[2 marks]</span></label>
            <div class="row">
                <input type="number" step="1" name="modA" value="<c:out value='${param.modA}'/>" placeholder="a (e.g. 17)" required>
                <span class="op">%</span>
                <input type="number" step="1" name="modB" value="<c:out value='${param.modB}'/>" placeholder="b (not 0)" required>
            </div>
        </div>

        <div class="question">
            <label for="angle1">Q3. Trigonometry — enter an angle θ for ${sessionScope.trigFuncs[0]}(θ°) <span class="marks">[3 marks]</span></label>
            <input type="number" step="1" id="angle1" name="angle1"
                   value="<c:out value='${param.angle1}'/>" placeholder="angle in degrees (e.g. 30)" required>
        </div>

        <div class="question">
            <label for="angle2">Q4. Trigonometry — enter an angle θ for ${sessionScope.trigFuncs[1]}(θ°) <span class="marks">[3 marks]</span></label>
            <input type="number" step="1" id="angle2" name="angle2"
                   value="<c:out value='${param.angle2}'/>" placeholder="angle in degrees (e.g. 45)" required>
        </div>

        <button type="submit">Get Questions</button>
    </form>
</div>
</body>
</html>
