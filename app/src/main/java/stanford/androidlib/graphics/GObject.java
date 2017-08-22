/*
 * @version 2017/02/28
 * - bug fix for setColor method
 * - added more methods that take a GPoint for x/y location
 * - alphabetized methods
 * @version 2017/02/21
 * - made setXxx methods return GObject
 * @version 2016/12/22
 * - added illegal arg checking to some methods e.g. setSize
 * @version 2016/02/17
 * - added setLocation(GObject)
 */

package stanford.androidlib.graphics;

import android.graphics.*;
import android.support.annotation.CallSuper;

/**
 * {@code GObject} is the superclass for all of the various kinds of graphical objects
 * such as lines, ovals, rectangles, ellipses, and other polygons.
 */
public abstract class GObject {
    // fields (instance variables)
    GCanvas gcanvas;
    Canvas canvas;
    Paint paint = new Paint();
    float x;
    float y;
    float width;
    float height;
    boolean visible = true;
    boolean isFilled;
    Paint fillColor;

    /**
     * Constructs a new empty object.
     */
    public GObject() {
        // default paint is stroked and 2px wide
        // because 1px paint basically doesn't show up
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
    }

    /**
     * Constructs a new empty object and adds it to the given canvas.
     */
    public GObject(GCanvas canvas) {
        this();
        canvas.add(this);
    }

    /**
     * Adds this GObject to the given graphical canvas.
     * A convenience method that just calls the canvas's add(GObject) method.
     */
    public GObject addTo(GCanvas canvas) {
        canvas.add(this);
        return this;
    }

    /**
     * Adds this GObject to the given graphical canvas.
     * A convenience method that just calls the canvas's add(GObject) method.
     */
    public GObject addTo(GCanvas canvas, float x, float y) {
        canvas.add(this, x, y);
        return this;
    }

    /**
     * Adds this GObject to the given graphical canvas.
     * A convenience method that just calls the canvas's add(GObject) method.
     */
    public GObject addTo(GCanvas canvas, GPoint location) {
        canvas.add(this, location.getX(), location.getY());
        return this;
    }

    /**
     * Checks to see whether a point is inside the object.
     */
    public boolean contains(float x, float y) {
        return getBounds().contains(x, y);
    }

    /**
     * Checks to see whether a point is inside the object.
     * @throws NullPointerException if pt is null
     */
    public boolean contains(GPoint pt) {
        if (pt == null) {
            throw new NullPointerException();
        }
        return getBounds().contains(pt);
    }

    /**
     * Returns the max Y of this object.
     */
    public final float getBottomY() {
        return getY() + getHeight();
    }

