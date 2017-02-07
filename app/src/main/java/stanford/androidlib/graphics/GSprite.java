/*
 * @version 2016/12/22
 * - added illegal arg / nullness checking to some methods e.g. setSize
 * @version 2016/05/21
 * - fixed minor bugs with setSize, isInBounds
 * @version 2016/02/29
 * - fixed bug with setBitmap(s) size
 * @version 2016/02/17
 * - added rotateVelocity
 * @version 2016/02/11
 * - added get/setBitmaps and related methods
 * - added 'extras' concept and get/set/has/removeExtra
 * @version 2016/02/08
 * - made it extend GObject
 * - canvas bounding methods
 * @version 2016/01/27
 * - initial version
 */

package stanford.androidlib.graphics;

import android.graphics.*;
import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import stanford.androidlib.SimpleCanvas;

/**
 * <p>
 * This class can be useful for creating objects in basic 2D games.
 * It represents a basic in-game entity with a position and size
 * that can move with a given velocity and acceleration around the screen.
 * It can also collide with other sprites.
 * A sprite can be drawn as either a bitmap image, a rectangle, or an oval.
 * </p>
 *
 * <p>
 * Since we cannot possibly think of all possible attributes you would want to
 * store inside your sprite objects, we provide an internal map of key/value
 * pairs called 'extra' properties.
 * You can call getExtra and setExtra to retrieve/store extra data inside any sprite.
 * </p>
 */
public class GSprite extends GObject {
    private static boolean ourDebug = false;
    private static Paint ourDebugColor = null;

    public static void setDebug(boolean debug) {
        if (debug) {
            ourDebugColor = new Paint(GColor.GREEN);
            ourDebugColor.setAlpha(128);
            ourDebugColor.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            ourDebugColor = null;
        }
        ourDebug = debug;
    }

    // private fields (instance variables)
    private RectF rect;
    private RectF collisionRect;
    private GObject shape;

    // images and walk cycle
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private int currentBitmap = 0;
    private int framesPerBitmap = 1;
    private int frameCount = 0;
    private boolean loopBitmaps = true;

    // velocity and acceleration
    private float dx = 0;
    private float dy = 0;
    private float accelerationX = 0;
    private float accelerationY = 0;

    // flags
    private boolean collidable = true;

    // extra properties
    private Map<String, Object> extraProperties = new HashMap<>();

    /**
     * Constructs a new empty sprite at (0, 0).
     */
    public GSprite() {
        this(new GRect(0, 0, 0, 0), 0, 0, 0, 0);
    }

    /**
     * Constructs a new sprite at (0, 0) that displays the given bitmap.
     */
    public GSprite(Bitmap bitmap) {
        this(bitmap, 0, 0);
    }

    /**
     * Constructs a new sprite at (0, 0) that displays the given bitmaps.
     * The list of bitmaps must be non-null and non-empty.
     */
    public GSprite(ArrayList<Bitmap> bitmaps) {
        this(bitmaps.get(0), 0, 0);
        this.bitmaps = new ArrayList<>(bitmaps);
    }

    /**
     * Constructs a new sprite at the given x/y location that displays the given bitmaps.
     * The list of bitmaps must be non-null and non-empty.
     */
    public GSprite(ArrayList<Bitmap> bitmaps, float x, float y) {
        this(bitmaps.get(0), x, y);
        this.bitmaps = new ArrayList<>(bitmaps);
    }

    /**
     * Constructs a new sprite at the given x/y location that displays the given bitmap.
     */
    public GSprite(Bitmap bitmap, float x, float y) {
        this(bitmap, x, y,
                bitmap.getWidth(),
                bitmap.getHeight());
    }

    /**
     * Constructs a new sprite at the given x/y location and size that displays the given bitmap.
     */
    public GSprite(Bitmap bitmap, float x, float y, float width, float height) {
        if (bitmap == null) {
            throw new NullPointerException();
        }
        this.paint = new Paint();
        this.bitmaps.add(bitmap);
        this.rect = new RectF(x, y, x + width, y + height);
        this.collisionRect = new RectF(x, y, x + width, y + height);
    }

