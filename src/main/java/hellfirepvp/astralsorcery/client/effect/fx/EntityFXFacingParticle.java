/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.fx;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXFacingParticle
 * Created by HellFirePvP
 * Date: 16.10.2016 / 16:10
 * <p>
 * 1.7.10 Port:
 * - Billboard particle that always faces the player
 * - Supports color, alpha fading, scaling, and motion
 * - Uses Tessellator for 1.7.10 rendering
 */
public class EntityFXFacingParticle extends EntityComplexFX {

    public static final BindableResource staticFlareTex = AssetLibrary
        .loadTexture(AssetLibrary.TextureLocation.EFFECT, "flarestatic");

    private double x, y, z;
    private double oldX, oldY, oldZ;
    private double yGravity = 0.004;
    private float scale = 1F;

    private AlphaFunction fadeFunction = AlphaFunction.CONSTANT;
    private ScaleFunction scaleFunction = ScaleFunction.IDENTITY;
    private RenderOffsetController renderOffsetController = null;
    private boolean distanceRemovable = true;
    private float alphaMultiplier = 1F;
    private float colorRed = 1F, colorGreen = 1F, colorBlue = 1F;
    private double motionX = 0, motionY = 0, motionZ = 0;

    public EntityFXFacingParticle(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldX = x;
        this.oldY = y;
        this.oldZ = z;
    }

    public EntityFXFacingParticle updatePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public EntityFXFacingParticle offset(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public EntityFXFacingParticle setScaleFunction(@Nonnull ScaleFunction scaleFunction) {
        this.scaleFunction = scaleFunction;
        return this;
    }

    public EntityFXFacingParticle enableAlphaFade(@Nonnull AlphaFunction function) {
        this.fadeFunction = function;
        return this;
    }

    public EntityFXFacingParticle setRenderOffsetController(RenderOffsetController renderOffsetController) {
        this.renderOffsetController = renderOffsetController;
        return this;
    }

    public EntityFXFacingParticle motion(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        return this;
    }

    public EntityFXFacingParticle gravity(double yGrav) {
        this.yGravity -= yGrav;
        return this;
    }

    public EntityFXFacingParticle scale(float scale) {
        this.scale = scale;
        return this;
    }

    public EntityFXFacingParticle setAlphaMultiplier(float alphaMul) {
        alphaMultiplier = alphaMul;
        return this;
    }

    public EntityFXFacingParticle setColor(Color color) {
        colorRed = ((float) color.getRed()) / 255F;
        colorGreen = ((float) color.getGreen()) / 255F;
        colorBlue = ((float) color.getBlue()) / 255F;
        return this;
    }

    public EntityFXFacingParticle setDistanceRemovable(boolean distanceRemovable) {
        this.distanceRemovable = distanceRemovable;
        return this;
    }

    public Vector3 getPosition() {
        return new Vector3(x, y, z);
    }

    public boolean isDistanceRemovable() {
        return distanceRemovable;
    }

    @Override
    public void tick() {
        super.tick();

        oldX = x;
        oldY = y;
        oldZ = z;
        x += motionX;
        y += (motionY - yGravity);
        z += motionZ;
    }

    /**
     * Fast render method for batch rendering particles
     * 1.7.10: Adapted for Tessellator API
     */
    public static <T extends EntityFXFacingParticle> void renderFast(float parTicks, List<T> particles) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);

        staticFlareTex.bind();

        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_QUADS);

        for (T particle : new ArrayList<>(particles)) {
            if (particle == null) continue;
            particle.renderFast(parTicks, t);
        }

        t.draw();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * Render particle to tessellator
     * 1.7.10: Uses Tessellator instead of BufferBuilder
     */
    public void renderFast(float pTicks, Tessellator tessellator) {
        float alpha = fadeFunction.getAlpha(age, maxAge);
        alpha *= alphaMultiplier;
        double intX = RenderingUtils.interpolate(oldX, x, pTicks);
        double intY = RenderingUtils.interpolate(oldY, y, pTicks);
        double intZ = RenderingUtils.interpolate(oldZ, z, pTicks);
        if (renderOffsetController != null) {
            Vector3 result = renderOffsetController.changeRenderPosition(
                this,
                new Vector3(intX, intY, intZ),
                new Vector3(motionX, motionY - yGravity, motionZ),
                pTicks);
            intX = result.getX();
            intY = result.getY();
            intZ = result.getZ();
        }
        float fScale = scale;
        fScale = scaleFunction.getScale(this, new Vector3(intX, intY, intZ), pTicks, fScale);
        RenderingUtils.renderFacingFullQuadVB(
            tessellator,
            intX,
            intY,
            intZ,
            pTicks,
            fScale,
            0,
            colorRed,
            colorGreen,
            colorBlue,
            alpha);
    }

    @Override
    public void render(float pTicks) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        float alpha = fadeFunction.getAlpha(age, maxAge);
        alpha *= alphaMultiplier;
        GL11.glColor4f(colorRed, colorGreen, colorBlue, alpha);
        staticFlareTex.bind();
        RenderingUtils.renderFacingQuad(
            RenderingUtils.interpolate(oldX, x, pTicks),
            RenderingUtils.interpolate(oldY, y, pTicks),
            RenderingUtils.interpolate(oldZ, z, pTicks),
            pTicks,
            scale,
            0,
            0,
            0,
            1,
            1);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * Gateway particle subtype
     */
    public static class Gateway extends EntityFXFacingParticle {

        public Gateway(double x, double y, double z) {
            super(x, y, z);
        }

    }

}
