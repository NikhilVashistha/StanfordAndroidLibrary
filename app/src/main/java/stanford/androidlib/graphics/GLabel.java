/*
 * @version 2016/02/15
 * - added resource ID based methods to support string resources and l10n
 */

package stanford.androidlib.graphics;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.StringRes;

/**
 * The <code>GLabel</code> class is a graphical object whose appearance
 * consists of a text string.
 */
public class GLabel extends GObject {
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;

    /**
     * The default font used to display strings.  You can change the font by invoking
     * the <a href="#setFont(Font)"><code>setFont</code></a> method.
     */
    public static final Typeface DEFAULT_FONT = Typeface.create("sansserif", 20);

    // private fields
    private String label;
    private Typeface labelFont;

    /**
     * Creates a new empty <code>GLabel</code> object with its top-left corner at (0, 0).
     *
     * @usage GLabel glabel = new GLabel();
     */
    public GLabel() {
        this("", 0, 0);
    }

    /**
     * Creates a new <code>GLabel</code> object at with its top-left corner (0, 0),
     * initialized to contain the specified string.
     *
     * @usage GLabel glabel = new GLabel(str);
     * @param str The initial contents of the <code>GLabel</code>
     */
    public GLabel(String str) {
        this(str, /* x */ 0, /* y */ 0);
    }

    /**
     * Creates a new <code>GLabel</code> object with its top-left corner at the specified position.
     *
     * @usage GLabel glabel = new GLabel(str, x, y);
     * @param str The initial contents of the <code>GLabel</code>
     * @param x The x-coordinate of the label origin
     * @param y The y-coordinate of the baseline for the label
     * @throws NullPointerException if str is null
     */
    public GLabel(String str, float x, float y) {
        if (str == null) {
            throw new NullPointerException();
        }
        paint.setStrokeWidth(0f);
        label = str;
        setFont(DEFAULT_FONT);
        setLocation(x, y);
    }

    /**
     * Creates a new <code>GLabel</code> object at with its top-left corner (0, 0),
     * initialized to contain the string with the specified resource ID.
     *
     * @usage GLabel glabel = new GLabel(id);
     * @param id The resource ID of the initial contents of the <code>GLabel</code>
     */
    public GLabel(@StringRes int id) {
        this(id, 0, 0);
    }

    /**
     * Creates a new <code>GLabel</code> object with its top-left corner at the specified position.
     *
     * @usage GLabel glabel = new GLabel(str, x, y);
     * @param id The ID of the resource string for the initial contents of the <code>GLabel</code>
     * @param x The x-coordinate of the label origin
     * @param y The y-coordinate of the baseline for the label
     * @throws NullPointerException if no string found for given resource ID
     */
    public GLabel(@StringRes int id, float x, float y) {
        paint.setStrokeWidth(0f);
        label = getStringFromId(id);
        if (label == null) {
            throw new NullPointerException();
        }
        setFont(DEFAULT_FONT);
        setLocation(x, y);
    }

    /**
     * Creates a new empty <code>GLabel</code> object with its top-left corner at (0, 0).
     *
     * @usage GLabel glabel = new GLabel();
     */
    public GLabel(GCanvas canvas) {
        this();
        canvas.add(this);
    }

    /**
     * Creates a new <code>GLabel</code> object at with its top-left corner (0, 0),
     * initialized to contain the specified string.
     *
     * @usage GLabel glabel = new GLabel(str);
     * @param str The initial contents of the <code>GLabel</code>
     */
    public GLabel(GCanvas canvas, String str) {
        this(str);
        canvas.add(this);
    }

    /**
     * Creates a new <code>GLabel</code> object with its top-left corner at the specified position.
     *
     * @usage GLabel glabel = new GLabel(str, x, y);
     * @param str The initial contents of the <code>GLabel</code>
     * @param x The x-coordinate of the label origin
     * @param y The y-coordinate of the baseline for the label
     * @throws NullPointerException if str is null
     */
    public GLabel(GCanvas canvas, String str, float x, float y) {
        this(str, x, y);
        canvas.add(this);
    }

    /**
     * Creates a new <code>GLabel</code> object at with its top-left corner (0, 0),
     * initialized to contain the string with the specified resource ID.
     *
     * @usage GLabel glabel = new GLabel(id);
     * @param id The resource ID of the initial contents of the <code>GLabel</code>
     */
    public GLabel(GCanvas canvas, @StringRes int id) {
        this(id);
        canvas.add(this);
    }

    /**
     * Creates a new <code>GLabel</code> object with its top-left corner at the specified position.
     *
     * @usage GLabel glabel = new GLabel(str, x, y);
     * @param id The ID of the resource string for the initial contents of the <code>GLabel</code>
     * @param x The x-coordinate of the label origin
     * @param y The y-coordinate of the baseline for the label
     * @throws NullPointerException if no string found for given resource ID
     */
    public GLabel(GCanvas canvas, @StringRes int id, float x, float y) {
        this(id, x, y);
        canvas.add(this);
    }

    /**
     * Returns the font in which the <code>GLabel</code> is displayed.
     *
     * @usage Font font = glabel.getFont();
     * @return The font in use by this object
     */
    public Typeface getFont() {
        return labelFont;
    }

