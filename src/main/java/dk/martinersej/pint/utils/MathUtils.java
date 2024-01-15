package dk.martinersej.pint.utils;

public class MathUtils {

    // random number between min and max
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}
