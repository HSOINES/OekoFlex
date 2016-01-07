package hsoines.oekoflex.strategies;

import java.util.Calendar;
import java.util.Date;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 22:11
 */
public final class DaytimePriceStrategy implements PriceStrategy {
    private final float priceDuringDay;
    private final float decreaseAtNightInPercent;

    public DaytimePriceStrategy(final float priceDuringDay, final float decreaseAtNightInPercent) {
        this.priceDuringDay = priceDuringDay;
        this.decreaseAtNightInPercent = decreaseAtNightInPercent;
    }


    @Override
    public float getPrice(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.HOUR_OF_DAY) > 8 && calendar.get(Calendar.HOUR_OF_DAY) < 20) {
            return priceDuringDay;
        } else {
            return priceDuringDay * (1 - decreaseAtNightInPercent);
        }
    }
}
