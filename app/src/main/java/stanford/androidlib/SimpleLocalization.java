/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import java.text.*;
import java.util.*;

/**
 * Utility class to help with localization of common values.
 */
public final class SimpleLocalization {
    private static Context context;
    private static SimpleLocalization INSTANCE = new SimpleLocalization();

    /*
     * You cannot directly construct an object of type SimpleLocalization.
     */
    private SimpleLocalization() {
        // empty
    }

    /*
     * Returns an object of type SimpleLocalization that is bound to the given context.
     * Pass your activity, fragment, etc. as the context.
     */
    public static SimpleLocalization with(Context context) {
        SimpleLocalization.context = context;
        return INSTANCE;
    }

    /**
     * Returns true if this app is running in a Left-to-Right (LTR) layout.
     */
    public boolean isLTR() {
        return !isRTL();
    }

    /**
     * Returns true if this app is running in a Right-to-Left (RTL) layout.
     */
    public boolean isRTL() {
        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration config = context.getResources().getConfiguration();
            return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        } else {
            final int directionality = Character.getDirectionality(Locale.getDefault().getDisplayName().charAt(0));
            return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                    directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        }
    }

    /**
     * Returns a date string formatted for this locale.
     */
    public String localizeDate(Date date) {
        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
        return fmt.format(date);
    }

    /**
     * Returns a number string formatted for this locale.
     */
    public String localizeNumber(int number) {
        NumberFormat fmt = NumberFormat.getInstance();
        return fmt.format(number);
    }

    /**
     * Returns a number string formatted for this locale.
     */
    public String localizeNumber(long number) {
        NumberFormat fmt = NumberFormat.getInstance();
        String result = fmt.format(number);
        return result;
    }

    /**
     * Returns a number string formatted for this locale.
     */
    public String localizeNumber(double number) {
        NumberFormat fmt = NumberFormat.getInstance();
        return fmt.format(number);
    }

    /**
     * Returns a currency number string formatted for this locale.
     */
    public String localizeCurrency(double amount) {
        return localizeCurrency(Currency.getInstance(Locale.getDefault()), amount);
    }

    /**
     * Returns a currency number string formatted for the given currency.
     */
    public String localizeCurrency(String currency, double amount) {
        return localizeCurrency(Currency.getInstance(currency), amount);
    }

    /**
     * Returns a currency number string formatted for the given currency.
     */
    public String localizeCurrency(Currency currency, double amount) {
        NumberFormat money = NumberFormat.getCurrencyInstance();
        money.setCurrency(Currency.getInstance("EUR"));
        String currencyText = money.format(amount);
        return currencyText;
    }

    public int parseLocalizedInt(String text) {
        NumberFormat fmt = NumberFormat.getInstance();
        try {
            int num = (Integer) fmt.parse(text);
            return num;
        } catch (ClassCastException | ParseException e) {
            throw new IllegalArgumentException("Unable to parse as localized int: \"" + text + "\"");
        }
    }

    public long parseLocalizedLong(String text) {
        NumberFormat fmt = NumberFormat.getInstance();
        try {
            return (Long) fmt.parse(text);
        } catch (ClassCastException | ParseException e) {
            throw new IllegalArgumentException("Unable to parse as localized long: \"" + text + "\"");
        }
    }

    public double parseLocalizedDouble(String text) {
        NumberFormat fmt = NumberFormat.getInstance();
        try {
            return (Double) fmt.parse(text);
        } catch (ClassCastException | ParseException e) {
            throw new IllegalArgumentException("Unable to parse as localized double: \"" + text + "\"");
        }
    }

    /**
     * Returns a float parsed from the given string.
     */
    public float parseLocalizedFloat(String text) {
        NumberFormat fmt = NumberFormat.getInstance();
        try {
            return (Float) fmt.parse(text);
        } catch (ClassCastException | ParseException e) {
            throw new IllegalArgumentException("Unable to parse as localized float: \"" + text + "\"");
        }
    }
}
