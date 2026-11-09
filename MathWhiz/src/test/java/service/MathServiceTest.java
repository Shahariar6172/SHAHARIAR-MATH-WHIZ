package service;

import static org.junit.jupiter.api.Assertions.*;

import model.Question;
import org.junit.jupiter.api.Test;

/**
 * TASK 09: Testing and Validation
 * চালাতে: mvn test
 */
class MathServiceTest {

    private static final double EPS = MathService.TOLERANCE;

    // ---------- Prime ----------
    @Test void prime7()  { assertEquals(1, MathService.isPrime(7)); }
    @Test void prime8()  { assertEquals(0, MathService.isPrime(8)); }
    @Test void prime1()  { assertEquals(0, MathService.isPrime(1)); }
    @Test void primeExtra() {
        assertEquals(1, MathService.isPrime(2));
        assertEquals(0, MathService.isPrime(0));
        assertEquals(0, MathService.isPrime(-7));  // negative prime না
        assertEquals(1, MathService.isPrime(97));
    }

    // ---------- Modulus ----------
    @Test void mod17by5()  { assertEquals(2, MathService.calculateMod(17, 5)); }
    @Test void modNegative() {
        assertEquals(-2, MathService.calculateMod(-17, 5)); // Java তে চিহ্ন a এর মতো
        assertEquals(2,  MathService.calculateMod(17, -5));
    }
    @Test void modZeroDivisor() {
        // Divisor 0 → validation error (exception)
        assertThrows(IllegalArgumentException.class, () -> MathService.calculateMod(10, 0));
    }

    // ---------- Trigonometry ----------
    @Test void sin30() { assertEquals(0.5, MathService.calculateSin(30), EPS); }
    @Test void cos60() { assertEquals(0.5, MathService.calculateCos(60), EPS); }
    @Test void tan45() { assertEquals(1.0, MathService.calculateTan(45), EPS); }
    @Test void tan90Undefined() {
        assertThrows(ArithmeticException.class, () -> MathService.calculateTan(90));
    }
    @Test void identity() {
        // x² + y² = 1
        for (int d = 0; d <= 360; d += 15) assertTrue(MathService.checkIdentity(d));
    }

    // ---------- Scoring ----------
    @Test void allCorrectGives10() {
        Question[] qs = {
            new Question("PRIME", 7, 0, 2),
            new Question("MOD", 17, 5, 2),
            new Question("SIN", 30, 0, 3),
            new Question("TAN", 45, 0, 3)
        };
        String[] answers = {"1", "2", "0.5", "1"};
        int total = 0;
        for (int i = 0; i < 4; i++) {
            qs[i].checkAnswer(answers[i]);
            total += qs[i].getEarnedMarks();
        }
        assertEquals(10, total);
        // NORMAL difficulty (×1.0) প্রয়োগের পরেও final score 10
        assertEquals(10, model.Difficulty.NORMAL.applyTo(total));
        assertEquals(7, model.Difficulty.NORMAL.applyTo(7));
    }

    @Test void threeOptionsWithOneCorrect() {
        // player এর দেওয়া সংখ্যা দিয়ে প্রশ্ন → ৩টা option, ঠিক ১টা সঠিক
        Question[] qs = {
            new Question("PRIME", 29, 0, 2),
            new Question("MOD", 17, 5, 2),
            new Question("SIN", 45, 0, 3),
            new Question("TAN", 135, 0, 3)
        };
        java.util.Random rnd = new java.util.Random(1);
        for (Question q : qs) {
            q.generateOptions(rnd);
            assertEquals(3, q.getOptions().size());
            assertEquals(3, new java.util.HashSet<>(q.getOptions()).size()); // সব আলাদা
            int right = 0;
            for (String opt : q.getOptions()) {
                q.checkAnswer(opt);
                if (q.isCorrect()) right++;
            }
            assertEquals(1, right);
        }
    }

    @Test void wrongAnswersGiveNoMarks() {
        Question q1 = new Question("PRIME", 8, 0, 2);
        q1.checkAnswer("1");               // ভুল
        Question q2 = new Question("COS", 60, 0, 3);
        q2.checkAnswer("0.8");             // ভুল
        Question q3 = new Question("MOD", 17, 5, 2);
        q3.checkAnswer("abc");             // সংখ্যা না
        assertEquals(0, q1.getEarnedMarks() + q2.getEarnedMarks() + q3.getEarnedMarks());
    }
}
