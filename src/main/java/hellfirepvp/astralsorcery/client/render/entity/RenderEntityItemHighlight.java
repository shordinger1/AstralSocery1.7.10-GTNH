/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityItemHighlight
 * Created by HellFirePvP
 * Date: 13.05.2016 / 13:59
 */
public class RenderEntityItemHighlight extends Render {

    // 1.7.10: Use RenderItem (not RenderEntityItem)
    private final RenderItem renderItem;

    // 1.7.10: Constructor initializes RenderItem
    public RenderEntityItemHighlight() {
        super();
        renderItem = new RenderItem();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!(entity instanceof EntityItemHighlighted)) return;

        EntityItemHighlighted eih = (EntityItemHighlighted) entity;
        // 1.7.10: EntityItem.age is a public field, not a method
        RenderingUtils.renderLightRayEffects(x, y + 0.5, z, eih.getHighlightColor(), 16024L, eih.age, 16, 20, 5);

        // 1.7.10: Use getEntityItem() instead of getItem()
        ItemStack stack = eih.getEntityItem();
        // 1.7.10: Use stackSize field (was renamed to getCount() in later versions)
        if (!(stack == null || stack.stackSize <= 0)) {
            EntityItem ei = new EntityItem(eih.worldObj, eih.posX, eih.posY, eih.posZ, stack);
            ei.age = eih.age;
            ei.hoverStart = eih.hoverStart;
            if (RenderingUtils.itemPhysics_fieldSkipRenderHook != null) {
                try {
                    RenderingUtils.itemPhysics_fieldSkipRenderHook.set(ei, true);
                } catch (Exception ignored) {}
            }
            renderItem.doRender(ei, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
