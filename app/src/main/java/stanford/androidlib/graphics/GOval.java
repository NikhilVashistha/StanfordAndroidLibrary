package stanford.androidlib.graphics;

/* Class: GOval */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * The <code>GOval</code> class is a graphical object whose appearance consists
 * of an oval.
 */
public class GOval extends GObject
        implements GFillable, GResizable, GScalable {

/* Constructor: GOval() */
    /**
     * Constructs a new 0x0 oval, positioned at the origin.
     *
     * @usage GOval goval = new GOval();
     */
    public GOval() {
        this(0, 0, 0, 0);
    }

/* Constructor: GOval(width, height) */
    /**
     * Constructs a new oval with the specified width and height,
     * positioned at the origin.
     *
     * @usage GOval goval = new GOval(width, height);
     * @param width The width of the oval in pixels
     * @param height The height of the oval in pixels
     */
    public GOval(float width, float height) {
        this(0, 0, width, height);
    }

/* Constructor: GOval(x, y, width, height) */
    /**
     * Constructs a new oval with the specified bounds.
     *
     * @usage GOval goval = new GOval(x, y, width, height);
     * @param x The x-coordinate of the upper left corner
     * @param y The y-coordinate of the upper left corner
     * @param width The width of the oval in pixels
     * @param height The height of the oval in pixels
     */
    public GOval(float x, float y, float width, float height) {
        frameWidth = width;
        frameHeight = height;
        setLocation(x, y);
    }

/* Method: contains(x, y) */
    /**
     * Checks to see whether a point is inside the object.
     *
     * @usage if (goval.contains(x, y)) . . .
     * @param x The x-coordinate of the point being tested
     * @param y The y-coordinate of the point being tested
     * @return <code>true</code> if the point (<code>x</code>,&nbsp;<code>y</code>) is inside
     *         the object, and <code>false</code> otherwise
     */
    public boolean contains(float x, float y) {
        float rx = frameWidth / 2;
        float ry = frameHeight / 2;
        if (rx == 0 || ry == 0) return false;
        float dx = x - (getX() + rx);
        float dy = y - (getY() + ry);
        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0;
    }

/* Method: paint(g) */
    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @noshow
     */
    public void paint(Canvas canvas) {
        if (isFilled()) {
            // fill interior first
            Paint fill = getFillColor();
            canvas.drawOval(
                    new RectF(
                            (float) getX(),
                            (float) getY(),
                            (float) getRightX(),
                            (float) getBottomY()),
                    fill);
        }

        // draw outline second
        canvas.drawOval(
                new RectF(
                        (float) getX(),
                        (float) getY(),
                        (float) getRightX(),
                        (float) getBottomY()),
                getPaint());
    }

/* Method: setFilled(fill) */
    /**
     * Sets whether this object is filled.
     *
     * @usage gobj.setFilled(fill);
     * @param fill <code>true</code> if the object should be filled, <code>false</code> for an outline
     */
    public void setFilled(boolean fill) {
        isFilled = fill;
        repaint();
    }

/* Method: isFilled() */
    /**
     * Returns whether this object is filled.
     *
     * @usage if (gobj.isFilled()) . . .
     * @return The color used to display the object
     */
    public boolean isFilled() {
        return isFilled;
    }

/* Method: setFillColor(color) */
    /**
     * Sets the color used to display the filled region of this object.
     *
     * @usage gobj.setFillColor(color);
     * @param color The color used to display the filled region of this object
     */
    public void setFillColor(Paint color) {
        fillColor = new Paint(color);
        if (fillColor != null) {
            fillColor.setStyle(Paint.Style.FILL);
            isFilled = true;
        }
        repaint();
    }

/* Method: getFillColor() */
    /**
     * Returns the color used to display the filled region of this object.  If
     * none has been set, <code>getFillColor</code> returns the color of the
     * object.
     *
     * @usage Color color = gobj.getFillColor();
     * @return The color used to display the filled region of this object
     */
    public Paint getFillColor() {
        return (fillColor == null) ? getColor() : fillColor;
    }

/* Method: setSize(width, height) */
    /**
     * Changes the size of this object to the specified width and height.
     *
     * @usage gobj.setSize(width, height);
     * @param width The new width of the object
     * @param height The new height of the object
     */
    public void setSize(float width, float height) {
        frameWidth = width;
        frameHeight = height;
        repaint();
    }

/* Method: setSize(size) */
    /**
     * Changes the size of this object to the specified <code>GDimension</code>.
     *
     * @usage gobj.setSize(size);
     * @param size A <code>GDimension</code> object specifying the size
     * @noshow
     */
    public final void setSize(GDimension size) {
        setSize(size.getWidth(), size.getHeight());
    }

/* Method: getSize() */
    /**
     * Returns the size of this object as a <code>GDimension</code>.
     *
     * @usage GDimension size = gobj.getSize();
     * @return The size of this object
     */
    public GDimension getSize() {
        return new GDimension(frameWidth, frameHeight);
    }

/* Method: setBounds(x, y, width, height) */
    /**
     * Changes the bounds of this object to the specified values.
     *
     * @usage gobj.setBounds(x, y, width, height);
     * @param x The new x-coordinate for the object
     * @param y The new y-coordinate for the object
     * @param width The new width of the object
     * @param height The new height of the object
     */
    public void setBounds(float x, float y, float width, float height) {
        frameWidth = width;
        frameHeight = height;
        setLocation(x, y);
    }

/* Method: setBounds(bounds) */
    /**
     * Changes the bounds of this object to the values from the specified
     * <code>Rectangle</code>.
     *
     * @usage gobj.setBounds(bounds);
     * @param bounds A <code>GRectangle</code> specifying the new bounds
     */
    public final void setBounds(GRectangle bounds) {
        setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

/* Method: getBounds() */
    /**
     * Returns the bounding box of this object.
     *
     * @usage GRectangle bounds = gobj.getBounds();
     * @return The bounding box for this object
     */
    public GRectangle getBounds() {
        return new GRectangle(getX(), getY(), frameWidth, frameHeight);
    }

/* Method: getWidth() */
    /**
     * Returns the width of this object as a float-precision value, which
     * is defined to be the width of the bounding box.
     *
     * @usage float width = gobj.getWidth();
     * @return The width of this object on the screen
     */
    public float getWidth() {
        return frameWidth;
    }

/* Method: getHeight() */
    /**
     * Returns the height of this object as a float-precision value, which
     * is defined to be the height of the bounding box.
     *
     * @usage float height = gobj.getHeight();
     * @return The height of this object on the screen
     */
    public float getHeight() {
        return frameHeight;
    }

/* Method: scale(sx, sy) */
    /**
     * Scales the object on the screen by the scale factors <code>sx</code> and <code>sy</code>.
     *
     * @usage gobj.scale(sx, sy);
     * @param sx The factor used to scale all coordinates in the x direction
     * @param sy The factor used to scale all coordinates in the y direction
     */
    public void scale(float sx, float sy) {
        frameWidth *= sx;
        frameHeight *= sy;
        repaint();
    }

/* Method: scale(sf) */
    /**
     * Scales the object on the screen by the scale factor <code>sf</code>, which applies
     * in both dimensions.
     *
     * @usage gobj.scale(sf);
     * @param sf The factor used to scale all coordinates in both dimensions
     */
    public final void scale(float sf) {
        scale(sf, sf);
    }

/* Inherited method: setLocation(x, y) */
/**
 * @inherited GObject#void setLocation(float x, float y)
 * Sets the location of this object to the point (<code>x</code>, <code>y</code>).
 */

/* Inherited method: setLocation(pt) */
/**
 * @inherited GObject#void setLocation(GPoint pt)
 * Sets the location of this object to the specified point.
 */

/* Inherited method: getLocation() */
/**
 * @inherited GObject#GPoint getLocation()
 * Returns the location of this object as a <code>GPoint</code>.
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

/* Inherited method: contains(pt) */
/**
 * @inherited GObject#boolean contains(GPoint pt)
 * Checks to see whether a point is inside the object.
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
 * Sets the color used to display this object.
 */

/* Inherited method: getColor() */
/**
 * @inherited GObject#Color getColor()
 * Returns the color used to display this object.
 */

/* Inherited method: setFillColor(color) */
/**
 * @inherited GObject#void setFillColor(Color color)
 * Sets the color used to display the filled region of this object.
 */

/* Inherited method: getFillColor() */
/**
 * @inherited GObject#Color getFillColor()
 * Returns the color used to display the filled region of this object.
 */

/* Inherited method: setFilled(fill) */
/**
 * @inherited GObject#void setFilled(boolean fill)
 * Sets whether this object is filled.
 */

/* Inherited method: isFilled() */
/**
 * @inherited GObject#boolean isFilled()
 * Returns whether this object is filled.
 */

/* Inherited method: setVisible(visible) */
/**
 * @inherited GObject#void setVisible(boolean visible)
 * Sets whether this object is visible.
 */

/* Inherited method: isVisible() */
/**
 * @inherited GObject#boolean isVisible()
 * Checks to see whether this object is visible.
 */

    /* Private instance variables */
    private boolean useArcs;
    private float frameWidth;
    private float frameHeight;
    private boolean isFilled;
    private Paint fillColor;

/* Serial version UID */
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;
}
