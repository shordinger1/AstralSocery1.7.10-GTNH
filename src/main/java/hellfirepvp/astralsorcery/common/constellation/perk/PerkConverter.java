/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk converter - Converts perk modifiers
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

/**
 * Perk converter - Converts perk modifiers (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Modifier conversion</li>
 * <li>Extra modifier generation</li>
 * <li>Application/removal hooks</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without PlayerProgress dependency</li>
 * <li>No ranged converter support for now</li>
 * </ul>
 */
public abstract class PerkConverter {

    /**
     * Get converter ID
     */
    public abstract String getId();

    /**
     * Convert a modifier
     *
     * @param player     The player
     * @param modifier   The original modifier
     * @param owningPerk The perk that owns this modifier
     * @return The converted modifier
     */
    public PerkAttributeModifier convertModifier(EntityPlayer player, PerkAttributeModifier modifier,
        AbstractPerk owningPerk) {
        return modifier; // Default: no conversion
    }

    /**
     * Gain extra modifiers from a base modifier
     *
     * @param player     The player
     * @param modifier   The base modifier
     * @param owningPerk The perk that owns this modifier
     * @return Collection of extra modifiers
     */
    public Collection<PerkAttributeModifier> gainExtraModifiers(EntityPlayer player, PerkAttributeModifier modifier,
        AbstractPerk owningPerk) {
        return Collections.emptyList(); // Default: no extra modifiers
    }

    /**
     * Called when converter is applied
     */
    public void onApply(EntityPlayer player, Side side) {}

    /**
     * Called when converter is removed
     */
    public void onRemove(EntityPlayer player, Side side) {}

}
