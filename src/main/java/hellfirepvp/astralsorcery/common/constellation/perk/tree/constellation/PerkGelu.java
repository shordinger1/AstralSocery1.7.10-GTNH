/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Gelu perk - Mining perk
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

/**
 * Gelu perk - Mining perk (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Mining speed bonuses</li>
 * <li>Tool efficiency</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>TODO: Implement mining speed modifiers</li>
 * </ul>
 */
public class PerkGelu extends ConstellationPerk {

    public PerkGelu(IConstellation constellation, int x, int y) {
        super("cst_gelu", constellation, x, y);

        // Add mining speed modifier: +20% mining speed
        addModifier(
            0.2F,
            PerkAttributeModifier.Mode.ADDED_MULTIPLY,
            hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry.ATTR_TYPE_MINING_SPEED);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // Modifiers are automatically applied by AttributeModifierPerk
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // Modifiers are automatically removed by AttributeModifierPerk
    }

}
