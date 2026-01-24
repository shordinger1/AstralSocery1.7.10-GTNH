/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * IReloadableResourceManager interface for resource management
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.resources.IReloadableResourceManager
 * In 1.7.10: Different resource management system
 */
public interface IReloadableResourceManager {

    void reloadResources(net.minecraft.client.resources.IResourceManager resourceManager);
}
