/*
 * @version 2016/02/18
 * - fixed bugs with drawing code (was drawing points before, not Path of lines)
 */

package stanford.androidlib.graphics;

import android.graphics.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The <code>GPolygon</code> class is a graphical object whose appearance consists
 * of a polygon.
 */
public class GPolygon extends GObject implements GFillable, GScalable {
    /**
     * The serialization code for this class.  This value should be incremented
     * whenever you change the structure of this class in an incompatible way,
     * typically by adding a new instance variable.
     */
    static final long serialVersionUID = 1L;

    // private fields
    private float xScale;
    private float yScale;
    private float rotation;
    private VertexList vertices;
    private Path path;
    private boolean cacheValid;
    private boolean complete;

    /**
     * Constructs a new empty polygon at the origin.
     *
     * @usage GPolygon gpoly = new GPolygon();
     */
    public GPolygon() {
        vertices = new VertexList();
        path = new Path();
        clear();
    }

    /**
     * Constructs a new empty polygon at (<code>x</code>, <code>y</code>).
     *
     * @usage GPolygon gpoly = new GPolygon(x, y);
     * @param x The x-coordinate of the origin of the polygon
     * @param y The y-coordinate of the origin of the polygon
     */
    public GPolygon(float x, float y) {
        this();
        setLocation(x, y);
    }

    /**
     * Constructs a new polygon from the specified array of <code>GPoint</code>
     * objects.  The polygon is automatically marked as complete.
     *
     * @usage GPolygon gpoly = new GPolygon(points);
     * @param points An array of <code>GPoint</code> objects specifying the vertices
     */
    public GPolygon(GPoint[] points) {
        this();
        vertices.add(points);
        markAsComplete();
    }

    /**
     * Adds a vertex at (<code>x</code>, <code>y</code>) relative to the polygon origin.
     *
     * @usage gpoly.addVertex(x, y);
     * @param x The x-coordinate of the vertex relative to the polygon origin
     * @param y The y-coordinate of the vertex relative to the polygon origin
     */
    public GPolygon addVertex(float x, float y) {
        if (complete) {
            throw new IllegalStateException("You can't add vertices to a GPolygon that has been "
                    + "marked as complete.");
        }
        vertices.addVertex(x, y);
        return this;
    }

    /**
     * Adds an edge to the polygon whose components are given by the displacements
     * <code>dx</code> and <code>dy</code> from the last vertex.
     *
     * @usage gpoly.addEdge(dx, dy);
     * @param dx The x displacement through which the edge moves
     * @param dy The y displacement through which the edge moves
     */
    public GPolygon addEdge(float dx, float dy) {
        if (complete) {
            throw new IllegalStateException("You can't add edges to a GPolygon that has been "
                    + "marked as complete.");
        }
        vertices.addEdge(dx, dy);
        return this;
    }

    /**
     * Adds an edge to the polygon specified in polar coordinates.  The length of the
     * edge is given by <code>r</code>, and the edge extends in direction <code>theta</code>,
     * measured in degrees counterclockwise from the +x axis.
     *
     * @usage gpoly.addPolarEdge(r, theta);
     * @param r The length of the edge
     * @param theta The angle at which the edge extends measured in degrees
     */
    public final GPolygon addPolarEdge(float r, float theta) {
        if (complete) {
            throw new IllegalStateException("You can't add edges to a GPolygon that has been "
                    + "marked as complete.");
        }
        vertices.addEdge(r * GMath.cosDegrees(theta), -r * GMath.sinDegrees(theta));
        return this;
    }

    /**
     * Adds a series of edges to the polygon that simulates the arc specified by
     * the parameters.  The <i>x</i> and <i>y</i> parameters for the arc bounding
     * box are computed implicitly by figuring out what values would place the
     * current vertex at the starting position.
     *
     * @usage gpoly.addArc(arcWidth, arcHeight, start, sweep);
     * @param arcWidth The width of the oval from which the arc is taken
     * @param arcHeight The height of the oval from which the arc is taken
     * @param start The angle at which the arc begins
     * @param sweep The extent of the arc
     */
    public GPolygon addArc(float arcWidth, float arcHeight, float start, float sweep) {
        if (complete) {
            throw new IllegalStateException("You can't add edges to a GPolygon that has been "
                    + "marked as complete.");
        }
        vertices.addArc(arcWidth, arcHeight, start, sweep);
        return this;
    }

    /**
     * Returns the coordinates of the last vertex added to the polygon, or <code>null</code>
     * if the polygon is empty.
     *
     * @usage GPoint vertex = gpoly.getCurrentPoint();
     * @return The last vertex added to the polygon, or <code>null</code> if empty
     */
    public GPoint getCurrentPoint() {
        return vertices.getCurrentPoint();
    }

    /**
     * Scales the polygon by the scale factors <code>sx</code> and <code>sy</code>.
     *
     * @usage gpoly.scale(sx, sy);
     * @param sx The factor used to scale all coordinates in the x direction
     * @param sy The factor used to scale all coordinates in the y direction
     */
    @Override
    public GObject scale(float sx, float sy) {
        xScale *= sx;
        yScale *= sy;
        return this;
    }

