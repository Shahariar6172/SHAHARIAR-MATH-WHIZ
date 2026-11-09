package model;

import java.io.Serializable;
import service.MathService;

/**
 * একটি প্রশ্ন। সঠিক উত্তর কোথাও store করা নেই —
 * প্রতিবার MathService দিয়ে হিসাব করে বের করা হয় (assignment এর শর্ত)।
 * Serializable: কারণ এই object session এ রাখা হবে।
 */
public class Question implements Serializable {

    // প্রশ্নের ধরন: PRIME, MOD, SIN, COS, TAN
    private final String type;
    private final int a;      // PRIME: সংখ্যা, MOD: ভাজ্য, TRIG: angle (degree)
    private final int b;      // MOD: ভাজক (অন্যগুলোতে ব্যবহার হয় না)
    private final int marks;

    // player এর উত্তর ও ফলাফল (submit এর পরে set হবে)
    private String userAnswer = "";
    private boolean correct = false;

    public Question(String type, int a, int b, int marks) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.marks = marks;
    }

    /** প্রশ্নের লেখা (game page এ দেখানোর জন্য) */
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

    /** সুন্দরভাবে সঠিক উত্তর দেখানোর জন্য (integer হলে দশমিক ছাড়া, নাহলে ৪ ঘর) */
    public String getCorrectAnswerText() {
        if (isIntegerQuestion()) {
            return String.valueOf((int) getCorrectAnswer());
        }
        return String.format("%.4f", getCorrectAnswer());
    }

    /**
     * player এর উত্তর check করা।
     * PRIME/MOD: integer হতে হবে এবং হুবহু মিলতে হবে।
     * TRIG: double, tolerance দিয়ে তুলনা।
     * ভুল format (যেমন "abc") দিলে উত্তর ভুল ধরা হয়।
     */
    public void checkAnswer(String input) {
        userAnswer = (input == null) ? "" : input.trim();
        try {
            if (isIntegerQuestion()) {
                int given = Integer.parseInt(userAnswer);       // C++ এর stoi() এর মতো
                correct = (given == (int) getCorrectAnswer());
            } else {
                double given = Double.parseDouble(userAnswer);  // C++ এর stod() এর মতো
                correct = MathService.isClose(getCorrectAnswer(), given);
            }
        } catch (NumberFormatException e) {
            correct = false; // সংখ্যা না দিলে ভুল
        }
    }

    private boolean isIntegerQuestion() {
        return type.equals("PRIME") || type.equals("MOD");
    }

    // ---------- Getters (JSP থেকে ${q.text} এভাবে access করতে লাগে) ----------
    public String getType()       { return type; }
    public int getMarks()         { return marks; }
    public String getUserAnswer() { return userAnswer; }
    public boolean isCorrect()    { return correct; }
    public int getEarnedMarks()   { return correct ? marks : 0; }
}
