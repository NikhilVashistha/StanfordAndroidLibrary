/*
 * @version 2016/05/21
 * - initial version
 */

package stanford.androidlib;

import android.content.*;
import android.graphics.*;
import android.support.annotation.*;
import android.view.View;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A utility class for loading and transforming Bitmap objects.
 *
 * <pre>
 * SimpleBitmap.with(context).getBitmap(R.drawable.foo);
 * </pre>
 */
public class SimpleBitmap {
    private static Context context = null;
    private static final SimpleBitmap INSTANCE = new SimpleBitmap();
    private boolean filter = true;

    /**
     * Returns a singleton SimpleBitmap instance bound to the given context.
     */
    public static SimpleBitmap with(Context context) {
        SimpleBitmap.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleBitmap instance bound to the given view's context.
     */
    public static SimpleBitmap with(View context) {
        Context newContext = context.getContext();
        if (SimpleBitmap.context == null || newContext != null) {
            SimpleBitmap.context = newContext;
        }
        return INSTANCE;
    }

    private SimpleBitmap() {
        // empty
    }

    /**
     * Returns the bitmap image for the resource file with the given ID.
     */
    public Bitmap get(@DrawableRes int id) {
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * Returns the bitmap image for the file located at the given web URL.
     * @throws IORuntimeException if the URL cannot be read or is not a valid image.
     */
    public Bitmap get(@NonNull String url) {
        try {
            URL theUrl = new URL(url);
            return BitmapFactory.decodeStream(theUrl.openStream());
        } catch (MalformedURLException mfurle) {
            throw new IllegalArgumentException("Invalid URL: " + url, mfurle);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Returns the bitmap image for the file located at the given web URL.
     * @throws IORuntimeException if the URL cannot be read or is not a valid image.
     */
    public Bitmap get(@NonNull URL url) {
        try {
            return BitmapFactory.decodeStream(url.openStream());
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Returns the bitmap that corresponds to the given resource ID.
     */
    public Bitmap get(@DrawableRes int id, float width, float height) {
        return scale(get(id), width, height);
    }

    /**
     * Returns a list of the bitmap images for the resource files with the given IDs.
     */
    public ArrayList<Bitmap> getAll(@DrawableRes int... ids) {
        ArrayList<Bitmap> list = new ArrayList<>();
        for (@DrawableRes int id : ids) {
            list.add(get(id));
        }
        return list;
    }

    /**
     * Returns a list of the bitmap images for the resource files with the given IDs,
     * scaled by the given factor.
     */
    public ArrayList<Bitmap> getAllScaled(float scaleFactor, @DrawableRes int... ids) {
        ArrayList<Bitmap> list = new ArrayList<>();
        for (@DrawableRes int id : ids) {
            list.add(scale(id, scaleFactor));
        }
        return list;
    }

    /**
     * Returns a list of the bitmap images for the resource files with the given IDs,
     * scaled to the given width and height.
     */
    public ArrayList<Bitmap> getAllScaled(float width, float height, @DrawableRes int... ids) {
        ArrayList<Bitmap> list = new ArrayList<>();
        for (@DrawableRes int id : ids) {
            list.add(scale(id, width, height));
        }
        return list;
    }

    /**
     * Whether we filter bitmaps on scale/rotate for smoothness.
     * Slower but better-looking. On by default.
     */
    public boolean isFiltered() {
        return filter;
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated by the given number of degrees
     * clockwise about its center point.
     */
    public Bitmap rotate(Bitmap bitmap, float degrees) {
        float centerX = bitmap.getWidth() / 2f;
        float centerY = bitmap.getHeight() / 2f;
        return rotate(bitmap, degrees, centerX, centerY);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated by the given number of degrees
     * clockwise about its center point.
     */
    public Bitmap rotate(@DrawableRes int id, float degrees) {
        return rotate(get(id), degrees);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated by the given number of degrees
     * clockwise about the point (rx, ry).
     */
    public Bitmap rotate(Bitmap bitmap, float degrees, float rx, float ry) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees, rx, ry);
        return Bitmap.createBitmap(bitmap,
                0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, /* filter */ true);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated by the given number of degrees
     * clockwise about the point (rx, ry).
     */
    public Bitmap rotate(@DrawableRes int id, float degrees, float rx, float ry) {
        return rotate(get(id), degrees, rx, ry);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated counter-clockwise by 90 degrees
     * about its center point.
     */
    public Bitmap rotateLeft(Bitmap bitmap) {
        return rotate(bitmap, /* degrees */ -90);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated counter-clockwise by 90 degrees
     * about its center point.
     */
    public Bitmap rotateLeft(@DrawableRes int id) {
        return rotate(get(id), /* degrees */ -90);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated clockwise by 90 degrees
     * about its center point.
     */
    public Bitmap rotateRight(Bitmap bitmap) {
        return rotate(bitmap, /* degrees */ 90);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated clockwise by 90 degrees
     * about its center point.
     */
    public Bitmap rotateRight(@DrawableRes int id) {
        return rotate(get(id), /* degrees */ 90);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the given size.
     */
    public Bitmap scale(Bitmap bitmap, float width, float height) {
        return Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, /* filter */ true);
    }

    /**
     * Returns the bitmap that corresponds to the given resource ID.
     */
    public Bitmap scale(@DrawableRes int id, float width, float height) {
        return scale(get(id), width, height);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized by the given
     * factor.  For example, if the scale factor is 0.5f, the image will
     * shrink to half its current size.
     */
    public Bitmap scale(Bitmap bitmap, float scaleFactor) {
        float width = bitmap.getWidth() * scaleFactor;
        float height = bitmap.getHeight() * scaleFactor;
        return scale(bitmap, width, height);
    }

    /**
     * Returns the bitmap that corresponds to the given resource ID.
     */
    public Bitmap scale(@DrawableRes int id, float scaleFactor) {
        return scale(get(id), scaleFactor);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the largest size
     * that can fit within the given width/height while maintaining its aspect ratio.
     */
    public Bitmap scaleToFit(Bitmap bitmap, float width, float height) {
        // example: image is  40x 30, fit to 200x400
        //                => 200x150
        // example: image is 400x300, fit to 200x400
        //                => 200x150
        // example: image is 400x300, fit to 1200x450
        //                => 600x450
        float widthRatio  = bitmap.getWidth() / width;
        float heightRatio = bitmap.getHeight() / height;
        if (widthRatio > heightRatio) {
            float scaleFactor = width / bitmap.getWidth();
            return scale(bitmap, scaleFactor);
        } else {
            float scaleFactor = height / bitmap.getHeight();
            return scale(bitmap, scaleFactor);
        }
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the largest size
     * that can fit within the given width/height while maintaining its aspect ratio.
     */
    public Bitmap scaleToFit(@DrawableRes int id, float width, float height) {
        return scaleToFit(get(id), width, height);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the largest size
     * that can fit within the given view's width/height while maintaining its aspect ratio.
     */
    public Bitmap scaleToFit(Bitmap bitmap, View view) {
        return scaleToFit(bitmap, view.getWidth(), view.getHeight());
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the largest size
     * that can fit within the given view's width/height while maintaining its aspect ratio.
     */
    public Bitmap scaleToFit(@DrawableRes int id, View view) {
        return scaleToFit(get(id), view);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the largest size
     * that can fit within the given canvas's width/height while maintaining its aspect ratio.
     */
    public Bitmap scaleToFit(Bitmap bitmap, Canvas canvas) {
        return scaleToFit(bitmap, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the largest size
     * that can fit within the given canvas's width/height while maintaining its aspect ratio.
     */
    public Bitmap scaleToFit(@DrawableRes int id, Canvas canvas) {
        return scaleToFit(get(id), canvas);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to have
     * the given height, and a proportionally scaled width to match.
     */
    public Bitmap scaleToHeight(Bitmap bitmap, float height) {
        float width = bitmap.getWidth() * height / bitmap.getHeight();
        return scale(bitmap, width, height);
    }

    /**
     * Returns the bitmap that corresponds to the given resource ID.
     */
    public Bitmap scaleToHeight(@DrawableRes int id, float height) {
        return scaleToHeight(get(id), height);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to have
     * the given width, and a proportionally scaled height to match.
     */
    public Bitmap scaleToWidth(Bitmap bitmap, float width) {
        float height = bitmap.getHeight() * width / bitmap.getWidth();
        return scale(bitmap, width, height);
    }

    /**
     * Returns the bitmap that corresponds to the given resource ID.
     */
    public Bitmap scaleToWidth(@DrawableRes int id, float width) {
        return scaleToWidth(get(id), width);
    }

    /**
     * Whether we filter bitmaps on scale/rotate for smoothness.
     * Slower but better-looking. On by default.
     */
    public SimpleBitmap setFiltered(boolean filter) {
        this.filter = filter;
        return this;
    }
}
