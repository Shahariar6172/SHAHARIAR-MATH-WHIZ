package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.GameScore;

/**
 * ScoreDAO (Data Access Object) — database এর সাথে সব কাজ এখানে।
 */
public class ScoreDAO {

    // ⚠️ নিজের MySQL username/password বসাও
    private static final String URL = "jdbc:mysql://localhost:3306/math_whiz_db";
    private static final String USER = "root";
    private static final String PASS = "";

    // class load হওয়ার সময় একবার MySQL driver load করা হয়
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /** নতুন database connection তৈরি */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * INSERT: player এর score save করা।
     * PreparedStatement এ "?" placeholder ব্যবহার করায় SQL Injection থেকে নিরাপদ।
     */
    public void saveScore(GameScore gs) throws SQLException {
        String sql = "INSERT INTO game_scores (player_name, score, difficulty, played_at) VALUES (?, ?, ?, ?)";

        // try-with-resources: কাজ শেষে connection নিজে থেকেই close হয়
        // (C++ এর RAII / destructor এর মতো)
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, gs.getPlayerName()); // ১ম ? = name
            ps.setInt(2, gs.getScore()); // ২য় ? = score
            ps.setString(3, gs.getDifficulty()); // ৩য় ? = difficulty
            ps.setTimestamp(4, gs.getPlayedAt()); // ৪র্থ ? = date/time
            ps.executeUpdate();
        }
    }

    /**
     * SELECT: সর্বোচ্চ ১০টি score।
     * score বেশি → আগে; score সমান হলে যে আগে খেলেছে সে আগে।
     */
    public List<GameScore> getTop10() throws SQLException {
        String sql = "SELECT * FROM game_scores ORDER BY score DESC, played_at ASC LIMIT 10";
        List<GameScore> list = new ArrayList<>(); // C++ এর vector<GameScore> এর মতো

        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            // প্রতিটি row পড়ে GameScore object বানানো
            while (rs.next()) {
                GameScore gs = new GameScore();
                gs.setId(rs.getInt("id"));
                gs.setPlayerName(rs.getString("player_name"));
                gs.setScore(rs.getInt("score"));
                gs.setDifficulty(rs.getString("difficulty"));
                gs.setPlayedAt(rs.getTimestamp("played_at"));
                list.add(gs);
            }
        }
        return list;
    }
}
