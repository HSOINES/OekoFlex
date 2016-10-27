package hsoines.oekoflex.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Verwendete Zahlenformate
 */
public final class NumberFormatUtil {

    private static NumberFormat numberInstance;

    static {
        String pattern = "###.##";
        numberInstance = new DecimalFormat(pattern);
        numberInstance.setMaximumFractionDigits(2);
    }

    public static String format(float v) {
        return numberInstance.format(v);
    }
}
