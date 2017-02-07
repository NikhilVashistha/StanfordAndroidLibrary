package stanford.androidlib.graphics;

/**
 * Specifies the characteristics of a graphical object that supports the
 * <code>setSize</code> and <code>setBounds</code> methods.
 */
public interface GResizable {
    /**
     * Changes the size of this object to the specified width and height.
     *
     * @usage gobj.setSize(width, height);
     * @param width The new width of the object
     * @param height The new height of the object
     */
    public GObject setSize(float width, float height);

    /**
     * Changes the size of this object as specified by the <code>GDimension</code>
     * object.
     *
     * @usage gobj.setSize(size);
     * @param size A <code>GDimension</code> object specifying the new size
     */
    public GObject setSize(GDimension size);

    /**
     * Changes the bounds of this object to the specified values.
     *
     * @usage gobj.setBounds(x, y, width, height);
     * @param x The new x-coordinate for the object
     * @param y The new y-coordinate for the object
     * @param width The new width of the object
     * @param height The new height of the object
     */
    public GObject setBounds(float x, float y, float width, float height);

    /**
     * Changes the bounds of this object to the values from the specified
     * <code>GRectangle</code>.
     *
     * @usage gobj.setBounds(bounds);
     * @param bounds A <code>GRectangle</code> specifying the new bounds
     */
    public GObject setBounds(GRectangle bounds);
}
