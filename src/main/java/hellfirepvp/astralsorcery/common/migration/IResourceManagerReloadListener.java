/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IResourceManagerReloadListener interface for resource reload listeners
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.resources.IResourceManagerReloadListener
 * In 1.7.10: Different resource management system
 */
public interface IResourceManagerReloadListener {

    void onResourceManagerReload(IResourceManager resourceManager);
}