    /**
     * Returns the bounding box of this object, which is defined to be
     * the smallest rectangle that covers everything drawn by the figure.
     */
    public GRectangle getBounds() {
        return new GRectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Returns the x-value of the center of this object.
     */
    public final float getCenterX() {
        return getX() + getWidth() / 2;
    }

    /**
     * Returns the y-value of the center of this object.
     */
    public final float getCenterY() {
        return getY() + getHeight() / 2;
    }

    /**
     * Returns the color used to display this object.
     */
    public final Paint getColor() {
        return paint;
    }

    /**
     * Returns the color used to display the filled region of this object.  If
     * none has been set, <code>getFillColor</code> returns the color of the
     * object.
     *
     * @usage Color color = gobj.getFillColor();
     * @return The color used to display the filled region of this object
     */
    public final Paint getFillColor() {
        return (fillColor == null) ? getColor() : fillColor;
    }

    /**
     * Returns the simple canvas this GObject is drawn inside of.
     * If none, returns null.
     */
    public final GCanvas getGCanvas() {
        return this.gcanvas;
    }

    /**
     * Returns the height of this object, which is defined to be the height of the bounding box.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns this object's leftmost x-coordinate.
     */
    public float getLeftX() {
        return getX();
    }

    /**
     * Returns the location of this object as a GPoint.
     */
    public final GPoint getLocation() {
        return new GPoint(getX(), getY());
    }

    /**
     * Returns the color used to display this object.
     */
    public final Paint getPaint() {
        return paint;
    }

    /**
     * Returns the max X of this object.
     */
    public final float getRightX() {
        return getX() + getWidth();
    }

    /**
     * Returns the size of the bounding box for this object.
     */
    public final GDimension getSize() {
        return new GDimension(getWidth(), getHeight());
    }

    /**
     * Returns this object's top y-coordinate.
     */
    public float getTopY() {
        return getY();
    }

    /**
     * Returns the width of this object, which is defined to be the width of the bounding box.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the x-coordinate of the object.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the object.
     */
    public float getY() {
        return y;
    }

    /**
     * True if this object touches the other object.
     * @throws NullPointerException if obj is null
     */
    public final boolean intersects(GObject obj) {
        return getBounds().intersects(obj.getBounds());
    }

    /**
     * Returns whether this object is filled.
     *
     * @usage if (gobj.isFilled()) . . .
     * @return The color used to display the object
     */
    public final boolean isFilled() {
        return isFilled;
    }

    /**
     * Checks to see whether this object is visible.
     */
    public final boolean isVisible() {
        return visible;
    }

    /**
     * Moves the object on the screen using the displacements dx and dy.
     */
    public final GObject moveBy(float dx, float dy) {
        return translate(dx, dy);
    }

    /**
     * Moves the object using displacements given in polar coordinates.
     */
    public final GObject moveByPolar(float r, float theta) {
        double d = theta * Math.PI / 180;
        return translate((float) (r * Math.cos(d)), (float) (-r * Math.sin(d)));
    }

    /**
     * Moves the object on the screen to the given x/y position.
     */
    public GObject moveTo(float x, float y) {
        return setLocation(x, y);
    }

    /**
     * Moves this GObject to have the same location as the given other GObject.
     * @throws NullPointerException if gobj is null
     */
    public final GObject moveTo(GObject gobj) {
        return setLocation(gobj.getX(), gobj.getY());
    }

    /**
     * Moves the object on the screen to the given point's x/y position.
     */
    public GObject moveTo(GPoint point) {
        return setLocation(point);
    }

    /**
     * Moves the object using displacements given in polar coordinates.
     */
    public final GObject moveToPolar(float r, float theta) {
        double d = theta * 3.141592653589793D / 180.0D;
        return moveTo((float) (r * Math.cos(d)), (float) (-r * Math.sin(d)));
    }

    /**
     * All subclasses of GObject must define a paint method which allows the object to draw itself
     * on the Graphics context passed in as the parameter g.
     */
    public abstract void paint(Canvas canvas);

    /**
     * Removes this object from its graphical canvas, if it is added to one.
     * Equivalent to calling remove(thisGObject) on the canvas.
     */
    public GObject remove() {
        if (this.gcanvas != null) {
            this.gcanvas.remove(this);
        }
        return this;
    }

    /**
     * Instructs this object to redraw itself by calling the postInvalidate method
     * on its enclosing GCanvas as needed.
     */
    @CallSuper
    public void repaint() {
        if (gcanvas != null && !gcanvas.isAnimated()) {
            gcanvas.postInvalidate();
        }
    }

    /**
     * Scales the object on the screen by the scale factors <code>sx</code> and <code>sy</code>.
     *
     * @usage gobj.scale(sx, sy);
     * @param sx The factor used to scale all coordinates in the x direction
     * @param sy The factor used to scale all coordinates in the y direction
     * @throws IllegalArgumentException if sx or sy is not a positive number
     */
    public GObject scale(float sx, float sy) {
        if (sx <= 0.0 || sy <= 0.0) {
            throw new IllegalArgumentException("illegal scale factors: " + sx + "x" + sy);
        }
        width *= sx;
        height *= sy;
        repaint();
        return this;
    }

    /**
     * Scales the object on the screen by the scale factor <code>sf</code>, which applies
     * in both dimensions.
     *
     * @usage gobj.scale(sf);
     * @param sf The factor used to scale all coordinates in both dimensions
     * @throws IllegalArgumentException if sf is not a positive number
     */
    public GObject scale(float sf) {
        return scale(sf, sf);
    }

    /**
     * Moves this object one step toward the back in the z dimension.
     */
    public final GObject sendBackward() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendBackward(this);
        repaint();
        return this;
    }

