package stanford.androidlib.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * The <code>GOval</code> class is a graphical object whose appearance consists
 * of an oval.
 */
public class GOval extends GObject
        implements GFillable, GResizable, GScalable {

    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;

    // private fields
    private boolean useArcs;

    /**
     * Constructs a new 0x0 oval, positioned at the origin.
     *
     * @usage GOval goval = new GOval();
     */
    public GOval() {
        this(0, 0, 0, 0);
    }

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
        setBounds(x, y, width, height);
    }

    /**
     * Constructs a new 0x0 oval, positioned at the origin.
     *
     * @usage GOval goval = new GOval();
     */
    public GOval(GCanvas canvas) {
        this();
        canvas.add(this);
    }

    /**
     * Constructs a new oval with the specified width and height,
     * positioned at the origin.
     *
     * @usage GOval goval = new GOval(width, height);
     * @param width The width of the oval in pixels
     * @param height The height of the oval in pixels
     */
    public GOval(GCanvas canvas, float width, float height) {
        this(width, height);
        canvas.add(this);
    }

    /**
     * Constructs a new oval with the specified bounds.
     *
     * @usage GOval goval = new GOval(x, y, width, height);
     * @param x The x-coordinate of the upper left corner
     * @param y The y-coordinate of the upper left corner
     * @param width The width of the oval in pixels
     * @param height The height of the oval in pixels
     */
    public GOval(GCanvas canvas, float x, float y, float width, float height) {
        this(x, y, width, height);
        canvas.add(this);
    }

    /**
     * Checks to see whether a point is inside the object.
     *
     * @usage if (goval.contains(x, y)) . . .
     * @param x The x-coordinate of the point being tested
     * @param y The y-coordinate of the point being tested
     * @return <code>true</code> if the point (<code>x</code>,&nbsp;<code>y</code>) is inside
     *         the object, and <code>false</code> otherwise
     */
    @Override
    public boolean contains(float x, float y) {
        float rx = getWidth() / 2;
        float ry = getHeight() / 2;
        if (rx == 0 || ry == 0) {
            return false;
        }
        float dx = x - (getX() + rx);
        float dy = y - (getY() + ry);
        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0;
    }

    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @noshow
     */
    public void paint(Canvas canvas) {
        if (isFilled()) {
            // fill interior first
            Paint fill = getFillColor();
            canvas.drawOval(new RectF(getX(), getY(), getRightX(), getBottomY()), fill);
        }

        // draw outline second
        canvas.drawOval(new RectF(getX(), getY(), getRightX(), getBottomY()), getPaint());
    }
}
