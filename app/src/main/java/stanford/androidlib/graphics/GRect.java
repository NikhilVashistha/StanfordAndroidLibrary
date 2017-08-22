package stanford.androidlib.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * The <code>GRect</code> class is a graphical object whose appearance consists
 * of a rectangular box.
 */
public class GRect extends GObject
        implements GFillable, GResizable, GScalable {
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;

    /**
     * Constructs a new 0x0 rectangle, positioned at the origin.
     *
     * @usage GRect grect = new GRect();
     */
    public GRect() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs a new rectangle with the specified width and height,
     * positioned at the origin.
     *
     * @usage GRect grect = new GRect(width, height);
     * @param width The width of the rectangle in pixels
     * @param height The height of the rectangle in pixels
     */
    public GRect(float width, float height) {
        this(0, 0, width, height);
    }

    /**
     * Constructs a new rectangle with the specified bounds.
     *
     * @usage GRect grect = new GRect(x, y, width, height);
     * @param x The x-coordinate of the upper left corner
     * @param y The y-coordinate of the upper left corner
     * @param width The width of the rectangle in pixels
     * @param height The height of the rectangle in pixels
     */
    public GRect(float x, float y, float width, float height) {
        setBounds(x, y, width, height);
    }

    /**
     * Constructs a new 0x0 rectangle, positioned at the origin.
     *
     * @usage GRect grect = new GRect();
     */
    public GRect(GCanvas canvas) {
        this();
        canvas.add(this);
    }

    /**
     * Constructs a new rectangle with the specified width and height,
     * positioned at the origin.
     *
     * @usage GRect grect = new GRect(width, height);
     * @param width The width of the rectangle in pixels
     * @param height The height of the rectangle in pixels
     */
    public GRect(GCanvas canvas, float width, float height) {
        this(width, height);
        canvas.add(this);
    }

    /**
     * Constructs a new rectangle with the specified bounds.
     *
     * @usage GRect grect = new GRect(x, y, width, height);
     * @param x The x-coordinate of the upper left corner
     * @param y The y-coordinate of the upper left corner
     * @param width The width of the rectangle in pixels
     * @param height The height of the rectangle in pixels
     */
    public GRect(GCanvas canvas, float x, float y, float width, float height) {
        this(x, y, width, height);
        canvas.add(this);
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
            canvas.drawRect(getX(), getY(), getRightX(), getBottomY(), fill);
        }

        // draw outline second
        canvas.drawRect(getX(), getY(), getRightX(), getBottomY(), getPaint());
    }
}
