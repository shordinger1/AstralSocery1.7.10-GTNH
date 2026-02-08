/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Vorux perk - Ranged combat perk
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

/**
 * Vorux perk - Ranged combat perk (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Ranged damage bonuses</li>
 * <li>Projectile speed bonuses</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>TODO: Implement projectile damage modifiers</li>
 * </ul>
 */
public class PerkVorux extends ConstellationPerk {

    public PerkVorux(IConstellation constellation, int x, int y) {
        super("cst_vorux", constellation, x, y);

        // Add ranged damage modifier: +25% ranged damage
        addModifier(
            0.25F,
            PerkAttributeModifier.Mode.ADDED_MULTIPLY,
            hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry.ATTR_TYPE_RANGED_DAMAGE);
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
