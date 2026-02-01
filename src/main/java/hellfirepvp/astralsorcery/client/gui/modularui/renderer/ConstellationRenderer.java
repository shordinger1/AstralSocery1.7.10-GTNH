/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Constellation Renderer - Renders constellations in GUIs
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui.renderer;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

/**
 * Renderer for constellations in GUI views
 * <p>
 * Renders constellation stars and connections
 */
public class ConstellationRenderer extends SkyRenderer {

    /** Constellation color for rendering */
    private Color constellationColor;

    /** Cached constellation positions */
    private Map<String, ConstellationDisplayInfo> cachedPositions = new HashMap<>();

    public ConstellationRenderer(int viewX, int viewY, int viewWidth, int viewHeight) {
        super(viewX, viewY, viewWidth, viewHeight);
        // Default constellation color (cyan/blue)
        this.constellationColor = new Color(100, 200, 255);
    }

    /**
     * Render a constellation at the specified position
     *
     * @param constellationName The constellation to render
     * @param offsetX X offset within view
     * @param offsetY Y offset within view
     * @param scale Scale multiplier
     * @param partialTicks Partial ticks
     */
    public void renderConstellation(String constellationName, int offsetX, int offsetY, float scale, float partialTicks) {
        // Lookup constellation from registry
        hellfirepvp.astralsorcery.common.constellation.IConstellation constellation =
            hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry.getConstellationByName(constellationName);

        if (constellation == null) {
            // Fallback to placeholder if constellation not found
            renderPlaceholderConstellation(offsetX, offsetY, scale, partialTicks);
            return;
        }

        // Set constellation color
        setConstellationColor(constellation.getConstellationColor());

        // Calculate base brightness with flicker
        float baseBrightness = canSeeSky() ? 1.0F : 0.5F;
        float flicker = calculateFlicker(partialTicks, constellationName.hashCode() % 20);
        float brightness = baseBrightness * flicker;

        // Render connections first (so stars appear on top)
        for (hellfirepvp.astralsorcery.common.constellation.star.StarConnection conn : constellation.getStarConnections()) {
            hellfirepvp.astralsorcery.common.constellation.star.StarLocation s1 = conn.from;
            hellfirepvp.astralsorcery.common.constellation.star.StarLocation s2 = conn.to;

            if (s1 != null && s2 != null) {
                // Map 31x31 grid coordinates to GUI coordinates
                float x1 = mapGridToScreen(s1.x, scale) + offsetX;
                float y1 = mapGridToScreen(s1.y, scale) + offsetY;
                float x2 = mapGridToScreen(s2.x, scale) + offsetX;
                float y2 = mapGridToScreen(s2.y, scale) + offsetY;

                renderConnection(x1, y1, x2, y2, brightness * 0.8F, 2F * scale);
            }
        }

        // Render stars
        for (hellfirepvp.astralsorcery.common.constellation.star.StarLocation star : constellation.getStars()) {
            float x = mapGridToScreen(star.x, scale) + offsetX;
            float y = mapGridToScreen(star.y, scale) + offsetY;
            int starSize = (int) (6 * scale);

            renderStar((int) x, (int) y, starSize, brightness);
        }
    }

    /**
     * Map constellation grid coordinate (0-30) to screen coordinate
     * The constellation grid is 31x31, centered at (15, 15)
     *
     * @param gridCoord Grid coordinate (0-30)
     * @param scale Scale multiplier
     * @return Screen coordinate
     */
    private float mapGridToScreen(int gridCoord, float scale) {
        // Map 0-30 to -scale to +scale, then center it
        float normalized = (gridCoord - 15.0F) / 15.0F; // -1.0 to 1.0
        return normalized * 50.0F * scale; // Scale to fit in view
    }

    /**
     * Render constellation connection line
     *
     * @param x1 Start X
     * @param y1 Start Y
     * @param x2 End X
     * @param y2 End Y
     * @param brightness Line brightness
     * @param thickness Line thickness
     */
    public void renderConnection(float x1, float y1, float x2, float y2, float brightness, float thickness) {
        if (brightness <= 0) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set color
        GL11.glColor4f(
            constellationColor.getRed() / 255F,
            constellationColor.getGreen() / 255F,
            constellationColor.getBlue() / 255F,
            brightness
        );

        // Draw line as quad (thickness)
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;

        float nx = -dy / len * thickness * 0.5F;
        float ny = dx / len * thickness * 0.5F;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x1 + nx, y1 + ny);
        GL11.glVertex2f(x1 - nx, y1 - ny);
        GL11.glVertex2f(x2 - nx, y2 - ny);
        GL11.glVertex2f(x2 + nx, y2 + ny);
        GL11.glEnd();

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Render a single constellation star
     *
     * @param x X position
     * @param y Y position
     * @param size Star size
     * @param brightness Star brightness
     */
    public void renderStar(int x, int y, int size, float brightness) {
        if (brightness <= 0) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set color
        GL11.glColor4f(
            constellationColor.getRed() / 255F,
            constellationColor.getGreen() / 255F,
            constellationColor.getBlue() / 255F,
            brightness
        );

        // Draw star as quad
        int halfSize = size / 2;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x - halfSize, y - halfSize);
        GL11.glVertex2f(x + halfSize, y - halfSize);
        GL11.glVertex2f(x + halfSize, y + halfSize);
        GL11.glVertex2f(x - halfSize, y + halfSize);
        GL11.glEnd();

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void render(float partialTicks) {
        // Render background stars
        renderStars(40, partialTicks);

        // Render active constellations based on current world state
        if (mc.theWorld != null) {
            renderVisibleConstellations(partialTicks);
        }
    }

