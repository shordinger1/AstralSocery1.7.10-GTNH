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
 * Class: PotionCheatDeath
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 *
 * 1.7.10 Migration:
 * - Extends AstralBasePotion (auto ID assignment)
 * - Marker effect for death prevention/resurrection
 * - Visual effects handled through packet system
 * - Actual death prevention implemented in living death event
 */
public class PotionCheatDeath extends AstralBasePotion {

    public static final PotionCheatDeath INSTANCE = new PotionCheatDeath();
    public static final int PHOENIX_COLOR = 0xFF5711; // Phoenix orange-red

    public PotionCheatDeath() {
        super(false, PHOENIX_COLOR); // Beneficial effect
        setIconIndex(4, 2);
        setHasStatusIcon(true);
    }

    @Override
    protected String getPotionNameKey() {
        return "effect.as.cheatdeath";
    }

}
