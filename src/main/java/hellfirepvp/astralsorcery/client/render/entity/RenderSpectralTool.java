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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.entities.EntitySpectralTool;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderSpectralTool
 * Created by HellFirePvP
 * Date: 11.10.2017 / 21:14
 */
// 1.7.10: Remove generic type parameter - Render class doesn't support generics in 1.7.10
public class RenderSpectralTool extends Render {

    // 1.7.10: No-arg constructor - renderManager not passed in 1.7.10
    public RenderSpectralTool() {
        super();
    }

    @Override
    // 1.7.10: doRender signature uses Entity instead of generic type
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!(entity instanceof EntitySpectralTool)) return; // 1.7.10: Type checking needed
        EntitySpectralTool ent = (EntitySpectralTool) entity;

        ItemStack is = ent.getItem();
        // 1.7.10: ItemStack.isEmpty() doesn't exist, use stackSize check
        if (is == null || is.stackSize <= 0) {
            return;
        }

        // 1.7.10: Use renderItemAsEntity since IBakedModel API is not available
        Blending.CONSTANT_ALPHA.applyStateManager();

        // 1.7.10: Simplified rendering - use entity item renderer
        RenderingUtils.renderItemAsEntity(is, x, y, z, partialTicks, ent.ticksExisted);

        Blending.DEFAULT.applyStateManager();

    }

    @Nullable
    @Override
    // 1.7.10: getEntityTexture uses Entity parameter, not generic type
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}
