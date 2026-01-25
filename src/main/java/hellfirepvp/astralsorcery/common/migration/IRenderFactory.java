/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IRenderFactory interface for creating renders
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.client.renderer.entity.Render;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.entity.RenderFactory
 * In 1.7.10: This concept doesn't exist, but we can use it for consistency
 */
public interface IRenderFactory<T extends net.minecraft.entity.Entity> {

    Render createRenderFor();
}