    /**
     * Constructs a new sprite at the given x/y location that displays the given graphical object.
     */
    public GSprite(GObject object) {
        this(object, object.getX(), object.getY(), object.getWidth(), object.getHeight());
    }

    /**
     * Constructs a new sprite at the given x/y location that displays a plain
     * rectangle of the given size.
     */
    public GSprite(float x, float y, float width, float height) {
        this(new GRect(x, y, x+width, y+height), x, y, width, height);
    }

    // helper constructor
    private GSprite(GObject object, float x, float y, float width, float height) {
        if (object == null) {
            throw new NullPointerException();
        }
        this.paint = new Paint();
        this.shape = object;
        this.rect = new RectF(x, y, x + width, y + height);
        this.collisionRect = new RectF(x, y, x + width, y + height);
    }

    /**
     * Adds this sprite to the given graphical canvas.
     * A convenience method that just calls the canvas's add(GObject) method.
     */
    @Override
    public GSprite addTo(GCanvas canvas) {
        canvas.add(this);
        return this;
    }

    /**
     * Adds this sprite to the given graphical canvas.
     * A convenience method that just calls the canvas's add(GObject) method.
     */
    public GSprite addTo(GCanvas canvas, float x, float y) {
        canvas.add(this, x, y);
        return this;
    }

    /**
     * Returns true if this sprite collides with the given other sprite.
     * Collision is determined by the collision rectangles of the two sprites.
     * By default this is just the bounding boxes of their bitmaps or GObjects.
     * But if you have set a collision margin, then that margin is used here.
     */
    public boolean collidesWith(GSprite otherSprite) {
        if (otherSprite == null || !collidable || !otherSprite.collidable) {
            return false;
        }
        if (!hasCollisionBoundary() && !otherSprite.hasCollisionBoundary()
                && shape != null && otherSprite.shape != null) {
            return shape.intersects(otherSprite.shape);
        } else {
            return RectF.intersects(collisionRect, otherSprite.collisionRect);
        }
    }

    /**
     * Returns true if this sprite collides with the given other sprite.
     * Collision is determined by the collision rectangles of the two sprites.
     * By default this is just the bounding boxes of their bitmaps or GObjects.
     * But if you have set a collision margin, then that margin is used here.
     */
    public boolean intersects(GSprite otherSprite) {
        return collidesWith(otherSprite);
    }

    /**
     * Returns true if you have set a collision margin.
     */
    public boolean hasCollisionBoundary() {
        return !collisionRect.equals(rect);
    }

    /**
     * Draws this sprite onto the given graphical canvas.
     */
    public void paint(Canvas canvas) {
        if (visible) {
            if (bitmaps != null && bitmaps.size() > currentBitmap) {
                Bitmap bitmap = bitmaps.get(currentBitmap);
                canvas.drawBitmap(bitmap, getX(), getY(), /* paint */ null);
            } else if (shape != null) {
                shape.paint(canvas);
            }
        }
        if (ourDebug) {
            // draw semi-transparent collision rectangle
            canvas.drawRect(collisionRect, ourDebugColor);

            // draw velocity vector if shape is moving
            if (isMoving()) {
                GArrow velocity = new GArrow(
                        getCenterX(), getCenterY(),
                        getCenterX() + dx, getCenterY() + dy);
                velocity.setPaint(ourDebugColor);
                velocity.paint(canvas);
            }
        }
    }

    /**
     * Inverts the sign of the sprite's velocity in the x direction.
     */
    public GSprite flipVelocityX() {
        return setVelocityX(-getVelocityX());
    }

    /**
     * Inverts the sign of the sprite's velocity in the y direction.
     */
    public GSprite flipVelocityY() {
        return setVelocityY(-getVelocityY());
    }

    /**
     * Inverts the sign of the sprite's velocity in both directions.
     */
    public GSprite flipVelocity() {
        flipVelocityX();
        return flipVelocityY();
    }

