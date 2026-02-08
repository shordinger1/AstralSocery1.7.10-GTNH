/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.registry.RegistryConstellations;

/**
 * Centralized access to constellation instances
 * <p>
 * This class provides a unified interface to access all constellation instances.
 * The actual constellation registration and initialization is handled by
 * {@link hellfirepvp.astralsorcery.common.registry.RegistryConstellations}.
 * <p>
 * <b>1.7.10 Implementation:</b>
 * This is a convenience wrapper around RegistryConstellations to maintain
 * API compatibility with the 1.12.2 version.
 *
 * @author HellFirePvP
 * @date 22.11.2016 / 12:52
 */
public class Constellations {

    // ========== Major Constellations ==========

    /**
     * Discidia - The constellation of offense and combat
     * Associated with aggressive actions and damage
     */
    public static IMajorConstellation discidia;

    /**
     * Armara - The constellation of defense and protection
     * Associated with defensive buffs and shielding
     */
    public static IMajorConstellation armara;

    /**
     * Vicio - The constellation of travel and movement
     * Associated with speed, teleportation, and mobility
     */
    public static IMajorConstellation vicio;

    /**
     * Aevitas - The constellation of creation and life
     * Associated with growth, agriculture, and biological processes
     */
    public static IMajorConstellation aevitas;

    /**
     * Evorsio - The constellation of destruction and breaking
     * Associated with mining, destruction, and harvesting
     */
    public static IMajorConstellation evorsio;

    /**
     * Vectras - The constellation of teleportation
     * (Added in later versions)
     */
    public static IMajorConstellation vectras;

    // ========== Weak Constellations ==========

    /**
     * Lucerna - Light constellation
     * Associated with illumination and brightness
     */
    public static IWeakConstellation lucerna;

    /**
     * Mineralis - Mineral constellation
     * Associated with ores, gems, and mining
     */
    public static IWeakConstellation mineralis;

    /**
     * Horologium - Time constellation
     * Associated with time manipulation
     */
    public static IWeakConstellation horologium;

    /**
     * Octans - Navigation constellation
     * Associated with direction and positioning
     */
    public static IWeakConstellation octans;

    /**
     * Bootes - Movement constellation
     * Associated with physical motion
     */
    public static IWeakConstellation bootes;

    /**
     * Fornax - Heat constellation
     * Associated with fire and heat
     */
    public static IWeakConstellation fornax;

    /**
     * Pelotrio - Triple constellation
     * Associated with tripartite effects
     */
    public static IWeakConstellation pelotrio;

    // ========== Minor Constellations ==========

    /**
     * Gelu - Cold constellation
     * Associated with ice, cold, and freezing
     */
    public static IMinorConstellation gelu;

    /**
     * Ulteria - Mystery constellation
     * Associated with hidden knowledge
     */
    public static IMinorConstellation ulteria;

    /**
     * Alcara - Twilight constellation
     * Associated with dusk and dawn
     */
    public static IMinorConstellation alcara;

    /**
     * Vorux - Night constellation
     * Associated with darkness and night
     */
    public static IMinorConstellation vorux;

    /**
     * Initialize constellation references from RegistryConstellations
     * <p>
     * This method should be called after RegistryConstellations.init() to
     * populate the static fields in this class with the registered constellation
     * instances.
     */
    public static void init() {
        // Major constellations
        discidia = RegistryConstellations.discidia;
        armara = RegistryConstellations.armara;
        vicio = RegistryConstellations.vicio;
        aevitas = RegistryConstellations.aevitas;
        evorsio = RegistryConstellations.evorsio;
        vectras = RegistryConstellations.vectras;

        // Weak constellations
        lucerna = RegistryConstellations.lucerna;
        mineralis = RegistryConstellations.mineralis;
        horologium = RegistryConstellations.horologium;
        octans = RegistryConstellations.octans;
        bootes = RegistryConstellations.bootes;
        fornax = RegistryConstellations.fornax;
        pelotrio = RegistryConstellations.pelotrio;

        // Minor constellations
        gelu = RegistryConstellations.gelu;
        ulteria = RegistryConstellations.ulteria;
        alcara = RegistryConstellations.alcara;
        vorux = RegistryConstellations.vorux;
    }
}