    /**
     * Rotates the polygon around its origin by the angle theta, measured in degrees.
     *
     * @usage gpoly.rotate(theta);
     * @param theta The angle of rotation in degrees counterclockwise
     */
    public GPolygon rotate(float theta) {
        rotation += theta;
        return this;
    }

    /**
     * Returns the bounding box of this object, which is defined to be the
     * smallest rectangle that covers everything drawn by the figure.
     *
     * @usage GRectangle bounds = gpoly.getBounds();
     * @return The bounding box for this object
     */
    public GRectangle getBounds() {
        return vertices.getBounds(getX(), getY(), xScale, yScale, rotation);
    }

    /**
     * Checks to see whether a point is inside the object.
     *
     * @usage if (gpoly.contains(x, y)) . . .
     * @param x The x-coordinate of the point being tested
     * @param y The y-coordinate of the point being tested
     * @return <code>true</code> if the point (<code>x</code>,&nbsp;<code>y</code>) is inside
     *         the object, and <code>false</code> otherwise
     */
    public boolean contains(float x, float y) {
        return vertices.contains((x - getX()) / xScale, (y - getY()) / yScale);
    }

    /**
     * Implements the <code>paint</code> operation for this graphical object.  This method
     * is not called directly by clients.
     * @noshow
     */
    public void paint(Canvas canvas) {
        if (vertices.size() == 0) {
            return;
        }

        // create path
        path.reset();
        GPoint point0 = vertices.get(0);
        path.moveTo(point0.getX(), point0.getY());
        for (int i = 1; i < vertices.size(); i++) {
            GPoint point = vertices.get(i);
            path.lineTo(point.getX(), point.getY());
        }

        if (isFilled()) {
            // fill interior first
            Paint fill = getFillColor();
            canvas.drawPath(path, fill);
        }

        // draw outline second
        canvas.drawPath(path, getPaint());
    }

    /**
     * Recalculates the vertices of the polygon so that they are positioned
     * relative to the geometric center of the object.  This method allows
     * clients to take a polygon drawn using mouse clicks on the screen and
     * then to reformulate it so that it can be displayed relative to its center.
     *
     * @usage gpoly.recenter();
     */
    public GPolygon recenter() {
        vertices.recenter();
        cacheValid = false;
        return this;
    }

    /**
     * Overrides <code>clone</code> in <code>Object</code> to make sure
     * that the vertex list is copied rather than shared.
     * @noshow
     */
    public Object clone() {
        try {
            GPolygon clone = (GPolygon) super.clone();
            clone.vertices = new VertexList(clone.vertices);
            return clone;
        } catch (Exception CloneNotSupportedException) {
            throw new IllegalStateException("Impossible exception");
        }
    }

    /**
     * Overrides <code>repaint</code> in <code>GObject</code> to invalidate the
     * cached polygon.
     * @noshow
     */
    @Override
    public void repaint() {
        cacheValid = false;
        super.repaint();
    }

    /**
     * Calling this method makes it illegal to add or remove vertices from the
     * polygon.  Subclasses can invoke this method to protect the integrity of
     * the structure from changes by the client.
     */
    protected GPolygon markAsComplete() {
        complete = true;
        return this;
    }

/* Protected method: clear() */
    /**
     * Calling this method deletes all vertices from the polygon and resets the
     * scale and rotation factors to the their default values.  Subclasses can
     * use this method to reconstruct a polygon.
     */
    protected GPolygon clear() {
        if (complete) {
            throw new IllegalStateException("You can't clear a GPolygon that has been "
                    + "marked as complete.");
        }
        vertices.clear();
        rotation = 0;
        xScale = 1.0f;
        yScale = 1.0f;
        cacheValid = false;
        return this;
    }

    /**
     * The <code>VertexList</code> class represents a list of vertices.
     */
    private static class VertexList implements Serializable {
        // private fields
        private ArrayList<GPoint> vertices;
        private float cx;
        private float cy;

        /**
         * Creates a new <code>VertexList</code> with no elements.
         */
        public VertexList() {
            vertices = new ArrayList<>();
            cx = 0;
            cy = 0;
        }

        /**
         * Creates a new <code>VertexList</code> that is a clone of the old one.
         */
        public VertexList(VertexList oldList) {
            this();
            for (int i = 0; i < oldList.vertices.size(); i++) {
                vertices.add(oldList.vertices.get(i));
            }
        }

        /**
         * Adds the specified vertex to the end of the list.
         */
        public synchronized void addVertex(float x, float y) {
            cx = x;
            cy = y;
            vertices.add(new GPoint(cx, cy));
        }

        /**
         * Adds the specified edge to the end of the list.
         */
        public synchronized void addEdge(float dx, float dy) {
            cx += dx;
            cy += dy;
            vertices.add(new GPoint(cx, cy));
        }

