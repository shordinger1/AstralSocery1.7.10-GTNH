/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Marble stairs block - decorative stairs
 ******************************************************************************/
package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.BlockStairs;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;

/**
 * BlockMarbleStairs - Marble stairs (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Decorative stairs based on BRICKS variant</li>
 * <li>Translucent (lightOpacity 0)</li>
 * <li>Auto-rotation on placement</li>
 * <li>Inherits texture from marble bricks</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockStairs constructor: (Block block, int metadata)</li>
 * <li>BlockStairs automatically handles icon retrieval from base block</li>
 * <li>No need to override getIcon() - parent class handles it</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * GameRegistry.registerBlock(blockMarbleStairs, "blockMarbleStairs");
 * </pre>
 */
public class BlockMarbleStairs extends BlockStairs {

    /**
     * Constructor
     * Creates stairs based on black marble bricks block.
     */
    public BlockMarbleStairs() {
        super(
            BlocksAS.blockBlackMarble, // Base block
            1 // Metadata for BRICKS variant
        );

        // Override hardness/resistance
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(soundTypeStone);
        setLightOpacity(0);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    // NOTE: BlockStairs in 1.7.10 automatically handles:
    // - Icon retrieval from base block via getIcon(side, meta)
    // - Rotation on placement (metadata 0-3 for facing)
    // - Upside-down placement (metadata 4-7)
    // - Collision box adjustment
    // - Rendering adjustments
    //
    // The parent BlockStairs.getIcon() method already correctly retrieves
    // icons from the base block (field_150149_b) with the specified metadata.
}
