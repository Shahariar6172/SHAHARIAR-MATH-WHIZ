package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import service.MathService;

/**
 * একটি প্রশ্ন। সংখ্যাগুলো (a, b) player নিজে দেয়।
 * সঠিক উত্তর কোথাও store করা নেই — প্রতিবার MathService দিয়ে হিসাব করা হয়।
 * তারপর game ৩টা option বানায় (১টা সঠিক + ২টা ভুল), player একটা select করে।
 * Serializable: কারণ এই object session এ রাখা হবে।
 */
public class Question implements Serializable {

    // প্রশ্নের ধরন: PRIME, MOD, SIN, COS, TAN
    private final String type;
    private final int a;      // PRIME: সংখ্যা, MOD: ভাজ্য, TRIG: angle (degree)
    private final int b;      // MOD: ভাজক (অন্যগুলোতে ব্যবহার হয় না)
    private final int marks;

    // ৩টা option এর মান (String হিসেবে, যেমন "1", "0.8660")
    private final List<String> options = new ArrayList<>();

    // player এর উত্তর ও ফলাফল (submit এর পরে set হবে)
    private String userAnswer = "";
    private boolean correct = false;

    public Question(String type, int a, int b, int marks) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.marks = marks;
    }

    /** প্রশ্নের লেখা */
    public String getText() {
        switch (type) {
            case "PRIME": return "Is " + a + " a prime number? (1 = Prime, 0 = Not Prime)";
            case "MOD":   return "What is " + a + " % " + b + " ?";
            case "SIN":   return "What is sin(" + a + "°) ?";
            case "COS":   return "What is cos(" + a + "°) ?";
            case "TAN":   return "What is tan(" + a + "°) ?";
            default:      return "Unknown question";
        }
    }

    /** Java method call করে সঠিক উত্তর হিসাব করা */
    public double getCorrectAnswer() {
        switch (type) {
            case "PRIME": return MathService.isPrime(a);
            case "MOD":   return MathService.calculateMod(a, b);
            case "SIN":   return MathService.calculateSin(a);
            case "COS":   return MathService.calculateCos(a);
            case "TAN":   return MathService.calculateTan(a);
            default:      throw new IllegalStateException("Unknown type");
        }
    }

    public String getCorrectAnswerText() {
        return format(getCorrectAnswer());
    }

    // ==================== ৩টা option তৈরি ====================
    /**
     * ১টা সঠিক উত্তর + ২টা কাছাকাছি ভুল উত্তর, তারপর এলোমেলো (shuffle) করা।
     */
    public void generateOptions(Random random) {
        double right = getCorrectAnswer();
        List<Double> wrong = new ArrayList<>(); // ভুল উত্তরের প্রার্থী তালিকা

        if (type.equals("PRIME")) {
            wrong.add(1 - right);   // 1 হলে 0, 0 হলে 1
            wrong.add(-1.0);        // ভুল option: -1
        } else if (type.equals("MOD")) {
            int r = (int) right;
            wrong.add((double) (a / b));   // ভাগফল (ভাগশেষ না) — সাধারণ ভুল
            wrong.add((double) (r + 1));
            wrong.add((double) (r - 1));
            wrong.add((double) (r + 2));
            Collections.shuffle(wrong, random);
        } else {
            // একই angle এর অন্য trig function এর মান — বিশ্বাসযোগ্য ভুল উত্তর
            if (!type.equals("SIN")) wrong.add(MathService.calculateSin(a));
            if (!type.equals("COS")) wrong.add(MathService.calculateCos(a));
            if (!type.equals("TAN")) {
                try {
                    wrong.add(MathService.calculateTan(a));
                } catch (ArithmeticException e) {
                    // tan undefined হলে বাদ
                }
            }
            wrong.add(-right);
            wrong.add(right + 0.5);
            wrong.add(right - 0.5);
            Collections.shuffle(wrong, random);
        }

        // সঠিক উত্তর আগে নিয়ে, তারপর এমন ২টা ভুল নেওয়া যেগুলো আলাদা
        List<Double> chosen = new ArrayList<>();
        chosen.add(right);
        for (double w : wrong) {
            if (chosen.size() == 3) break;
            if (isDifferent(w, chosen)) chosen.add(w);
        }
        // খুব কম ক্ষেত্রে যথেষ্ট না পেলে right + 1, right + 2 ... দিয়ে পূরণ
        for (int k = 1; chosen.size() < 3; k++) {
            if (isDifferent(right + k, chosen)) chosen.add(right + k);
        }

        Collections.shuffle(chosen, random);
        options.clear();
        for (double v : chosen) options.add(format(v));
    }

    /** নতুন মানটা আগের সব option থেকে আলাদা কিনা */
    private boolean isDifferent(double value, List<Double> list) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return false;
        for (double x : list) {
            if (Math.abs(x - value) <= MathService.TOLERANCE) return false;
        }
        return true;
    }

    // ==================== উত্তর check ====================
    /**
     * player যে option select করেছে তার মান check করা।
     * PRIME/MOD: integer হুবহু মিলতে হবে। TRIG: tolerance দিয়ে তুলনা।
     */
    public void checkAnswer(String input) {
        userAnswer = (input == null) ? "" : input.trim();
        try {
            if (isIntegerQuestion()) {
                int given = Integer.parseInt(userAnswer);
                correct = (given == (int) getCorrectAnswer());
            } else {
                double given = Double.parseDouble(userAnswer);
                correct = MathService.isClose(getCorrectAnswer(), given);
            }
        } catch (NumberFormatException e) {
            correct = false; // কিছু select না করলে ভুল
        }
    }

    /** মানকে সুন্দর লেখায় রূপান্তর (integer হলে দশমিক ছাড়া, নাহলে ৪ ঘর) */
    private String format(double v) {
        if (isIntegerQuestion()) {
            return String.valueOf((int) Math.round(v));
        }
        if (Math.abs(v) < 0.00005) v = 0.0; // "-0.0000" এড়াতে
        // Locale.US: সবসময় "." দশমিক চিহ্ন, যাতে parseDouble ঠিকমতো কাজ করে
        return String.format(Locale.US, "%.4f", v);
    }

    private boolean isIntegerQuestion() {
        return type.equals("PRIME") || type.equals("MOD");
    }

    // ---------- Getters (JSP থেকে ${q.text} এভাবে access করতে লাগে) ----------
    public String getType()          { return type; }
    public int getMarks()            { return marks; }
    public List<String> getOptions() { return options; }
    public String getUserAnswer()    { return userAnswer; }
    public boolean isCorrect()       { return correct; }
    public int getEarnedMarks()      { return correct ? marks : 0; }
}
