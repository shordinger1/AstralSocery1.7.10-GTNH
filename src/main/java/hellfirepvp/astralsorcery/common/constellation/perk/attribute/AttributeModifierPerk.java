/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Attribute modifier perk - Perk with attribute modifiers
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Attribute modifier perk - Perk with attribute modifiers (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Attribute modifier management</li>
 * <li>Automatic tooltip generation</li>
 * <li>Modifier application/removal</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without PerkConverter system</li>
 * <li>No AttributeTypeRegistry (uses string-based type lookup)</li>
 * </ul>
 */
public class AttributeModifierPerk extends AttributeConverterPerk {

    private List<PerkAttributeModifier> typeModifierList = new ArrayList<>();

    /**
     * Create a new attribute modifier perk
     */
    public AttributeModifierPerk(String name, int x, int y) {
        super(name, x, y);
    }

    /**
     * Add a modifier by type string
     *
     * @param modifier The modifier value
     * @param mode     The modifier mode
     * @param type     The attribute type string
     * @return The created modifier, or null if type invalid
     */
    @Nullable
    public <T extends PerkAttributeModifier> T addModifier(float modifier, PerkAttributeModifier.Mode mode,
        String type) {
        // TODO: Implement AttributeTypeRegistry when ready
        // PerkAttributeType attrType = AttributeTypeRegistry.getType(type);
        // if (attrType != null) {
        // return addModifier((T) attrType.createModifier(modifier, mode));
        // }
        // For now, create modifier directly
        PerkAttributeModifier mod = new PerkAttributeModifier(type, mode, modifier);
        return addModifier((T) mod);
    }

    /**
     * Add a modifier
     *
     * @param modifier The modifier to add
     * @return The added modifier
     */
    @Nullable
    protected <T extends PerkAttributeModifier> T addModifier(T modifier) {
        typeModifierList.add(modifier);
        return modifier;
    }

    /**
     * Get modifiers for player
     */
    protected Collection<PerkAttributeModifier> getModifiers(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(this.typeModifierList);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        super.applyPerkLogic(player, side);

        LogHelper.debug("Applying modifiers of " + getRegistryName());

        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            LogHelper.debug("Applying modifier " + modifier.getId());
            if (!attr.applyModifier(player, modifier.getAttributeType(), modifier)) {
                LogHelper.warn("Could not apply modifier " + modifier.getId() + " - already applied!");
            }
        }
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        LogHelper.debug("Removing modifiers of " + getRegistryName());

        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            LogHelper.debug("Removing modifier " + modifier.getId());
            if (!attr.removeModifier(player, modifier.getAttributeType(), modifier)) {
                LogHelper.warn("Could not remove modifier " + modifier.getId() + " - not applied!");
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        Collection<PerkAttributeModifier> modifiers = this
            .getModifiers(Minecraft.getMinecraft().thePlayer, Side.CLIENT);
        boolean addEmptyLine = !modifiers.isEmpty();

        if (canSeeClient()) {
            for (PerkAttributeModifier modifier : modifiers) {
                String modifierDisplay = modifier.getLocalizedDisplayString();
                if (modifierDisplay != null) {
                    tooltip.add(modifierDisplay);
                } else {
                    addEmptyLine = false;
                }
            }
        }

        return addEmptyLine;
    }

}
