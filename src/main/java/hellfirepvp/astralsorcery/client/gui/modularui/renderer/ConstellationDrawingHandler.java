/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Constellation Drawing Handler - Handles interactive constellation drawing
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui.renderer;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Handler for interactive constellation drawing
 * <p>
 * Manages user mouse input to draw constellation lines
 */
public class ConstellationDrawingHandler {

    /** List of drawn lines */
    private List<DrawnLine> drawnLines = new LinkedList<>();

    /** Current line being drawn */
    private Point lineStart = null;
    private Point lineEnd = null;

    /** Constellation star positions for matching */
    private Map<Point, Rectangle> constellationStars = null;

    /** View bounds */
    private int viewX;
    private int viewY;
    private int viewWidth;
    private int viewHeight;

    public ConstellationDrawingHandler(int viewX, int viewY, int viewWidth, int viewHeight) {
        this.viewX = viewX;
        this.viewY = viewY;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    /**
     * Set the constellation star positions for matching
     *
     * @param stars Map of star positions to their hitboxes
     */
    public void setConstellationStars(Map<Point, Rectangle> stars) {
        this.constellationStars = stars;
    }

    /**
     * Handle mouse click - start drawing a line
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return true if drawing started
     */
    public boolean onMouseClick(int mouseX, int mouseY) {
        if (!isInDrawingArea(mouseX, mouseY)) {
            return false;
        }

        lineStart = new Point(mouseX, mouseY);
        lineEnd = new Point(mouseX, mouseY);
        return true;
    }

    /**
     * Handle mouse drag - update end point of current line
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    public void onMouseDrag(int mouseX, int mouseY) {
        if (lineStart != null) {
            lineEnd = new Point(mouseX, mouseY);
        }
    }

    /**
     * Handle mouse release - complete the line
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @return true if a line was added
     */
    public boolean onMouseRelease(int mouseX, int mouseY) {
        if (lineStart == null) {
            return false;
        }

        lineEnd = new Point(mouseX, mouseY);

        // Check if line is long enough (not just a click)
        double length = lineStart.distance(lineEnd);
        if (length > 2) {
            DrawnLine line = new DrawnLine(
                new Point(lineStart.x - viewX, lineStart.y - viewY),
                new Point(lineEnd.x - viewX, lineEnd.y - viewY)
            );
            drawnLines.add(line);
        }

        // Reset current line
        lineStart = null;
        lineEnd = null;
        return true;
    }

    /**
     * Clear all drawn lines
     */
    public void clearLines() {
        drawnLines.clear();
    }

    /**
     * Get all drawn lines
     */
    public List<DrawnLine> getDrawnLines() {
        return drawnLines;
    }

    /**
     * Check if currently drawing a line
     */
    public boolean isDrawing() {
        return lineStart != null;
    }

    /**
     * Get the current line being drawn (or null if not drawing)
     */
    public DrawnLine getCurrentLine() {
        if (lineStart != null && lineEnd != null) {
            return new DrawnLine(
                new Point(lineStart.x - viewX, lineStart.y - viewY),
                new Point(lineEnd.x - viewX, lineEnd.y - viewY)
            );
        }
        return null;
    }

    /**
     * Check if the drawn lines match a constellation pattern
     *
     * @param expectedConnections Expected constellation connections
     * @return true if the pattern matches
     */
    public boolean matchesConstellation(List<ConstellationConnection> expectedConnections) {
        if (expectedConnections.size() != drawnLines.size()) {
            return false;
        }

        if (constellationStars == null) {
            return false;
        }

        // Check each expected connection
        for (ConstellationConnection expected : expectedConnections) {
            Rectangle fromRect = constellationStars.get(expected.from);
            Rectangle toRect = constellationStars.get(expected.to);

            if (fromRect == null || toRect == null) {
                return false;
            }

            if (!containsMatch(drawnLines, fromRect, toRect)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if any drawn line matches the given star rectangles
     */
    private boolean containsMatch(List<DrawnLine> lines, Rectangle r1, Rectangle r2) {
        for (DrawnLine line : lines) {
            Point start = line.start;
            Point end = line.end;
            // Adjust to absolute coordinates
            start = new Point(start.x + viewX, start.y + viewY);
            end = new Point(end.x + viewX, end.y + viewY);

            if ((r1.contains(start) && r2.contains(end)) ||
                (r2.contains(start) && r1.contains(end))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if point is in drawing area
     */
    private boolean isInDrawingArea(int x, int y) {
        return x >= viewX && x <= viewX + viewWidth &&
               y >= viewY && y <= viewY + viewHeight;
    }

    /**
     * Represents a drawn line
     */
    public static class DrawnLine {
        public final Point start;
        public final Point end;

        public DrawnLine(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Represents a constellation connection to match
     */
    public static class ConstellationConnection {
        public final Point from;
        public final Point to;

        public ConstellationConnection(Point from, Point to) {
            this.from = from;
            this.to = to;
        }
    }

}
