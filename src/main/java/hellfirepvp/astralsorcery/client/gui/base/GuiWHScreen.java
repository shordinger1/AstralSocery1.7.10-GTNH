/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.base;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Mouse;

import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiWHScreen
 * Created by HellFirePvP
 * Date: 11.08.2016 / 20:06
 */
public abstract class GuiWHScreen extends GuiScreen {

    protected final int guiHeight;
    protected final int guiWidth;
    protected int guiLeft, guiTop;

    protected boolean closeWithInventoryKey = true;

    protected GuiWHScreen(int guiHeight, int guiWidth) {
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @Override
    public void initGui() {
        super.initGui();

        initComponents();
    }

    public int getGuiHeight() {
        return guiHeight;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    public int getGuiWidth() {
        return guiWidth;
    }

    private void initComponents() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
    }

    protected void drawWHRect(AbstractRenderableTexture resource) {
        resource.bindTexture();
        drawRect(guiLeft, guiTop, guiWidth, guiHeight);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        if (closeWithInventoryKey && keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
            Minecraft.getMinecraft()
                .displayGuiScreen(null);

            if (Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft()
                    .setIngameFocus();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1 && !handleRightClickClose(mouseX, mouseY)) {
            Minecraft.getMinecraft()
                .displayGuiScreen(null);

            if (Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft()
                    .setIngameFocus();
            }
        }
    }

    /**
     * @return false if rightclick is not handled any other way and allow for close. true to deny rightclick close and
     *         handle otherwise;
     */
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        return false;
    }

    public Point getCurrentMousePoint() {
        int guiMouseX = Mouse.getEventX() * width / mc.displayWidth;
        int guiMouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        return new Point(guiMouseX, guiMouseY);
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height,
        Rectangle.Float uvBounds) {
        drawTexturedRect(offsetX, offsetY, width, height, uvBounds.x, uvBounds.y, uvBounds.width, uvBounds.height);
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height, float uFrom,
        float vFrom, float uWidth, float vWidth) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, uFrom, vFrom + vWidth);
        tes.addVertexWithUV(offsetX + width, offsetY + height, zLevel, uFrom + uWidth, vFrom + vWidth);
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, uFrom + uWidth, vFrom);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, uFrom, vFrom);
        tes.draw();
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height,
        AbstractRenderableTexture tex) {
        Point.Double off = tex.getUVOffset();
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, off.x, off.y + tex.getVWidth());
        tes.addVertexWithUV(
            offsetX + width,
            offsetY + height,
            zLevel,
            off.x + tex.getUWidth(),
            off.y + tex.getVWidth());
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, off.x + tex.getUWidth(), off.y);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, off.x, off.y);
        tes.draw();
    }

    protected void drawTexturedRectAtCurrentPos(double width, double height, float uFrom, float vFrom, float uWidth,
        float vWidth) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(0, 0 + height, zLevel, uFrom, vFrom + vWidth);
        tes.addVertexWithUV(0 + width, 0 + height, zLevel, uFrom + uWidth, vFrom + vWidth);
        tes.addVertexWithUV(0 + width, 0, zLevel, uFrom + uWidth, vFrom);
        tes.addVertexWithUV(0, 0, zLevel, uFrom, vFrom);
        tes.draw();
    }

    protected void drawTexturedRectAtCurrentPos(double width, double height) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(0, 0 + height, zLevel, 0, 1);
        tes.addVertexWithUV(0 + width, 0 + height, zLevel, 1, 1);
        tes.addVertexWithUV(0 + width, 0, zLevel, 1, 0);
        tes.addVertexWithUV(0, 0, zLevel, 0, 0);
        tes.draw();
    }

    protected void drawRectDetailed(float offsetX, float offsetY, float width, float height) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, 0, 1);
        tes.addVertexWithUV(offsetX + width, offsetY + height, zLevel, 1, 1);
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, 1, 0);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, 0, 0);
        tes.draw();
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, 0, 1);
        tes.addVertexWithUV(offsetX + width, offsetY + height, zLevel, 1, 1);
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, 1, 0);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, 0, 0);
        tes.draw();
    }

}
