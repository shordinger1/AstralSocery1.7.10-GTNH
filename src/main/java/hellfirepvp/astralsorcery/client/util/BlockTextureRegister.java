/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Block Texture Register - Centralized texture registration for all blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Centralized block texture registration with detailed logging
 */
@SideOnly(Side.CLIENT)
public final class BlockTextureRegister {

    private BlockTextureRegister() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static boolean registered = false;

    // ========== Marble Icons ==========
    @SideOnly(Side.CLIENT)
    public static IIcon marbleRaw;
    @SideOnly(Side.CLIENT)
    public static IIcon marbleBricks;
    @SideOnly(Side.CLIENT)
    public static IIcon marblePillar;

    // ========== Black Marble Icons ==========
    @SideOnly(Side.CLIENT)
    public static IIcon blackMarbleRaw;
    @SideOnly(Side.CLIENT)
    public static IIcon blackMarbleBricks;
    @SideOnly(Side.CLIENT)
    public static IIcon blackMarblePillar;

    // ========== Machine Icons ==========
    @SideOnly(Side.CLIENT)
    public static IIcon attunementRelay;
    @SideOnly(Side.CLIENT)
    public static IIcon starlightWell;

    // ========== Crystal Icons ==========
    @SideOnly(Side.CLIENT)
    private static IIcon[] gemStages = new IIcon[5];
    @SideOnly(Side.CLIENT)
    private static IIcon[] celestialStages = new IIcon[5];

    /**
     * Register all block textures with detailed logging
     * Call this in your block's registerBlockIcons() method
     */
    public static void registerAll(IIconRegister reg) {
        if (registered) {
            return;
        }

        hellfirepvp.astralsorcery.common.util.LogHelper
            .info("=== BlockTextureRegister: Starting texture registration ===");

        // Marble
        marbleRaw = regAndLog("blocks/marble_raw", reg);
        marbleBricks = regAndLog("blocks/marble_bricks", reg);
        marblePillar = regAndLog("blocks/marble_pillar", reg);

        // Black Marble
        blackMarbleRaw = regAndLog("blocks/black_marble_raw", reg);
        blackMarbleBricks = regAndLog("blocks/black_marble_bricks", reg);
        blackMarblePillar = regAndLog("blocks/black_marble_pillar", reg);

        // Machines
        attunementRelay = regAndLog("models/attunement_relay/attunement_relay", reg);
        starlightWell = regAndLog("models/lightwell/lightwell", reg);

        // Crystals
        gemStages[0] = regAndLog("blocks/crystal/gem_stage_1_crystal", reg);
        gemStages[1] = regAndLog("blocks/crystal/gem_stage_2_crystal", reg);
        gemStages[2] = regAndLog("blocks/crystal/gem_stage_3_moon_crystal", reg);
        gemStages[3] = regAndLog("blocks/crystal/gem_stage_3_sun_crystal", reg);
        gemStages[4] = regAndLog("blocks/crystal/gem_stage_3_earth_crystal", reg);

        celestialStages[0] = regAndLog("blocks/crystal/celestial_cluster_core", reg);
        celestialStages[1] = regAndLog("blocks/crystal/celestial_cluster_stage_1", reg);
        celestialStages[2] = regAndLog("blocks/crystal/celestial_cluster_stage_2", reg);
        celestialStages[3] = regAndLog("blocks/crystal/celestial_cluster_stage_3", reg);
        celestialStages[4] = regAndLog("blocks/crystal/celestial_cluster_stage_4", reg);

        registered = true;
        hellfirepvp.astralsorcery.common.util.LogHelper.info("=== BlockTextureRegister: Registration complete ===");
    }

    /**
     * Helper to register and log icon
     */
    private static IIcon regAndLog(String path, IIconRegister reg) {
        String fullPath = "astralsorcery:" + path;
        IIcon icon = reg.registerIcon(fullPath);

        if (icon == null) {
            hellfirepvp.astralsorcery.common.util.LogHelper.error("  [FAIL] '" + path + "' → NULL");
        } else {
            hellfirepvp.astralsorcery.common.util.LogHelper.info("  [OK] '" + path + "' → " + icon.getIconName());
        }

        return icon;
    }

    // ========== Getters ==========

    public static IIcon getMarbleRaw() {
        return marbleRaw;
    }

    public static IIcon getMarbleBricks() {
        return marbleBricks;
    }

    public static IIcon getMarblePillar() {
        return marblePillar;
    }

    public static IIcon getBlackMarbleRaw() {
        return blackMarbleRaw;
    }

    public static IIcon getBlackMarbleBricks() {
        return blackMarbleBricks;
    }

    public static IIcon getBlackMarblePillar() {
        return blackMarblePillar;
    }

    public static IIcon getAttunementRelay() {
        return attunementRelay;
    }

    public static IIcon getStarlightWell() {
        return starlightWell;
    }

    public static IIcon getGemStage(int stage) {
        if (stage < 0 || stage >= gemStages.length) {
            return gemStages[0];
        }
        return gemStages[stage];
    }

    public static IIcon getCelestialCrystalStage(int stage) {
        if (stage < 0 || stage >= celestialStages.length) {
            return celestialStages[0];
        }
        return celestialStages[stage];
    }
}
