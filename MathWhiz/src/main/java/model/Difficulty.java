package model;

/**
 * Difficulty — game এর level আর তার score multiplier।
 * enum = নির্দিষ্ট কিছু মানের তালিকা (C++ এর enum class এর মতো, কিন্তু field/method রাখা যায়)।
 *
 * এখন শুধু NORMAL আছে (multiplier = 1.0), তাই সর্বোচ্চ score 10 ই থাকে।
 * ভবিষ্যতে নতুন level যোগ করতে শুধু এখানে একটা লাইন লিখলেই হবে, যেমন: HARD(1.5)
 */
public enum Difficulty {

    NORMAL(1.0);

    private final double multiplier;

    Difficulty(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Final Score = round(Base Score × Multiplier)
     * NORMAL এ: 7 × 1.0 = 7
     */
    public int applyTo(int baseScore) {
        return (int) Math.round(baseScore * multiplier);
    }
}
