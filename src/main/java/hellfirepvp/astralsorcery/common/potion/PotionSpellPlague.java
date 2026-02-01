/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.base.AstralBasePotion;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionSpellPlague
 * Created by HellFirePvP
 * Date: 07.07.2017 / 10:51
 *
 * 1.7.10 Migration:
 * - Extends AstralBasePotion (auto ID assignment)
 * - Removed Capability system dependency (not available in 1.7.10)
 * - Uncurable by milk/default items
 * - Effect logic handled through event system
 */
public class PotionSpellPlague extends AstralBasePotion {

    public static final PotionSpellPlague INSTANCE = new PotionSpellPlague();

    public PotionSpellPlague() {
        super(true, 0x680190); // Bad effect, deep purple
        setIconIndex(3, 2);
        setHasStatusIcon(true);
    }

    @Override
    protected String getPotionNameKey() {
        return "effect.as.spellplague";
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        // 1.7.10: Removed Capability system dependency
        // Original code had commented-out SpellPlague Capability logic
        // Effect is now handled through event listeners
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    /**
     * Get curative items for this potion
     * <p>
     * Spell Plague cannot be cured by normal means (milk, etc.)
     * <p>
     * <b>1.7.10 Note</b>: In 1.7.10, potions are curable by milk by default.
     * To make this potion incurable, the event system must intercept cure attempts.
     * This method is provided for API compatibility with future versions.
     *
     * @return Empty list (no curative items)
     */
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList(); // Cannot be cured by normal means
    }

}
