/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * RenderEntityObservatoryHelper - Observatory helper renderer (invisible)
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.client.util.ResourceLocationRegister;

/**
 * RenderEntityObservatoryHelper - Observatory helper renderer (1.7.10)
 * <p>
 * <b>Rendering Features:</b>
 * <ul>
 * <li>Entity is invisible - used as a mount for telescope riding</li>
 * <li>No rendering performed</li>
 * </ul>
 * <p>
 * <b>1.7.10 API:</b>
 * <ul>
 * <li>Extends {@link Render}</li>
 * <li>Does not render anything</li>
 * </ul>
 */
public class RenderEntityObservatoryHelper extends Render {

    public RenderEntityObservatoryHelper() {
        this.shadowSize = 0.0F; // No shadow
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float pitch, float partialTicks) {
        // This entity is invisible - it's just a mount entity for the telescope
        // Don't render anything
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // No texture needed for invisible entity
        return ResourceLocationRegister.getEntityObservatoryHelper();
    }

    /**
     * Check if this renderer should render the entity
     * Always returns false since this entity should never be rendered
     */
    public boolean shouldRender(Entity entity, double x, double y, double z) {
        // Never render the observatory helper entity
        return false;
    }
}
