/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base class for fixed-size GUI screens
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.base;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * GuiWHScreen - Width/Height-based screen (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Fixed-size GUI that centers on screen</li>
 * <li>Automatic centering based on screen size</li>
 * <li>Right-click to close</li>
 * <li>Inventory key to close</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes:</b>
 * <ul>
 * <li>initGui() called when GUI is opened</li>
 * <li>drawScreen() renders the GUI</li>
 * <li>keyTyped() handles keyboard input</li>
 * <li>mouseClicked() handles mouse input</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public abstract class GuiWHScreen extends GuiScreen {

    protected final int guiHeight;
    protected final int guiWidth;
    protected int guiLeft, guiTop;
    protected float zLevel = 0.0F;

    protected boolean closeWithInventoryKey = true;

    /**
     * Constructor
     *
     * @param guiHeight Fixed GUI height
     * @param guiWidth  Fixed GUI width
     */
    public GuiWHScreen(int guiHeight, int guiWidth) {
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @Override
    public void initGui() {
        super.initGui();
        initComponents();
    }

    /**
     * Initialize components
     * Centers the GUI on screen
     */
    protected void initComponents() {
        guiLeft = (width - guiWidth) / 2;
        guiTop = (height - guiHeight) / 2;
    }

    /**
     * Get GUI height
     */
    public int getGuiHeight() {
        return guiHeight;
    }

    /**
     * Get GUI width
     */
    public int getGuiWidth() {
        return guiWidth;
    }

    /**
     * Get GUI left offset
     */
    public int getGuiLeft() {
        return guiLeft;
    }

    /**
     * Get GUI top offset
     */
    public int getGuiTop() {
        return guiTop;
    }

    /**
     * Draw a textured rectangle
     *
     * @param x       X position
     * @param y       Y position
     * @param width   Width
     * @param height  Height
     * @param u       U texture coordinate
     * @param v       V texture coordinate
     * @param uWidth  Width in texture
     * @param vHeight Height in texture
     */
    protected void drawTexturedRect(double x, double y, double width, double height, float u, float v, float uWidth,
        float vHeight) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, zLevel);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0, u, v + vHeight);
        tessellator.addVertexWithUV(x + width, y + height, 0.0, u + uWidth, v + vHeight);
        tessellator.addVertexWithUV(x + width, y, 0.0, u + uWidth, v);
        tessellator.addVertexWithUV(x, y, 0.0, u, v);
        tessellator.draw();
        GL11.glPopMatrix();
    }

    /**
     * Draw a full-texture rectangle
     *
     * @param x      X position
     * @param y      Y position
     * @param width  Width
     * @param height Height
     */
    protected void drawRect(double x, double y, double width, double height) {
        drawTexturedRect(x, y, width, height, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        // Close with inventory key
        if (closeWithInventoryKey && keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.displayGuiScreen(null);
            if (mc.currentScreen == null) {
                mc.setIngameFocus();
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Right-click to close
        if (mouseButton == 1 && !handleRightClickClose(mouseX, mouseY)) {
            mc.displayGuiScreen(null);
            if (mc.currentScreen == null) {
                mc.setIngameFocus();
            }
            return;
        }
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle right-click close
     *
     * @return true if right-click was handled and GUI should not close
     */
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        return false;
    }
}
