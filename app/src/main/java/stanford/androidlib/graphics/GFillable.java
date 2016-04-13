package stanford.androidlib.graphics;

/* Interface: GFillable */

import android.graphics.Paint;

/**
 * Specifies the characteristics of a graphical object that supports filling.
 */
public interface GFillable {

/* Method: setFilled(fill) */
    /**
     * Sets whether this object is filled.
     *
     * @usage gobj.setFilled(fill);
     * @param fill <code>true</code> if the object should be filled, <code>false</code> for an outline
     */
    public void setFilled(boolean fill);

/* Method: isFilled() */
    /**
     * Returns whether this object is filled.
     *
     * @usage if (gobj.isFilled()) . . .
     * @return The color used to display the object
     */
    public boolean isFilled();

/* Method: setFillColor(color) */
    /**
     * Sets the color used to display the filled region of this object.
     *
     * @usage gobj.setFillColor(color);
     * @param color The color used to display the filled region of this object
     */
    public void setFillColor(Paint color);

/* Method: getFillColor() */
    /**
     * Returns the color used to display the filled region of this object.  If
     * none has been set, <code>getFillColor</code> returns the color of the
     * object.
     *
     * @usage Color color = gobj.getFillColor();
     * @return The color used to display the filled region of this object
     */
    public Paint getFillColor();

}
