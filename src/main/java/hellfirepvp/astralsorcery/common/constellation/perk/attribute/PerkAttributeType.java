/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk attribute type - Base class for attribute types
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;

/**
 * Perk attribute type - Base class for attribute types (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Type identification</li>
 * <li>Modifier creation</li>
 * <li>Player application tracking</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without AttributeReader system</li>
 * <li>Uses UUID for player tracking</li>
 * </ul>
 */
public class PerkAttributeType {

    protected static final Random rand = new Random();

    // May be used by subclasses to more efficiently track who's got a perk applied
    private Map<Side, List<UUID>> applicationCache = new HashMap<>();

    private final String type;
    private final boolean isOnlyMultiplicative;

    /**
     * Create a new attribute type
     *
     * @param type The type string
     */
    public PerkAttributeType(String type) {
        this(type, false);
    }

    /**
     * Create a new attribute type
     *
     * @param type                 The type string
     * @param isOnlyMultiplicative True if only multiplicative modifiers allowed
     */
    public PerkAttributeType(String type, boolean isOnlyMultiplicative) {
        this.type = type;
        this.isOnlyMultiplicative = isOnlyMultiplicative;
    }

    /**
     * Get type string
     */
    public String getTypeString() {
        return type;
    }

    /**
     * Check if multiplicative only
     */
    public boolean isMultiplicative() {
        return isOnlyMultiplicative;
    }

    /**
     * Get unlocalized name
     */
    public String getUnlocalizedName() {
        return String.format("perk.attribute.%s.name", getTypeString());
    }

    /**
     * Initialize the attribute type
     */
    protected void init() {}

    /**
     * Create a modifier
     *
     * @param modifier The modifier value
     * @param mode     The modifier mode
     * @return The created modifier
     */
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        if (isMultiplicative() && mode == PerkAttributeModifier.Mode.ADDITION) {
            throw new IllegalArgumentException("Tried creating addition-modifier for a multiplicative-only modifier!");
        }
        return new PerkAttributeModifier(getTypeString(), mode, modifier);
    }

    /**
     * Called when modifier is applied to player
     */
    public void onApply(EntityPlayer player, Side side) {
        List<UUID> applied = applicationCache.computeIfAbsent(side, s -> new ArrayList<>());
        if (!applied.contains(player.getUniqueID())) {
            applied.add(player.getUniqueID());
        }
    }

    /**
     * Called when modifier is removed from player
     */
    public void onRemove(EntityPlayer player, Side side, boolean removedCompletely) {
        if (removedCompletely) {
            List<UUID> applied = applicationCache.get(side);
            if (applied != null) {
                applied.remove(player.getUniqueID());
            }
        }
    }

    /**
     * Called when a mode is applied
     */
    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {}

    /**
     * Called when a mode is removed
     */
    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side,
        boolean removedCompletely) {}

    /**
     * Check if type is applied to player
     */
    public boolean hasTypeApplied(EntityPlayer player, Side side) {
        List<UUID> applied = applicationCache.get(side);
        return applied != null && applied.contains(player.getUniqueID());
    }

    /**
     * Clear all applications on a side
     */
    public final void clear(Side side) {
        applicationCache.remove(side);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeType that = (PerkAttributeType) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
