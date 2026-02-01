/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.potion;

import hellfirepvp.astralsorcery.common.base.AstralBasePotion;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionTimeFreeze
 * Created by HellFirePvP
 * Date: 12.02.2018 / 23:03
 *
 * 1.7.10 Migration:
 * - Extends AstralBasePotion (auto ID assignment)
 * - Marker effect for time-related abilities
 * - Actual logic implemented in event system
 */
public class PotionTimeFreeze extends AstralBasePotion {

    public static final PotionTimeFreeze INSTANCE = new PotionTimeFreeze();
    public static final int TIMEFREEZE_COLOR = 0xB89AFF; // Light purple

    public PotionTimeFreeze() {
        super(false, TIMEFREEZE_COLOR); // Beneficial effect
        setIconIndex(1, 2);
        setHasStatusIcon(true);
    }

    @Override
    protected String getPotionNameKey() {
        return "effect.as.timefreeze";
    }

}
