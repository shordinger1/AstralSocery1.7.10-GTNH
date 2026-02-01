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
 * Class: MoonPhase
 * Created by HellFirePvP
 * Date: 17.11.2016 / 02:45
 *
 * 1.7.10 Migration: No API changes needed
 */
public enum MoonPhase {

    FULL,
    WANING3_4,
    WANING1_2,
    WANING1_4,
    NEW,
    WAXING1_4,
    WAXING1_2,
    WAXING3_4;

    /**
     * Get current moon phase from world time
     * 1.7.10: Uses worldTime directly
     *
     * @param worldTime The world time in ticks
     * @return Current moon phase
     */
    public static MoonPhase getPhaseFromWorldTime(long worldTime) {
        // In vanilla 1.7.10, moon phase cycles every 8 days
        // phase = (int)((worldTime / 24000L) % 8L) & 7
        long phase = (worldTime / 24000L) % 8L;
        return values()[(int) phase];
    }

}
