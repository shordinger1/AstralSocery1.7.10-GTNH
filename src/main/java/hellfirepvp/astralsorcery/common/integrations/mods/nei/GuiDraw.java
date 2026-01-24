/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.nei;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Utility class for NEI GUI drawing operations
 * Wrapper for codechicken.lib.gui.GuiDraw
 */
public class GuiDraw {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void changeTexture(String texture) {
        changeTexture(new ResourceLocation(texture));
    }

    public static void changeTexture(ResourceLocation texture) {
        mc.getTextureManager()
            .bindTexture(texture);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(x, y, u, v, width, height, 0);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int z) {
        float uScale = 0.00390625F;
        float vScale = 0.00390625F;
        drawTexturedModalRect(x, y, u, v, width, height, uScale, vScale, z);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float uScale,
        float vScale) {
        drawTexturedModalRect(x, y, u, v, width, height, uScale, vScale, 0);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float uScale,
        float vScale, int z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, z);
        mc.renderEngine.bindTexture(mc.renderWorldLastTexture = new ResourceLocation("textures/atlas/blocks.png")); // placeholder
        // Use the NEI GuiDraw implementation
        codechicken.lib.gui.GuiDraw.drawTexturedModalRect(x, y, u, v, width, height);
        GL11.glPopMatrix();
    }
}
