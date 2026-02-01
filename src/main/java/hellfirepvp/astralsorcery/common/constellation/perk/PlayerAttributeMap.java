/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Player attribute map - Stores applied perk modifiers for a player
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Player attribute map - Stores applied perk modifiers for a player (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Tracks applied perks and modifiers</li>
 * <li>Applies/removes attribute modifiers</li>
 * <li>Calculates modifier values</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without PerkConverter system</li>
 * <li>No AttributeTypeRegistry for now</li>
 * <li>Modifier calculation simplified</li>
 * </ul>
 */
public class PlayerAttributeMap {

    private Side side;
    private Set<AbstractPerk> cacheAppliedPerks = new HashSet<>();
    private Map<String, List<PerkAttributeModifier>> attributes = new HashMap<>();

    /**
     * Create a new player attribute map
     *
     * @param side The side (CLIENT or SERVER)
     */
    PlayerAttributeMap(Side side) {
        this.side = side;
    }

    /**
     * Apply a modifier to the player
     *
     * @param player   The player
     * @param type     The attribute type
     * @param modifier The modifier to apply
     * @return true if applied, false if already present
     */
    public boolean applyModifier(EntityPlayer player, String type, PerkAttributeModifier modifier) {
        boolean noModifiers = getModifiersByType(type, modifier.getMode()).isEmpty();
        List<PerkAttributeModifier> modifiers = attributes.computeIfAbsent(type, t -> Lists.newArrayList());
        if (modifiers.contains(modifier)) {
            return false;
        }

        modifiers.add(modifier);
        LogHelper.debug("Applied modifier " + modifier.getId() + " for type " + type);
        return true;
    }

    /**
     * Remove a modifier from the player
     *
     * @param player   The player
     * @param type     The attribute type
     * @param modifier The modifier to remove
     * @return true if removed, false if not present
     */
    public boolean removeModifier(EntityPlayer player, String type, PerkAttributeModifier modifier) {
        List<PerkAttributeModifier> modifiers = attributes.get(type);
        if (modifiers == null) {
            return false;
        }

        if (modifiers.remove(modifier)) {
            boolean completelyRemoved = modifiers.isEmpty();
            LogHelper.debug("Removed modifier " + modifier.getId() + " for type " + type);
            return true;
        }
        return false;
    }

    /**
     * Mark perk as applied
     */
    boolean markPerkApplied(AbstractPerk perk) {
        return !cacheAppliedPerks.contains(perk) && cacheAppliedPerks.add(perk);
    }

    /**
     * Mark perk as removed
     */
    boolean markPerkRemoved(AbstractPerk perk) {
        return cacheAppliedPerks.remove(perk);
    }

    /**
     * Check if perk is applied
     */
    boolean isPerkApplied(AbstractPerk perk) {
        return cacheAppliedPerks.contains(perk);
    }

    /**
     * Get all applied perks
     */
    Set<AbstractPerk> getCacheAppliedPerks() {
        return cacheAppliedPerks;
    }

    /**
     * Get modifiers by type and mode
     */
    private List<PerkAttributeModifier> getModifiersByType(String type, PerkAttributeModifier.Mode mode) {
        List<PerkAttributeModifier> modifiers = attributes.getOrDefault(type, new ArrayList<>());
        return modifiers.stream()
            .filter(mod -> mod.getMode() == mode)
            .collect(Collectors.toList());
    }

    /**
     * Get modifier value for type
     *
     * @param player          The player
     * @param type            The attribute type
     * @param applicableModes The modes to apply
     * @return The calculated modifier value
     */
    public float getModifier(EntityPlayer player, String type, Collection<PerkAttributeModifier.Mode> applicableModes) {
        float mod = 1F;

        // Apply addition modifiers
        if (applicableModes.contains(PerkAttributeModifier.Mode.ADDITION)) {
            for (PerkAttributeModifier modifier : getModifiersByType(type, PerkAttributeModifier.Mode.ADDITION)) {
                mod += modifier.getFlatValue();
            }
        }

        // Apply added multiply modifiers
        if (applicableModes.contains(PerkAttributeModifier.Mode.ADDED_MULTIPLY)) {
            float multiply = mod;
            for (PerkAttributeModifier modifier : getModifiersByType(type, PerkAttributeModifier.Mode.ADDED_MULTIPLY)) {
                mod += multiply * modifier.getFlatValue();
            }
        }

        // Apply stacking multiply modifiers
        if (applicableModes.contains(PerkAttributeModifier.Mode.STACKING_MULTIPLY)) {
            for (PerkAttributeModifier modifier : getModifiersByType(
                type,
                PerkAttributeModifier.Mode.STACKING_MULTIPLY)) {
                mod *= (modifier.getFlatValue() - 1F) + 1;
            }
        }
        return mod;
    }

    /**
     * Get modifier value for type (all modes)
     */
    public float getModifier(EntityPlayer player, String type) {
        return getModifier(player, type, Arrays.asList(PerkAttributeModifier.Mode.values()));
    }

    /**
     * Get modifier value for type (single mode)
     */
    public float getModifier(EntityPlayer player, String type, PerkAttributeModifier.Mode mode) {
        return getModifier(player, type, Lists.newArrayList(mode));
    }

    /**
     * Modify a value with all applicable modifiers
     *
     * @param player The player
     * @param type   The attribute type
     * @param value  The base value
     * @return The modified value
     */
    public float modifyValue(EntityPlayer player, String type, float value) {
        for (PerkAttributeModifier mod : getModifiersByType(type, PerkAttributeModifier.Mode.ADDITION)) {
            value += mod.getFlatValue();
        }
        float multiply = value;
        for (PerkAttributeModifier mod : getModifiersByType(type, PerkAttributeModifier.Mode.ADDED_MULTIPLY)) {
            value += multiply * mod.getFlatValue();
        }
        for (PerkAttributeModifier mod : getModifiersByType(type, PerkAttributeModifier.Mode.STACKING_MULTIPLY)) {
            value *= (mod.getFlatValue() - 1F) + 1F;
        }
        return value;
    }

}
