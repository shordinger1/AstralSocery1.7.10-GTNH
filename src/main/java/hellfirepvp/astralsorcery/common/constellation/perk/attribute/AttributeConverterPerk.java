/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Attribute converter perk - Base class for perks with converters
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.constellation.perk.ProgressGatedPerk;

/**
 * Attribute converter perk - Base class for perks with converters (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Converter management</li>
 * <li>Ranged converter support</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without IConverterProvider interface</li>
 * <li>PerkConverter simplified</li>
 * </ul>
 */
public abstract class AttributeConverterPerk extends ProgressGatedPerk {

    private List<PerkConverter> converters = new ArrayList<>();

    /**
     * Create a new attribute converter perk
     */
    public AttributeConverterPerk(String name, int x, int y) {
        super(name, x, y);
    }

    /**
     * Add a converter
     */
    public <T> T addConverter(PerkConverter converter) {
        this.converters.add(converter);
        return (T) this;
    }

    /**
     * Get converters for player
     */
    public List<PerkConverter> getConverters(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(converters);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // Override in subclasses
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // Override in subclasses
    }

}
