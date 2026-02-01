/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Marble double slab block - full block made of two slabs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

/**
 * BlockMarbleDoubleSlab - Marble double slab (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Full block (two slabs combined)</li>
 * <li>Inherits all properties from BlockMarbleSlab</li>
 * <li>Auto-converts when two half slabs are placed together</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Only overrides isDouble() to return true</li>
 * <li>All other logic inherited from BlockMarbleSlab</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * GameRegistry.registerBlock(blockMarbleDoubleSlab, "blockMarbleDoubleSlab");
 * </pre>
 */
public class BlockMarbleDoubleSlab extends BlockMarbleSlab {

    /**
     * Constructor - double slab
     */
    public BlockMarbleDoubleSlab() {
        // Call parent constructor - inherits all settings
        super();
    }

    /**
     * Is this a double slab?
     */
    public boolean isDouble() {
        return true; // This is double slab (full block)
    }

    // NOTE: All other methods inherited from BlockMarbleSlab
    // This is the simplest possible implementation - only 26 lines in original
}
