package stanford.androidlib.graphics;

import android.graphics.Canvas;
import java.util.*;

/**
 * This class defines a graphical object that consists of a collection
 * of other graphical objects.  Once assembled, the internal objects
 * can be manipulated as a unit.
 */
public class GCompound extends GObject implements GScalable, Iterable<GObject> {
    // private fields
    private boolean complete;
    private List<GObject> contents;
    private GObject lastObject;
    private GObject dragObject;

    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>GCompound</code> object with no internal components.
     *
     * @usage GCompound gcomp = new GCompound();
     */
    public GCompound() {
        contents = new ArrayList<>();
        complete = false;
    }

    /**
     * Adds a new graphical object to this <code>GCompound</code>.
     *
     * @usage gcomp.add(gobj);
     * @param gobj The graphical object to add
     */
    public void add(GObject gobj) {
        if (complete) {
            throw new IllegalStateException("You can't add objects to a GCompound that has been "
                    + "marked as complete.");
        }
        contents.add(gobj);
        repaint();
    }

    /**
     * Adds the graphical object to this canvas and sets its location
     * to the point (<code>x</code>,&nbsp;<code>y</code>).
     *
     * @usage gc.add(gobj, x, y);
     * @param gobj The graphical object to add
     * @param x The new x-coordinate for the object
     * @param y The new y-coordinate for the object
     */
    public final void add(GObject gobj, float x, float y) {
        gobj.setLocation(x, y);
        add(gobj);
    }

    /**
     * Adds the graphical object to this canvas and sets its location to the specified point.
     *
     * @usage gc.add(gobj, pt);
     * @param gobj The graphical object to add
     * @param pt A <code>GPoint</code> object giving the coordinates of the point
     */
    public final void add(GObject gobj, GPoint pt) {
        add(gobj, pt.getX(), pt.getY());
    }

    /**
     * Removes a graphical object from this <code>GCompound</code>.
     *
     * @usage gcomp.remove(gobj);
     * @param gobj The graphical object to remove
     */
    public void remove(GObject gobj) {
        if (complete) {
            throw new IllegalStateException("You can't remove objects from a GCompound that has been "
                    + "marked as complete.");
        }
        contents.remove(gobj);
        repaint();
    }

    /**
     * Removes all graphical objects from this <code>GCompound</code>.
     *
     * @usage gcomp.removeAll();
     */
    public void removeAll() {
        if (complete) {
            throw new IllegalStateException("You can't remove objects from a GCompound that has been "
                    + "marked as complete.");
        }
        contents.clear();
        repaint();
    }

    /**
     * Returns the number of graphical objects stored in this container.
     *
     * @usage int n = gcomp.getElementCount();
     * @return The number of graphical objects in this container
     */
    public int getElementCount() {
        return contents.size();
    }

    /**
     * Returns the graphical object at the specified index, numbering from back
     * to front in the the <i>z</i> dimension.
     *
     * @usage GObject gobj = gcomp.getElement(index);
     * @param index The index of the component to return
     * @return The graphical object at the specified index
     */
    public GObject getElement(int index) {
        return contents.get(index);
    }

