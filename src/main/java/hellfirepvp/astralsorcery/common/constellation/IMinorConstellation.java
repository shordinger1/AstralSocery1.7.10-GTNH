/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMinorConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:09
 *
 * 1.7.10 Migration: No changes needed
 */
public interface IMinorConstellation extends IConstellation {

    /**
     * Get the moon phases when this constellation can appear
     * 
     * @param rSeed Random seed for phase shifting
     * @return List of applicable moon phases
     */
    public List<MoonPhase> getShowupMoonPhases(long rSeed);

}
