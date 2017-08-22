package stanford.androidlib.graphics;

/**
 * This class is a double-precision version of the <code>Point</code> class
 * in <code>java.awt</code>.
 */
public class GPoint {
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;


    // private fields
    private float xc;
    private float yc;

    /**
     * Constructs a new <code>GPoint</code> at the origin (0, 0).
     *
     * @usage pt = new GPoint();
     */
    public GPoint() {
        this(0, 0);
    }

    /**
     * Constructs a new <code>GPoint</code> with the specified coordinates.
     *
     * @usage pt = new GPoint(x, y);
     * @param x The x-coordinate of the point
     * @param y The y-coordinate of the point
     */
    public GPoint(float x, float y) {
        xc = x;
        yc = y;
    }

    /**
     * Constructs a new <code>GPoint</code> from an existing one.
     *
     * @usage pt = new GPoint(p);
     * @param p The original point
     */
    public GPoint(GPoint p) {
        this(p.xc, p.yc);
    }

    /**
     * Returns the x coordinate of this <code>GPoint</code>.
     *
     * @usage x = pt.getX();
     * @return The x coordinate of this <code>GPoint</code>
     */
    public float getX() {
        return xc;
    }

    /**
     * Returns the y coordinate of this <code>GPoint</code>.
     *
     * @usage y = pt.getY();
     * @return The y coordinate of this <code>GPoint</code>
     */
    public float getY() {
        return yc;
    }

    /**
     * Sets the location of the <code>GPoint</code> to the specified <code>x</code>
     * and <code>y</code> values.
     *
     * @usage pt.setLocation(x, y);
     * @param x The new x-coordinate for the point
     * @param y The new y-coordinate for the point
     */
    public GPoint setLocation(float x, float y) {
        xc = x;
        yc = y;
        return this;
    }

    /**
     * Sets the location of the <code>GPoint</code> to that of an existing one.
     *
     * @usage pt.setLocation(p);
     * @param p An existing <code>GPoint</code> specifying the new location
     */
    public GPoint setLocation(GPoint p) {
        return setLocation(p.xc, p.yc);
    }

    /**
     * Returns a new <code>GPoint</code> whose coordinates are the same as this one.
     *
     * @usage p = pt.getLocation();
     * @return A new point with the same coordinates
     */
    public GPoint getLocation() {
        return new GPoint(xc, yc);
    }

    /**
     * Adjusts the coordinates of a point by the specified <code>dx</code> and
     * <code>dy</code> offsets.
     *
     * @usage pt.translate(dx, dy);
     * @param dx The change in the x direction (positive is rightward)
     * @param dy The change in the y direction (positive is downward)
     */
    public GPoint translate(float dx, float dy) {
        xc += dx;
        yc += dy;
        return this;
    }

    /**
     * Returns an integer hash code for the point.  The hash code for a
     * <code>GPoint</code> is constructed from the hash codes from the
     * <code>float</code> values of the coordinates, which are the ones used in the
     * <code>equals</code> method.
     *
     * @usage hash = pt.hashCode();
     * @return The hash code for this pt
     * @noshow
     */
    public int hashCode() {
        return Float.valueOf((float) xc).hashCode() ^ (37 * Float.valueOf((float) yc).hashCode());
    }

    /**
     * Tests whether two <code>GPoint</code> objects are equal.
     * Because floating-point values are inexact, this method checks for
     * equality by comparing the <code>float</code> values (rather than the
     * <code>float</code> values) of the coordinates.
     *
     * @usage if (pt.equals(obj)) . . .
     * @param obj Any object
     * @return <code>true</code> if the <code>obj</code> is a <code>GPoint</code>
     *         equal to this one, and <code>false</code> otherwise
     * @noshow
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof GPoint)) {
            return false;
        }
        GPoint pt = (GPoint) obj;
        return ((float) xc == (float) pt.xc) && ((float) yc == (float) pt.yc);
    }

    /**
     * Converts this <code>GPoint</code> to its string representation.
     *
     * @usage str = rect.toString();
     * @return A string representation of this point
     * @noshow
     */
    public String toString() {
        return "(" + (float) xc + ", " + (float) yc + ")";
    }
}
