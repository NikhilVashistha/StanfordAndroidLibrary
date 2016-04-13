/*
 * @version 2016/02/17
 * - added setLocation(GObject)
 */

package stanford.androidlib.graphics;

import android.graphics.*;

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
     * Checks to see whether a point is inside the object.
     */
    public boolean contains(float x, float y) {
        return getBounds().contains(x, y);
    }

    /**
     * Checks to see whether a point is inside the object.
     */
    public boolean contains(GPoint pt) {
        return getBounds().contains(pt);
    }

    /**
     * Returns the bounding box of this object, which is defined to be
     * the smallest rectangle that covers everything drawn by the figure.
     */
    public GRectangle getBounds() {
        return new GRectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Returns the color used to display this object.
     */
    public Paint getColor() {
        return paint;
    }

    /**
     * Returns the color used to display this object.
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * Returns the height of this object, which is defined to be the height of the bounding box.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns the location of this object as a GPoint.
     */
    public GPoint getLocation() {
        return new GPoint(getX(), getY());
    }

//    public GContainer getParent()
//    Returns the parent of this object, which is the canvas or compound object in which it is enclosed.

    /**
     * Returns the size of the bounding box for this object.
     */
    public GDimension getSize() {
        return new GDimension(getWidth(), getHeight());
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
     * Returns the x-value of the center of this object.
     */
    public float getCenterX() {
        return getX() + getWidth() / 2;
    }

    /**
     * Returns the max X of this object.
     */
    public float getRightX() {
        return getX() + getWidth();
    }

    /**
     * Returns the y-value of the center of this object.
     */
    public float getCenterY() {
        return getY() + getHeight() / 2;
    }

    /**
     * Returns the max Y of this object.
     */
    public float getBottomY() {
        return getY() + getHeight();
    }

    /**
     * True if this object touches the other object.
     */
    public boolean intersects(GObject obj) {
        return getBounds().intersects(obj.getBounds());
    }

    /**
     * Checks to see whether this object is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Moves the object on the screen using the displacements dx and dy.
     */
    public void moveBy(float dx, float dy) {
        translate(dx, dy);
    }

    /**
     * Moves the object using displacements given in polar coordinates.
     */
    public void moveByPolar(float r, float theta) {
        double d = theta * 3.141592653589793D / 180.0D;
        translate((float) (r * Math.cos(d)), (float) (-r * Math.sin(d)));
    }

    /**
     * Moves this GObject to have the same location as the given other GObject.
     */
    public void moveTo(GObject gobj) {
        setLocation(gobj.getX(), gobj.getY());
    }

    /**
     * Moves the object on the screen using the displacements dx and dy.
     */
    public void translate(float dx, float dy) {
        setLocation(x + dx, y + dy);
    }

    /**
     * Moves the object on the screen using the displacements dx and dy.
     */
    public void moveTo(float x, float y) {
        setLocation(x, y);
    }

    /**
     * Moves the object using displacements given in polar coordinates.
     */
    public void moveToPolar(float r, float theta) {
        double d = theta * 3.141592653589793D / 180.0D;
        moveTo((float) (r * Math.cos(d)), (float) (-r * Math.sin(d)));
    }

    /**
     * All subclasses of GObject must define a paint method which allows the object to draw itself on the Graphics context passed in as the parameter g.
     */
    public abstract void paint(Canvas canvas);

    public void repaint() {
        // TODO
//        if (canvas == null && gcanvas != null) {
//            canvas = gcanvas.getCanvas();
//        }
//        if (canvas != null) {
//            paint(canvas);
//        }
        if (gcanvas != null && !gcanvas.isAnimated()) {
            gcanvas.postInvalidate();
        }
    }

    /**
     * Moves this object one step toward the back in the z dimension.
     */
    public void sendBackward() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendBackward(this);
        repaint();
    }

    /**
     * Moves this object one step toward the front in the z dimension.
     */
    public void sendForward() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendForward(this);
        repaint();
    }

    /**
     * Moves this object to the back of the display in the z dimension.
     */
    public void sendToBack() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendToBack(this);
        repaint();
    }

    /**
     * Moves this object to the front of the display in the z dimension.
     */
    public void sendToFront() {
        if (gcanvas == null) {
            throw new IllegalStateException("Cannot sendToBack if not added to a gcanvas");
        }
        gcanvas.sendToFront(this);
        repaint();
    }

    /**
     * Sets the canvas this object is in.
     */
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        repaint();
    }

    /**
     * Sets the color used to display this object.
     */
    public void setColor(Paint paint) {
        GColor.matchColor(paint, this.paint);
        repaint();
    }

    /**
     * Sets the color used to display this object.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
        repaint();
    }

    /**
     * Sets the location of this object to the point (x, y).
     */
    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
        repaint();
    }

    /**
     * Moves this GObject to have the same location as the given other GObject.
     */
    public void setLocation(GObject gobj) {
        setLocation(gobj.getX(), gobj.getY());
    }

    /**
     * Sets the x-location of this object.
     * The y-location is unchanged.
     */
    public void setX(float x) {
        setLocation(x, getY());
    }

    /**
     * Sets the y-location of this object.
     * The x-location is unchanged.
     */
    public void setY(float y) {
        setLocation(getX(), y);
    }

    /**
     * Sets the object's rightmost x-coordinate to be the given value.
     * This does not resize the object but moves it as though you had
     * set its leftmost x-coordinate to rightX - getWidth().
     */
    public void setRightX(float rightX) {
        setX(rightX - getWidth());
    }

    /**
     * Sets the object's bottom y-coordinate to be the given value.
     * This does not resize the object but moves it as though you had
     * set its top y-coordinate to bottomY - getHeight().
     */
    public void setBottomY(float bottomY) {
        setY(bottomY - getHeight());
    }

    /**
     * Sets the canvas on which this GObject should be drawn.
     * You probably should not call this directly, and should call add(GObject) on
     * the canvas instead.
     */
    public void setGCanvas(GCanvas gcanvas) {
        this.gcanvas = gcanvas;
        this.canvas = gcanvas.getCanvas();
    }

    /**
     * Returns the simple canvas this GObject is drawn inside of.
     * If none, returns null.
     */
    public GCanvas getGCanvas() {
        return this.gcanvas;
    }

    /**
     * Sets whether this object is visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        repaint();
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
}
