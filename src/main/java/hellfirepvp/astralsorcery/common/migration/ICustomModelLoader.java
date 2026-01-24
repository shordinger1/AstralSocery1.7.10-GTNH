/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ICustomModelLoader interface for custom model loaders
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.util.ResourceLocation;

/**
 * Compatibility interface for 1.7.10.
 * In 1.12.2: net.minecraft.client.resources.IModelCustomLoader/ICustomModelLoader
 * In 1.7.10: Different model loading system
 */
public interface ICustomModelLoader {

    boolean accepts(ResourceLocation modelLocation);

    IModel loadModel(ResourceLocation modelLocation) throws Exception;

    void onResourceManagerReload(net.minecraft.client.resources.IResourceManager resourceManager);
}
