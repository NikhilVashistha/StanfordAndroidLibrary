/*
 * @version 2016/02/18
 * - initial version
 */

package stanford.androidlib.graphics;

import android.graphics.Canvas;

/**
 * This class represents a line with an arrowhead at one or both ends.
 * This class is still under development and is currently undocumented.
 * A future release of the library will document the methods fully.
 */
public class GArrow extends GLine {
    private static final double EDGE_LINE_WIDTH = 1;

    private boolean arrowheadStart = false;
    private boolean arrowheadEnd   = true;
    private boolean closed = true;
    private float arrowSize = 10;
    private float angle = (float) (Math.PI / 7.0f);

    /**
     * Constructs an arrow at (0, 0) of length 0.
     */
    public GArrow() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs an arrow from (0, 0) to (x1, y1).
     */
    public GArrow(float x1, float y1) {
        this(0, 0, x1, y1);
    }

    /**
     * Constructs an arrow from (x0, y0) to (x1, y1).
     */
    public GArrow(float x0, float y0, float x1, float y1) {
        super(x0, y0, x1, y1);
    }

    public boolean hasArrowheadStart() {
        return arrowheadStart;
    }

    public boolean hasArrowheadEnd() {
        return arrowheadStart;
    }

    public GArrow setArrowheadStart(boolean value) {
        arrowheadStart = value;
        return this;
    }

    public GArrow setArrowheadEnd(boolean value) {
        arrowheadEnd = value;
        return this;
    }

    @Override
    public void paint(Canvas canvas) {
        super.paint(canvas);

        float p1x = getStartX();
        float p1y = getStartY();
        float p2x = getEndX();
        float p2y = getEndY();
        float dx = p2x - p1x;
        float dy = p2y - p1y;

        if (arrowheadStart) {
            // paint start arrowhead

        }

        if (arrowheadEnd) {
            // TODO: paint end arrowhead
            // canvas.drawOval(new RectF(p2x-3, p2y-3, p2x+3, p2y+3), getPaint());
            float backward = (float) Math.atan2(-dy, -dx);

            float backLX = polarMoveX(p2x, arrowSize, backward + angle);
            float backLY = polarMoveY(p2y, arrowSize, backward + angle);
            float backRX = polarMoveX(p2x, arrowSize, backward - angle);
            float backRY = polarMoveY(p2y, arrowSize, backward - angle);

            if (closed) {
                GPolygon arrowhead = new GPolygon();
                arrowhead.addVertex(p2x, p2y);
                // arrowhead.addEdge(backLX - p2x, backLY - p2y);
                // arrowhead.addEdge(backRX - backLX, backRY - backLY);
                arrowhead.addEdge(backLX - p2x, p2y - backLY);
                arrowhead.addEdge(backRX - backLX, backLY - backRY);
                // arrowhead.setLineWidth(EDGE_LINE_WIDTH);
                arrowhead.setColor(getPaint());
                arrowhead.setFillColor(getPaint());
                arrowhead.paint(canvas);
            } else {
                GLine arrowheadLine1 = new GLine(p2x, p2y, backLX, backLY);
                arrowheadLine1.setColor(getPaint());
                // arrowheadLine1.setLineWidth(EDGE_LINE_WIDTH);
                arrowheadLine1.paint(canvas);

                GLine arrowheadLine2 = new GLine(p2x, p2y, backRX, backRY);
                arrowheadLine2.setColor(getPaint());
                // arrowheadLine2.setLineWidth(EDGE_LINE_WIDTH);
                arrowheadLine2.paint(canvas);
            }

        }
    }

    public float getArrowSize() {
        return arrowSize;
    }

    public GArrow setArrowSize(float arrowSize) {
        this.arrowSize = arrowSize;
        return this;
    }

    public boolean isClosed() {
        return closed;
    }

    public GArrow setClosed(boolean closed) {
        this.closed = closed;
        return this;
    }

    private float polarMoveX(float x, float r, float theta) {
        float dx = (float) (Math.cos(theta) * r);
        float newX = x + dx;
        return newX;
    }

    private float polarMoveY(float y, float r, float theta) {
        float dy = (float) (Math.sin(theta) * -r);
        float newY = y + dy;
        return newY;
    }
}