    /**
     * Returns this label's current font style.
     */
    public int getFontStyle() {
        return labelFont.getStyle();
    }

    /**
     * Returns the string displayed by this object.
     *
     * @usage String str = glabel.getLabel();
     * @return The string displayed by this object
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the string displayed by this object.
     *
     * @usage String str = glabel.getText();
     * @return The string displayed by this object
     */
    public String getText() {
        return label;
    }

    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @throws NullPointerException if canvas is null
     * @noshow
     */
    public void paint(Canvas canvas) {
        if (canvas == null) {
            throw new NullPointerException();
        }

        // shift downward by height so that x/y coord passed represents top-left
        Paint paint = getPaint();
        canvas.drawText(this.label, getX(), getY() + getHeight(), paint);
    }

    /**
     * Returns the width of this string, as it appears on the display.
     *
     * @usage float width = glabel.getWidth();
     * @return The width of this object
     */
    @Override
    public float getWidth() {
        return getPaint().measureText(label);
    }

    /**
     * Returns the height of this string, as it appears on the display.
     *
     * @usage float height = glabel.getHeight();
     * @return The height of this string
     */
    @Override
    public float getHeight() {
        Rect bounds = new Rect();
        getPaint().getTextBounds(label, 0, label.length(), bounds);
        return bounds.height();
    }

    /**
     * Returns the font size currently used by this label.
     */
    public float getFontSize() {
        return getPaint().getTextSize();
    }

    /**
     * Sets this label to use the given font style,
     * such as TypeFace.BOLD.
     * @throws IllegalArgumentException if style is not a valid font style
     */
    public GLabel setFontStyle(int style) {
        verifyFontStyle(style);
        getPaint().setTypeface(Typeface.create(this.labelFont, style));
        repaint();
        return this;
    }

    /**
     * Changes the font used to display the <code>GLabel</code>.  This call will
     * usually change the size of the displayed object and will therefore affect
     * the result of calls to <a href="GObject.html#getSize()"><code>getSize</code></a>
     * and <a href="GObject.html#getBounds()"><code>getBounds</code></a>.
     *
     * @usage glabel.setFont(font);
     * @param font A <code>Font</code> object indicating the new font
     * @throws NullPointerException if font is null
     */
    public GLabel setFont(Typeface font) {
        if (font == null) {
            throw new NullPointerException();
        }
        labelFont = font;
        getPaint().setTypeface(font);
        repaint();
        return this;
    }

    /**
     * Sets this GLabel to use the given font at the given size.
     * @throws IllegalArgumentException if size is 0 or negative
     * @throws NullPointerException if font is null
     */
    public GLabel setFont(Typeface font, float size) {
        if (font == null) {
            throw new NullPointerException();
        }
        labelFont = font;
        getPaint().setTypeface(font);
        setFontSize(size);
        return this;
    }

    /**
     * Sets this GLabel to use the given font at the given style and size.
     * @throws IllegalArgumentException if size is 0 or negative, or style is not a valid font style
     * @throws NullPointerException if font family is null
     */
    public GLabel setFont(Typeface fontFamily, int style, float size) {
        if (fontFamily == null) {
            throw new NullPointerException();
        }
        verifyFontStyle(style);
        labelFont = Typeface.create(fontFamily, style);
        setFontSize(size);
        return this;
    }

    // helper to make sure font style is not bogus
    private void verifyFontStyle(int style) {
        if (style != Typeface.NORMAL
                && style != Typeface.ITALIC
                && style != Typeface.BOLD
                && style != Typeface.BOLD_ITALIC) {
            throw new IllegalArgumentException("invalid font style (" + style + "); did you mix up the order of the style and size parameters?");
        }
    }

    /**
     * Changes the font used to display the <code>GLabel</code> as specified by
     * the string <code>str</code>.
     *
     * @usage glabel.setFont(str);
     * @param str A <code>String</code> specifying the new font
     * @throws NullPointerException if the string is null
     */
    public GLabel setFont(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        return setFont(Typeface.create(str, 12));
    }

    /**
     * Sets this label to use a font of the given size.
     * @throws IllegalArgumentException if size is 0 or negative
     */
    public GLabel setFontSize(float size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Illegal font size: " + size);
        }
        getPaint().setTextSize(size);
        repaint();
        return this;
    }

    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setLabel(R.string.someID);
     * @param id The new string to display's resource ID
     */
    public GLabel setLabel(@StringRes int id) {
        label = getStringFromId(id);   // cannot be null
        repaint();
        return this;
    }

    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setLabel(str);
     * @param str The new string to display
     * @throws NullPointerException if string is null
     */
    public GLabel setLabel(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        label = str;
        repaint();
        return this;
    }

    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setText(R.string.someID);
     * @param id The new string to display's ID
     */
    public GLabel setText(@StringRes int id) {
        return setLabel(id);
    }

    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setText(str);
     * @param str The new string to display
     * @throws NullPointerException if string is null
     */
    public GLabel setText(String str) {
        return setLabel(str);
    }

    // helper to convert string resource ID into that string
    private String getStringFromId(@StringRes int id) {
        Context context = GCanvas.__getCanvasContext();
        return context.getResources().getString(id);
    }
}
