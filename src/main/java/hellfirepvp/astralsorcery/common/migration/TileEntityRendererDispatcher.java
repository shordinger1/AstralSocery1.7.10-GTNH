/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * TileEntityRendererDispatcher class for TE rendering dispatch
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
 * In 1.7.10: Different TE rendering system
 */
public class TileEntityRendererDispatcher {

    public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();

    public TileEntityRendererDispatcher() {}
}
