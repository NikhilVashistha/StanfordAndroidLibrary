/*
 * @version 2016/02/08
 * - moved GObject stuff to separate GCanvas class
 * @version 2016/02/07
 * - added GObject classes and add(GObject)
 * @version 2016/01/27
 * - initial version
 */

package stanford.androidlib;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.*;

/**
 * A SimpleCanvas is meant as a 2D drawing surface for graphics in games and animations.
 * It provides convenience methods to make basic Android programming easier for students
 * and new developers.  In your view class, you should write:
 *
 * <pre>
 * public class MyCanvas extends SimpleCanvas { ... }
 * </pre>
 */
public abstract class SimpleCanvas extends View
        implements View.OnTouchListener, View.OnKeyListener {
    protected Canvas canvas = new Canvas();
    protected Paint paint = new Paint();
    private AnimationLoop animationLoop = null;
    private int animationTickCount = 0;

    /**
     * Required constructor; your SimpleCanvas subclass must implement a constructor
     * with exactly the same parameters.
     */
    public SimpleCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnKeyListener(this);
        setOnTouchListener(this);
    }

    /**
     * Begins animating this canvas with the given number of frames per second.
     * If fps is 0 or negative, stops animating.
     */
    public void animate(int fps) {
        animationStop();
        if (fps > 0) {
            animationLoop = new AnimationLoop(this, fps);
            animationLoop.start();
        }
    }

    /**
     * Halts animation until animationResume or animate is called.
     * If you have never called animate yet, no effect.
     */
    public void animationPause() {
        if (animationLoop != null) {
            animationLoop.stop();
        }
    }

    /**
     * Continues animation that was previously started and paused.
     * If you have never called animate yet, no effect.
     */
    public void animationResume() {
        if (animationLoop != null) {
            animationLoop.start();
        }
    }

    /**
     * Halts animation.
     * If not animating, no effect.
     */
    public void animationStop() {
        if (animationLoop != null) {
            animationLoop.stop();
            animationTickCount = 0;
            animationLoop = null;
        }
    }

    /**
     * Returns whether animation is currently running and/or paused.
     */
    public boolean isAnimated() {
        return animationLoop != null;
    }

    /**
     * Call this to indicate that you want your simple canvas to receive events
     * when the user types keys.
     */
    public void enableKeyboardEvents() {
        requestFocus();
        setFocusableInTouchMode(true);
    }

    /**
     * Returns the simple activity that this canvas is inside of.
     */
    public SimpleActivity getSimpleActivity() {
        if (this.getContext() instanceof SimpleActivity) {
            return (SimpleActivity) this.getContext();
        } else {
            return SimpleActivity.getCurrentActivity();
        }
    }

    /**
     * Returns the bitmap that corresponds to the given resource ID.
     */
    public Bitmap getBitmap(@DrawableRes int id) {
        return BitmapFactory.decodeResource(getContext().getResources(), id);
    }

    /**
     * Returns the bitmap that corresponds to the given resource ID.
     */
    public Bitmap getScaledBitmap(@DrawableRes int id, float width, float height) {
        return scaleBitmap(getBitmap(id), width, height);
    }

    /**
     * Returns a new bitmap based on the given bitmap but rotated by the given number of degrees
     * clockwise about its center point.
     */
    public Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        float centerX = bitmap.getWidth() / 2f;
        float centerY = bitmap.getHeight() / 2f;
        return rotateBitmap(bitmap, degrees, centerX, centerY);
    }


    /**
     * Returns a new bitmap based on the given bitmap but rotated by the given number of degrees
     * clockwise about the point (rx, ry).
     */
    public Bitmap rotateBitmap(Bitmap bitmap, float degrees, float rx, float ry) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees, rx, ry);
        return Bitmap.createBitmap(bitmap,
                0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, /* filter */ true);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to the given size.
     */
    public Bitmap scaleBitmap(Bitmap bitmap, float width, float height) {
        return Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, /* filter */ true);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to have
     * the given width, and a proportionally scaled height to match.
     */
    public Bitmap scaleToWidth(Bitmap bitmap, float width) {
        float height = bitmap.getHeight() * width / bitmap.getWidth();
        return scaleBitmap(bitmap, width, height);
    }

    /**
     * Returns a new bitmap which is the given bitmap resized to have
     * the given height, and a proportionally scaled width to match.
     */
    public Bitmap scaleToHeight(Bitmap bitmap, float height) {
        float width = bitmap.getWidth() * height / bitmap.getHeight();
        return scaleBitmap(bitmap, width, height);
    }

    /**
     * Returns the drawing canvas found inside of this object.
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Returns a new Paint created using the given parameters.
     */
    public Paint createPaint(int r, int g, int b) {
        return createPaint(r, g, b, /* alpha */ 255);
    }

    /**
     * Returns a new Paint created using the given parameters.
     */
    public Paint createPaint(int r, int g, int b, int alpha) {
        return createPaint(r, g, b, alpha, /* style */ Paint.Style.FILL_AND_STROKE);
    }

    /**
     * Returns a new Paint created using the given parameters.
     */
    public Paint createPaint(int r, int g, int b, int alpha, Paint.Style style) {
        Paint paint = new Paint();
        paint.setARGB(alpha, r, g, b);
        paint.setStyle(style);
        return paint;
    }

    /**
     * Returns a new font typeface created using the given parameters.
     */
    public Typeface createFont(String name) {
        return createFont(name, /* style */ Typeface.NORMAL);
    }

    /**
     * Returns a new font typeface created using the given parameters.
     */
    public Typeface createFont(String name, int style) {
        Typeface font = null;
        try {
            font = Typeface.createFromAsset(getContext().getAssets(), name);
        } catch (Exception e) {
            // empty
        }
        if (font == null) {
            // create from system font name
            font = Typeface.create(name, style);
        }
        return font;
    }

    /**
     * Returns a new font typeface created using the given parameters.
     */
    public Typeface createFont(Typeface familyName) {
        return createFont(familyName, /* style */ Typeface.NORMAL);
    }

    /**
     * Returns a new font typeface created using the given parameters.
     */
    public Typeface createFont(Typeface familyName, int style) {
        Typeface font = Typeface.create(familyName, style);
        return font;
    }

    /**
     * Sets this canvas to use the given RGB color for all drawing.
     * Each parameter should be between 0-255 inclusive.
     */
    public void setColor(int r, int g, int b) {
        setColor(r, g, b, /* alpha */ 255);
    }

    /**
     * Sets this canvas to use the given ARGB for all drawing.
     * Each parameter should be between 0-255 inclusive.
     */
    public void setColor(int r, int g, int b, int alpha) {
        paint.setARGB(alpha, r, g, b);
    }

    /**
     * Sets this canvas to use the given alpha transparency from 0-255 for all drawing.
     */
    public void setAlpha(int alpha) {
        int color = paint.getColor();
        color = (color & 0x00ffffff) | ((alpha << 24) & 0xff000000);
        paint.setColor(color);
    }

    /**
     * Sets this canvas to use the given font for all drawing.
     */
    public void setFontFamily(String fontFamily) {
        paint.setTypeface(createFont(fontFamily));
    }

    /**
     * Sets this canvas to use the given font for all drawing.
     */
    public void setFontFileName(String fontFileName) {
        paint.setTypeface(createFont(fontFileName));
    }

    /**
     * Sets this canvas to use the given font for all drawing.
     */
    public void setFont(Typeface font) {
        paint.setTypeface(font);
    }

    /**
     * Sets this canvas to use the given font for all drawing.
     */
    public void setFont(Typeface fontFamily, int style) {
        paint.setTypeface(Typeface.create(fontFamily, style));
    }

    /**
     * Sets this canvas to use the given font for all drawing.
     */
    public void setFont(Typeface fontFamily, int style, float size) {
        paint.setTypeface(Typeface.create(fontFamily, style));
        paint.setTextSize(size);
    }

    /**
     * Sets this canvas to use the given font size for all drawing.
     */
    public void setFontSize(float size) {
        paint.setTextSize(size);
    }

    /**
     * Sets this canvas to use the given paint style.
     */
    public void setPaintStyle(Paint.Style style) {
        paint.setStyle(style);
    }


    /// begin drawing methods

    /**
     * Draws the given bitmap image onto this canvas.
     */
    public void drawBitmap(Bitmap image, int x, int y) {
        canvas.drawBitmap(image, x, y, /* paint */ null);
    }

    /**
     * Draws the given bitmap image onto this canvas.
     */
    public void drawImage(Bitmap image, int x, int y) {
        drawBitmap(image, x, y);
    }

    /**
     * Draws the given oval onto this canvas.
     */
    public void drawOval(int x1, int y1, int x2, int y2) {
        drawOval(x1, y1, x2, y2, paint);
    }

    /**
     * Draws the given oval onto this canvas.
     */
    public void drawOval(int x1, int y1, int x2, int y2, Paint paint) {
        canvas.drawOval(new RectF(x1, y1, x2, y2), paint);
    }

    /**
     * Draws the given rectangle onto this canvas.
     */
    public void drawRect(int x1, int y1, int x2, int y2) {
        drawRect(x1, y1, x2, y2, paint);
    }

    /**
     * Draws the given rectangle onto this canvas.
     */
    public void drawRect(int x1, int y1, int x2, int y2, Paint paint) {
        canvas.drawRect(x1, y1, x2, y2, paint);
    }

    /**
     * Draws the given rounded rectangle onto this canvas.
     */
    public void drawRoundRect(int x1, int y1, int x2, int y2, int rx, int ry) {
        drawRoundRect(x1, y1, x2, y2, rx, ry, paint);
    }

    /**
     * Draws the given rounded rectangle onto this canvas.
     */
    public void drawRoundRect(int x1, int y1, int x2, int y2, int rx, int ry, Paint paint) {
        canvas.drawRoundRect(new RectF(x1, y1, x2, y2), rx, ry, paint);
    }

    /**
     * Draws the given string onto this canvas.
     */
    public void drawString(String text, int x, int y) {
        drawText(text, x, y, paint);
    }

    /**
     * Draws the given string onto this canvas.
     */
    public void drawText(String text, int x, int y) {
        drawText(text, x, y, paint);
    }

    /**
     * Draws the given string onto this canvas.
     */
    public void drawText(String text, int x, int y, Paint paint) {
        canvas.drawText(text, x, y, paint);
    }

    /// end drawing methods

    /**
     * Required method of OnTouchListener interface.
     * Override this to handle touch events.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // empty; override me
        return true;
    }

    /**
     * Required method of OnKeyListener interface.
     * Override this to handle key events.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // empty; override me
        return true;
    }

    /**
     * This is a callback that will be called on your canvas every time the animation
     * ticks each frame of animation.
     */
    public void onAnimateTick() {
        // empty; override me
    }

    /**
     * This is called each time there is a tick of animation by the AnimationLoop.
     * Clients should not call this method directly.
     */
    public final void preAnimateTick() {
        animationTickCount++;
    }

    /**
     * Returns the number of frames of animation that have already passed.
     * If animation is not in progress, returns 0.
     */
    public int getAnimationTickCount() {
        return animationTickCount;
    }

    /**
     * This View lifecycle method is overridden to stop any animation from
     * running if this canvas is removed from the screen.
     */
    @Override
    @CallSuper
    protected void onDetachedFromWindow() {
        animationStop();
        super.onDetachedFromWindow();
    }
}