    /**
     * Rotates this sprite's velocity in x and y dimension by the given angle in degrees, clockwise.
     * For example, passing 90 is a 'right' turn, -90 is a 'left' turn, and 180 is an about-face.
     */
    public GSprite rotateVelocity(float degrees) {
        // convert current velocity to polar
        float radians = (float) Math.toRadians(degrees);

        // round to 4 digits after decimal
        float newDx = (float) (Math.round((dx * Math.cos(radians) - dy * Math.sin(radians)) * 1e4) / 1e4);
        float newDy = (float) (Math.round((dx * Math.sin(radians) + dy * Math.cos(radians)) * 1e4) / 1e4);
        dx = newDx;
        dy = newDy;
        return this;
    }

    /**
     * Returns this sprite's x acceleration; this will be 0 unless you have called setAcceleration.
     */
    public float getAccelerationX() {
        return accelerationX;
    }

    /**
     * Returns this sprite's yacceleration; this will be 0 unless you have called setAcceleration.
     */
    public float getAccelerationY() {
        return accelerationY;
    }

    /**
     * Returns this sprite's leftmost x-coordinate.
     */
    @Override
    public float getX() {
        return rect.left;
    }

    /**
     * Returns this sprite's top y-coordinate.
     */
    @Override
    public float getY() {
        return rect.top;
    }

    /**
     * Returns the sprite's width.
     */
    public float getWidth() {
        return rect.width();
    }

    /**
     * Returns this sprite's height.
     */
    public float getHeight() {
        return rect.height();
    }

    /**
     * Returns this sprite's velocity in the x direction.
     */
    public float getVelocityX() {
        return dx;
    }

    /**
     * Returns this sprite's velocity in the y direction.
     */
    public float getVelocityY() {
        return dy;
    }

    /**
     * Returns this sprite's collision margin in the x direction.
     * This will be 0 unless you have called setCollisionMargin previously.
     * The collision margin can be used to make a smaller collision rectangle
     * for collision detection.
     */
    public float getCollisionMarginX() {
        return collisionRect.left - rect.left;
    }

    /**
     * Returns this sprite's collision margin in the x direction on the left side.
     * This will be 0 unless you have called setCollisionMargin previously.
     * The collision margin can be used to make a smaller collision rectangle
     * for collision detection.
     */
    public float getCollisionMarginLeft() {
        return collisionRect.left - rect.left;
    }

    /**
     * Returns this sprite's collision margin in the x direction on the right side.
     * This will be 0 unless you have called setCollisionMargin previously.
     * The collision margin can be used to make a smaller collision rectangle
     * for collision detection.
     */
    public float getCollisionMarginRight() {
        return rect.right - collisionRect.right;
    }

    /**
     * Returns this sprite's collision margin in the y direction on top.
     * This will be 0 unless you have called setCollisionMargin previously.
     * The collision margin can be used to make a smaller collision rectangle
     * for collision detection.
     */
    public float getCollisionMarginY() {
        return collisionRect.top - rect.top;
    }

    /**
     * Returns this sprite's collision margin in the y direction on top.
     * This will be 0 unless you have called setCollisionMargin previously.
     * The collision margin can be used to make a smaller collision rectangle
     * for collision detection.
     */
    public float getCollisionMarginTop() {
        return collisionRect.top - rect.top;
    }

    /**
     * Returns this sprite's collision margin in the y direction.
     * This will be 0 unless you have called setCollisionMargin previously.
     * The collision margin can be used to make a smaller collision rectangle
     * for collision detection.
     */
    public float getCollisionMarginBottom() {
        return collisionRect.top - rect.top;
    }

    /**
     * Returns the bitmap for this sprite as passed to the constructor.
     * If a GObject was passed instead, this will be null.
     */
    public Bitmap getBitmap() {
        if (bitmaps != null && !bitmaps.isEmpty()) {
            return bitmaps.get(currentBitmap);
        } else {
            return null;
        }
    }