    /**
     * Moves this object one step toward the front in the z dimension.
     */
    public final GObject sendForward() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendForward(this);
        repaint();
        return this;
    }

    /**
     * Moves this object to the back of the display in the z dimension.
     */
    public final GObject sendToBack() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendToBack(this);
        repaint();
        return this;
    }

    /**
     * Moves this object to the front of the display in the z dimension.
     */
    public final GObject sendToFront() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendToFront(this);
        repaint();
        return this;
    }

    /**
     * Sets the object's bottom y-coordinate to be the given value.
     * This does not resize the object but moves it as though you had
     * set its top y-coordinate to bottomY - getHeight().
     */
    public GObject setBottomY(float bottomY) {
        setY(bottomY - getHeight());
        return this;
    }

    /**
     * Changes the bounds of this object to the specified values.
     *
     * @usage gimage.setBounds(x, y, width, height);
     * @param x The new x-coordinate for the object
     * @param y The new y-coordinate for the object
     * @param width The new width of the object
     * @param height The new height of the object
     * @throws IllegalArgumentException if width or height is negative
     */
    public GObject setBounds(float x, float y, float width, float height) {
        setSize(width, height);
        setLocation(x, y);
        return this;
    }

    /**
     * Changes the bounds of this object to the specified values.
     *
     * @usage gimage.setBounds(x, y, width, height);
     * @param location The new x/y-coordinates for the object
     * @param size The new width/height of the object
     * @throws IllegalArgumentException if width or height is negative
     */
    public GObject setBounds(GPoint location, GDimension size) {
        setLocation(location);
        setSize(size);
        return this;
    }

    /**
     * Changes the bounds of this object to the values from the specified
     * <code>GRectangle</code>.
     *
     * @usage gimage.setBounds(bounds);
     * @param bounds A <code>GRectangle</code> specifying the new bounds
     * @throws IllegalArgumentException if rect's width or height is negative
     */
    public GObject setBounds(GRectangle bounds) {
        setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        return this;
    }

    /**
     * Sets the canvas this object is in.
     * If null is passed, this object will not be associated with any canvas.
     */
    public final GObject setCanvas(Canvas canvas) {
        this.canvas = canvas;
        repaint();
        return this;
    }

    /**
     * Sets the color used to display this object.
     * This differs slightly from setPaint in that setPaint replaces ALL aspects of
     * the paint, while setColor changes this object's color while retaining any other aspects of
     * the paint such as font size.
     * @throws NullPointerException if paint is null.
     */
    public GObject setColor(Paint paint) {
        if (paint == null) {
            throw new NullPointerException();
        }
        if (this.paint == null) {
            this.paint = paint;
        } else {
            // BUGFIX: copy over the color from the parameter paint,
            // while retaining other attributes of this.paint (e.g. font size)
            GColor.matchColor(/* src */ paint, /* dst */ this.paint);
        }
        repaint();
        return this;
    }

    /**
     * Sets the color used to display the filled region of this object.
     *
     * @usage gobj.setFillColor(color);
     * @param color The color used to display the filled region of this object
     * @throws NullPointerException if color is null
     */
    public GObject setFillColor(Paint color) {
        if (color == null) {
            throw new NullPointerException();
        }
        fillColor = new Paint(color);
        fillColor.setStyle(Paint.Style.FILL);
        isFilled = true;
        return this;
    }

    /**
     * Sets whether this object is filled.
     *
     * @usage gobj.setFilled(fill);
     * @param fill <code>true</code> if the object should be filled, <code>false</code> for an outline
     */
    public GObject setFilled(boolean fill) {
        isFilled = fill;
        if (!fill) {
            this.fillColor = null;
        }
        return this;
    }

    /**
     * Sets the canvas on which this GObject should be drawn.
     * You probably should not call this directly, and should call add(GObject) on
     * the canvas instead.
     * If gcanvas is null, this object will not be associated with any canvas.
     */
    public GObject setGCanvas(GCanvas gcanvas) {
        this.gcanvas = gcanvas;
        if (gcanvas == null) {
            this.canvas = null;
        } else {
            this.canvas = gcanvas.getCanvas();
        }
        return this;
    }

    /**
     * Sets the location of this object to the point (x, y).
     */
    public GObject setLocation(float x, float y) {
        this.x = x;
        this.y = y;
        repaint();
        return this;
    }

    /**
     * Sets the location of this object to the given point's (x, y) location.
     */
    public GObject setLocation(GPoint point) {
        return setLocation(point.getX(), point.getY());
    }

    /**
     * Moves this GObject to have the same location as the given other GObject.
     * @throws NullPointerException if gobj is null.
     */
    public GObject setLocation(GObject gobj) {
        setLocation(gobj.getX(), gobj.getY());
        return this;
    }

    /**
     * Sets the paint used to display this object.
     * This differs slightly from setColor in that setPaint replaces ALL aspects of
     * the paint, while setColor changes this object's color while retaining any other aspects of
     * the paint such as font size.
     * @throws NullPointerException if paint is null.
     */
    public GObject setPaint(Paint paint) {
        if (paint == null) {
            throw new NullPointerException();
        }
        this.paint = paint;
        repaint();
        return this;
    }

    /**
     * Sets the object's rightmost x-coordinate to be the given value.
     * This does not resize the object but moves it as though you had
     * set its leftmost x-coordinate to rightX - getWidth().
     */
    public GObject setRightX(float rightX) {
        setX(rightX - getWidth());
        return this;
    }

    /**
     * Changes the size of this object to the specified <code>GDimension</code>.
     *
     * @usage gimage.setSize(size);
     * @param size A <code>GDimension</code> object specifying the size
     * @noshow
     * @throws NullPointerException if size is null
     */
    public GObject setSize(GDimension size) {
        setSize(size.getWidth(), size.getHeight());
        return this;
    }

    /**
     * Changes the size of this object to the specified width and height.
     *
     * @usage gimage.setSize(width, height);
     * @param width The new width of the object
     * @param height The new height of the object
     * @throws IllegalArgumentException if width or height is negative
     */
    public GObject setSize(float width, float height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("illegal size: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Sets whether this object is visible.
     */
    public GObject setVisible(boolean visible) {
        this.visible = visible;
        repaint();
        return this;
    }

    /**
     * Sets the x-location of this object.
     * The y-location is unchanged.
     */
    public GObject setX(float x) {
        setLocation(x, getY());
        return this;
    }

    /**
     * Sets the y-location of this object.
     * The x-location is unchanged.
     */
    public GObject setY(float y) {
        setLocation(getX(), y);
        return this;
    }

    /**
     * Returns a string representation of this object, including its
     * class name, x/y coordinates, width, and height.
     * Useful for debugging.
     */
    public String toString() {
        String className = getClass().getName();
        int index = className.lastIndexOf('.');
        if (index >= 0) {
            className = className.substring(index + 1);
        }
        return className + ":x=" + getX() + ",y=" + getY() + ",w=" + getWidth() + ",h=" + getHeight();
    }

    /**
     * Moves the object on the screen using the displacements dx and dy.
     */
    public GObject translate(float dx, float dy) {
        return setLocation(x + dx, y + dy);
    }
}
