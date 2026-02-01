/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Locatable interface for world positions
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.world.World;

/**
 * Interface for objects that have a world position
 * Used by constellation effects and other location-based systems
 */
public interface ILocatable {

    /**
     * Get the world this object is in
     * 
     * @return The world object
     */
    World getWorld();

    /**
     * Get the X coordinate
     * 
     * @return X position
     */
    double getX();

    /**
     * Get the Y coordinate
     * 
     * @return Y position
     */
    double getY();

    /**
     * Get the Z coordinate
     * 
     * @return Z position
     */
    double getZ();

}