    /**
     * Returns the list of bitmaps for this sprite.
     * If a GObject was passed instead, this will be null.
     */
    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }

    /**
     * Sets the list of bitmaps for this sprite.
     */
    public GSprite setBitmaps(ArrayList<Bitmap> bitmaps) {
        synchronized (this) {
            this.bitmaps = bitmaps;
            this.currentBitmap = 0;
            if (!bitmaps.isEmpty()) {
                setSizeFromBitmap(bitmaps.get(0));
            }
        }
        return this;
    }

    /**
     * Sets the list of bitmaps for this sprite.
     */
    public GSprite setBitmaps(Bitmap... bitmaps) {
        ArrayList<Bitmap> newBitmaps = new ArrayList<>();
        Collections.addAll(newBitmaps, bitmaps);
        synchronized (this) {
            this.currentBitmap = 0;
            this.bitmaps = newBitmaps;
            if (bitmaps.length > 0) {
                setSizeFromBitmap(bitmaps[0]);
            }
        }
        return this;
    }

    /**
     * Returns the index of the bitmap currently being displayed
     * in this sprite's list of bitmaps.
     */
    public int getCurrentBitmapIndex() {
        return this.currentBitmap;
    }

    /**
     * Sets the index of the bitmap currently being displayed
     * in this sprite's list of bitmaps.
     */
    public GSprite setCurrentBitmapIndex(int index) {
        if (index < 0 || index >= this.bitmaps.size()) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        this.currentBitmap = index;
        return this;
    }

    /**
     * Returns the number of frames of animation that each bitmap
     * in the list should be displayed before automatically cycling
     * to the next frame bitmap.
     */
    public int getFramesPerBitmap() {
        return this.framesPerBitmap;
    }

    /**
     * Sets the number of frames of animation that each bitmap
     * in the list should be displayed before automatically cycling
     * to the next frame bitmap.
     */
    public GSprite setFramesPerBitmap(int framesPerBitmap) {
        if (framesPerBitmap <= 0) {
            throw new IllegalArgumentException("must be > 0: " + framesPerBitmap);
        }
        this.framesPerBitmap = framesPerBitmap;
        return this;
    }

    /**
     * Returns whether this sprite can be collided with.
     * This will be true unless you have called setCollidable.
     */
    public boolean isCollidable() {
        return collidable;
    }

    /**
     * Returns whether this sprite has a non-zero velocity in any dimension.
     */
    public boolean isMoving() {
        return dx != 0 || dy != 0;
    }

    /**
     * Returns whether this sprite is within the bounds of its canvas.
     * This will use the canvas last passed to paint() if any.
     */
    public boolean isInBounds() {
        return isInBounds(getCanvasFromGCanvas());
    }

    /**
     * Returns whether this sprite is within the bounds of the given canvas.
     */
    public boolean isInBounds(Canvas canvas) {
        return canvas != null
                && isInBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Returns whether this sprite is within the bounds of the given rectangle.
     */
    public boolean isInBounds(RectF rect) {
        return rect != null
                && isInBounds(rect.left, rect.top, rect.right, rect.bottom);
    }

    /**
     * Returns whether this sprite is within the bounds of the given x/y area.
     */
    public boolean isInBounds(float leftX, float topY, float rightX, float bottomY) {
        return isInBoundsHorizontal(leftX, rightX) && isInBoundsVertical(topY, bottomY);
    }

    /**
     * Returns whether this sprite is within the horizontal (x) bounds of its canvas.
     * This will use the canvas last passed to paint() if any.
     */
    public boolean isInBoundsHorizontal() {
        return isInBoundsHorizontal(getCanvasFromGCanvas());
    }

    /**
     * Returns whether this sprite is within the horizontal (x) bounds of the given canvas.
     */
    public boolean isInBoundsHorizontal(Canvas canvas) {
        return canvas != null && isInBoundsHorizontal(0, canvas.getWidth());
    }

    /**
     * Returns whether this sprite is within the horizontal (x) bounds of the given rectangle.
     */
    public boolean isInBoundsHorizontal(RectF rect) {
        return rect != null && isInBoundsHorizontal(rect.left, rect.right);
    }

    /**
     * Returns whether this sprite is within the horizontal (x) bounds of the given x area.
     */
    public boolean isInBoundsHorizontal(float leftX, float rightX) {
        return rect.left >= leftX && rect.right < rightX;
    }

    /**
     * Returns whether this sprite is within the vertical (y) bounds of its canvas.
     * This will use the canvas last passed to paint() if any.
     */
    public boolean isInBoundsVertical() {
        return isInBoundsVertical(getCanvasFromGCanvas());
    }

    /**
     * Returns whether this sprite is within the vertical (y) bounds of the given canvas.
     */
    public boolean isInBoundsVertical(Canvas canvas) {
        return canvas != null && isInBoundsVertical(0, canvas.getHeight());
    }

    /**
     * Returns whether this sprite is within the vertical (y) bounds of the given rectangle.
     */
    public boolean isInBoundsVertical(RectF rect) {
        return rect != null && isInBoundsVertical(rect.top, rect.bottom);
    }

    /**
     * Returns whether this sprite is within the vertical (y) bounds of the given y area.
     */
    public boolean isInBoundsVertical(float topY, float bottomY) {
        return rect.top >= topY && rect.bottom < bottomY;
    }

    /**
     * Moves this sprite so that it is within the bounds of its canvas.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top/left edges if needed.
     * This will use the canvas last passed to paint() if any.
     */
    public GSprite bound() {
        return bound(getCanvasFromGCanvas());
    }

    /**
     * Moves this sprite so that it is within the bounds of the given canvas.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top/left edges if needed.
     */
    public GSprite bound(Canvas canvas) {
        if (canvas != null) {
            bound(0, 0, canvas.getWidth(), canvas.getHeight());
        }
        return this;
    }

    /**
     * Moves this sprite so that it is within the bounds of the given rectangle.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top/left edges if needed.
     */
    public GSprite bound(RectF rect) {
        if (rect != null) {
            bound(rect.left, rect.top, rect.right, rect.bottom);
        }
        return this;
    }

    /**
     * Moves this sprite so that it is within the bounds of the given x/y area.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top/left edges if needed.
     */
    public GSprite bound(float leftX, float topY, float rightX, float bottomY) {
        boundHorizontal(leftX, rightX);
        return boundVertical(topY, bottomY);
    }

    /**
     * Moves this sprite so that it is within the horizontal (x) bounds of its canvas.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the left edge if needed.
     */
    public GSprite boundHorizontal() {
        return boundHorizontal(getCanvasFromGCanvas());
    }

    /**
     * Moves this sprite so that it is within the horizontal (x) bounds of the given canvas.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the left edge if needed.
     */
    public GSprite boundHorizontal(Canvas canvas) {
        if (canvas != null) {
            boundHorizontal(0, canvas.getWidth());
        }
        return this;
    }

    /**
     * Moves this sprite so that it is within the horizontal (x) bounds of the given rectangle.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the left edge if needed.
     */
    public GSprite boundHorizontal(RectF rect) {
        if (rect != null) {
            boundHorizontal(rect.left, rect.right);
        }
        return this;
    }

    /**
     * Moves this sprite so that it is within the horizontal (x) bounds of the given x area.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the left edge if needed.
     */
    public GSprite boundHorizontal(float leftX, float rightX) {
        if (rect.right >= rightX) {
            setRightX(rightX - Math.ulp(rightX));
        }
        if (rect.left < leftX) {
            setX(leftX);
        }
        return this;
    }

    /**
     * Moves this sprite so that it is within the vertical (y) bounds of its canvas.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top edge if needed.
     */
    public GSprite boundVertical() {
        return boundVertical(getCanvasFromGCanvas());
    }

    /**
     * Moves this sprite so that it is within the vertical (y) bounds of the given canvas.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top edge if needed.
     */
    public GSprite boundVertical(Canvas canvas) {
        if (canvas != null) {
            boundVertical(0, canvas.getHeight());
        }
        return this;
    }

    /**
     * Moves this sprite so that it is within the vertical (y) bounds of the given rectangle.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top edge if needed.
     */
    public GSprite boundVertical(RectF rect) {
        if (rect != null) {
            boundVertical(rect.top, rect.bottom);
        }
        return this;
    }

    /**
     * Moves this sprite so that it is within the vertical (y) bounds of the given y area.
     * If it is outside the bounds on a given edge, it will be moved to that edge,
     * favoring the top edge if needed.
     */
    public GSprite boundVertical(float topY, float bottomY) {
        if (rect.bottom >= bottomY) {
            setBottomY(bottomY - Math.ulp(bottomY));
        }
        if (rect.top < topY) {
            setY(topY);
        }
        return this;
    }

    /**
     * Sets the x acceleration of this sprite.
     * Acceleration will be applied on each tick of animation, increasing or decreasing
     * the sprite's velocity.
     */
    public GSprite setAccelerationX(float accelerationX) {
        this.accelerationX = accelerationX;
        return this;
    }

    /**
     * Sets the y acceleration of this sprite.
     * Acceleration will be applied on each tick of animation, increasing or decreasing
     * the sprite's velocity.
     */
    public GSprite setAccelerationY(float accelerationY) {
        this.accelerationY = accelerationY;
        return this;
    }

    /**
     * Sets the x and y acceleration of this sprite.
     * Acceleration will be applied on each tick of animation, increasing or decreasing
     * the sprite's velocity.
     */
    public GSprite setAcceleration(float accelerationX, float accelerationY) {
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        return this;
    }

    /**
     * Sets a collision margin for this sprite.
     * Collision is determined by the collision rectangles of the two sprites.
     * By default this is just the bounding boxes of their bitmaps or GObjects.
     * But if you have set a collision margin, then that margin is used here.
     * For example, on a 20x20 pixel sprite, setting a collision margin of 5px
     * would mean that only the innermost 10x10 pixels would count as a collision.
     */
    public GSprite setCollisionMargin(float px) {
        return setCollisionMargin(
                /* left */   px,
                /* top */    px,
                /* right */  px,
                /* bottom */ px);
    }

    /**
     * Sets a collision margin for this sprite on all four sides.
     * Collision is determined by the collision rectangles of the two sprites.
     * By default this is just the bounding boxes of their bitmaps or GObjects.
     * But if you have set a collision margin, then that margin is used here.
     * For example, on a 20x20 pixel sprite, setting a collision margin of 4px and 6px
     * would mean that only the innermost 12x8 pixels would count as a collision.
     */
    public GSprite setCollisionMargin(float pxX, float pxY) {
        return setCollisionMargin(
                /* left */   pxX,
                /* top */    pxY,
                /* right */  pxX,
                /* bottom */ pxY);
    }

    /**
     * Sets a collision margin for this sprite on all four sides.
     * Collision is determined by the collision rectangles of the two sprites.
     * By default this is just the bounding boxes of their bitmaps or GObjects.
     * But if you have set a collision margin, then that margin is used here.
     * For example, on a 20x20 pixel sprite, setting a collision margin of 4px and 6px
     * would mean that only the innermost 12x8 pixels would count as a collision.
     */
    public GSprite setCollisionMargin(float pxLeft, float pxTop, float pxRight, float pxBottom) {
        collisionRect.left = rect.left + pxLeft;
        collisionRect.top = rect.top + pxTop;
        collisionRect.right = rect.right - pxRight;
        collisionRect.bottom = rect.bottom - pxBottom;
        if (collisionRect.left > collisionRect.right || collisionRect.top > collisionRect.bottom) {
            throw new IllegalArgumentException("Collision margin too large"
                    + " (left=" + pxLeft + " top=" + pxTop + " right=" + pxRight + " bottom=" + pxBottom + ")"
                    + "; exceeds sprite size");
        }
        return this;
    }

    /**
     * Sets a collision margin for this sprite on the left and right sides.
     */
    public GSprite setCollisionMarginX(float pxX) {
        return setCollisionMargin(
                /* left */   pxX,
                /* top */    getCollisionMarginTop(),
                /* right */  pxX,
                /* bottom */ getCollisionMarginBottom());
    }

    /**
     * Sets a collision margin for this sprite on the left side only.
     */
    public GSprite setCollisionMarginLeft(float pxLeft) {
        return setCollisionMarginX(pxLeft);
    }

    /**
     * Sets a collision margin for this sprite on the right side only.
     */
    public GSprite setCollisionMarginRight(float pxRight) {
        return setCollisionMargin(
                /* left */   getCollisionMarginLeft(),
                /* top */    getCollisionMarginTop(),
                /* right */  pxRight,
                /* bottom */ getCollisionMarginBottom());
    }

    /**
     * Sets a collision margin for this sprite on the left and right sides.
     */
    public GSprite setCollisionMarginX(float pxLeft, float pxRight) {
        return setCollisionMargin(
                /* left */   pxLeft,
                /* top */    getCollisionMarginTop(),
                /* right */  pxRight,
                /* bottom */ getCollisionMarginBottom());
    }

    /**
     * Sets a collision margin for this sprite on the top and bottom sides.
     */
    public GSprite setCollisionMarginY(float pxY) {
        return setCollisionMarginY(pxY, pxY);
    }

    /**
     * Sets a collision margin for this sprite on the top and bottom sides.
     */
    public GSprite setCollisionMarginY(float pxTop, float pxBottom) {
        return setCollisionMargin(
                /* left */   getCollisionMarginLeft(),
                /* top */    pxTop,
                /* right */  getCollisionMarginRight(),
                /* bottom */ pxBottom);
    }

    /**
     * Sets a collision margin for this sprite on the top side.
     */
    public GSprite setCollisionMarginTop(float pxTop) {
        return setCollisionMarginY(pxTop);
    }

    /**
     * Sets a collision margin for this sprite on the bottom side.
     */
    public GSprite setCollisionMarginBottom(float pxBottom) {
        return setCollisionMargin(
                /* left */   getCollisionMarginLeft(),
                /* top */    getCollisionMarginTop(),
                /* right */  getCollisionMarginRight(),
                /* bottom */ pxBottom);
    }

    /**
     * Sets this sprite to use the given bitmap to draw itself.
     * If the sprite has both a bitmap and a GObject set, the bitmap will take predence
     * and will be drawn.
     * If the sprite uses a cycle of multiple bitmaps, use setBitmaps instead.
     */
    public GSprite setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException();
        }
        ArrayList<Bitmap> newBitmaps = new ArrayList<>();
        newBitmaps.add(bitmap);
        synchronized (this) {
            currentBitmap = 0;
            bitmaps = newBitmaps;
            setSizeFromBitmap(bitmap);
        }
        return this;
    }

    /**
     * Sets whether it is possible to collide with this sprite.
     * If false, collidesWith will always return false.
     */
    public GSprite setCollidable(boolean collidable) {
        this.collidable = collidable;
        return this;
    }

    /**
     * Moves this sprite to the given x/y location.
     */
    @Override
    public GSprite setLocation(float x, float y) {
        super.setLocation(x, y);
        float mX = getCollisionMarginX();
        float mY = getCollisionMarginY();
        rect.offsetTo(x, y);
        collisionRect.offsetTo(x + mX, y + mY);
        if (shape != null) {
            shape.setLocation(x, y);
        }
        return this;
    }

    /**
     * Sets this sprite to use the given size.
     */
    @Override
    public GSprite setSize(float width, float height) {
        super.setSize(width, height);
        float marginLeft = getCollisionMarginLeft();
        float marginTop = getCollisionMarginTop();
        float marginRight = getCollisionMarginRight();
        float marginBottom = getCollisionMarginBottom();
        rect.set(rect.left, rect.top, rect.left + width, rect.top + height);
        return setCollisionMargin(marginLeft, marginTop, marginRight, marginBottom);
    }

    /**
     * Sets this sprite to move with the given velocity.
     * Every time update() is called, its x/y position will change by
     * the given dx and dy amounts.
     */
    public GSprite setVelocity(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
        return this;
    }

    /**
     * Sets this sprite to move with the given velocity in the x direction.
     * Every time update() is called, its x position will change by
     * the given dx amount.
     * dy is unchanged by this call.
     */
    public GSprite setVelocityX(float dx) {
        this.dx = dx;
        return this;
    }

    /**
     * Sets this sprite to move with the given velocity in the y direction.
     * Every time update() is called, its y position will change by
     * the given dy amount.
     * dx is unchanged by this call.
     */
    public GSprite setVelocityY(float dy) {
        this.dy = dy;
        return this;
    }

    /**
     * Returns the extra property inside this sprite with the given name.
     * If the given extra property has not been previously set inside this sprite, returns null.
     * The sprite's internal map of extra properties is meant as a catch-all
     * to allow you to stuff arbitrary data inside a sprite that is useful
     * for your particular game.
     * The return type here is 'T' so you don't need to typecast.
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtra(String name) {
        return (T) extraProperties.get(name);
    }

    /**
     * Returns all extras in this sprite as a Bundle.
     * This is a deep copy; modifying the bundle does not modify this sprite.
     */
    public Bundle getExtras() {
        Bundle bundle = new Bundle();
        for (String name : extraProperties.keySet()) {
            bundle.putSerializable(name, (Serializable) extraProperties.get(name));
        }
        return bundle;
    }

    /**
     * Adds all extras from the given Bundle into this Sprite.
     * Does not clear any previously existing extras.
     * Makes a deep copy.
     */
    public void putExtras(Bundle bundle) {
        for (String key : bundle.keySet()) {
            extraProperties.put(key, bundle.getSerializable(key));
        }
    }

    /**
     * Returns whether this sprite has the given extra property inside it.
     */
    public boolean hasExtra(String name) {
        return extraProperties.containsKey(name);
    }

    /**
     * Removes the extra property inside this sprite with the given name.
     * The sprite's internal map of extra properties is meant as a catch-all
     * to allow you to stuff arbitrary data inside a sprite that is useful
     * for your particular game.
     */
    public GSprite removeExtra(String name) {
        extraProperties.remove(name);
        return this;
    }

    /**
     * Removes all extra properties inside this sprite.
     */
    public GSprite clearExtras() {
        extraProperties.clear();
        return this;
    }

    /**
     * Sets the extra property inside this sprite with the given name.
     * The sprite's internal map of extra properties is meant as a catch-all
     * to allow you to stuff arbitrary data inside a sprite that is useful
     * for your particular game.
     */
    public GSprite setExtra(String name, Object value) {
        extraProperties.put(name, value);
        return this;
    }

    /**
     * Halts the object, setting its velocity and acceleration to 0.
     */
    public GSprite stop() {
        setAcceleration(0, 0);
        setVelocity(0, 0);
        return this;
    }

    /**
     * Returns a string representation of this sprite for debugging,
     * including its GObject if any, and its collision rectangle.
     */
    public String toString() {
        return "GSprite{shape=" + shape + ", collRect=" + collisionRect + "}";
    }

    /**
     * Called by SimpleCanvas every time the animation ticks.
     * Moves the sprite by dx and dy and applies acceleration.
     */
    public void update() {
        rect.offset(dx, dy);
        collisionRect.offset(dx, dy);
        if (shape != null) {
            shape.translate(dx, dy);
        }
        dx += accelerationX;
        dy += accelerationY;

        // animate bitmap if necessary
        if (bitmaps != null && !bitmaps.isEmpty()) {
            synchronized (this) {
                frameCount++;
                if (frameCount % framesPerBitmap == 0) {
                    // if loopBitmaps flag is turned off,
                    // stay stopped at the last bitmap when sequence ends
                    if (currentBitmap != bitmaps.size() - 1 || loopBitmaps) {
                        currentBitmap = (currentBitmap + 1) % bitmaps.size();
                    }
                }
            }
        }
    }

    /**
     * Sets whether the sequence of bitmaps (as passed to the constructor or setBitmaps)
     * should loop back to the first one after it ends.
     * Default true.
     */
    public GSprite setLoopBitmaps(boolean loop) {
        this.loopBitmaps = loop;
        return this;
    }

    /**
     * Returns whether the sequence of bitmaps (as passed to the constructor or setBitmaps)
     * should loop back to the first one after it ends.
     * Default true.
     */
    public boolean isLoopBitmaps() {
        return loopBitmaps;
    }

    // extracts the canvas out of a simplecanvas object
    private Canvas getCanvasFromGCanvas() {
        SimpleCanvas simpleCanvas = getGCanvas();
        if (simpleCanvas == null) {
            throw new IllegalStateException("you must add this object to a GCanvas first");
        }
        return simpleCanvas.getCanvas();
    }

    private void setSizeFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            setSize(bitmap.getWidth(), bitmap.getHeight());
        }
    }
}
