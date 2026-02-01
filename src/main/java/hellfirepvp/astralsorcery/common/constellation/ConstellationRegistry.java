/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Constellation registry
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Registry for all constellations
 *
 * 1.7.10 Migration:
 * - Simplified registration system
 * - No GameStages integration
 */
public class ConstellationRegistry {

    private static final Map<String, IConstellation> constellations = new HashMap<String, IConstellation>();
    private static final List<IConstellation> constellationList = new ArrayList<IConstellation>();

    /**
     * Register a constellation
     * 
     * @param constellation The constellation to register
     */
    public static void registerConstellation(IConstellation constellation) {
        if (constellation == null) {
            throw new IllegalArgumentException("Cannot register null constellation!");
        }

        String name = constellation.getUnlocalizedName();
        if (constellations.containsKey(name)) {
            LogHelper.warn("Duplicate constellation registration: " + name);
            return;
        }

        constellations.put(name, constellation);
        constellationList.add(constellation);

        LogHelper.debug("Registered constellation: " + name);
    }

    /**
     * Get a constellation by its unlocalized name
     * 
     * @param name The unlocalized name
     * @return The constellation, or null if not found
     */
    public static IConstellation getConstellationByName(String name) {
        return constellations.get(name);
    }

    /**
     * Get all registered constellations
     * 
     * @return Unmodifiable list of all constellations
     */
    public static List<IConstellation> getAllConstellations() {
        return Collections.unmodifiableList(constellationList);
    }

    /**
     * Get all major constellations
     * 
     * @return List of major constellations
     */
    public static java.util.List<IMajorConstellation> getMajorConstellations() {
        java.util.List<IMajorConstellation> major = new java.util.ArrayList<IMajorConstellation>();
        for (IConstellation c : constellationList) {
            if (c instanceof IMajorConstellation) {
                major.add((IMajorConstellation) c);
            }
        }
        return Collections.unmodifiableList(major);
    }

    /**
     * Get all minor constellations
     * 
     * @return List of minor constellations
     */
    public static java.util.List<IMinorConstellation> getMinorConstellations() {
        java.util.List<IMinorConstellation> minor = new java.util.ArrayList<IMinorConstellation>();
        for (IConstellation c : constellationList) {
            if (c instanceof IMinorConstellation) {
                minor.add((IMinorConstellation) c);
            }
        }
        return Collections.unmodifiableList(minor);
    }

    /**
     * Get all weak constellations (includes major)
     * 
     * @return List of weak constellations
     */
    public static java.util.List<IWeakConstellation> getWeakConstellations() {
        java.util.List<IWeakConstellation> weak = new java.util.ArrayList<IWeakConstellation>();
        for (IConstellation c : constellationList) {
            if (c instanceof IWeakConstellation) {
                weak.add((IWeakConstellation) c);
            }
        }
        return Collections.unmodifiableList(weak);
    }

    /**
     * Get the count of registered constellations
     * 
     * @return The count
     */
    public static int getConstellationCount() {
        return constellationList.size();
    }

    /**
     * Clear all registered constellations (for testing/debugging)
     */
    public static void clear() {
        constellations.clear();
        constellationList.clear();
    }

    /**
     * Check if a constellation is registered
     * 
     * @param constellation The constellation to check
     * @return true if registered
     */
    public static boolean isRegistered(IConstellation constellation) {
        return constellationList.contains(constellation);
    }
}
