/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ResourceLocation Register - Centralized texture location registration for entities and TileEntities
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Centralized ResourceLocation registration for entities and TileEntity renderers
 * <p>
 * This class provides static access to all ResourceLocation objects used in:
 * - Entity renderers (getEntityTexture())
 * - TileEntity renderers (bindTexture())
 * <p>
 * In 1.7.10, entities and TESRs use ResourceLocation instead of IIcon:
 * - Entity renderers return ResourceLocation from getEntityTexture()
 * - TESRs call bindTexture(ResourceLocation) before rendering
 */
@SideOnly(Side.CLIENT)
public final class ResourceLocationRegister {

    private ResourceLocationRegister() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ========== Entity Textures ==========

    // Crystal entities
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entityCrystal;

    // Spark/Effect entities
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entityFlare;
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entitySparkIllumination;
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entitySparkLiquid;
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entitySparkNocturnal;

    // Tool entities
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entityGrapplingHook;
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entityCrystalTool;
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entitySpectralTool;

    // Celestial entities
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entityShootingStar;
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entityStarburst;

    // Helper entities
    @SideOnly(Side.CLIENT)
    public static ResourceLocation entityObservatoryHelper;

    // Item entities
    @SideOnly(Side.CLIENT)
    public static ResourceLocation itemStardust;

    // ========== TESR Textures (for special TileEntity rendering effects) ==========

    // Altar effects
    @SideOnly(Side.CLIENT)
    public static ResourceLocation altarGlow;

    // Lightwell effects
    @SideOnly(Side.CLIENT)
    public static ResourceLocation lightwellGlow;

    // Chalice effects
    @SideOnly(Side.CLIENT)
    public static ResourceLocation chaliceLiquid;

    // Misc effects
    @SideOnly(Side.CLIENT)
    public static ResourceLocation beamEffect;
    @SideOnly(Side.CLIENT)
    public static ResourceLocation particleGlow;

    private static boolean registered = false;

    /**
     * Register all ResourceLocation objects
     * Call this during client initialization (ClientProxy.preInit or init)
     */
    public static void registerAll() {
        if (registered) {
            return;
        }

        hellfirepvp.astralsorcery.common.util.LogHelper
            .info("=== ResourceLocationRegister: Starting texture location registration ===");

        // Entity textures - use procedural rendering or effect textures
        // Most entities use procedural rendering and don't need PNG files
        entityCrystal = regAndLog("textures/models/crystal_big_colorless.png"); // For crystal entities
        entityFlare = regAndLog("textures/effect/flare1.png"); // Flare effect texture
        entitySparkIllumination = regAndLog("textures/effect/star1.png"); // Illumination spark
        entitySparkLiquid = regAndLog("textures/effect/square.png"); // Liquid spark
        entitySparkNocturnal = regAndLog("textures/effect/star2.png"); // Nocturnal spark
        entityGrapplingHook = regAndLog("textures/effect/hook.png"); // Grappling hook
        entityCrystalTool = regAndLog("textures/models/crystal_big_colorless.png"); // Crystal tool
        entitySpectralTool = regAndLog("textures/items/crystal_pickaxe.png"); // Spectral tool (placeholder)
        entityShootingStar = regAndLog("textures/effect/star1.png"); // Shooting star
        entityStarburst = regAndLog("textures/effect/burst1.png"); // Starburst effect
        entityObservatoryHelper = regAndLog("textures/models/observatory/observatory_base.png"); // Observatory

        // Item textures (used in entity rendering)
        itemStardust = regAndLog("textures/items/stardust.png");

        // TESR effect textures - use actual effect directory
        altarGlow = regAndLog("textures/effect/halo1.png"); // Altar glow effect
        lightwellGlow = regAndLog("textures/models/lightwell/lightwell.png"); // Lightwell glow
        chaliceLiquid = regAndLog("textures/models/chalice/chalice.png"); // Chalice liquid
        beamEffect = regAndLog("textures/effect/lightbeam.png"); // Light beam effect
        particleGlow = regAndLog("textures/effect/flarestatic.png"); // Particle glow

        registered = true;
        hellfirepvp.astralsorcery.common.util.LogHelper.info("=== ResourceLocationRegister: Registration complete ===");
    }

    /**
     * Helper to register and log ResourceLocation
     */
    private static ResourceLocation regAndLog(String path) {
        ResourceLocation loc = new ResourceLocation("astralsorcery", path);
        hellfirepvp.astralsorcery.common.util.LogHelper.info("  [OK] '" + path + "' → " + loc.toString());
        return loc;
    }

    // ========== Entity Texture Getters ==========

    public static ResourceLocation getEntityCrystal() {
        return entityCrystal;
    }

    public static ResourceLocation getEntityFlare() {
        return entityFlare;
    }

    public static ResourceLocation getEntitySparkIllumination() {
        return entitySparkIllumination;
    }

    public static ResourceLocation getEntitySparkLiquid() {
        return entitySparkLiquid;
    }

    public static ResourceLocation getEntitySparkNocturnal() {
        return entitySparkNocturnal;
    }

    public static ResourceLocation getEntityGrapplingHook() {
        return entityGrapplingHook;
    }

    public static ResourceLocation getEntityCrystalTool() {
        return entityCrystalTool;
    }

    public static ResourceLocation getEntitySpectralTool() {
        return entitySpectralTool;
    }

    public static ResourceLocation getEntityShootingStar() {
        return entityShootingStar;
    }

    public static ResourceLocation getEntityStarburst() {
        return entityStarburst;
    }

    public static ResourceLocation getEntityObservatoryHelper() {
        return entityObservatoryHelper;
    }

    public static ResourceLocation getItemStardust() {
        return itemStardust;
    }

    // ========== TESR Effect Texture Getters ==========

    public static ResourceLocation getAltarGlow() {
        return altarGlow;
    }

    public static ResourceLocation getLightwellGlow() {
        return lightwellGlow;
    }

    public static ResourceLocation getChaliceLiquid() {
        return chaliceLiquid;
    }

    public static ResourceLocation getBeamEffect() {
        return beamEffect;
    }

    public static ResourceLocation getParticleGlow() {
        return particleGlow;
    }
}
