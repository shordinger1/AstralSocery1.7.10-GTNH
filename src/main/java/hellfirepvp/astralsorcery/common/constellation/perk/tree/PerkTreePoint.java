/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Perk tree point - Represents a point in the perk tree
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree;

import java.awt.Point;
import java.util.Objects;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;

/**
 * Perk tree point - Represents a point in the perk tree (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Position-based layout</li>
 * <li>Allocation status tracking</li>
 * <li>Render size configuration</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified rendering - no batch rendering for now</li>
 * <li>No PerkRender interface</li>
 * </ul>
 */
public class PerkTreePoint<T extends AbstractPerk> {

    private Point offset;
    private final T perk;
    private int renderSize;

    private static final int spriteSize = 11;

    public PerkTreePoint(T perk, Point offset) {
        this.offset = offset;
        this.perk = perk;
        this.renderSize = spriteSize;
    }

    /**
     * Set render size
     */
    public void setRenderSize(int renderSize) {
        this.renderSize = renderSize;
    }

    /**
     * Get render size
     */
    public int getRenderSize() {
        return renderSize;
    }

    /**
     * Get perk
     */
    public T getPerk() {
        return perk;
    }

    /**
     * Get offset position
     */
    public Point getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkTreePoint<?> that = (PerkTreePoint<?>) o;
        return Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset);
    }

    /**
     * Allocation status enum
     */
    public static enum AllocationStatus {

        UNALLOCATED,
        ALLOCATED,
        UNLOCKABLE

    }

}
