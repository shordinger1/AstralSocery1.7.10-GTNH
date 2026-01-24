/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

// 1.7.10: IRenderFactory removed - doesn't exist in 1.7.10
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityNoOp
 * Created by HellFirePvP
 * Date: 03.07.2017 / 13:36
 */
// 1.7.10: Remove generic type parameter - Render class doesn't support generics in 1.7.10
public class RenderEntityNoOp<T extends Entity> extends Render {

    // 1.7.10: No-arg constructor - renderManager not passed in 1.7.10
    public RenderEntityNoOp() {
        super();
    }

    // 1.7.10: Only one doRender method needed with Entity parameter
    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // No-op render
    }

    @Override
    // 1.7.10: getEntityTexture uses Entity parameter, not generic type
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}
