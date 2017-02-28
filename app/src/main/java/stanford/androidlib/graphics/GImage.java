/*
 * @version 2016/12/22
 * - added nullness and range checking to some methods
 */

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
public class GImage extends GObject implements GResizable {
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;

    /* Private instance variables */
    private Context context;
    private Bitmap myImage;
    private boolean sizeDetermined;

    /**
     * Creates a new GImage object at the origin.
     * @throws NullPointerException if image is null
     */
    public GImage(Bitmap image) {
        this(image, 0, 0);
    }

    /**
     * Creates a new GImage object at the specified coordinates.
     * @throws NullPointerException if image is null
     */
    public GImage(Bitmap image, float x, float y) {
        setImage(image);
        setLocation(x, y);
    }

    /**
     * Creates a new GImage object at the origin inside the given view.
     * @throws NullPointerException if view or image is null
     */
    public GImage(View view, Bitmap image) {
        this(view, image, 0, 0);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given view.
     * @throws NullPointerException if view or image is null
     */
    public GImage(View view, Bitmap image, float x, float y) {
        this.context = view.getContext();
        setImage(image);
        setLocation(x, y);
    }

    /**
     * Creates a new GImage object at the origin inside the given context.
     * @throws NullPointerException if context is null
     */
    public GImage(Context context, @DrawableRes int imageID) {
        this(context, imageID, 0, 0);
    }

    /**
     * Creates a new GImage object at the origin inside the given view.
     * @throws NullPointerException if view is null
     */
    public GImage(View view, @DrawableRes int imageID) {
        this(view, imageID, 0, 0);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given view.
     * @throws NullPointerException if view is null
     */
    public GImage(View view, @DrawableRes int imageID, float x, float y) {
        this(view.getContext(), imageID, x, y);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given context.
     * @throws NullPointerException if context is null
     */
    public GImage(Context context, @DrawableRes int imageID, float x, float y) {
        this(BitmapFactory.decodeResource(context.getResources(), imageID), x, y);
        this.context = context;
    }

    /**
     * Creates a new GImage object at the origin.
     * @throws NullPointerException if image is null
     */
    public GImage(GCanvas canvas, Bitmap image) {
        this(image);
        canvas.add(this);
    }

    /**
     * Creates a new GImage object at the specified coordinates.
     * @throws NullPointerException if image is null
     */
    public GImage(GCanvas canvas, Bitmap image, float x, float y) {
        this(image, x, y);
        canvas.add(this);
    }

    /**
     * Creates a new GImage object at the origin inside the given view.
     * @throws NullPointerException if view or image is null
     */
    public GImage(GCanvas canvas, View view, Bitmap image) {
        this(view, image);
        canvas.add(this);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given view.
     * @throws NullPointerException if view or image is null
     */
    public GImage(GCanvas canvas, View view, Bitmap image, float x, float y) {
        this(view, image, x, y);
        canvas.add(this);
    }

    /**
     * Creates a new GImage object at the origin inside the given context.
     * @throws NullPointerException if context is null
     */
    public GImage(GCanvas canvas, Context context, @DrawableRes int imageID) {
        this(context, imageID);
        canvas.add(this);
    }

    /**
     * Creates a new GImage object at the origin inside the given view.
     * @throws NullPointerException if view is null
     */
    public GImage(GCanvas canvas, View view, @DrawableRes int imageID) {
        this(view, imageID);
        canvas.add(this);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given view.
     * @throws NullPointerException if view is null
     */
    public GImage(GCanvas canvas, View view, @DrawableRes int imageID, float x, float y) {
        this(view, imageID, x, y);
        canvas.add(this);
    }

    /**
     * Creates a new GImage object at the given x/y location inside the given context.
     * @throws NullPointerException if context is null
     */
    public GImage(GCanvas canvas, Context context, @DrawableRes int imageID, float x, float y) {
        this(context, imageID, x, y);
        canvas.add(this);
    }

    /**
     * Resets the image used by this <code>GImage</code> object to the new image
     * specified as an argument.  Calling <code>setImage</code> automatically changes
     * the size of the image to be equal to that of the image data.
     *
     * @usage gimage.setImage(image);
     * @param image The image to use as the contents of this <code>GImage</code>
     * @throws NullPointerException if image is null
     */
    public GImage setImage(Bitmap image) {
        if (image == null) {
            throw new NullPointerException();
        }
        myImage = image;
        sizeDetermined = false;
        determineSize();
        return this;
    }

    /**
     * Resets the image used by this <code>GImage</code> object to the one identified
     * by the argument <code>name</code>, which is processed exactly as described
     * in the constructors.  Calling <code>setImage</code> automatically changes
     * the size of the image to be equal to that of the image data.
     *
     * @usage gimage.setImage(name);
     */
    public GImage setImage(@DrawableRes int imageID) {
        if (context == null) {
            throw new IllegalStateException("You must construct GImage with a Context in order to use setImage(int).");
        }
        return setImage(BitmapFactory.decodeResource(context.getResources(), imageID));
    }

    /**
     * Returns the image stored inside this <code>GImage</code>.
     *
     * @usage Image image = gimage.getImage();
     * @return The <code>Image</code> object stored inside this <code>GImage</code>
     */
    public Bitmap getImage() {
        return myImage;
    }

    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @throws NullPointerException if canvas is null
     * @noshow
     */
    public void paint(Canvas canvas) {
        canvas.drawBitmap(this.myImage, getX(), getY(), /* paint */ null);
    }

//    /**
//     * Scales the object on the screen by the scale factors <code>sx</code> and <code>sy</code>.
//     *
//     * @usage gobj.scale(sx, sy);
//     * @param sx The factor used to scale all coordinates in the x direction
//     * @param sy The factor used to scale all coordinates in the y direction
//     */
//    public void scale(float sx, float sy) {
//        myWidth *= sx;
//        myHeight *= sy;
//    }
//
//    /**
//     * Scales the object on the screen by the scale factor <code>sf</code>, which applies
//     * in both dimensions.
//     *
//     * @usage gobj.scale(sf);
//     * @param sf The factor used to scale all coordinates in both dimensions
//     */
//    public final void scale(float sf) {
//        scale(sf, sf);
//    }
//

    /**
     * Sets the context used by this image.
     * Can be null.
     * @param context the context to use
     */
    public GImage setContext(Context context) {
        this.context = context;
        return this;
    }

    /**
     * Returns a two-dimensional array of pixel values from the stored image.
     * Not yet implemented.
     *
     * @usage int[][] array = gimage.getPixelArray();
     * @return A two-dimensional array of pixel values from the stored image
     * @noshow
     */
    public int[][] getPixelArray() {
        // return MediaTools.getPixelArray(myImage);
        // TODO
        return null;
    }

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
     * @throws IllegalArgumentException if any of r/g/b/a are not in range 0-255
     */
    public static int createRGBPixel(int red, int green, int blue, int alpha) {
        GColor.ensureLegalColorComponent(red);
        GColor.ensureLegalColorComponent(green);
        GColor.ensureLegalColorComponent(blue);
        GColor.ensureLegalColorComponent(alpha);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Computes the size of the image.
     */
    private void determineSize() {
        if (sizeDetermined || myImage == null) {
            return;
        }
        width = myImage.getWidth();
        height = myImage.getHeight();
        sizeDetermined = true;
    }
}
