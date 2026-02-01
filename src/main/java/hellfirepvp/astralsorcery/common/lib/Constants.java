/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * This is the main constants file for Astral Sorcery mod for Minecraft 1.7.10
 * Based on the 1.12.2 version analysis and adapted for 1.7.10 Forge.
 *
 * Copyright (c) 2026 Astral Sorcery Port Team
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

/**
 * Mod constants for Astral Sorcery
 */
public final class Constants {

    private Constants() {} // Prevent instantiation

    // Mod identification
    public static final String MODID = "astralsorcery";
    public static final String MODNAME = "Astral Sorcery";
    public static final String VERSION = "1.7.10-1.0.0-GTNH";
    public static final String RESOURCE_DOMAIN = "astralsorcery";
    public static final String RESOURCE_ROOT = MODID + ":";

    // Update channel
    public static final String UPDATE_CHANNEL = "beta";

    // Dependencies
    public static final String DEPENDENCIES = "" + "after:gregtech;"
        + "after:BloodMagic;"
        + "after:Botania;"
        + "after:Thaumcraft;"
        + "after:ThaumicTinkerer;";

    // Accepted Minecraft versions
    public static final String ACCEPTED_VERSIONS = "[1.7.10]";

    // Client-side only marker
    public static final String CLIENT_SIDE_ONLY = "Client-side only operation";

    // Server-side only marker
    public static final String SERVER_SIDE_ONLY = "Server-side only operation";

    // Proxy paths
    public static final String CLIENT_PROXY = "hellfirepvp.astralsorcery.client.ClientProxy";
    public static final String SERVER_PROXY = "hellfirepvp.astralsorcery.proxy.CommonProxy";

    // GUI IDs
    public static final int GUI_ID_ALTAR = 0;
    public static final int GUI_ID_OBSERVATORY = 1;
    public static final int GUI_ID_TREE_BEACON = 2;
    public static final int GUI_ID_CELESTIAL_GATEWAY = 3;
    // Well does not have a GUI - removed GUI_ID_WELL

    // Network constants
    public static final String NETWORK_CHANNEL = "AS_MAIN";
    public static final int NETWORK_VERSION = 1;

    // Config categories
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_CLIENT = "client";
    public static final String CATEGORY_SERVER = "server";
    public static final String CATEGORY_DEBUG = "debug";

    // Debug flags
    public static boolean IS_DEVMODE = false;
    public static boolean IS_DEBUG = false;

    // Creative tabs
    public static final String CREATIVE_TAB_NAME = "astralsorcery";

    // Default values
    public static final int DEFAULT_ENCHANTMENT_ID_BASE = 100;

    // Integration checks
    public static boolean hasGregTech = false;
    public static boolean hasBloodMagic = false;
    public static boolean hasBotania = false;
    public static boolean hasThaumcraft = false;

    /**
     * Called during mod loading to set up integration flags
     */
    public static void setupIntegrations() {
        // These will be set during FML loading
        // Example: hasGregTech = Loader.isModLoaded("gregtech");
    }
}
