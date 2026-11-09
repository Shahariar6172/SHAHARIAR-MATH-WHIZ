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
import model.Difficulty;
import model.GameScore;
import model.Question;
import service.MathService;

/**
 * GameServlet — browser এর request গ্রহণ করে, logic চালায়, তারপর JSP তে পাঠায়।
 * একটাই Servlet তিনটা URL handle করে:
 *   /start       → নাম নিয়ে game page (player সংখ্যা দিবে)
 *   /options     → player এর সংখ্যা দিয়ে ৪টি প্রশ্ন ও প্রতিটির ৩টা option তৈরি
 *   /submit      → select করা উত্তর check, score হিসাব, DB তে save
 *   /leaderboard → Top 10 দেখানো
 */
@WebServlet(urlPatterns = {"/start", "/options", "/submit", "/leaderboard"})
public class GameServlet extends HttpServlet {

    private static final Difficulty DIFFICULTY = Difficulty.NORMAL; // আপাতত একটাই level

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
            case "/start":   startGame(req, resp);    break;
            case "/options": makeOptions(req, resp);  break;
            case "/submit":  submitAnswers(req, resp); break;
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

        // Q3, Q4 এর জন্য random trig function (sin/cos/tan) বাছাই
        String[] funcs = {"sin", "cos", "tan"};
        List<String> trigFuncs = new ArrayList<>();
        trigFuncs.add(funcs[random.nextInt(3)]);
        trigFuncs.add(funcs[random.nextInt(3)]);

        HttpSession session = req.getSession();
        session.setAttribute("playerName", name);
        session.setAttribute("trigFuncs", trigFuncs);
        session.removeAttribute("questions");

        req.getRequestDispatcher("/game.jsp").forward(req, resp);
    }

    // ==================== /options ====================
    @SuppressWarnings("unchecked")
    private void makeOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("trigFuncs") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        List<String> trigFuncs = (List<String>) session.getAttribute("trigFuncs");
        List<String> errors = new ArrayList<>();

        // player এর দেওয়া সংখ্যা পড়া (শুধু integer গ্রহণযোগ্য)
        Integer primeNum = readInt(req, "primeNumber", "Q1 number", errors);
        Integer modA     = readInt(req, "modA", "Q2 dividend (a)", errors);
        Integer modB     = readInt(req, "modB", "Q2 divisor (b)", errors);
        Integer angle1   = readInt(req, "angle1", "Q3 angle", errors);
        Integer angle2   = readInt(req, "angle2", "Q4 angle", errors);

        // Divisor 0 হলে validation error (calculateMod নিজেই exception দেয়)
        if (modA != null && modB != null) {
            try {
                MathService.calculateMod(modA, modB);
            } catch (IllegalArgumentException e) {
                errors.add("Q2: The divisor must not be zero.");
            }
        }
        // tan(90°), tan(270°) ... undefined হলে validation error
        checkTan(trigFuncs.get(0), angle1, "Q3", errors);
        checkTan(trigFuncs.get(1), angle2, "Q4", errors);

        // ভুল থাকলে একই page এ error সহ ফেরত (দেওয়া মানগুলো থেকে যাবে)
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/game.jsp").forward(req, resp);
            return;
        }

        // ৪টি প্রশ্ন তৈরি: ১টি prime, ১টি modulus, ২টি trigonometry
        List<Question> qs = new ArrayList<>();
        qs.add(new Question("PRIME", primeNum, 0, 2));
        qs.add(new Question("MOD", modA, modB, 2));
        qs.add(new Question(trigFuncs.get(0).toUpperCase(), angle1, 0, 3));
        qs.add(new Question(trigFuncs.get(1).toUpperCase(), angle2, 0, 3));

        // প্রতিটি প্রশ্নের জন্য ৩টা option (১টা সঠিক)
        for (Question q : qs) {
            q.generateOptions(random);
        }

        session.setAttribute("questions", qs);
        req.getRequestDispatcher("/options.jsp").forward(req, resp);
    }

    /** form থেকে integer পড়া; integer না হলে error list এ message যোগ করে null দেয় */
    private Integer readInt(HttpServletRequest req, String field, String label, List<String> errors) {
        String value = req.getParameter(field);
        if (value == null || value.trim().isEmpty()) {
            errors.add(label + ": please enter a number.");
            return null;
        }
        try {
            return Integer.parseInt(value.trim()); // C++ এর stoi() এর মতো
        } catch (NumberFormatException e) {
            errors.add(label + ": only whole numbers (integers) are allowed.");
            return null;
        }
    }

    /** tan এর angle এ cos = 0 হলে error */
    private void checkTan(String func, Integer angle, String label, List<String> errors) {
        if (!"tan".equals(func) || angle == null) return;
        try {
            MathService.calculateTan(angle);
        } catch (ArithmeticException e) {
            errors.add(label + ": tan(" + angle + "°) is undefined. Please choose another angle.");
        }
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

        // player এর select করা option Java method দিয়ে check
        for (int i = 0; i < qs.size(); i++) {
            qs.get(i).checkAnswer(req.getParameter("answer" + i));
        }

        boolean primeCorrect = qs.get(0).isCorrect();
        boolean modCorrect   = qs.get(1).isCorrect();
        boolean trig1Correct = qs.get(2).isCorrect();
        boolean trig2Correct = qs.get(3).isCorrect();

        // ধাপ ১: Base Score (assignment অনুযায়ী)
        int baseScore = 0;
        if (primeCorrect) baseScore += 2;
        if (modCorrect)   baseScore += 2;
        if (trig1Correct) baseScore += 3;
        if (trig2Correct) baseScore += 3;

        // ধাপ ২: Difficulty multiplier প্রয়োগ → Final Score
        // NORMAL এ multiplier = 1.0, তাই final score = base score (সর্বোচ্চ 10)
        int totalScore = DIFFICULTY.applyTo(baseScore);

        // Database এ save (difficulty নাম "NORMAL" হিসেবে যায়)
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            dao.saveScore(new GameScore(name, totalScore, DIFFICULTY.name(), now));
        } catch (SQLException e) {
            req.setAttribute("dbError", "Score could not be saved: " + e.getMessage());
        }

        req.setAttribute("playerName", name);
        req.setAttribute("questions", qs);
        req.setAttribute("baseScore", baseScore);
        req.setAttribute("multiplier", DIFFICULTY.getMultiplier());
        req.setAttribute("totalScore", totalScore);
        req.setAttribute("difficulty", DIFFICULTY.name());

        // একই প্রশ্নে আবার submit যেন না হয়
        session.removeAttribute("questions");
        session.removeAttribute("trigFuncs");

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
