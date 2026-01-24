/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderItemAccessor;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IGuiRenderablePage
 * Created by HellFirePvP
 * Date: 30.08.2016 / 11:21
 */
public interface IGuiRenderablePage {

    public static final IGuiRenderablePage GUI_INTERFACE = (offsetX, offsetY, pTicks, zLevel, mouseX, mouseY) -> {};

    static final BindableResource resStar = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");

    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY);

    default public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX,
        float mouseY) {}

    default public boolean propagateMouseClick(int mouseX, int mouseZ) {
        return false;
    }

    default public Rectangle drawItemStack(ItemStack stack, int offsetX, int offsetY, float zLevel) {
        return drawItemStack(stack, offsetX, offsetY, zLevel, getStandardFontRenderer(), getRenderItem());
    }

    default public Rectangle drawItemStack(ItemStack stack, int offsetX, int offsetY, float zLevel,
        FontRenderer fontRenderer, RenderItem ri) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        float zIR = ri.zLevel;
        ri.zLevel = zLevel;
        RenderHelper.enableGUIStandardItemLighting();

        ri.renderItemAndEffectIntoGUI(fontRenderer, Minecraft.getMinecraft().renderEngine, stack, offsetX, offsetY);
        ri.renderItemOverlayIntoGUI(fontRenderer, Minecraft.getMinecraft().renderEngine, stack, offsetX, offsetY);

        RenderHelper.disableStandardItemLighting();
        ri.zLevel = zIR;
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        return new Rectangle(offsetX, offsetY, 16, 16);
    }

    default public Rectangle drawInfoStar(float offsetX, float offsetY, float zLevel, float widthHeightBase,
        float pTicks) {

        float tick = ClientScheduler.getClientTick() + pTicks;
        float deg = (tick * 2) % 360F;
        float wh = widthHeightBase
            - (widthHeightBase / 6F) * (WrapMathHelper.sin((float) Math.toRadians(((tick) * 4) % 360F)) + 1F);
        drawInfoStarSingle(offsetX, offsetY, zLevel, wh, Math.toRadians(deg));

        deg = ((tick + 22.5F) * 2) % 360F;
        wh = widthHeightBase
            - (widthHeightBase / 6F) * (WrapMathHelper.sin((float) Math.toRadians(((tick + 45F) * 4) % 360F)) + 1F);
        drawInfoStarSingle(offsetX, offsetY, zLevel, wh, Math.toRadians(deg));

        return new Rectangle(
            WrapMathHelper.floor(offsetX - widthHeightBase / 2F),
            WrapMathHelper.floor(offsetY - widthHeightBase / 2F),
            WrapMathHelper.floor(widthHeightBase),
            WrapMathHelper.floor(widthHeightBase));
    }

    default public void drawInfoStarSingle(float offsetX, float offsetY, float zLevel, float widthHeight, double deg) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        resStar.bind();
        Vector3 offset = new Vector3(-widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv01 = new Vector3(-widthHeight / 2D, widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv11 = new Vector3(widthHeight / 2D, widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv10 = new Vector3(widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);

        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX + uv01.getX(), offsetY + uv01.getY(), zLevel, 0, 1);
        tes.addVertexWithUV(offsetX + uv11.getX(), offsetY + uv11.getY(), zLevel, 1, 1);
        tes.addVertexWithUV(offsetX + uv10.getX(), offsetY + uv10.getY(), zLevel, 1, 0);
        tes.addVertexWithUV(offsetX + offset.getX(), offsetY + offset.getY(), zLevel, 0, 0);
        tes.draw();

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    default public void drawRect(double offsetX, double offsetY, double width, double height, double zLevel) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, 0, 1);
        tes.addVertexWithUV(offsetX + width, offsetY + height, zLevel, 1, 1);
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, 1, 0);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, 0, 0);
        tes.draw();
    }

    default public void drawRectPart(double offsetX, double offsetY, double width, double height, double zLevel,
        double u, double v, double uLength, double vLength) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, zLevel, u, v + vLength);
        tes.addVertexWithUV(offsetX + width, offsetY + height, zLevel, u + uLength, v + vLength);
        tes.addVertexWithUV(offsetX + width, offsetY, zLevel, u + uLength, v);
        tes.addVertexWithUV(offsetX, offsetY, zLevel, u, v);
        tes.draw();
    }

    default public RenderItem getRenderItem() {
        return RenderItemAccessor.getRenderItem();
    }

    default public FontRenderer getStandardFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    default public FontRenderer getStandardGalFontRenderer() {
        return Minecraft.getMinecraft().standardGalacticFontRenderer;
    }

    default public String getDescriptionFromStarlightAmount(String locTierTitle, int amtRequired, int maxAmount) {
        String base = "astralsorcery.journal.recipe.amt.";
        String ext;
        float perc = ((float) amtRequired) / ((float) maxAmount);
        if (perc <= 0.1) {
            ext = "lowest";
        } else if (perc <= 0.25) {
            ext = "low";
        } else if (perc <= 0.5) {
            ext = "avg";
        } else if (perc <= 0.75) {
            ext = "more";
        } else if (perc <= 0.9) {
            ext = "high";
        } else if (perc > 1) {
            ext = "toomuch";
        } else {
            ext = "highest";
        }
        return String.format("%s: %s", locTierTitle, I18n.format(String.format("%s%s", base, ext)));
    }

}
