/*
 * @version 2016/02/15
 * - added resource ID based methods to support string resources and l10n
 */

package stanford.androidlib.graphics;

/* Class: GLabel */

import android.content.Context;
import android.graphics.*;
import android.support.annotation.StringRes;

/**
 * The <code>GLabel</code> class is a graphical object whose appearance
 * consists of a text string.
 */
public class GLabel extends GObject {

/* Constant: DEFAULT_FONT */
    /**
     * The default font used to display strings.  You can change the font by invoking
     * the <a href="#setFont(Font)"><code>setFont</code></a> method.
     */
    public static final Typeface DEFAULT_FONT = Typeface.create("sansserif", 16);

/* Constructor: GLabel() */
    /**
     * Creates a new empty <code>GLabel</code> object with its top-left corner at (0, 0).
     *
     * @usage GLabel glabel = new GLabel();
     */
    public GLabel() {
        this("", 0, 0);
    }

/* Constructor: GLabel(str) */
    /**
     * Creates a new <code>GLabel</code> object at with its top-left corner (0, 0),
     * initialized to contain the specified string.
     *
     * @usage GLabel glabel = new GLabel(str);
     * @param str The initial contents of the <code>GLabel</code>
     */
    public GLabel(String str) {
        this(str, 0, 0);
    }

/* Constructor: GLabel(str, x, y) */
    /**
     * Creates a new <code>GLabel</code> object with its top-left corner at the specified position.
     *
     * @usage GLabel glabel = new GLabel(str, x, y);
     * @param str The initial contents of the <code>GLabel</code>
     * @param x The x-coordinate of the label origin
     * @param y The y-coordinate of the baseline for the label
     */
    public GLabel(String str, float x, float y) {
        paint.setStrokeWidth(0f);
        label = str;
        setFont(DEFAULT_FONT);
        setLocation(x, y);
    }

/* Constructor: GLabel(id) */
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

/* Constructor: GLabel(str, x, y) */
    /**
     * Creates a new <code>GLabel</code> object with its top-left corner at the specified position.
     *
     * @usage GLabel glabel = new GLabel(str, x, y);
     * @param id The ID of the resource string for the initial contents of the <code>GLabel</code>
     * @param x The x-coordinate of the label origin
     * @param y The y-coordinate of the baseline for the label
     */
    public GLabel(@StringRes int id, float x, float y) {
        paint.setStrokeWidth(0f);
        label = getStringFromId(id);
        setFont(DEFAULT_FONT);
        setLocation(x, y);
    }

/* Method: getFont() */
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

/* Method: getLabel() */
    /**
     * Returns the string displayed by this object.
     *
     * @usage String str = glabel.getLabel();
     * @return The string displayed by this object
     */
    public String getLabel() {
        return label;
    }

/* Method: getText() */
    /**
     * Returns the string displayed by this object.
     *
     * @usage String str = glabel.getText();
     * @return The string displayed by this object
     */
    public String getText() {
        return label;
    }

/* Method: paint(g) */
    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @noshow
     */
    public void paint(Canvas canvas) {
        // shift downward by height so that x/y coord passed represents top-left
        Paint paint = getPaint();
        canvas.drawText(this.label,
                (float) getX(),
                (float) getY() + getHeight(),
                paint);
    }

/* Method: getWidth() */
    /**
     * Returns the width of this string, as it appears on the display.
     *
     * @usage float width = glabel.getWidth();
     * @return The width of this object
     */
    public float getWidth() {
        return getPaint().measureText(label);
    }

/* Method: getHeight() */
    /**
     * Returns the height of this string, as it appears on the display.
     *
     * @usage float height = glabel.getHeight();
     * @return The height of this string
     */
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
     */
    public void setFontStyle(int style) {
        verifyFontStyle(style);
        getPaint().setTypeface(Typeface.create(this.labelFont, style));
        repaint();
    }

/* Method: setFont(font) */
    /**
     * Changes the font used to display the <code>GLabel</code>.  This call will
     * usually change the size of the displayed object and will therefore affect
     * the result of calls to <a href="GObject.html#getSize()"><code>getSize</code></a>
     * and <a href="GObject.html#getBounds()"><code>getBounds</code></a>.
     *
     * @usage glabel.setFont(font);
     * @param font A <code>Font</code> object indicating the new font
     */
    public void setFont(Typeface font) {
        labelFont = font;
        getPaint().setTypeface(font);
        repaint();
    }

    /**
     * Sets this GLabel to use the given font at the given size.
     */
    public void setFont(Typeface font, float size) {
        labelFont = font;
        getPaint().setTypeface(font);
        setFontSize(size);
    }

