/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Wand Augment - Constellation-based wand enhancements
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

/**
 * Wand augment types (1.7.10)
 * <p>
 * Each augment corresponds to a major constellation and provides
 * special abilities when applied to a wand.
 * <p>
 * <b>Augments:</b>
 * <ul>
 * <li>AEVITAS - Creates vanishing blocks around player</li>
 * <li>DISCIDIA - Attack damage bonus (future)</li>
 * <li>VICIO - Launch player forward</li>
 * <li>ARMARA - Defensive stance</li>
 * <li>EVORSIO - Attack damage bonus (future)</li>
 * </ul>
 */
public enum WandAugment {

    AEVITAS,
    DISCIDIA,
    VICIO,
    ARMARA,
    EVORSIO;

    /**
     * Get the augment associated with a constellation
     *
     * @param cst The constellation
     * @return The augment, or null if not found
     */
    @Nullable
    public static WandAugment getByConstellation(IMajorConstellation cst) {
        if (cst == null) {
            return null;
        }
        for (WandAugment wa : values()) {
            if (cst.getUnlocalizedName()
                .endsWith(wa.name()
                    .toLowerCase())) {
                return wa;
            }
        }
        return null;
    }

    /**
     * Get augment by name
     *
     * @param name The augment name
     * @return The augment, or null if not found
     */
    @Nullable
    public static WandAugment getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Check if constellation matches this augment
     *
     * @param cst The constellation to check
     * @return true if matches
     */
    public boolean matchesConstellation(IMajorConstellation cst) {
        if (cst == null) {
            return false;
        }
        return this == getByConstellation(cst);
    }
}
