package dk.martinersej.pint.utils;

import org.apache.commons.lang3.text.WordUtils;

public class StringUtils {

    public static String formatEnum(Enum<?> obj) {
        String name = obj.name();
        return WordUtils.capitalizeFully(name.replace("_", " "));
    }

    public static String formatNumber(double number) {
        String minus = "";
        if (number < 0) {
            minus = "-";
            number = Math.abs(number);
        }
        String[] suffixes = {"", "K", "M", "B", "T", "Q", "Qu", "Sx", "Sp", "O", "N"};
        int suffixIndex = 0;
        while (number >= 1000 && suffixIndex < suffixes.length - 1) {
            number /= 1000.00;
            suffixIndex++;
        }
        if (number % 1 == 0) {
            return String.format("%s%.0f%s", minus, number, suffixes[suffixIndex]);
        } else {
            return String.format("%s%.2f%s", minus, number, suffixes[suffixIndex]);
        }
    }

    public static String progressBar(double current, double target, int size) {
        double progress = current / target;
        int displayProgress = (int) Math.round(progress * size);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= size; i++) {
            if (i <= displayProgress)
                sb.append("§a§l|");
            else
                sb.append("§c§l|");
        }
        return sb.toString();
    }

    public static boolean isNumeric(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
