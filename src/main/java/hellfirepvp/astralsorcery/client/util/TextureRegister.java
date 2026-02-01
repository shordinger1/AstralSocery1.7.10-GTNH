/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Simple icon registration - direct texture path mapping
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Simple texture registration helper
 * <p>
 * Directly maps texture names to IIcons without complex configuration.
 * <p>
 * Usage in Block classes:
 * 
 * <pre>
 * {@literal @}SideOnly(Side.CLIENT)
 * private IIcon icon;
 *
 * {@literal @}Override
 * {@literal @}SideOnly(Side.CLIENT)
 * public void registerBlockIcons(IIconRegister reg) {
 *     icon = TextureRegister.registerBlockIcon(reg, "black_marble_bricks");
 * }
 * </pre>
 */
@SideOnly(Side.CLIENT)
public final class TextureRegister {

    private TextureRegister() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Register a block icon
     * <p>
     * Texture path: assets/astralsorcery/textures/blocks/{textureName}.png
     *
     * @param reg         The IIconRegister
     * @param textureName The texture file name without .png extension
     * @return Registered IIcon
     *
     *         Example:
     *         registerBlockIcon(reg, "black_marble_bricks")
     *         → assets/astralsorcery/textures/blocks/black_marble_bricks.png
     */
    public static IIcon registerBlockIcon(IIconRegister reg, String textureName) {
        String fullPath = "astralsorcery:blocks/" + textureName;
        return reg.registerIcon(fullPath);
    }

    /**
     * Register an item icon
     * <p>
     * Texture path: assets/astralsorcery/textures/items/{textureName}.png
     *
     * @param reg         The IIconRegister
     * @param textureName The texture file name without .png extension
     * @return Registered IIcon
     *
     *         Example:
     *         registerItemIcon(reg, "crystal_pickaxe")
     *         → assets/astralsorcery/textures/items/crystal_pickaxe.png
     */
    public static IIcon registerItemIcon(IIconRegister reg, String textureName) {
        String fullPath = "astralsorcery:items/" + textureName;
        return reg.registerIcon(fullPath);
    }

    /**
     * Register an icon with custom mod ID
     * <p>
     * For cross-mod texture references
     *
     * @param reg         The IIconRegister
     * @param modId       The mod ID (e.g., "minecraft", "gregtech")
     * @param textureName The texture path
     * @return Registered IIcon
     *
     *         Example:
     *         registerIcon(reg, "minecraft", "stone")
     *         → assets/minecraft/textures/blocks/stone.png
     */
    public static IIcon registerIcon(IIconRegister reg, String modId, String textureName) {
        String fullPath = modId + ":" + textureName;
        return reg.registerIcon(fullPath);
    }

    /**
     * Check if a texture resource exists
     *
     * @param modId       The mod ID
     * @param texturePath The texture path (e.g., "blocks/black_marble_bricks")
     * @return true if the texture exists
     */
    public static boolean textureExists(String modId, String texturePath) {
        try {
            ResourceLocation location = new ResourceLocation(modId, "textures/" + texturePath + ".png");
            net.minecraft.client.Minecraft.getMinecraft()
                .getResourceManager()
                .getAllResources(location);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the full resource location for a texture
     *
     * @param modId       The mod ID
     * @param texturePath The texture path
     * @return ResourceLocation for the texture
     */
    public static ResourceLocation getResourceLocation(String modId, String texturePath) {
        return new ResourceLocation(modId, "textures/" + texturePath + ".png");
    }

    // ========== Model Texture Registration ==========

    /**
     * Register a model texture from textures/models/ directory
     * <p>
     * For complex blocks, entities, and TileEntities that use pre-rendered model textures.
     * <p>
     * Texture path: assets/astralsorcery/textures/models/{texturePath}.png
     *
     * @param reg         The IIconRegister
     * @param texturePath The texture path relative to models/ (e.g., "altar/altar_1_top")
     * @return Registered IIcon
     *
     *         Example:
     *         registerModelIcon(reg, "altar/altar_1_top")
     *         → assets/astralsorcery/textures/models/altar/altar_1_top.png
     */
    public static IIcon registerModelIcon(IIconRegister reg, String texturePath) {
        String fullPath = "astralsorcery:models/" + texturePath;
        return reg.registerIcon(fullPath);
    }

    /**
     * Register all icons for a multi-face block model
     * <p>
     * Convenience method for blocks with separate top/bottom/side textures in models/.
     *
     * @param reg       The IIconRegister
     * @param modelPath The model directory (e.g., "altar/altar_1")
     * @return IIcon array with [bottom, top, side]
     *
     *         Example:
     *         registerPillarModel(reg, "altar/altar_1")
     *         → [altar_1_bottom, altar_1_top, altar_1_side]
     */
    public static IIcon[] registerPillarModel(IIconRegister reg, String modelPath) {
        IIcon[] icons = new IIcon[3];
        icons[0] = registerModelIcon(reg, modelPath + "_bottom");
        icons[1] = registerModelIcon(reg, modelPath + "_top");
        icons[2] = registerModelIcon(reg, modelPath + "_side");
        return icons;
    }

    /**
     * Register all icons for a complex machine model
     * <p>
     * For machines with base and glass components (e.g., attunement_relay, lightwell).
     *
     * @param reg         The IIconRegister
     * @param machineName The machine name (e.g., "attunement_relay/attunement_relay")
     * @return IIcon array with [base, glass]
     *
     *         Example:
     *         registerMachineModel(reg, "attunement_relay/attunement_relay")
     *         → [attunement_relay, attunement_relay_glass]
     */
    public static IIcon[] registerMachineModel(IIconRegister reg, String machineName) {
        IIcon[] icons = new IIcon[2];
        icons[0] = registerModelIcon(reg, machineName);
        icons[1] = registerModelIcon(reg, machineName + "_glass");
        return icons;
    }
}
