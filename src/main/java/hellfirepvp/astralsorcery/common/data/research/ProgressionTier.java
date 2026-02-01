/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Progression tier - Research progression tier levels
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

/**
 * Progression tier - Research progression tier levels (1.7.10)
 * <p>
 * <b>Tiers:</b>
 * <ul>
 * <li>DISCOVERY - Basic discovery tier</li>
 * <li>ATTUNEMENT - Constellation attunement</li>
 * <li>CONSTELLATION_CRAFT - Constellation-based crafting</li>
 * <li>TRAIT_CRAFT - Trait-based crafting</li>
 * <li>BRILLIANCE - Advanced tier</li>
 * </ul>
 */
public enum ProgressionTier {

    DISCOVERY,
    ATTUNEMENT,
    CONSTELLATION_CRAFT,
    TRAIT_CRAFT,
    BRILLIANCE;

    /**
     * Check if this tier is later or equal to another tier
     *
     * @param other The other tier to compare
     * @return true if this tier >= other tier
     */
    public boolean isThisLaterOrEqual(ProgressionTier other) {
        return this.ordinal() >= other.ordinal();
    }

    /**
     * Check if there is a next tier
     *
     * @return true if not the highest tier
     */
    public boolean hasNextTier() {
        return this.ordinal() < values().length - 1;
    }

    /**
     * Get the next tier
     *
     * @return The next tier, or this if already highest
     */
    public ProgressionTier next() {
        if (hasNextTier()) {
            return values()[this.ordinal() + 1];
        }
        return this;
    }

}
