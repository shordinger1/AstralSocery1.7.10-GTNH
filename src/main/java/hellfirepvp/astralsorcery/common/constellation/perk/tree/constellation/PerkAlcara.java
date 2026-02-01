/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Alcara perk - Experience conversion perk
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.constellation;

import net.minecraft.entity.player.EntityPlayer;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

/**
 * Alcara perk - Experience conversion perk (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Converts perk XP to perk effect</li>
 * <li>Focus category (only one can be active)</li>
 * </ul>
 */
public class PerkAlcara extends ConstellationPerk {

    public PerkAlcara(IConstellation constellation, int x, int y) {
        super("cst_alcara", constellation, x, y);
        setCategory(CATEGORY_FOCUS);

        // Add converter: converts perk XP to perk effect at 50% rate
        this.addConverter(new PerkConverter() {

            @Override
            public String getId() {
                return "alcara_xp_convert";
            }

            @Override
            public PerkAttributeModifier convertModifier(EntityPlayer player, PerkAttributeModifier modifier,
                AbstractPerk owningPerk) {
                if (modifier.getAttributeType()
                    .equals(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP)) {
                    float value = modifier.getFlatValue();
                    switch (modifier.getMode()) {
                        case ADDITION:
                        case ADDED_MULTIPLY:
                            // Create new modifier with converted type and value
                            return new PerkAttributeModifier(
                                AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT,
                                modifier.getMode(),
                                value * 0.5F);
                        case STACKING_MULTIPLY:
                            float val = value - 1;
                            val *= 0.5F;
                            return new PerkAttributeModifier(
                                AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT,
                                modifier.getMode(),
                                val + 1F);
                        default:
                            break;
                    }
                }
                return modifier;
            }
        });
    }

    @Override
    protected void applyPerkLogic(net.minecraft.entity.player.EntityPlayer player, cpw.mods.fml.relauncher.Side side) {
        // Converter handles all logic
    }

    @Override
    protected void removePerkLogic(net.minecraft.entity.player.EntityPlayer player, cpw.mods.fml.relauncher.Side side) {
        // Converter handles all logic
    }

}
