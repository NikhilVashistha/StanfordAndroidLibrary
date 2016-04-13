package stanford.androidlib.graphics;

/* Interface: GScalable */
/**
 * Specifies the characteristics of a graphical object that supports the
 * <code>scale</code> method.
 */
public interface GScalable {

/* Method: scale(sx, sy) */
    /**
     * Scales the object on the screen by the scale factors <code>sx</code> and <code>sy</code>.
     *
     * @usage gobj.scale(sx, sy);
     * @param sx The factor used to scale all coordinates in the x direction
     * @param sy The factor used to scale all coordinates in the y direction
     */
    public void scale(float sx, float sy);

/* Method: scale(sf) */
    /**
     * Scales the object on the screen by the scale factor <code>sf</code>, which applies
     * in both dimensions.
     *
     * @usage gobj.scale(sf);
     * @param sf The factor used to scale all coordinates in both dimensions
     */
    public void scale(float sf);

}
