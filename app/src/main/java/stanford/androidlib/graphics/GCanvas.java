/*
 * @version 2016/12/22
 * - added illegal arg / nullness checking to some methods
 * @version 2016/02/15
 * - added __getCanvasContext
 * @version 2016/02/11
 * - added get/setBackgroundColor
 * - added get/setDisplayBuffer and scaled drawing
 */

package stanford.androidlib.graphics;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import java.util.*;
import stanford.androidlib.*;

/**
 * A GCanvas is similar to a GraphicsProgram from the Stanford/ACM Java Task Force
 * library.  It allows you to add various GObjects to it and will maintain their state
 * for you.  You can update/modify the state of the GObjects, and the changes will appear
 * on screen immediately.  A GCanvas can also be easily animated, making it a useful
 * basis for simple graphical animations and 2D games.
 *
 * <pre>
 * public class MyCanvas extends GCanvas {
 *     ...
 *     public void init() {
 *         ...
 *     }
 * }
 * </pre>
 */
public abstract class GCanvas extends SimpleCanvas implements Iterable<GObject> {
    // context used by GObject classes (e.g. GLabel) for loading resources
    private static Context applicationContext = null;

    /**
     * Returns a Context that GObjects can use to load resources.
     * @noshow
     */
    static Context __getCanvasContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("You cannot create GObjects (such as GLabel) before any GCanvas has been constructed; "
                    + "we suggest putting initialization of any GObjects into your canvas class's init() method");
        }
        return applicationContext;
    }

    private Paint background = new Paint(GColor.WHITE);
    private final List<GObject> gobjects = new ArrayList<>();
    private Bitmap displayBuffer = null;
    private Rect drawSrc = null;
    private RectF drawDst = null;
    private boolean firstDraw = false;
    private boolean initialized = false;

    /**
     * Required constructor; your canvas subclass must implement a constructor
     * with exactly the same parameters.
     */
    public GCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (applicationContext == null) {
            applicationContext = context;
        }
    }

    /**
     * Adds the given GObject to this canvas.
     * On every repaint / onDraw operation, all added GObjects will be drawn.
     * @throws NullPointerException if obj is null
     */
    public void add(GObject obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        obj.setGCanvas(this);
        gobjects.add(obj);
    }

    /**
     * Adds the given GObject to this canvas at the given x/y location.
     * On every repaint / onDraw operation, all added GObjects will be drawn.
     * @throws NullPointerException if obj is null
     */
    public void add(GObject obj, float x, float y) {
        if (obj == null) {
            throw new NullPointerException();
        }
        obj.setGCanvas(this);
        obj.setLocation(x, y);
        gobjects.add(obj);
    }

    /**
     * Adds the given GObject to this canvas at the given x/y location.
     * On every repaint / onDraw operation, all added GObjects will be drawn.
     * @throws NullPointerException if obj or point is null
     */
    public void add(GObject obj, GPoint point) {
        add(obj, point.getX(), point.getY());
    }

    /**
     * Returns whether this canvas contains the given graphical object.
     * @throws NullPointerException if obj is null
     */
    public boolean contains(GObject obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return gobjects.contains(obj);
    }

    /**
     * Returns the GObject found at the given x/y location in this canvas.
     * If no object touches that location, null is returned.
     * If multiple objects touch that loaction, the highest one in the Z-ordering is returned.
     */
    public GObject getElementAt(float x, float y) {
        for (int i = gobjects.size() - 1; i >= 0; i--) {
            GObject obj = gobjects.get(i);
            if (obj.contains(x, y)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Returns the GObject found at the given x/y location in this canvas.
     * If no object touches that location, null is returned.
     * If multiple objects touch that loaction, the highest one in the Z-ordering is returned.
     * @throws NullPointerException if the point is null
     */
    public GObject getElementAt(GPoint point) {
        return getElementAt(point.getX(), point.getY());
    }

    /**
     * Returns the GObject found at any of the given x/y locations in this canvas.
     * The locations are treated as {x1, y1, x2, y2, ...} and are tried in order.
     * If no object touches any of these locations, null is returned.
     * If multiple objects touch the first found loaction, the highest one in the Z-ordering is returned.
     */
    public GObject getElementAt(float... xy) {
        for (int i = 0; i < xy.length - 1; i += 2) {
            GObject obj = getElementAt(xy[i], xy[i + 1]);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Returns the GObject found at any of the given x/y locations in this canvas.
     * The locations are tried in order.
     * If no object touches any of these locations, null is returned.
     * If multiple objects touch the first found loaction, the highest one in the Z-ordering is returned.
     * @throws NullPointerException if any point is null
     */
    public GObject getElementAt(GPoint... points) {
        for (GPoint point : points) {
            GObject obj = getElementAt(point);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Returns the graphical object at the specified index,
     * numbering from back (0) to front in the the z ordering.
     * @throws IndexOutOfBoundsException if index is negative or exceeds number of objects in canvas
     */
    public GObject getElement(int index) {
        return gobjects.get(index);
    }

    /**
     * Returns the number of graphical objects stored in this canvas.
     */
    public int getElementCount() {
        return gobjects.size();
    }

    /**
     * Draws the canvas; you should not override this method.
     * If you want to draw things onto a GCanvas, you have to do it by
     * adding GObjects to the canvas which are then drawn.
     * If you want to override this method, you should probably be extending
     * SimpleCanvas instead or writing your own custom View subclass.
     */
    @Override
    protected final void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Canvas drawingSurface = (displayBuffer == null) ? canvas : new Canvas(displayBuffer);

        // fill with background color, maybe
        if (background != null) {
            drawingSurface.drawColor(background.getColor());
        } else if (displayBuffer != null) {
            drawingSurface.drawColor(0);   // fill with transparency
        }

        // draw every GObject that has been added so far
        for (GObject gobject : gobjects) {
            gobject.setGCanvas(this);
            gobject.setCanvas(drawingSurface);
            gobject.paint(drawingSurface);
        }

        if (displayBuffer != null) {
            this.drawDst.set(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(displayBuffer, drawSrc, drawDst, /* paint */ null);
        }

        firstDraw = false;
    }

    /**
     * Removes the given graphical object, if it was contained in this canvas.
     * @throws NullPointerException if obj is null
     */
    public void remove(GObject obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        gobjects.remove(obj);
    }

    /**
     * Removes all graphical objects from this canvas.
     */
    public void removeAll() {
        gobjects.clear();
    }

    /**
     * Moves the given GObject back/down by 1 in the Z-ordering.
     * If the given GObject is not added to this canvas, has no effect.
     * @throws NullPointerException if obj is null
     */
    public void sendBackward(GObject obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        int index = gobjects.indexOf(obj);
        if (index > 0) {
            synchronized (gobjects) {
                gobjects.remove(index);
                gobjects.add(index - 1, obj);
            }
        }
    }

    /**
     * Moves the given GObject forward/up by 1 in the Z-ordering.
     * If the given GObject is not added to this canvas, has no effect.
     * @throws NullPointerException if obj is null
     */
    public void sendForward(GObject obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        int index = gobjects.indexOf(obj);
        if (index >= 0 && index < gobjects.size() - 1) {
            synchronized (gobjects) {
                gobjects.remove(index);
                gobjects.add(index + 1, obj);
            }
        }
    }

    /**
     * Moves the given GObject to the back/bottom of the Z-ordering.
     * If the given GObject is not added to this canvas, has no effect.
     * @throws NullPointerException if obj is null
     */
    public void sendToBack(GObject obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        int index = gobjects.indexOf(obj);
        if (index > 0) {
            synchronized (gobjects) {
                gobjects.remove(index);
                gobjects.add(0, obj);
            }
        }
    }

    /**
     * Moves the given GObject to the front/top of the Z-ordering.
     * If the given GObject is not added to this canvas, has no effect.
     * @throws NullPointerException if obj is null
     */
    public void sendToFront(GObject obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        int index = gobjects.indexOf(obj);
        if (index >= 0 && index < gobjects.size() - 1) {
            synchronized (gobjects) {
                gobjects.remove(index);
                gobjects.add(obj);
            }
        }
    }

    /**
     * Returns true if canvas has already been initialized; that is, if init() has been called.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns an iterator over the GObjects found in this canvas.
     */
    @Override
    public Iterator<GObject> iterator() {
        return iterator(/* copy */ true);
    }

    /**
     * Returns an iterator over the GObjects found in this canvas.
     * If the copy flag passed is true (default), makes a copy of the internal
     * list of GObjects so that you will not receive a ConcurrentModificationException
     * if you try to add/remove GObjects during a for-each loop over a GCanvas.
     * If the copy flag passed is false, iterates over the actual internal array list
     * of GObjects inside this canvas.
     */
    public Iterator<GObject> iterator(boolean copy) {
        if (copy) {
            return new ArrayList<>(gobjects).iterator();
        } else {
            return gobjects.iterator();
        }
    }

    /**
     * Returns the background color that will be drawn on this canvas.
     * If no background has been set, the default background is white.
     */
    public Paint getBackgroundColor() {
        return this.background;
    }

    /**
     * Sets a background color that will be drawn on this canvas.
     * If no background has been set, the default background is white.
     * @throws NullPointerException if color is null
     */
    public void setBackgroundColor(Paint color) {
        if (color == null) {
            throw new NullPointerException();
        }
        this.background = color;
    }

    /**
     * Helper to flag canvas as initialized or not initialized.
     * You probably should not call this directly.
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Called once after the canvas is put onto the screen.
     * This method is here for compatibility with the Stanford ACM Java library.
     * You can put code here to initialize your various shapes and drawings.
     *
     * <p>
     *     The main reason you might want to put initialization code here, rather than
     *     in the canvas constructor, is because the canvas doesn't yet know its width/height
     *     and some other attributes at constructor time, but it does know those things
     *     when init() is called.
     * </p>
     */
    public abstract void init();

    /**
     * This method is automatically called once for each frame of animation by the
     * AnimationLoop.  Your canvas class should override this method to contain
     * any code you want to run each frame of animation.
     * The idea is that you should put a SINGLE frame's worth of work inside here,
     * not an entire game loop.
     * The game animation loop is being done for you implicitly by GCanvas and
     * AnimationLoop classes when you call animate().
     */
    @Override
    @CallSuper
    public void onAnimateTick() {
        super.onAnimateTick();
        if (!firstDraw) {
            // update all sprites
            for (GObject gobject : gobjects) {
                if (gobject instanceof GSprite) {
                    GSprite sprite = (GSprite) gobject;
                    sprite.update();
                }
            }
        }
    }

    /**
     * Returns the display buffer you previously passed to setDisplayBuffer, or null if none has been set.
     */
    public Bitmap getDisplayBuffer() {
        return this.displayBuffer;
    }

    /**
     * Sets a display buffer on which to draw.
     * If you set this, all onDraw calls will draw onto the given bitmap image,
     * and then the bitmap will be scaled and drawn onto the canvas.
     * Initially the display buffer is null; call setDisplayBuffer(null); to turn
     * off scaled drawing.
     */
    public void setDisplayBuffer(Bitmap bitmap) {
        this.displayBuffer = bitmap;
        if (bitmap == null) {
            drawSrc = null;
            drawDst = null;
        } else {
            this.drawSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            this.drawDst = new RectF(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Sets the display buffer to a new empty bitmap image of the given size.
     * Equivalent to calling Bitmap.createBitmap and then passing the result to
     * setDisplayBuffer.
     */
    public void createDisplayBuffer(int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        setDisplayBuffer(bmp);
    }

    /**
     * This view lifecycle method is overridden so that we can ensure that init()
     * is called on the canvas before it is shown.
     */
    @Override
    @CallSuper
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!isInEditMode()) {
            ensureInitialized();
        }
    }

    // call init() if it has not been called
    private void ensureInitialized() {
        if (!initialized) {
            init();
            initialized = true;
        }
    }
}
