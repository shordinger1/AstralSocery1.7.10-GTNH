/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Bore block - Automated drilling machine block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockBore - Bore machine main block (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Automated drilling machine</li>
 * <li>Requires bore head block below to function</li>
 * <li>BlockContainer with TileEntity</li>
 * <li>Multiblock structure observer</li>
 * <li>Can mine liquids or create vortexes</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Material: Wood</li>
 * <li>BlockContainer with createNewTileEntity(World, int)</li>
 * <li>No BlockPos - uses (x, y, z) coordinates</li>
 * <li>No IBlockState - uses metadata directly</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had:
 * <ul>
 * <li>BlockStructureObserver interface - multiblock structure verification</li>
 * <li>onBlockActivated() - places bore head from player's hand</li>
 * <li>isSideSolid(), isTopSolid() - 1.8+ methods</li>
 * <li>TileBore with drilling logic</li>
 * <li>Liquid mining and vortex creation</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // Place bore block
 * // Right-click with bore head item to attach it below
 * // Bore will automatically drill based on configuration
 * </pre>
 */
public class BlockBore extends BlockContainer {

    /**
     * Constructor
     */
    public BlockBore() {
        super(Material.wood);

        setHardness(3.0F);
        setResistance(25.0F);
        setStepSound(soundTypeWood);
        setHarvestLevel("axe", 2); // Stone tier or higher
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.GOLD specifically
        // Using default wood color
    }

    /**
     * Is this a full block?
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * Does this block render normally?
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Get render type - use TESR for OBJ model rendering
     */
    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    /**
     * On block activated - place bore head below
     * 1.7.10: Simplified from 1.12.2 version
     * Removed: EnumHand parameter (off-hand is 1.9+)
     * Removed: BlockStructureObserver interface (custom multiblock system)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Check if block below is replaceable (air or replaceable)
        int belowY = y - 1;
        if (belowY >= 0) {
            net.minecraft.block.Block blockBelow = world.getBlock(x, belowY, z);
            if (blockBelow == null || blockBelow.isAir(world, x, belowY, z)
                || blockBelow.getMaterial()
                    .isReplaceable()) {

                // Check if player is holding a bore head item
                net.minecraft.item.ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
                if (held != null && held.stackSize > 0) {
                    net.minecraft.item.Item heldItem = held.getItem();

                    // Check if held item is an ItemBlock and its block is BlockBoreHead
                    // REMOVED: ((ItemBlock) heldItem).getBlock() - 1.7.10 ItemBlock doesn't have this method
                    // Instead, check if the held item's Item is the same as BlockBoreHead's Item
                    if (heldItem instanceof net.minecraft.item.ItemBlock) {
                        net.minecraft.item.ItemBlock itemBlock = (net.minecraft.item.ItemBlock) heldItem;
                        // In 1.7.10, need to check against the registered Item for BlockBoreHead
                        // Using field_150939_a which is the block field (obfuscated name for 'block' field)
                        // Or compare against ItemsAS.boreHead item if available
                        net.minecraft.block.Block heldBlock = itemBlock.field_150939_a;
                        if (heldBlock instanceof BlockBoreHead) {
                            if (!world.isRemote) {
                                // Place bore head block below
                                int meta = held.getItemDamage();
                                // 1.7.10: Use setBlock() with metadata
                                world.setBlock(x, belowY, z, heldBlock, meta, 3);

                                // Play sound
                                world.playSoundEffect(x + 0.5, belowY + 0.5, z + 0.5, "step.wood", 1F, 1F);

                                // Consume item if not in creative mode
                                if (!player.capabilities.isCreativeMode) {
                                    held.stackSize--;
                                    if (held.stackSize <= 0) {
                                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get the item dropped
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);

    }

    /**
     * Quantity dropped
     */
    public int quantityDropped(Random rand) {
        return 1;

    }

    /**
     * Create new tile entity
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileBore();
    }
    // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
    //
    // ❌ isSideSolid(IBlockState, IBlockAccess, BlockPos, EnumFacing)
    // - 1.8+ signature
    // - Returns false
    //
    // ❌ isTopSolid(IBlockState)
    // - 1.8+ method
    // - Returns false
    //
    // ❌ BlockStructureObserver interface
    // - Custom interface for multiblock structure
    // - Verifies bore head placement
    //
    // ❌ Bore head placement logic
    // - Checks block below
    // - Places bore head based on item metadata
    // - Consumes item from player hand

    /**
     * NOTE: Bore system
     * <p>
     * Original version integrates with:
     * - BlockBoreHead - bore head block placed below
     * - TileBore - main logic for drilling
     * - BlockStructureObserver - multiblock verification
     * - Liquid mining system
     * - Entity attraction vortex
     * <p>
     * In 1.7.10, this needs:
     * - TileBore implementation
     * - BlockBoreHead block
     * - Structure observer system
     * - Drilling automation logic
     */
}
