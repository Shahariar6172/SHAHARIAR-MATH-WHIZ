package service;

/**
 * MathService — সব গাণিতিক হিসাব এখানে।
 * সব method "static", তাই object না বানিয়েই MathService.isPrime(7) এভাবে call করা যায়
 * (C++ এর static member function এর মতো)।
 */
public class MathService {

    // দশমিক উত্তর তুলনার জন্য tolerance (যেমন 0.707 আর 0.7071 কে সমান ধরা হবে)
    public static final double TOLERANCE = 0.01;

    // ==================== TASK 03: Prime Number ====================
    /**
     * সংখ্যাটি prime হলে 1, না হলে 0 return করে।
     * 2 এর কম সংখ্যা prime না।
     */
    public static int isPrime(int number) {
        if (number < 2) {
            return 0;
        }
        // শুধু √number পর্যন্ত check করলেই যথেষ্ট
        for (int i = 2; (long) i * i <= number; i++) {
            if (number % i == 0) {
                return 0; // ভাগ গেছে, তাই prime না
            }
        }
        return 1;
    }

    // ==================== TASK 04: Modulus ====================
    /**
     * a % b এর ভাগশেষ return করে।
     * b = 0 হলে IllegalArgumentException throw করে (C++ এ এটা crash/undefined হতো)।
     * Java তে negative এর ক্ষেত্রে ফলাফলের চিহ্ন a এর মতো হয়: -17 % 5 = -2
     */
    public static int calculateMod(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        return a % b;
    }

    // ==================== TASK 05: Trigonometry ====================
    // Math.sin/cos/tan radian এ কাজ করে, তাই আগে degree → radian করা হয়।

    public static double calculateSin(double degree) {
        double radian = Math.toRadians(degree); // radian = degree × π / 180
        return Math.sin(radian);
    }

    public static double calculateCos(double degree) {
        double radian = Math.toRadians(degree);
        return Math.cos(radian);
    }

    /**
     * tan = sin / cos. cos = 0 হলে (90°, 270° ...) tan undefined,
     * তাই তখন ArithmeticException throw করা হয়।
     */
    public static double calculateTan(double degree) {
        double radian = Math.toRadians(degree);
        // double এ cos(90°) ঠিক 0 হয় না (≈ 6e-17), তাই খুব ছোট মান দিয়ে check
        if (Math.abs(Math.cos(radian)) < 1e-10) {
            throw new ArithmeticException("tan(" + degree + "°) is undefined");
        }
        return Math.tan(radian);
    }

    /**
     * Reference: sin²θ + cos²θ = 1 (unit circle: x² + y² = 1)।
     * নিজের trig method গুলো ঠিক আছে কিনা যাচাই করতে কাজে লাগে।
     */
    public static boolean checkIdentity(double degree) {
        double s = calculateSin(degree);
        double c = calculateCos(degree);
        return Math.abs(s * s + c * c - 1) < 1e-9;
    }

    // ==================== দশমিক তুলনা ====================
    /** দুইটি double এর পার্থক্য TOLERANCE এর মধ্যে হলে সমান ধরা হয় */
    public static boolean isClose(double expected, double actual) {
        return Math.abs(expected - actual) <= TOLERANCE;
    }
}
