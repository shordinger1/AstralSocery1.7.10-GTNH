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
 * Class: PotionDropModifier
 * Created by HellFirePvP
 * Date: 11.02.2018 / 17:17
 *
 * 1.7.10 Migration:
 * - Extends AstralBasePotion (auto ID assignment)
 * - Marker effect for loot modification
 * - Actual drop logic implemented in loot event handlers
 */
public class PotionDropModifier extends AstralBasePotion {

    public static final PotionDropModifier INSTANCE = new PotionDropModifier();
    public static final int DROP_COLOR = 0xFFD114; // Golden yellow

    public PotionDropModifier() {
        super(false, DROP_COLOR); // Beneficial effect
        setIconIndex(2, 2);
        setHasStatusIcon(true);
    }

    @Override
    protected String getPotionNameKey() {
        return "effect.as.dropmod";
    }

}
