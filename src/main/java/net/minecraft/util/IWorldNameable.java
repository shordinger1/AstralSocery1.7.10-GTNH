/*******************************************************************************
 * Compatibility Interface for IWorldNameable (1.12.2) in 1.7.10
 * This interface provides a way for objects to have a custom display name.
 * Similar to methods in IInventory, but for non-inventory objects.
 ******************************************************************************/

package net.minecraft.util;

import javax.annotation.Nullable;

/**
 * Interface for objects that can have custom names.
 * Provides 1.12.2 API compatibility for 1.7.10.
 */
public interface IWorldNameable {

    /**
     * Returns true if this object has a custom name.
     */
    boolean hasCustomName();

    /**
     * Get the name of this object.
     */
    String getName();

    /**
     * Get the display name as a chat component.
     */
    @Nullable
    IChatComponent getDisplayName();
}
