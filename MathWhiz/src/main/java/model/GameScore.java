package model;

import java.sql.Timestamp;

/**
 * GameScore — database এর game_scores table এর এক row কে represent করে।
 * (C++ এর একটা simple struct/class এর মতো, শুধু data রাখে)
 */
public class GameScore {

    private int id;
    private String playerName;
    private int score;
    private String difficulty;
    private Timestamp playedAt;

    // খালি constructor
    public GameScore() { }

    public GameScore(String playerName, int score, String difficulty, Timestamp playedAt) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.playedAt = playedAt;
    }

    // ---------- Getters & Setters ----------
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getPlayerName()             { return playerName; }
    public void setPlayerName(String name)    { this.playerName = name; }

    public int getScore()                     { return score; }
    public void setScore(int score)           { this.score = score; }

    public String getDifficulty()             { return difficulty; }
    public void setDifficulty(String d)       { this.difficulty = d; }

    public Timestamp getPlayedAt()            { return playedAt; }
    public void setPlayedAt(Timestamp t)      { this.playedAt = t; }
}
