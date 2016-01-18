package hsoines.oekoflex.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 14:20
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