        /**
         * Adds a series of edges to the polygon that simulates the arc specified by
         * the parameters.  The <i>x</i> and <i>y</i> parameters for the arc bounding
         * box are computed implicitly by figuring out what values would place the
         * current vertex at the starting position.
         */
        public void addArc(float arcWidth, float arcHeight, float start, float sweep) {
            float aspectRatio = arcHeight / arcWidth;
            float rx = arcWidth / 2.0f;
            float ry = arcHeight / 2.0f;
            float x0 = cx - rx * GMath.cosDegrees(start);
            float y0 = cy + ry * GMath.sinDegrees(start);
            if (sweep > 359.99) sweep = 360;
            if (sweep < -359.99) sweep = -360;
            float dt = (float) Math.atan2(1, Math.max(arcWidth, arcHeight));
            int nSteps = (int) (GMath.toRadians(Math.abs(sweep)) / dt);
            dt = GMath.toRadians(sweep) / nSteps;
            float theta = GMath.toRadians(start);
            for (int i = 0; i < nSteps; i++) {
                theta += dt;
                float px = x0 + rx * (float) Math.cos(theta);
                float py = y0 - rx * (float) Math.sin(theta) * aspectRatio;
                addVertex(px, py);
            }
        }

        /**
         * Adds copies of the points to the end of the vertex list.
         */
        public synchronized void add(GPoint[] array) {
            for (GPoint anArray : array) {
                vertices.add(new GPoint(anArray.getX(), anArray.getY()));
            }
        }

        /**
         * Returns the point in this list from the given index.
         */
        public GPoint get(int index) {
            return vertices.get(index);
        }

        /**
         * Removes the specified vertex from the list.
         */
        public synchronized void remove(GPoint vertex) {
            vertices.remove(vertex);
        }

        /**
         * Removes all vertices from the list.
         */
        public synchronized void clear() {
            vertices.clear();
        }

        /**
         * Returns the number of vertices in the list.
         */
        public int size() {
            return vertices.size();
        }

        /**
         * Returns the coordinates of the last vertex added to the polygon, or <code>null</code>
         * if the polygon is empty.
         */
        public GPoint getCurrentPoint() {
            return (vertices.size() == 0) ? null : new GPoint(cx, cy);
        }

        /**
         * Returns the bounding box for the polygon.
         */
        public synchronized GRectangle getBounds(float x0, float y0, float xScale, float yScale, float rotation) {
            int nPoints = vertices.size();
            if (nPoints == 0) return new GRectangle();
            float xMin = 0;
            float xMax = 0;
            float yMin = 0;
            float yMax = 0;
            float sinTheta = GMath.sinDegrees(rotation);
            float cosTheta = GMath.cosDegrees(rotation);
            boolean first = true;
            for (int i = 0; i < vertices.size(); i++) {
                GPoint vertex = vertices.get(i);
                float x = x0 + xScale * (cosTheta * vertex.getX() + sinTheta * vertex.getY());
                float y = y0 + yScale * (cosTheta * vertex.getY() - sinTheta * vertex.getX());
                if (first) {
                    xMin = x;
                    xMax = x;
                    yMin = y;
                    yMax = y;
                    first = false;
                } else {
                    xMin = Math.min(xMin, x);
                    xMax = Math.max(xMax, x);
                    yMin = Math.min(yMin, y);
                    yMax = Math.max(yMax, y);
                }
            }
            return new GRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
        }

        /**
         * Returns <code>true</code> if the polygon described by this
         * <code>VertexList</code> contains the specified point.
         */
        public synchronized boolean contains(float x, float y) {
            int nPoints = vertices.size();
            boolean isContained = false;
            for (int i = 0; i < nPoints; i++) {
                GPoint v1 = vertices.get(i);
                GPoint v2 = vertices.get((i + 1) % nPoints);
                if (((v1.getY() < y) && (v2.getY() >= y)) || ((v2.getY() < y) && (v1.getY() >= y))) {
                    if (v1.getX() + (y - v1.getY()) / (v2.getY() - v1.getY()) * (v2.getX() - v1.getX()) < x) {
                        isContained = !isContained;
                    }
                }
            }
            return isContained;
        }

        /**
         * Recalculates the vertices of the polygon so that they are positioned
         * relative to the geometric center of the object.  This method allows
         * clients to take a polygon drawn using mouse clicks on the screen and
         * then to reformulate it so that it can be displayed relative to its center.
         */
        public void recenter() {
            float xMin = 0;
            float xMax = 0;
            float yMin = 0;
            float yMax = 0;
            boolean first = true;
            for (int i = 0; i < vertices.size(); i++) {
                GPoint vertex = vertices.get(i);
                if (first) {
                    xMin = vertex.getX();
                    xMax = vertex.getX();
                    yMin = vertex.getY();
                    yMax = vertex.getY();
                    first = false;
                } else {
                    xMin = Math.min(xMin, vertex.getX());
                    xMax = Math.max(xMax, vertex.getX());
                    yMin = Math.min(yMin, vertex.getY());
                    yMax = Math.max(yMax, vertex.getY());
                }
            }
            float xc = (xMin + xMax) / 2;
            float yc = (yMin + yMax) / 2;
            for (int i = 0; i < vertices.size(); i++ ) {
                GPoint vertex = vertices.get(i);
                vertex.translate(-xc, -yc);
            }
        }
    }
}
