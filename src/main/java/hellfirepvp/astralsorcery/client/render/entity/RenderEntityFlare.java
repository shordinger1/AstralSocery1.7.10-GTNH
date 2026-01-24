/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.common.entities.EntityFlare;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityFlare
 * Created by HellFirePvP
 * Date: 07.02.2017 / 12:21
 */
public class RenderEntityFlare extends Render {

    // 1.7.10: Render constructor requires RenderManager parameter
    public RenderEntityFlare() {
        super(); // 1.7.10: No-arg constructor is fine for simple renders
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity instanceof EntityFlare) {
            doRenderFlare((EntityFlare) entity, x, y, z, entityYaw, partialTicks);
        }
    }

    private void doRenderFlare(EntityFlare entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Original render logic
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
