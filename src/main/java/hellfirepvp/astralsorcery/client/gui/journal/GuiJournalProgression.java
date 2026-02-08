/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Journal Progression GUI - Constellation tree viewer
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.gui.journal.base.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * GuiJournalProgression - Progression tree viewer (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Display constellation progression tree</li>
 * <li>Mouse drag to pan view</li>
 * <li>Scroll to zoom in/out</li>
 * <li>Click constellations to view details</li>
 * <li>Progress-based visibility</li>
 * </ul>
 * <p>
 * <b>Controls:</b>
 * <ul>
 * <li>Left-click + drag: Pan view</li>
 * <li>Scroll wheel: Zoom in/out</li>
 * <li>Right-click: Return to previous page</li>
 * <li>Click constellation: View details</li>
 * </ul>
 * <p>
 * <b>Zoom Levels:</b>
 * <ul>
 * <li>1.0x - Galaxy overview (all constellations)</li>
 * <li>4.0x - Constellation clusters</li>
 * <li>6.0x - Individual constellations</li>
 * <li>10.0x - Constellation details</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class GuiJournalProgression extends GuiScreenJournal {

    private static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(
        "astralsorcery",
        "textures/gui/guijblankbook.png");
    private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation(
        "astralsorcery",
        "textures/gui/guijresoverlay.png");

    private static GuiJournalProgression currentInstance = null;

    // View properties
    private double offsetX = 0;
    private double offsetY = 0;
    private double zoom = 1.0;

    // Mouse drag state
    private boolean isDragging = false;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;

    // Constellation positions (world coordinates)
    private Map<IConstellation, ConstellationPosition> constellationPositions = new HashMap<>();

    /**
     * Constructor
     */
    public GuiJournalProgression() {
        super(10); // Bookmark index 10 for progression
        initializeConstellationPositions();
        centerView();
    }

    /**
     * Get the current instance
     *
     * @return The current instance, or a new one if none exists
     */
    public static GuiJournalProgression getInstance() {
        if (currentInstance == null) {
            currentInstance = new GuiJournalProgression();
        }
        return currentInstance;
    }

    /**
     * Reset the current instance
     */
    public static void resetInstance() {
        currentInstance = null;
    }

    /**
     * Initialize constellation positions
     * Positions are in "world coordinates" (before zoom/offset)
     */
    private void initializeConstellationPositions() {
        constellationPositions.clear();

        // Get all major constellations
        int index = 0;
        for (IConstellation constellation : ConstellationRegistry.getMajorConstellations()) {
            // Arrange in a circular pattern
            double angle = (index * 2 * Math.PI) / ConstellationRegistry.getMajorConstellations()
                .size();
            double radius = 200; // Distance from center

            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;

            constellationPositions.put(constellation, new ConstellationPosition(x, y));
            index++;
        }

        LogHelper
            .debug("[GuiJournalProgression] Initialized " + constellationPositions.size() + " constellation positions");
    }

    /**
     * Center the view on the constellation tree
     */
    private void centerView() {
        offsetX = 0;
        offsetY = 0;
        zoom = 1.0;
        LogHelper.debug("[GuiJournalProgression] Centered view");
    }

    @Override
    public void initGui() {
        super.initGui();
        LogHelper.debug("[GuiJournalProgression] Initialized GUI");
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        currentInstance = this; // Save instance for returning
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(TEXTURE_BACKGROUND);
        drawRect(guiLeft, guiTop, guiWidth, guiHeight);

        // Setup render state for constellation tree
        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft + guiWidth / 2, guiTop + guiHeight / 2, 0.0F);
        GL11.glScalef((float) zoom, (float) zoom, 1.0F);
        GL11.glTranslatef((float) offsetX, (float) offsetY, 0.0F);

        // Draw constellations
        drawConstellations(mouseX, mouseY);

        // Draw connections
        drawConstellationConnections();

        GL11.glPopMatrix();

        // Draw overlay (UI elements on top)
        drawOverlay(mouseX, mouseY);
    }

    /**
     * Draw constellations
     */
    private void drawConstellations(int mouseX, int mouseY) {
        PlayerProgress progress = ResearchManager.getProgress(mc.thePlayer);

        for (Map.Entry<IConstellation, ConstellationPosition> entry : constellationPositions.entrySet()) {
            IConstellation constellation = entry.getKey();
            ConstellationPosition pos = entry.getValue();

            // Check if discovered
            boolean discovered = progress.hasConstellationDiscovered(constellation);
            if (!discovered) {
                continue; // Don't show undiscovered constellations
            }

            // Calculate screen position
            double screenX = pos.x;
            double screenY = pos.y;

            // Draw constellation node
            GL11.glPushMatrix();
            GL11.glTranslated(screenX, screenY, 0.0F);

            // Get constellation color
            java.awt.Color color = constellation.getConstellationColor();

            // Draw node circle
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 255);
            drawCircle(0, 0, 20);

            // Draw border
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            drawCircleOutline(0, 0, 20);

            GL11.glEnable(GL11.GL_TEXTURE_2D);

            // Draw name
            FontRenderer font = mc.fontRenderer;
            String name = I18n.format(constellation.getUnlocalizedName());
            float nameWidth = font.getStringWidth(name);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(-nameWidth / 2, -30, 0.0F);
            font.drawString(name, 0, 0, 0x00DDDDDD);

            GL11.glPopMatrix();
        }
    }

    /**
     * Draw constellation connections
     */
    private void drawConstellationConnections() {
        PlayerProgress progress = ResearchManager.getProgress(mc.thePlayer);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
        GL11.glLineWidth(2.0F);

        // Draw connections between constellations
        // For now, just connect nearby constellations
        IConstellation[] constellations = constellationPositions.keySet()
            .toArray(new IConstellation[0]);

        for (int i = 0; i < constellations.length; i++) {
            IConstellation c1 = constellations[i];
            if (!progress.hasConstellationDiscovered(c1)) {
                continue;
            }

            ConstellationPosition pos1 = constellationPositions.get(c1);

            for (int j = i + 1; j < constellations.length; j++) {
                IConstellation c2 = constellations[j];
                if (!progress.hasConstellationDiscovered(c2)) {
                    continue;
                }

                ConstellationPosition pos2 = constellationPositions.get(c2);

                // Calculate distance
                double dist = Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2));

                // Only connect if close enough
                if (dist < 300) {
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2d(pos1.x, pos1.y);
                    GL11.glVertex2d(pos2.x, pos2.y);
                    GL11.glEnd();
                }
            }
        }

        GL11.glLineWidth(1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Draw overlay UI
     */
    private void drawOverlay(int mouseX, int mouseY) {
        // Draw overlay texture
        mc.renderEngine.bindTexture(TEXTURE_OVERLAY);
        drawTexturedRect(guiLeft, guiTop, guiWidth, guiHeight, 0.0F, 0.0F, 1.0F, 1.0F);

        // Draw title
        String title = I18n.format("gui.journal.bm.constellations.name");
        FontRenderer font = mc.fontRenderer;
        float titleWidth = font.getStringWidth(title);

        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft + guiWidth / 2 - titleWidth / 2, guiTop + 20, 0.0F);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        font.drawString(title, 0, 0, 0x00DDDDDD);
        GL11.glPopMatrix();

        // Draw zoom level
        String zoomText = String.format("Zoom: %.1fx", zoom);
        font.drawString(zoomText, guiLeft + 10, guiTop + guiHeight - 20, 0x00AAAAAA);

        // Draw controls hint
        String hint = I18n.format("misc.journal.info.1");
        font.drawString(hint, guiLeft + 10, guiTop + guiHeight - 35, 0x00AAAAAA);
    }

    /**
     * Draw a filled circle
     */
    private void drawCircle(double x, double y, double radius) {
        int segments = 32;
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(x, y);
        for (int i = 0; i <= segments; i++) {
            double angle = (i * 2 * Math.PI) / segments;
            double px = x + Math.cos(angle) * radius;
            double py = y + Math.sin(angle) * radius;
            GL11.glVertex2d(px, py);
        }
        GL11.glEnd();
    }

    /**
     * Draw a circle outline
     */
    private void drawCircleOutline(double x, double y, double radius) {
        int segments = 32;
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < segments; i++) {
            double angle = (i * 2 * Math.PI) / segments;
            double px = x + Math.cos(angle) * radius;
            double py = y + Math.sin(angle) * radius;
            GL11.glVertex2d(px, py);
        }
        GL11.glEnd();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) { // Left-click
            // Start dragging
            isDragging = true;
            dragStartX = mouseX;
            dragStartY = mouseY;
            dragOffsetX = offsetX;
            dragOffsetY = offsetY;

            // Check for constellation click
            checkConstellationClick(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        super.mouseMovedOrUp(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            // Stop dragging
            isDragging = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (isDragging && clickedMouseButton == 0) {
            // Drag view
            double dx = (mouseX - dragStartX) / zoom;
            double dy = (mouseY - dragStartY) / zoom;
            offsetX = dragOffsetX + dx;
            offsetY = dragOffsetY + dy;
        }
    }

    /**
     * Handle mouse wheel for zooming
     */
    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            // Zoom in/out
            double zoomFactor = 1.1;
            if (wheel > 0) {
                // Zoom in
                zoom = Math.min(zoom * zoomFactor, 10.0);
            } else {
                // Zoom out
                zoom = Math.max(zoom / zoomFactor, 1.0);
            }
            LogHelper.debug("[GuiJournalProgression] Zoom: " + zoom);
        }
    }

    /**
     * Check if a constellation was clicked
     */
    private void checkConstellationClick(int mouseX, int mouseY) {
        // Convert screen coordinates to world coordinates
        double worldX = (mouseX - guiLeft - guiWidth / 2) / zoom - offsetX;
        double worldY = (mouseY - guiTop - guiHeight / 2) / zoom - offsetY;

        PlayerProgress progress = ResearchManager.getProgress(mc.thePlayer);

        for (Map.Entry<IConstellation, ConstellationPosition> entry : constellationPositions.entrySet()) {
            IConstellation constellation = entry.getKey();
            ConstellationPosition pos = entry.getValue();

            if (!progress.hasConstellationDiscovered(constellation)) {
                continue;
            }

            // Check if click is within constellation node
            double dist = Math.sqrt(Math.pow(worldX - pos.x, 2) + Math.pow(worldY - pos.y, 2));
            if (dist < 20) {
                // Constellation clicked
                LogHelper.debug("[GuiJournalProgression] Clicked constellation: " + constellation.getUnlocalizedName());
                // TODO: Open constellation details GUI
                return;
            }
        }
    }

    /**
     * Constellation position in world coordinates
     */
    private static class ConstellationPosition {

        final double x;
        final double y;

        ConstellationPosition(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
