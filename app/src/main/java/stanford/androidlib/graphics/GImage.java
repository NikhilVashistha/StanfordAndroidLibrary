package stanford.androidlib.graphics;

/* Class: GImage */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.view.View;

/**
 * The <code>GImage</code> class is a graphical object whose appearance is
 * defined by an image.
 */
public class GImage extends GObject implements GResizable, GScalable {
    /**
     * Creates a new GImage object at the origin.
     */
    public GImage(Bitmap image) {
        this(image, 0, 0);
    }

    /**
     * Creates a new GImage object at the specified coordinates.
     */
    public GImage(Bitmap image, float x, float y) {
        setImage(image);
        setLocation(x, y);
    }

    /**
     * Creates a new GImage object at the origin inside the given view.
     */
    public GImage(View view, Bitmap image) {
        this(view, image, 0, 0);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given view.
     */
    public GImage(View view, Bitmap image, float x, float y) {
        this.context = view.getContext();
        setImage(image);
        setLocation(x, y);
    }

    /**
     * Creates a new GImage object at the origin inside the given context.
     */
    public GImage(Context context, @DrawableRes int imageID) {
        this(context, imageID, 0, 0);
    }

    /**
     * Creates a new GImage object at the origin inside the given view.
     */
    public GImage(View view, @DrawableRes int imageID) {
        this(view, imageID, 0, 0);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given view.
     */
    public GImage(View view, @DrawableRes int imageID, float x, float y) {
        this(view.getContext(), imageID, x, y);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given context.
     */
    public GImage(Context context, @DrawableRes int imageID, float x, float y) {
        this(BitmapFactory.decodeResource(context.getResources(), imageID), x, y);
        this.context = context;
    }

/* Method: setImage(image) */
    /**
     * Resets the image used by this <code>GImage</code> object to the new image
     * specified as an argument.  Calling <code>setImage</code> automatically changes
     * the size of the image to be equal to that of the image data.
     *
     * @usage gimage.setImage(image);
     * @param image The image to use as the contents of this <code>GImage</code>
     */
    public void setImage(Bitmap image) {
        myImage = image;
        sizeDetermined = false;
        determineSize();
    }

/* Method: setImage(name) */
    /**
     * Resets the image used by this <code>GImage</code> object to the one identified
     * by the argument <code>name</code>, which is processed exactly as described
     * in the constructors.  Calling <code>setImage</code> automatically changes
     * the size of the image to be equal to that of the image data.
     *
     * @usage gimage.setImage(name);
     */
    public void setImage(@DrawableRes int imageID) {
        if (context == null) {
            throw new IllegalStateException("You must construct GImage with a Context in order to use setImage(int).");
        }
        setImage(BitmapFactory.decodeResource(context.getResources(), imageID));
    }

/* Method: getImage() */
    /**
     * Returns the image stored inside this <code>GImage</code>.
     *
     * @usage Image image = gimage.getImage();
     * @return The <code>Image</code> object stored inside this <code>GImage</code>
     */
    public Bitmap getImage() {
        return myImage;
    }

/* Method: paint(g) */
    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @noshow
     */
    public void paint(Canvas canvas) {
        canvas.drawBitmap(this.myImage, getX(), getY(), /* paint */ null);
    }

/* Method: setSize(width, height) */
    /**
     * Changes the size of this object to the specified width and height.
     *
     * @usage gimage.setSize(width, height);
     * @param width The new width of the object
     * @param height The new height of the object
     */
    public void setSize(float width, float height) {
        myWidth = width;
        myHeight = height;
    }

/* Method: setSize(size) */
    /**
     * Changes the size of this object to the specified <code>GDimension</code>.
     *
     * @usage gimage.setSize(size);
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
     * @usage GDimension size = gimage.getSize();
     * @return The size of this object
     */
    public GDimension getSize() {
        return new GDimension(myWidth, myHeight);
    }

/* Method: setBounds(x, y, width, height) */
    /**
     * Changes the bounds of this object to the specified values.
     *
     * @usage gimage.setBounds(x, y, width, height);
     * @param x The new x-coordinate for the object
     * @param y The new y-coordinate for the object
     * @param width The new width of the object
     * @param height The new height of the object
     */
    public void setBounds(float x, float y, float width, float height) {
        myWidth = width;
        myHeight = height;
        setLocation(x, y);
    }

/* Method: setBounds(bounds) */
    /**
     * Changes the bounds of this object to the values from the specified
     * <code>GRectangle</code>.
     *
     * @usage gimage.setBounds(bounds);
     * @param bounds A <code>GRectangle</code> specifying the new bounds
     */
    public final void setBounds(GRectangle bounds) {
        setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

/* Method: getBounds() */
    /**
     * Returns the bounding box of this object.
     *
     * @usage GRectangle bounds = gimage.getBounds();
     * @return The bounding box for this object
     */
    public GRectangle getBounds() {
        // determineSize();
        // TODO
        return new GRectangle(getX(), getY(), myWidth, myHeight);
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
        myWidth *= sx;
        myHeight *= sy;
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

    public void setContext(Context context) {
        this.context = context;
    }

/* Method: getPixelArray() */
    /**
     * Returns a two-dimensional array of pixel values from the stored image.
     *
     * @usage int[][] array = gimage.getPixelArray();
     * @return A two-dimensional array of pixel values from the stored image
     */
    public int[][] getPixelArray() {
        // return MediaTools.getPixelArray(myImage);
        // TODO
        return null;
    }

/* Static method: getAlpha(pixel) */
    /**
     * Returns the alpha component from an RGB value.
     *
     * @usage int alpha = GImage.getAlpha(pixel);
     * @param pixel An <code>int</code> containing a pixel value as alpha/red/green/blue.
     * @return The alpha component of the pixel
     */
    public static int getAlpha(int pixel) {
        return (pixel >> 24) & 0xFF;
    }

/* Static method: getRed(pixel) */
    /**
     * Returns the red component from an RGB value.
     *
     * @usage int red = GImage.getRed(pixel);
     * @param pixel An <code>int</code> containing a pixel value as alpha/red/green/blue.
     * @return The red component of the pixel
     */
    public static int getRed(int pixel) {
        return (pixel >> 16) & 0xFF;
    }

/* Static method: getGreen(pixel) */
    /**
     * Returns the green component from an RGB value.
     *
     * @usage int green = GImage.getGreen(pixel);
     * @param pixel An <code>int</code> containing a pixel value as alpha/red/green/blue.
     * @return The green component of the pixel
     */
    public static int getGreen(int pixel) {
        return (pixel >> 8) & 0xFF;
    }

/* Static method: getBlue(pixel) */
    /**
     * Returns the blue component from an RGB value.
     *
     * @usage int blue = GImage.getBlue(pixel);
     * @param pixel An <code>int</code> containing a pixel value as alpha/red/green/blue.
     * @return The blue component of the pixel
     */
    public static int getBlue(int pixel) {
        return pixel & 0xFF;
    }

/* Static method: createRGBPixel(red, green, blue) */
    /**
     * Creates an opaque pixel value with the color components given by
     * <code>red</code>, <code>green</code>, and <code>blue</code>.
     *
     * @usage int pixel = GImage.createRGBPixel(red, green, blue);
     * @param red The red component of the pixel (0 to 255)
     * @param green The green component of the pixel (0 to 255)
     * @param blue The blue component of the pixel (0 to 255)
     * @return An opaque pixel value containing these components
     */
    public static int createRGBPixel(int red, int green, int blue) {
        return createRGBPixel(red, green, blue, 0xFF);
    }

/* Static method: createRGBPixel(red, green, blue, alpha) */
    /**
     * Creates a pixel value with the color components given by
     * <code>red</code>, <code>green</code>, and <code>blue</code>
     * and the transparency value <code>alpha</code>.
     *
     * @usage int pixel = GImage.createRGBPixel(red, green, blue);
     * @param red The red component of the pixel (0 to 255)
     * @param green The green component of the pixel (0 to 255)
     * @param blue The blue component of the pixel (0 to 255)
     * @param alpha The transparency value of the pixel (0 to 255)
     * @return A pixel value containing these components
     */
    public static int createRGBPixel(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
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

/* Inherited method: getWidth() */
/**
 * @inherited GObject#float getWidth()
 * Returns the width of this object as a float-precision value, which
 * is defined to be the width of the bounding box.
 */

/* Inherited method: getHeight() */
/**
 * @inherited GObject#float getHeight()
 * Returns the height of this object as a float-precision value, which
 * is defined to be the height of the bounding box.
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

/* Inherited method: contains(x, y) */
/**
 * @inherited GObject#boolean contains(float x, float y)
 * Checks to see whether a point is inside the object.
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

/* Private method: determineSize() */
    /**
     * Computes the size of the image.
     */
    private void determineSize() {
        if (sizeDetermined || myImage == null) return;
        myWidth = myImage.getWidth();
        myHeight = myImage.getHeight();
        sizeDetermined = true;
    }

    /* Private instance variables */
    private Context context;
    private Bitmap myImage;
    private float myWidth;
    private float myHeight;
    private boolean sizeDetermined;

/* Serial version UID */
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;
}