    /**
     * Returns the topmost graphical object that contains the point
     * (<code>x</code>, <code>y</code>), or <code>null</code> if no such
     * object exists.  Note that these coordinates are relative to the
     * location of the compound object and not to the canvas in which
     * it is displayed.
     *
     * @usage GObject gobj = gcomp.getElementAt(x, y);
     * @param x The x-coordinate of the point being tested
     * @param y The y-coordinate of the point being tested
     * @return The graphical object at the specified location, or <code>null</code>
     *         if no such object exists
     */
    public GObject getElementAt(float x, float y) {
        for (GObject obj : contents) {
            if (obj.contains(x - getX(), y - getY())) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Returns the topmost graphical object that contains the specified point,
     * or <code>null</code> if no such object exists.
     *
     * @usage GObject gobj = gc.getElementAt(pt);
     * @param pt The coordinates being tested
     * @return The graphical object at the specified location, or <code>null</code>
     *         if no such object exists
     */
    public final GObject getElementAt(GPoint pt) {
        return getElementAt(pt.getX(), pt.getY());
    }

    /**
     * Returns an <code>Iterator</code> that cycles through the elements within
     * this container in the default direction, which is from back to front.
     * You can also run the iterator in the opposite direction by using the
     * <a href="#iterator(int)"><code>iterator</code></a><code>(</code><font
     * size=-1><i>direction</i></font><code>)</code> form of this method.
     *
     * @usage Iterator&lt;GObject&gt; i = gc.iterator();
     * @return An <code>Iterator</code> ranging over the elements of the
     *         container from back to front
     */
    public Iterator<GObject> iterator() {
        return contents.iterator();
    }

    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @noshow
     */
    public void paint(Canvas canvas) {
        canvas.translate(GMath.round(getX()), GMath.round(getY()));
        for (GObject obj : contents) {
            obj.paint(canvas);
        }
        canvas.translate(-GMath.round(getX()), -GMath.round(getY()));
    }

    /**
     * Scales every object contained in this compound by the scale factors
     * <code>sx</code> and <code>sy</code>.  Automatic repaint is turned off
     * during the scaling operation so that at most one repaint is performed.
     *
     * @usage gcomp.scale(sx, sy);
     * @param sx The factor used to scale all coordinates in the x direction
     * @param sy The factor used to scale all coordinates in the y direction
     */
    @Override
    public void scale(float sx, float sy) {
        for (int i = getElementCount() - 1; i >= 0; i--) {
            GObject gobj = getElement(i);
            gobj.setLocation(sx * gobj.getX(), sy * gobj.getY());
            if (gobj instanceof GScalable) {
                ((GScalable) gobj).scale(sx, sy);
            }
        }
        repaint();
    }

    /**
     * Returns the bounding rectangle for this compound object, which consists of
     * the union of the bounding rectangles for each of the components.
     *
     * @usage GRectangle bounds = gcomp.getBounds();
     * @return A <code>GRectangle</code> that bounds the components of this object
     */
    @Override
    public GRectangle getBounds() {
        GRectangle bounds = new GRectangle();
        for (GObject obj : contents) {
            bounds = bounds.union(obj.getBounds());
        }
        bounds.translate(getX(), getY());
        return bounds;
    }

    /**
     * Checks to see whether a point is "inside" the compound, which means that it is
     * inside one of the components.
     *
     * @usage if (gcomp.contains(x, y)) . . .
     * @param x The x-coordinate of the point being tested
     * @param y The y-coordinate of the point being tested
     * @return <code>true</code> if the point (<code>x</code>,&nbsp;<code>y</code>) is inside
     *         the compound, and <code>false</code> otherwise
     */
    @Override
    public boolean contains(float x, float y) {
        return getElementAt(x, y) != null;
    }

///* Method: getCanvasPoint(localPoint) */
//    /**
//     * Converts the location of the specified point in this compound to
//     * the corresponding point in the enclosing canvas.
//     *
//     * @usage canvasPoint = gcomp.getCanvasPoint(localPoint);
//     * @param localPoint The coordinates in the space of the compound
//     * @return The coordinates in the space of the enclosing <code>GCanvas</code>
//     */
//    public final GPoint getCanvasPoint(GPoint localPoint) {
//        return getCanvasPoint(localPoint.getX(), localPoint.getY());
//    }

///* Method: getCanvasPoint(x, y) */
//    /**
//     * Converts the location of the specified point in this compound to
//     * the corresponding point in the enclosing canvas.
//     *
//     * @usage canvasPoint = gcomp.getCanvasPoint(x, y);
//     * @param x The x coordinate in the space of the compound
//     * @param y The y coordinate in the space of the compound
//     * @return The coordinates in the space of the enclosing <code>GCanvas</code>
//     */
//    public GPoint getCanvasPoint(float x, float y) {
//        for (GContainer c = this; c instanceof GCompound; ) {
//            GCompound comp = (GCompound) c;
//            x += comp.getX();
//            y += comp.getY();
//            c = comp.getParent();
//        }
//        return new GPoint(x, y);
//    }

///* Method: getLocalPoint(canvasPoint) */
//    /**
//     * Converts the location of the specified point on the enclosing canvas
//     * to the corresponding point in the space of this compound.
//     *
//     * @usage localPoint = gcomp.getLocalPoint(canvasPoint);
//     * @param canvasPoint The coordinates in the space of the enclosing <code>GCanvas</code>
//     * @return The coordinates in the space of the compound
//     */
//    public final GPoint getLocalPoint(GPoint canvasPoint) {
//        return getLocalPoint(canvasPoint.getX(), canvasPoint.getY());
//    }

///* Method: getLocalPoint(x, y) */
//    /**
//     * Converts the specified point on the enclosing canvas to the
//     * corresponding point in the space of this compound.
//     *
//     * @usage localPoint = gcomp.getCanvasPoint(x, y);
//     * @param x The x coordinate in the space of the space of the enclosing <code>GCanvas</code>
//     * @param y The y coordinate in the space of the space of the enclosing <code>GCanvas</code>
//     * @return The coordinates in the space of the compound
//     */
//    public GPoint getLocalPoint(float x, float y) {
//        for (GContainer c = this; c instanceof GCompound; ) {
//            GCompound comp = (GCompound) c;
//            x -= comp.getX();
//            y -= comp.getY();
//            c = comp.getParent();
//        }
//        return new GPoint(x, y);
//    }

    /**
     * Calling this method makes it illegal to add or remove elements from the
     * compound object.  Subclasses can invoke this method to protect the
     * integrity of the structure from changes by the client.
     *
     * @usage gcomp.markAsComplete();
     */
    public void markAsComplete() {
        complete = true;
    }

    /**
     * Implements the <code>sendToFront</code> function from the <code>GContainer</code>
     * interface.  Clients should not be calling this method, but the semantics of
     * interfaces forces it to be exported.
     * @noshow
     */
    protected void sendToFront(GObject gobj) {
        for (GObject obj : contents) {
            obj.sendToFront();
        }
        repaint();
    }

    /**
     * Implements the <code>sendToBack</code> function from the <code>GContainer</code>
     * interface.  Clients should not be calling this method, but the semantics of
     * interfaces forces it to be exported.
     * @noshow
     */
    protected void sendToBack(GObject gobj) {
        for (GObject obj : contents) {
            obj.sendToBack();
        }
        repaint();
    }

    /**
     * Implements the <code>sendForward</code> function from the <code>GContainer</code>
     * interface.  Clients should not be calling this method, but the semantics of
     * interfaces forces it to be exported.
     * @noshow
     */
    protected void sendForward(GObject gobj) {
        for (GObject obj : contents) {
            obj.sendForward();
        }
        repaint();
    }

    /**
     * Implements the <code>sendBackward</code> function from the <code>GContainer</code>
     * interface.  Clients should not be calling this method, but the semantics of
     * interfaces forces it to be exported.
     * @noshow
     */
    protected void sendBackward(GObject gobj) {
        for (GObject obj : contents) {
            obj.sendBackward();
        }
        repaint();
    }
}
