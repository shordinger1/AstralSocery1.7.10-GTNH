/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.potion;

import java.awt.*;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionCustomTexture
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:36
 */
public abstract class PotionCustomTexture extends Potion {

    protected static final Random rand = new Random();

    protected PotionCustomTexture(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public abstract BindableResource getResource();

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        Tessellator tes = Tessellator.instance;
        double wh = 18;
        double offsetX = 6;
        double offsetY = 7;
        Color c = new Color(getLiquidColor());
        float red = ((float) c.getRed()) / 255F;
        float green = ((float) c.getGreen()) / 255F;
        float blue = ((float) c.getBlue()) / 255F;

        getResource().bind();
        tes.startDrawing(GL11.GL_QUADS);
        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        int rgb = ((int) (red * 255) << 16) | ((int) (green * 255) << 8) | (int) (blue * 255);
        tes.setColorRGBA_I(rgb, 255);
        tes.addVertexWithUV(x + offsetX, y + offsetY, 0, 0, 0);
        tes.addVertexWithUV(x + offsetX, y + offsetY + wh, 0, 0, 1);
        tes.addVertexWithUV(x + offsetX + wh, y + offsetY + wh, 0, 1, 1);
        tes.addVertexWithUV(x + offsetX + wh, y + offsetY, 0, 1, 0);
        tes.draw();
        TextureHelper.refreshTextureBindState();
    }


    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        Tessellator tes = Tessellator.instance;
        double wh = 18;
        double offsetX = 3;
        double offsetY = 3;
        Color c = new Color(getLiquidColor());
        float red = ((float) c.getRed()) / 255F;
        float green = ((float) c.getGreen()) / 255F;
        float blue = ((float) c.getBlue()) / 255F;

        getResource().bind();
        tes.startDrawing(GL11.GL_QUADS);
        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        int rgb = ((int) (red * 255) << 16) | ((int) (green * 255) << 8) | (int) (blue * 255);
        tes.setColorRGBA_I(rgb, (int) (alpha * 255));
        tes.addVertexWithUV(x + offsetX, y + offsetY, 0, 0, 0);
        tes.addVertexWithUV(x + offsetX, y + offsetY + wh, 0, 0, 1);
        tes.addVertexWithUV(x + offsetX + wh, y + offsetY + wh, 0, 1, 1);
        tes.addVertexWithUV(x + offsetX + wh, y + offsetY, 0, 1, 0);
        tes.draw();
        TextureHelper.refreshTextureBindState();
    }
}
