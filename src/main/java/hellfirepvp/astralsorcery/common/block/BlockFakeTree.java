/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Fake tree block - Patreon supporter tree beacon decoration
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * BlockFakeTree - Patreon tree beacon decoration block (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Invisible barrier block for Patreon supporters</li>
 * <li>Has TileEntity storing faked block state</li>
 * <li>Unbreakable (setBlockUnbreakable)</li>
 * <li>No drops</li>
 * <li>Invisible render type</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Material: Use glass instead of BARRIER (doesn't exist in 1.7.10)</li>
 * <li>Render type: Return -1 for invisible</li>
 * <li>createTileEntity: Uses createNewTileEntity(World, int) signature</li>
 * <li>No BlockPos - uses (x, y, z) coordinates</li>
 * <li>No RayTraceResult - uses MovingObjectPosition</li>
 * <li>No addDestroyEffects - not available in 1.7.10 Block class</li>
 * <li>No randomDisplayTick - signature is different</li>
 * <li>No getSoundType with IBlockState - not available</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had many 1.8+ features that cannot be implemented:
 * <ul>
 * <li>addDestroyEffects(World, BlockPos, ParticleManager) - 1.8+ method</li>
 * <li>randomDisplayTick(IBlockState, World, BlockPos, Random) - 1.8+ signature</li>
 * <li>getSoundType(IBlockState, World, BlockPos, Entity) - 1.8+ signature</li>
 * <li>getDrops(NonNullList, IBlockAccess, BlockPos, IBlockState, int) - 1.8+ signature</li>
 * <li>getBlockLayer() - doesn't exist in 1.7.10</li>
 * <li>Material.BARRIER - doesn't exist in 1.7.10</li>
 * <li>EnumBlockRenderType - doesn't exist in 1.7.10</li>
 * </ul>
 * <p>
 * <b>Patreon Features:</b>
 * <ul>
 * <li>Particle effects for tree beacons</li>
 * <li>Custom colors for Patreon supporters</li>
 * <li>TODO: When PatreonEffectHelper is implemented</li>
 * </ul>
 */
public class BlockFakeTree extends BlockContainer {

    /**
     * Constructor
     */
    public BlockFakeTree() {
        super(Material.glass); // 1.7.10: Use glass instead of BARRIER

        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(0.6F);

        // NOTE: Not in creative tab - only for Patreon supporters
    }

    /**
     * Get render type - invisible
     */
    public int getRenderType() {
        return -1; // Invisible (same as BlockAir, BlockEndPortal)
    }

    /**
     * Is this a full block?
     */
    public boolean isOpaqueCube() {
        return false;

    }

    /**
     * Does this block render normally?
     */
    public boolean renderAsNormalBlock() {
        return false;

    }

    /**
     * No drops
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<ItemStack>(); // Empty - no drops
    }

    /**
     * Quantity dropped
     */
    public int quantityDropped(Random rand) {
        return 0; // No drops
    }

    /**
     * Item dropped
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return null; // No item dropped
    }

    /**
     * Pick block - return faked block's item
     * <p>
     * TODO: When TileFakeTree is implemented
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        // TODO: When TileFakeTree is implemented:
        // TileFakeTree tft = (TileFakeTree) world.getTileEntity(x, y, z);
        // if(tft != null && tft.getFakedState() != null) {
        // Block block = tft.getFakedState().getBlock();
        // int meta = tft.getFakedState().getMetadata();
        // return new ItemStack(block, 1, meta);
        // }
        return null; // For now, return null
    }

    /**
     * Create new tile entity
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileFakeTree();
    }
}
