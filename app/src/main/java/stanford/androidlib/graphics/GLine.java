package stanford.androidlib.graphics;

import android.graphics.Canvas;

/**
 * The <code>GLine</code> class is a graphical object whose appearance consists
 * of a line segment.
 */
public class GLine extends GObject implements GScalable {

    /**
     * This constant defines how close (measured in pixel units) a point has
     * to be to a line before that point is considered to be "contained" within
     * the line.
     */
    public static final float LINE_TOLERANCE = 1.5f;

    /**
     * Constructs a line segment from its endpoints.  The point
     * (<code>0</code>,&nbsp;<code>0</code>) defines the start of the
     * line and the point (<code>0</code>,&nbsp;<code>0</code>) defines
     * the end.
     *
     * @usage GLine gline = new GLine();
     */
    public GLine() {
        setLocation(0, 0);
        dx = 0;
        dy = 0;
    }

    /**
     * Constructs a line segment from its endpoints.  The point
     * (<code>0</code>,&nbsp;<code>0</code>) defines the start of the
     * line and the point (<code>x1</code>,&nbsp;<code>y1</code>) defines
     * the end.
     *
     * @usage GLine gline = new GLine(x1, y1);
     * @param x1 The x-coordinate of the end of the line
     * @param y1 The y-coordinate of the end of the line
     */
    public GLine(float x1, float y1) {
        setLocation(0, 0);
        dx = x1;
        dy = y1;
    }

    /**
     * Constructs a line segment from its endpoints.  The point
     * (<code>x0</code>,&nbsp;<code>y0</code>) defines the start of the
     * line and the point (<code>x1</code>,&nbsp;<code>y1</code>) defines
     * the end.
     *
     * @usage GLine gline = new GLine(x0, y0, x1, y1);
     * @param x0 The x-coordinate of the start of the line
     * @param y0 The y-coordinate of the start of the line
     * @param x1 The x-coordinate of the end of the line
     * @param y1 The y-coordinate of the end of the line
     */
    public GLine(float x0, float y0, float x1, float y1) {
        setLocation(x0, y0);
        dx = x1 - x0;
        dy = y1 - y0;
    }

    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @noshow
     */
    @Override
    public void paint(Canvas canvas) {
        float x = getX();
        float y = getY();
        canvas.drawLine(x, y, (x + dx), (y + dy), getPaint());
    }

    /**
     * Returns the bounding box for this object.
     *
     * @usage GRectangle bounds = gline.getBounds();
     * @return The bounding box for this object
     */
    @Override
    public GRectangle getBounds() {
        float x = Math.min(getX(), getX() + dx);
        float y = Math.min(getY(), getY() + dy);
        return new GRectangle(x, y, Math.abs(dx), Math.abs(dy));
    }

    /**
     * Sets the initial point in the line to (<code>x</code>,&nbsp;<code>y</code>),
     * leaving the end point unchanged.  This method is therefore different from
     * <code>setLocation</code>, which
     * moves both components of the line segment.
     *
     * @usage gline.setStartPoint(x, y);
     * @param x The new x-coordinate of the origin
     * @param y The new y-coordinate of the origin
     */
    public void setStartPoint(float x, float y) {
        dx += getX() - x;
        dy += getY() - y;
        setLocation(x, y);
    }

    /**
     * Returns the coordinates of the initial point in the line.  This method is
     * identical to <code>getLocation</code> and exists only to
     * provide symmetry with <code>setStartPoint</code>.
     *
     * @usage GPoint pt = gline.getStartPoint();
     * @return The coordinates of the origin of the line
     */
    public GPoint getStartPoint() {
        return getLocation();
    }

    /**
     * Returns the start point's x-coordinate.
     * Equivalent to getX().
     */
    public float getStartX() {
        return getX();
    }

    /**
     * Returns the start point's y-coordinate.
     * Equivalent to getY().
     */
    public float getStartY() {
        return getY();
    }

    /**
     * Returns the end point's x-coordinate.
     */
    public float getEndX() {
        return getX() + dx;
    }

    /**
     * Returns the end point's y-coordinate.
     */
    public float getEndY() {
        return getY() + dy;
    }

    /**
     * Sets the end point of the line to the point (<code>x</code>,&nbsp;<code>y</code>).
     * The origin of the line remains unchanged.
     *
     * @usage gline.setEndPoint(x, y);
     * @param x The new x-coordinate of the end point
     * @param y The new y-coordinate of the end point
     */
    public void setEndPoint(float x, float y) {
        dx = x - getX();
        dy = y - getY();
    }

    /**
     * Returns the end point of the line as a <code>GPoint</code> object.
     *
     * @usage GPoint pt = gline.getEndPoint();
     * @return The coordinates of the end point of the line
     */
    public GPoint getEndPoint() {
        return new GPoint(getX() + dx, getY() + dy);
    }

    /**
     * Scales the line on the screen by the scale factors <code>sx</code> and <code>sy</code>.
     * This method changes only the end point of the line, leaving the start of the line fixed.
     *
     * @usage gline.scale(sx, sy);
     * @param sx The factor used to scale all coordinates in the x direction
     * @param sy The factor used to scale all coordinates in the y direction
     */
    @Override
    public void scale(float sx, float sy) {
        dx *= sx;
        dy *= sy;
    }

    /**
     * Checks to see whether a point is inside the object.  For the <code>GLine</code>
     * class, containment is defined to mean that the point is within
     * <a href="#LINE_TOLERANCE"><code>LINE_TOLERANCE</code></a> pixels of the
     * line.
     *
     * @usage if (gline.contains(x, y)) . . .
     * @param x The x-coordinate of the point being tested
     * @param y The y-coordinate of the point being tested
     * @return <code>true</code> if the point (<code>x</code>,&nbsp;<code>y</code>) is inside
     */
    @Override
    public boolean contains(float x, float y) {
        float x0 = getX();
        float y0 = getY();
        float x1 = x0 + dx;
        float y1 = y0 + dy;
        float tSquared = LINE_TOLERANCE * LINE_TOLERANCE;
        if (distanceSquared(x, y, x0, y0) < tSquared) return true;
        if (distanceSquared(x, y, x1, y1) < tSquared) return true;
        if (x < Math.min(x0, x1) - LINE_TOLERANCE) return false;
        if (x > Math.max(x0, x1) + LINE_TOLERANCE) return false;
        if (y < Math.min(y0, y1) - LINE_TOLERANCE) return false;
        if (y > Math.max(y0, y1) + LINE_TOLERANCE) return false;
        if (x0 - x1 == 0 && y0 - y1 == 0) return false;
        float u = ((x - x0) * (x1 - x0) + (y - y0) * (y1 - y0)) / distanceSquared(x0, y0, x1, y1);
        return distanceSquared(x, y, x0 + u * (x1 - x0), y0 + u * (y1 - y0)) < tSquared;
    }

    /**
     * Returns the square of the distance between (<code>x0</code>,&nbsp;<code>y0</code>)
     * and (<code>x1</code>,&nbsp;<code>y1</code>).
     */
    private float distanceSquared(float x0, float y0, float x1, float y1) {
        return (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
    }

    /* Private instance variables */
    private float dx, dy;

    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;
}
