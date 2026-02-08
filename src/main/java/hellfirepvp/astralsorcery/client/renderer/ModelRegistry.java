/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Model Registry - Registers OBJ models for blocks and items
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Model Registry for OBJ models
 * <p>
 * NOTE: This class is kept for compatibility but is no longer used.
 * All OBJ rendering is now handled by AstralRenderLoader using Forge's WavefrontObject directly.
 * <p>
 * Custom OBJ loaders (ObjBlockRenderer, ObjModelLoader, ObjRenderHelper) have been removed.
 * All OBJ rendering now uses:
 * - Forge's AdvancedModelLoader.loadModel() to load OBJ models
 * - Forge's WavefrontObject.renderPart() for multi-texture rendering
 * - TESR (TileEntitySpecialRenderer) or ISimpleBlockRenderingHandler for rendering
 */
@SideOnly(Side.CLIENT)
@Deprecated
public class ModelRegistry {

    /**
     * Register all OBJ models
     * NOTE: This method is deprecated and does nothing.
     * OBJ model loading is now handled by AstralRenderLoader.
     *
     * @deprecated Use AstralRenderLoader.init() instead
     */
    @Deprecated
    public static void registerModels() {
        LogHelper.info("OBJ Model System: Now handled by AstralRenderLoader using Forge's WavefrontObject");
        LogHelper.info("  - Custom OBJ loaders (ObjBlockRenderer, ObjModelLoader, ObjRenderHelper) removed");
        LogHelper.info("  - All OBJ rendering uses Forge's built-in WavefrontObject");
        LogHelper.info("  - Multi-texture rendering via renderPart() method");
    }
}
