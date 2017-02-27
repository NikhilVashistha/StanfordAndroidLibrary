/*
 * @version 2016/12/22
 * - added illegal arg checking to some methods e.g. makeColor
 */

package stanford.androidlib.graphics;

import android.graphics.Paint;

/**
 * A class of constant Paint objects representing commonly used colors.
 * Meant to replicate the main usage of the java.awt.Color class as a source
 * of constant colors.  For other colors, use makeColor or just construct
 * your own Paint object.
 */
public final class GColor {
    GColor() {
        // empty
    }

    private static final int BRIGHTEN_INCREMENT = 32;
    private static final int COMPONENT_MAX = 255;
    private static final int COMPONENT_MIN = 0;

    /** A color constant representing the color black. */
    public static final Paint BLACK      = makeColor(  0,   0,   0);

    /** A color constant representing the color blue. */
    public static final Paint BLUE       = makeColor(  0,   0, 255);

    /** A color constant representing the color brown. */
    public static final Paint BROWN      = makeColor(139,  69,  19);

    /** A color constant representing the color cyan. */
    public static final Paint CYAN       = makeColor(  0, 255, 255);

    /** A color constant representing the color black. */
    public static final Paint DARK_GRAY  = makeColor( 64,  64,  64);

    /** A color constant representing the color black. */
    public static final Paint GRAY       = makeColor(128, 128, 128);

    /** A color constant representing the color black. */
    public static final Paint GREEN      = makeColor(  0, 255,   0);

    /** A color constant representing the color black. */
    public static final Paint LIGHT_GRAY = makeColor(192, 192, 192);

    /** A color constant representing the color magenta. */
    public static final Paint MAGENTA    = makeColor(255,   0, 255);   // same as PURPLE

    /** A color constant representing the color orange. */
    public static final Paint ORANGE     = makeColor(255, 200,   0);

    /** A color constant representing the color pink. */
    public static final Paint PINK       = makeColor(255, 175, 175);

    /** A color constant representing the color purple. */
    public static final Paint PURPLE     = makeColor(255,   0, 255);   // same as MAGENTA

    /** A color constant representing the color red. */
    public static final Paint RED        = makeColor(255,   0,   0);

    /** A color constant representing the color white. */
    public static final Paint WHITE      = makeColor(255, 255, 255);

    /** A color constant representing the color yellow. */
    public static final Paint YELLOW     = makeColor(255, 255,   0);

    private static int brighten(int colorComponent) {
        return Math.min(COMPONENT_MAX, colorComponent + BRIGHTEN_INCREMENT);
    }

    private static int darken(int colorComponent) {
        return Math.max(COMPONENT_MIN, colorComponent - BRIGHTEN_INCREMENT);
    }

    /**
     * Returns a new paint that is slightly brighter than the given paint.
     * @throws NullPointerException if paint is null
     */
    public static Paint brighter(Paint paint) {
        int argb  = paint.getColor();
        int alpha = GImage.getAlpha(argb);
        int red   = brighten(GImage.getRed(argb));
        int green = brighten(GImage.getGreen(argb));
        int blue  = brighten(GImage.getBlue(argb));
        return makeColor(red, green, blue);
    }

    /**
     * Returns a new paint that is slightly darker than the given paint.
     * @throws NullPointerException if paint is null
     */
    public static Paint darker(Paint paint) {
        int argb  = paint.getColor();
        int alpha = GImage.getAlpha(argb);
        int red   = darken(GImage.getRed(argb));
        int green = darken(GImage.getGreen(argb));
        int blue  = darken(GImage.getBlue(argb));
        return makeColor(red, green, blue);
    }

    /**
     * Returns a new paint with the given ARGB components from 0-255.
     * @throws IllegalArgumentException if any of a/r/g/b is not between 0-255
     */
    public static Paint makeColor(int a, int r, int g, int b) {
        ensureLegalColorComponent(a);
        ensureLegalColorComponent(r);
        ensureLegalColorComponent(g);
        ensureLegalColorComponent(b);
        Paint paint = new Paint();
        paint.setARGB(a, r, g, b);
        paint.setAntiAlias(true);
        return paint;
    }

    /**
     * Returns a new paint with the given RGB components from 0-255.
     * @throws IllegalArgumentException if any of r/g/b is not between 0-255
     */
    public static Paint makeColor(int r, int g, int b) {
        return makeColor(/* alpha */ 255, r, g, b);
    }

    /**
     * Modifies dst to have the same ARGB values as src.
     */
    public static void matchColor(Paint src, Paint dst) {
        int argb  = src.getColor();
        int alpha = GImage.getAlpha(argb);
        int red   = GImage.getRed(argb);
        int green = GImage.getGreen(argb);
        int blue  = GImage.getBlue(argb);
        dst.setARGB(alpha, red, green, blue);
    }

    /**
     * Returns a randomly chosen color.
     * Its R-G-B values will all be random from 0-255.
     */
    public static Paint random() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return makeColor(r, g, b);
    }

    /*
     * Helper to check that an int is between 0-255.
     */
    protected static void ensureLegalColorComponent(int rgb) {
        if (rgb < COMPONENT_MIN || rgb > COMPONENT_MAX) {
            throw new IllegalArgumentException("RGB component out of range "
                    + COMPONENT_MIN + "-" + COMPONENT_MAX + ": " + rgb);
        }
    }
}
