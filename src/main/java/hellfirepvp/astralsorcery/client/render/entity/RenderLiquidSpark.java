/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.common.entities.EntityLiquidSpark;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderLiquidSpark
 * Created by HellFirePvP
 * Date: 28.10.2017 / 23:58
 */
// 1.7.10: Remove generic type parameter - Render class doesn't support generics in 1.7.10
public class RenderLiquidSpark extends Render {

    // 1.7.10: No-arg constructor - renderManager not passed in 1.7.10
    public RenderLiquidSpark() {
        super();
    }

    @Override
    // 1.7.10: doRender signature uses Entity instead of generic type
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!(entity instanceof EntityLiquidSpark)) return; // 1.7.10: Type checking needed
    }

    @Nullable
    @Override
    // 1.7.10: getEntityTexture uses Entity parameter, not generic type
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}
