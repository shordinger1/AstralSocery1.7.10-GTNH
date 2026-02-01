/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IWeakConstellation
 * Created by HellFirePvP
 * Date: 03.01.2017 / 13:28
 *
 * 1.7.10 Migration:
 * - Removed ConstellationEffect dependency (will implement later)
 * - getRitualEffect() returns null for now (ConstellationEffect system not yet implemented)
 */
public interface IWeakConstellation extends IConstellation {

    /**
     * Get the ritual effect for this constellation
     *
     * <p>
     * <b>1.7.10 Implementation Note:</b>
     * </p>
     * This method currently returns null. The ConstellationEffect system has not yet
     * been migrated to 1.7.10. This is a placeholder for future implementation.
     *
     * <p>
     * The ritual effect system provides special bonuses or effects when rituals
     * are performed with this constellation active. This will be implemented in a
     * future phase when the core ritual system is complete.
     * </p>
     *
     * @param origin The origin location/object for the effect
     * @return The ritual effect, or null if not yet implemented
     */
    default public Object getRitualEffect(Object origin) {
        return null;
    }

}
