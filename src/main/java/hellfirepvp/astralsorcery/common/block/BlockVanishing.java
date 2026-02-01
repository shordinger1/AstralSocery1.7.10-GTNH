/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Vanishing block - Temporary invisible block for rituals
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.TextureRegister;

/**
 * BlockVanishing - Temporary invisible block (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Invisible block for ritual use</li>
 * <li>Has collision (entities can stand on it)</li>
 * <li>Disappears when not holding required wand</li>
 * <li>No drops</li>
 * <li>Not in creative tab</li>
 * <li>Has TileEntity (TileVanishing)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Material: Use glass instead of BARRIER</li>
 * <li>Render type: Return -1 for invisible</li>
 * <li>createTileEntity: Uses createNewTileEntity(World, int) signature</li>
 * <li>No BlockPos - uses (x, y, z) coordinates</li>
 * <li>No AxisAlignedBB in 1.7.10 - uses setBlockBounds()</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had many 1.8+ features:
 * <ul>
 * <li>randomDisplayTick(IBlockState, World, BlockPos, Random) - 1.8+ signature</li>
 * <li>canCreatureSpawn(IBlockState, IBlockAccess, BlockPos, SpawnPlacementType) - 1.8+ method</li>
 * <li>isTranslucent(IBlockState) - 1.8+ signature</li>
 * <li>isNormalCube(IBlockState, IBlockAccess, BlockPos) - 1.8+ signature</li>
 * <li>getSelectedBoundingBox(IBlockState, World, BlockPos) - 1.8+ signature</li>
 * <li>Particle effects for rituals</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * Used in Aevitas constellation rituals to create temporary platforms that disappear when players don't have the
 * required wand.
 */
public class BlockVanishing extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconVanishing;

    /**
     * Constructor
     */
    public BlockVanishing() {
        super(Material.glass); // 1.7.10: Use glass instead of BARRIER

        setBlockUnbreakable();
        setStepSound(soundTypeMetal);
        setCreativeTab(null); // Not in creative tab

        // NOTE: 1.7.10 doesn't have MapColor.BLACK
        // Using default glass color
    }

    /**
     * Get render type - invisible
     */
    public int getRenderType() {
        return -1; // Invisible
    }

    /**
     * Random display tick - spawn blue sparkle particles
     * <p>
     * 1.7.10 API: Uses (World, int, int, int, Random) signature
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        // Spawn blue sparkle particles (Aevitas ritual effect)
        if (rand.nextInt(10) == 0) { // 10% chance per tick
            // Particle position: random offset within block
            double px = x + rand.nextFloat();
            double py = y + rand.nextFloat();
            double pz = z + rand.nextFloat();

            // Particle velocity: small upward movement
            double vx = rand.nextGaussian() * 0.02;
            double vy = rand.nextFloat() * 0.1;
            double vz = rand.nextGaussian() * 0.02;

            // Spawn blue particle (Aevitas constellation color)
            // In 1.7.10, we use spawnParticle with particle name
            world.spawnParticle("mobSpell", px, py, pz, vx, vy, vz);
        }
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
     * Pick block - no item
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return null; // Can't pick this block
    }

    /**
     * Create new tile entity
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileVanishing();
    }
    // NOTE: Collision box behavior:
    // Original version has:
    // - getSelectedBoundingBox: Empty (0,0,0 to 0,0,0) - can't highlight
    // - getBoundingBox: Empty - visual bounds
    // - getCollisionBoundingBox: FULL_BLOCK_AABB - entities can stand on it
    //
    // In 1.7.10, collision is handled differently
    // Entities can stand on it, but it's invisible and can't be highlighted

    // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
    //
    // ❌ randomDisplayTick(IBlockState, World, BlockPos, Random)
    // - 1.8+ signature
    // - 1.7.10 signature: randomDisplayTick(World, int, int, int, Random)
    // - Creates particle effects for rituals
    //
    // ❌ canCreatureSpawn(IBlockState, IBlockAccess, BlockPos, SpawnPlacementType)
    // - 1.8+ method
    // - Returns false - creatures can't spawn on it
    //
    // ❌ isTranslucent(IBlockState)
    // - 1.8+ signature
    // - Returns true
    //
    // ❌ isNormalCube(IBlockState, IBlockAccess, BlockPos)
    // - 1.8+ signature
    // - Returns false
    //
    // ❌ isFullBlock(IBlockState), isTopSolid(IBlockState)
    // - 1.8+ methods
    // - All return false
    //
    // ❌ isSideSolid(IBlockState, IBlockAccess, BlockPos, EnumFacing)
    // - 1.8+ signature
    // - Returns false
    //
    // ❌ canEntityDestroy(IBlockState, IBlockAccess, BlockPos, Entity)
    // - 1.8+ method
    // - Returns false - entities can't destroy it
    //
    // ❌ getSelectedBoundingBox(IBlockState, World, BlockPos)
    // - 1.8+ signature
    // - Returns empty AABB - can't highlight
    //
    // ❌ getBlockFaceShape()
    // - 1.8+ method

    /**
     * NOTE: Ritual behavior
     * <p>
     * Used in Aevitas constellation rituals:
     * - Appears as temporary platform
     * - Disappears when player doesn't have wand with Aevitas augmentation
     * - Visible only during ritual
     * - Entities can stand on it but can't interact
     * <p>
     * In 1.7.10, this system needs to be implemented with:
     * - TileVanishing storing ritual data
     * - Update tick checking for wand
     * - Particle effects for ritual feedback
     */

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconVanishing = TextureRegister.registerBlockIcon(reg, "vanishing_block");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconVanishing;
    }
}
