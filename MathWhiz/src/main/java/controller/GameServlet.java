package controller;

import dao.ScoreDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.GameScore;
import model.Question;

/**
 * GameServlet — browser এর request গ্রহণ করে, logic চালায়, তারপর JSP তে পাঠায়।
 * একটাই Servlet তিনটা URL handle করে:
 *   /start       → নাম নিয়ে ৪টি প্রশ্ন তৈরি
 *   /submit      → উত্তর check, score হিসাব, DB তে save
 *   /leaderboard → Top 10 দেখানো
 */
@WebServlet(urlPatterns = {"/start", "/submit", "/leaderboard"})
public class GameServlet extends HttpServlet {

    private static final String DIFFICULTY = "NORMAL"; // আপাতত একটাই level

    private final ScoreDAO dao = new ScoreDAO();
    private final Random random = new Random();

    // GET request (যেমন link এ click করলে)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("/leaderboard".equals(req.getServletPath())) {
            showLeaderboard(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/index.jsp"); // অন্য সব → home
        }
    }

    // POST request (form submit করলে)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        switch (req.getServletPath()) {
            case "/start":  startGame(req, resp);    break;
            case "/submit": submitAnswers(req, resp); break;
            default:        doGet(req, resp);
        }
    }

    // ==================== /start ====================
    private void startGame(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("playerName");

        // নাম খালি হলে error সহ আবার home page
        if (name == null || name.trim().isEmpty()) {
            req.setAttribute("error", "Please enter your name.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }
        name = name.trim();
        if (name.length() > 100) name = name.substring(0, 100); // DB column = VARCHAR(100)

        // session এ নাম ও প্রশ্ন রাখা (পরের request এ লাগবে)
        HttpSession session = req.getSession();
        session.setAttribute("playerName", name);
        session.setAttribute("questions", generateQuestions());

        req.getRequestDispatcher("/game.jsp").forward(req, resp);
    }

    /** ৪টি প্রশ্ন: ১টি prime, ১টি modulus, ২টি trigonometry */
    private List<Question> generateQuestions() {
        List<Question> qs = new ArrayList<>();

        // Q1: Prime — 1 থেকে 100 এর মধ্যে random সংখ্যা
        qs.add(new Question("PRIME", random.nextInt(100) + 1, 0, 2));

        // Q2: Modulus — a = 10..99, b = 2..12 (b কখনো 0 হবে না)
        qs.add(new Question("MOD", random.nextInt(90) + 10, random.nextInt(11) + 2, 2));

        // Q3, Q4: Trigonometry (3 marks করে)
        qs.add(randomTrigQuestion());
        qs.add(randomTrigQuestion());
        return qs;
    }

    private Question randomTrigQuestion() {
        int[] angles = {0, 30, 45, 60, 90, 120, 135, 150, 180, 210, 225, 240, 270, 300, 315, 330};
        String[] funcs = {"SIN", "COS", "TAN"};

        String func = funcs[random.nextInt(funcs.length)];
        int angle = angles[random.nextInt(angles.length)];

        // tan(90°), tan(270°) undefined — তাই নতুন angle বাছাই
        while (func.equals("TAN") && (angle == 90 || angle == 270)) {
            angle = angles[random.nextInt(angles.length)];
        }
        return new Question(func, angle, 0, 3);
    }

    // ==================== /submit ====================
    @SuppressWarnings("unchecked")
    private void submitAnswers(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // session না থাকলে (যেমন সরাসরি /submit এ গেলে) home এ ফেরত
        if (session == null || session.getAttribute("questions") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        String name = (String) session.getAttribute("playerName");
        List<Question> qs = (List<Question>) session.getAttribute("questions");

        // প্রতিটি উত্তর Java method দিয়ে check
        for (int i = 0; i < qs.size(); i++) {
            qs.get(i).checkAnswer(req.getParameter("answer" + i));
        }

        boolean primeCorrect = qs.get(0).isCorrect();
        boolean modCorrect   = qs.get(1).isCorrect();
        boolean trig1Correct = qs.get(2).isCorrect();
        boolean trig2Correct = qs.get(3).isCorrect();

        // Scoring Logic (assignment অনুযায়ী)
        int totalScore = 0;
        if (primeCorrect) totalScore += 2;
        if (modCorrect)   totalScore += 2;
        if (trig1Correct) totalScore += 3;
        if (trig2Correct) totalScore += 3;

        // Database এ save
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            dao.saveScore(new GameScore(name, totalScore, DIFFICULTY, now));
        } catch (SQLException e) {
            req.setAttribute("dbError", "Score could not be saved: " + e.getMessage());
        }

        req.setAttribute("playerName", name);
        req.setAttribute("questions", qs);
        req.setAttribute("totalScore", totalScore);
        req.setAttribute("difficulty", DIFFICULTY);

        // একই প্রশ্নে আবার submit যেন না হয়
        session.removeAttribute("questions");

        req.getRequestDispatcher("/result.jsp").forward(req, resp);
    }

    // ==================== /leaderboard ====================
    private void showLeaderboard(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("scores", dao.getTop10());
        } catch (SQLException e) {
            req.setAttribute("dbError", "Could not load leaderboard: " + e.getMessage());
        }
        req.getRequestDispatcher("/leaderboard.jsp").forward(req, resp);
    }
}