    /**
     * Sets this GLabel to use the given font at the given style and size.
     */
    public void setFont(Typeface fontFamily, int style, float size) {
        verifyFontStyle(style);
        labelFont = Typeface.create(fontFamily, style);
        setFontSize(size);
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

/* Method: setFont(str) */
    /**
     * Changes the font used to display the <code>GLabel</code> as specified by
     * the string <code>str</code>.
     *
     * @usage glabel.setFont(str);
     * @param str A <code>String</code> specifying the new font
     */
    public void setFont(String str) {
        setFont(Typeface.create(str, 12));
    }

    /**
     * Sets this label to use a font of the given size.
     */
    public void setFontSize(float size) {
        getPaint().setTextSize(size);
        repaint();
    }

/* Method: setLabel(str) */
    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setLabel(R.string.someID);
     * @param id The new string to display's resource ID
     */
    public void setLabel(@StringRes int id) {
        label = getStringFromId(id);
        repaint();
    }

/* Method: setLabel(str) */
    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setLabel(str);
     * @param str The new string to display
     */
    public void setLabel(String str) {
        label = str;
        repaint();
    }

/* Method: setText(str) */
    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setText(R.string.someID);
     * @param id The new string to display's ID
     */
    public void setText(@StringRes int id) {
        setLabel(id);
    }

/* Method: setText(str) */
    /**
     * Changes the string stored within the <code>GLabel</code> object, so that
     * a new text string appears on the display.
     *
     * @usage glabel.setText(str);
     * @param str The new string to display
     */
    public void setText(String str) {
        setLabel(str);
    }

    // helper to convert string resource ID into that string
    private String getStringFromId(@StringRes int id) {
        Context context = GCanvas.__getCanvasContext();
        return context.getResources().getString(id);
    }

/* Inherited method: getSize() */
/**
 * @inherited GObject#GDimension getSize()
 * Returns the size of the bounding box for this object.
 */

/* Inherited method: contains(x, y) */
/**
 * @inherited GObject#boolean contains(float x, float y)
 * Checks to see whether a point is "inside" the string, which is defined to be
 * inside the bounding rectangle.
 */

/* Inherited method: contains(pt) */
/**
 * @inherited GObject#boolean contains(GPoint pt)
 * Checks to see whether a point is inside the object.
 */

/* Inherited method: setLocation(x, y) */
/**
 * @inherited GObject#void setLocation(float x, float y)
 * Sets the location of the <code>GLabel</code> to the point (<code>x</code>, <code>y</code>).
 * For a <code>GLabel</code>, the location is the point on the text baseline at which the
 * text starts.
 */

/* Inherited method: setLocation(pt) */
/**
 * @inherited GObject#void setLocation(GPoint pt)
 * Sets the location of this object to the specified point.
 */

/* Inherited method: getLocation() */
/**
 * @inherited GObject#GPoint getLocation()
 * Returns the location of the <code>GLabel</code> as a <code>GPoint</code> object.
 */

/* Inherited method: getX() */
/**
 * @inherited GObject#float getX()
 * Returns the x-coordinate of the object.
 */

/* Inherited method: getY() */
/**
 * @inherited GObject#float getY()
 * Returns the y-coordinate of the object.
 */

/* Inherited method: move(dx, dy) */
/**
 * @inherited GObject#void move(float dx, float dy)
 * Moves the object on the screen using the displacements <code>dx</code> and <code>dy</code>.
 */

/* Inherited method: movePolar(r, theta) */
/**
 * @inherited GObject#void movePolar(float r, float theta)
 * Moves the object using displacements given in polar coordinates.
 */

/* Inherited method: sendToFront() */
/**
 * @inherited GObject#void sendToFront()
 * Moves this object to the front of the display in the <i>z</i> dimension.
 */

/* Inherited method: sendToBack() */
/**
 * @inherited GObject#void sendToBack()
 * Moves this object to the back of the display in the <i>z</i> dimension.
 */

/* Inherited method: sendForward() */
/**
 * @inherited GObject#void sendForward()
 * Moves this object one step toward the front in the <i>z</i> dimension.
 */

/* Inherited method: sendBackward() */
/**
 * @inherited GObject#void sendBackward()
 * Moves this object one step toward the back in the <i>z</i> dimension.
 */

/* Inherited method: setColor(color) */
/**
 * @inherited GObject#void setColor(Color color)
 * Sets the color used to display the text of the <code>GLabel</code>.
 */

/* Inherited method: getColor() */
/**
 * @inherited GObject#Color getColor()
 * Returns the color used to display the text of the <code>GLabel</code>.
 */

/* Inherited method: setVisible(visible) */
/**
 * @inherited GObject#void setVisible(boolean visible)
 * Sets the visibility status of the <code>GLabel</code>.
 */

/* Inherited method: isVisible() */
/**
 * @inherited GObject#boolean isVisible()
 * Checks to see whether the <code>GLabel</code> is visible.
 */

    /* Private instance variables */
    private String label;
    private Typeface labelFont;

/* Serial version UID */
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;
}
