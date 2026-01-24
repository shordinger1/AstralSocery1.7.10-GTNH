/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.fx;

import java.awt.*;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.GLAllocation;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXCrystal
 * Created by HellFirePvP
 * Date: 01.03.2019 / 17:14
 */
public class EntityFXCrystal extends EntityComplexFX {

    private static final AbstractRenderableTexture texWhite = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_white");
    private static int lCrystal = -1;

    private double x, y, z;
    private double oldX, oldY, oldZ;
    private float scale = 1F;
    private float rX, rY, rZ;

    private AlphaFunction fadeFunction = AlphaFunction.CONSTANT;
    private ScaleFunction scaleFunction = ScaleFunction.IDENTITY;
    private float alphaMultiplier = 1F;
    private float colorRed = 1F, colorGreen = 1F, colorBlue = 1F;
    private double motionX = 0, motionY = 0, motionZ = 0;

    public EntityFXCrystal(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldX = x;
        this.oldY = y;
        this.oldZ = z;
    }

    public EntityFXCrystal updatePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public EntityFXCrystal offset(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public EntityFXCrystal rotation(float x, float y, float z) {
        this.rX = x;
        this.rY = y;
        this.rZ = z;
        return this;
    }

    public EntityFXCrystal setScaleFunction(@Nonnull ScaleFunction scaleFunction) {
        this.scaleFunction = scaleFunction;
        return this;
    }

    public EntityFXCrystal enableAlphaFade(@Nonnull AlphaFunction function) {
        this.fadeFunction = function;
        return this;
    }

    public EntityFXCrystal motion(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        return this;
    }

    public EntityFXCrystal scale(float scale) {
        this.scale = scale;
        return this;
    }

    public EntityFXCrystal setAlphaMultiplier(float alphaMul) {
        alphaMultiplier = alphaMul;
        return this;
    }

    public EntityFXCrystal setColor(Color color) {
        colorRed = ((float) color.getRed()) / 255F;
        colorGreen = ((float) color.getGreen()) / 255F;
        colorBlue = ((float) color.getBlue()) / 255F;
        return this;
    }

    @Override
    public void tick() {
        super.tick();

        oldX = x;
        oldY = y;
        oldZ = z;
        x += motionX;
        y += motionY;
        z += motionZ;
    }

    @Override
    public void render(float pTicks) {
        Blending.DEFAULT.applyStateManager();

        float alpha = fadeFunction.getAlpha(age, maxAge);
        alpha *= alphaMultiplier;
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
        double iX = RenderingUtils.interpolate(oldX, x, pTicks);
        double iY = RenderingUtils.interpolate(oldY, y, pTicks);
        double iZ = RenderingUtils.interpolate(oldZ, z, pTicks);

        float fScale = scale;
        fScale = scaleFunction.getScale(this, new Vector3(iX, iY, iZ), pTicks, fScale);

        renderCrystal();

        Blending.DEFAULT.applyStateManager();
    }

    private void renderCrystal() {
        texWhite.bindTexture();
        if (lCrystal == -1) {
            lCrystal = GLAllocation.generateDisplayLists(1);
            OBJModelLibrary.bigCrystal.renderAll(true);
        }
    }

}
