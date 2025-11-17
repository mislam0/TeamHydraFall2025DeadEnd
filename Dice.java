/* 
 * Authors: Rahsun
 */

import java.util.Random;

public class Dice {
    private Random random;
    private boolean isMaxRoll;

    

    public Dice() {
        random = new Random();
    }

    // Roll a die with 'sides' number of sides
    public int roll(int sides) {
        if (sides < 2) {
            throw new IllegalArgumentException("Dice must have at least 2 sides");
        }
        return random.nextInt(sides) + 1; // random.nextInt(n) gives 0 to n-1
    }
    public boolean rollIsMax(int roll, int sides) {
        return roll == sides;
    }

    // Convenience methods for specific dice
    public int d2() {
        return roll(2);
    }

    public int d4() {
        return roll(4);
    }

    public int d8() {
        return roll(8);
    }

    public int d12() {
        return roll(12);
    }
    public int dX(int n) {
    return roll(n); // uses your existing generic roll method
}

public int rollDice(String notation) {
    notation = notation.toLowerCase().trim();

    // handle formats like "d6"
    if (notation.startsWith("d")) {
        int sides = Integer.parseInt(notation.substring(1));
        if (sides == 4 || sides == 8 || sides == 12) {
            isMaxRoll = true;
        }
        return roll(sides);
    }

    // handle formats like "2d6"
    if (notation.contains("d")) {
        String[] parts = notation.split("d");
        int num = Integer.parseInt(parts[0]);
        int sides = Integer.parseInt(parts[1]);

        int total = 0;
        for (int i = 0; i < num; i++) {
            total += roll(sides);
        }
        return total;
    }

    throw new IllegalArgumentException("Invalid dice notation: " + notation);
}

public boolean isMaxRoll() {
        return isMaxRoll;
    }

    public void setMaxRoll(boolean isMaxRoll) {
        this.isMaxRoll = isMaxRoll;
    }



}