    /**
     * Render all currently visible constellations
     *
     * @param partialTicks Partial ticks for animation
     */
    public void renderVisibleConstellations(float partialTicks) {
        java.util.List<hellfirepvp.astralsorcery.common.constellation.IConstellation> visibleConstellations =
            hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler.getInstance()
                .getVisibleConstellations(mc.theWorld);

        if (visibleConstellations.isEmpty()) {
            return;
        }

        // Calculate layout for multiple constellations
        int count = visibleConstellations.size();
        if (count == 1) {
            // Single constellation: center it
            hellfirepvp.astralsorcery.common.constellation.IConstellation c = visibleConstellations.get(0);
            renderConstellation(c.getUnlocalizedName(), viewWidth / 2, viewHeight / 2, 1.5F, partialTicks);
        } else {
            // Multiple constellations: arrange in grid
            int cols = (int) Math.ceil(Math.sqrt(count));
            int rows = (int) Math.ceil((double) count / cols);

            float cellWidth = viewWidth / cols;
            float cellHeight = viewHeight / rows;

            for (int i = 0; i < count && i < 4; i++) { // Limit to 4 constellations max
                hellfirepvp.astralsorcery.common.constellation.IConstellation c = visibleConstellations.get(i);
                int col = i % cols;
                int row = i / cols;

                float offsetX = (col * cellWidth) + (cellWidth / 2);
                float offsetY = (row * cellHeight) + (cellHeight / 2);
                float scale = 0.8F; // Smaller scale for multiple constellations

                renderConstellation(c.getUnlocalizedName(), (int) offsetX, (int) offsetY, scale, partialTicks);
            }
        }
    }

    /**
     * Render a specific constellation at center of view
     *
     * @param constellation The constellation to render
     * @param partialTicks Partial ticks
     */
    public void renderConstellationCentered(hellfirepvp.astralsorcery.common.constellation.IConstellation constellation,
                                              float partialTicks) {
        if (constellation == null) return;

        renderConstellation(constellation.getUnlocalizedName(), viewWidth / 2, viewHeight / 2, 1.5F, partialTicks);
    }

    /**
     * Render a list of constellations with custom positioning
     *
     * @param constellations List of constellations to render
     * @param positions List of (x, y) positions for each constellation
     * @param scales List of scales for each constellation
     * @param partialTicks Partial ticks
     */
    public void renderConstellations(java.util.List<hellfirepvp.astralsorcery.common.constellation.IConstellation> constellations,
                                      java.util.List<java.awt.Point> positions,
                                      java.util.List<Float> scales,
                                      float partialTicks) {
        if (constellations == null || positions == null || scales == null) return;

        int count = Math.min(constellations.size(), Math.min(positions.size(), scales.size()));

        for (int i = 0; i < count; i++) {
            hellfirepvp.astralsorcery.common.constellation.IConstellation c = constellations.get(i);
            java.awt.Point pos = positions.get(i);
            float scale = scales.get(i);

            renderConstellation(c.getUnlocalizedName(), pos.x, pos.y, scale, partialTicks);
        }
    }

    /**
     * Placeholder constellation rendering (diamond shape)
     * TODO: Replace with actual constellation data
     */
    private void renderPlaceholderConstellation(int offsetX, int offsetY, float scale, float partialTicks) {
        float flicker = calculateFlicker(partialTicks, 0);
        float brightness = (canSeeSky() ? 1 : 0.5F) * flicker;

        // Draw 4 stars in diamond pattern
        int centerX = offsetX + (int) (50 * scale);
        int centerY = offsetY + (int) (50 * scale);

        renderStar(centerX, centerY - 20, 6, brightness);
        renderStar(centerX + 20, centerY, 6, brightness);
        renderStar(centerX, centerY + 20, 6, brightness);
        renderStar(centerX - 20, centerY, 6, brightness);

        // Draw connections
        renderConnection(centerX, centerY - 20, centerX + 20, centerY, brightness * 0.8F, 2F);
        renderConnection(centerX + 20, centerY, centerX, centerY + 20, brightness * 0.8F, 2F);
        renderConnection(centerX, centerY + 20, centerX - 20, centerY, brightness * 0.8F, 2F);
        renderConnection(centerX - 20, centerY, centerX, centerY - 20, brightness * 0.8F, 2F);
    }

    /**
     * Set constellation render color
     */
    public void setConstellationColor(Color color) {
        this.constellationColor = color;
    }

    /**
     * Display info for a constellation in the view
     */
    public static class ConstellationDisplayInfo {
        public final float displayX;
        public final float displayY;
        public final float scale;
        public final long lastUpdate;

        public ConstellationDisplayInfo(float x, float y, float scale) {
            this.displayX = x;
            this.displayY = y;
            this.scale = scale;
            this.lastUpdate = System.currentTimeMillis();
        }
    }

}
