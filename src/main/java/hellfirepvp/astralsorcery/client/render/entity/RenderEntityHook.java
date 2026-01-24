/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.entities.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityHook
 * Created by HellFirePvP
 * Date: 24.06.2017 / 22:10
 */
// 1.7.10: Remove generic type parameter - Render class doesn't support generics in 1.7.10
public class RenderEntityHook extends Render {

    private static BindableResource texConn;

    // 1.7.10: Constructor needs to handle shadow size initialization
    public RenderEntityHook() {
        super();
        this.shadowSize = 0F; // 1.7.10: shadowSize field exists in Render class
    }

    // 1.7.10: doRender signature uses Entity instead of generic type
    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!(entity instanceof EntityGrapplingHook)) return; // 1.7.10: Type checking needed
        EntityGrapplingHook ent = (EntityGrapplingHook) entity;
        if (texConn == null) {
            texConn = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flaresmall");
        }

        float alphaMultiplier = 1;
        // 1.7.10: Use ent instead of entity for EntityGrapplingHook-specific methods
        if (ent.isDespawning()) {
            alphaMultiplier = Math.max(0, 0.5F - ent.despawnPercentage(partialTicks));
        }
        if (alphaMultiplier <= 1E-4) {
            return;
        }

        // 1.7.10: Tessellator.instance is the correct way to get instance
        Tessellator tes = Tessellator.instance;

        Blending.DEFAULT.applyStateManager();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Vector3 iPosE = RenderingUtils.interpolatePosition(ent, partialTicks); // 1.7.10: Use local variable

        SpriteSheetResource sprite = SpriteLibrary.spriteHook;
        sprite.getResource()
            .bindTexture();
        Tuple<Double, Double> currentUV = sprite.getUVOffset(ClientScheduler.getClientTick() + ent.ticksExisted); // 1.7.10:
                                                                                                                  // Use
                                                                                                                  // local
                                                                                                                  // variable
        tes.startDrawing(GL11.GL_QUADS);
        RenderingUtils.renderFacingQuadVB(
            tes,
            iPosE.getX(),
            iPosE.getY(),
            iPosE.getZ(),
            partialTicks,
            1.3F,
            0F,
            currentUV.key,
            currentUV.value,
            sprite.getULength(),
            sprite.getVLength(),
            1F,
            1F,
            1F,
            alphaMultiplier);
        tes.draw();

        // 1.7.10: Use local variable instead of generic parameter
        List<Vector3> drawPoints = ent.buildPoints(partialTicks);
        Blending.ADDITIVE_ALPHA.applyStateManager();

        texConn.bind();

        tes.startDrawing(GL11.GL_QUADS);
        for (Vector3 pos : drawPoints) {
            RenderingUtils.renderFacingQuadVB(
                tes,
                iPosE.getX() + pos.getX(),
                iPosE.getY() + pos.getY(),
                iPosE.getZ() + pos.getZ(),
                partialTicks,
                0.25F,
                0,
                0,
                0,
                1,
                1,
                0.2F,
                0.15F,
                0.7F,
                0.8F * alphaMultiplier);
        }
        tes.draw();

        Blending.DEFAULT.applyStateManager();

        TextureHelper.refreshTextureBindState();
    }

    @Nullable
    @Override
    // 1.7.10: getEntityTexture uses Entity parameter, not generic type
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}
