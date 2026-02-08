/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

/**
 * Integration management for Astral Sorcery
 * <p>
 * This class provides a centralized location for managing mod integrations.
 * Individual mod integrations are handled through the ModIntegration system.
 * <p>
 * <b>1.7.10 Available Integrations:</b>
 * <ul>
 * <li>GregTech (GTNH)</li>
 * <li>BloodMagic</li>
 * <li>Botania</li>
 * <li>Thaumcraft</li>
 * <li>NEI (Not Enough Items)</li>
 * <li>CraftTweaker (Minetweaker)</li>
 * </ul>
 *
 * @author HellFirePvP
 * @date 10.01.2017 / 23:16
 */
public class Integrations {

    /**
     * Integration states
     * <p>
     * These fields are populated during mod initialization to indicate
     * which integration mods are currently loaded.
     */
    public static boolean GREGTECH_LOADED = false;
    public static boolean BLOODMAGIC_LOADED = false;
    public static boolean BOTANIA_LOADED = false;
    public static boolean THAUMCRAFT_LOADED = false;
    public static boolean NEI_LOADED = false;
    public static boolean CRAFTTWEAKER_LOADED = false;

    /**
     * Initialize integration states
     * <p>
     * Called during FML pre-initialization to detect which mods are loaded.
     */
    public static void init() {
        // Integration detection handled by FML
        // Individual integrations initialized in ModIntegration classes

        // These are set by the respective ModIntegration classes
    }

}
