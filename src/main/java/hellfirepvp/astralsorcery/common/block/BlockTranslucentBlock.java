/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Translucent block - Invisible camouflage block for hidden passages
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
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
 * BlockTranslucentBlock - Invisible camouflage block (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Invisible but pass-through block</li>
 * <li>Displays as another block (camouflage)</li>
 * <li>Has TileEntity storing faked block state</li>
 * <li>Delegates interaction to faked block</li>
 * <li>No drops</li>
 * <li>Created by illumination wand</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Material: Use glass instead of BARRIER</li>
 * <li>Render type: Return -1 for invisible</li>
 * <li>createTileEntity: Uses createNewTileEntity(World, int) signature</li>
 * <li>No BlockPos - uses (x, y, z) coordinates</li>
 * <li>No IBlockState - can't store faked state directly</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had many 1.8+ features:
 * <ul>
 * <li>addDestroyEffects(World, BlockPos, ParticleManager) - 1.8+ method</li>
 * <li>randomDisplayTick(IBlockState, World, BlockPos, Random) - 1.8+ signature</li>
 * <li>getSoundType(IBlockState, World, BlockPos, Entity) - 1.8+ signature</li>
 * <li>getFakedStateTile() - uses IBlockState</li>
 * <li>Particle effects (white flare particles)</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * Created by ItemIlluminationWand to make hidden passages and伪装 walls.
 * The block is invisible and pass-through, but displays as another block.
 * Delegates sound and activation to the faked block.
 */
public class BlockTranslucentBlock extends BlockContainer {

    /** Icon for item rendering */
    @SideOnly(Side.CLIENT)
    private IIcon iconTranslucent;

    /**
     * Constructor
     */
    public BlockTranslucentBlock() {
        super(Material.glass); // 1.7.10: Use glass instead of BARRIER

        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(0.6F);

        // NOTE: Not in creative tab - created by wand
    }

    /**
     * Register block icons
     * NOTE: Block is invisible (renderType -1), texture only used for item display
     * Using marble_bricks as placeholder since this block is created by wand only
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconTranslucent = TextureRegister.registerBlockIcon(reg, "marble_bricks");
    }

    /**
     * Get icon for rendering
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconTranslucent;
    }

    /**
     * Get render type - invisible
     */
    public int getRenderType() {
        return -1; // Invisible
    }

    /**
     * Random display tick - spawn white flare particles
     * <p>
     * 1.7.10 API: Uses (World, int, int, int, Random) signature
     * Creates white flare particles to indicate camouflaged block
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        // Spawn white flare particles occasionally
        if (rand.nextInt(20) == 0) { // 5% chance per tick
            // Particle position: random offset within block
            double px = x + rand.nextFloat();
            double py = y + rand.nextFloat();
            double pz = z + rand.nextFloat();

            // Particle velocity: small random movement
            double vx = (rand.nextFloat() - 0.5F) * 0.02;
            double vy = rand.nextFloat() * 0.05;
            double vz = (rand.nextFloat() - 0.5F) * 0.02;

            // Spawn white particle (camouflage indicator)
            world.spawnParticle("spell", px, py, pz, vx, vy, vz);
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
     * Pick block - return faked block's item
     * <p>
     * Returns the item of the faked block when using middle-click
     */
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        hellfirepvp.astralsorcery.common.tile.TileTranslucent tt = (hellfirepvp.astralsorcery.common.tile.TileTranslucent) world
            .getTileEntity(x, y, z);

        if (tt != null) {
            Block fakedBlock = tt.getFakedBlock();
            int fakedMeta = tt.getFakedMeta();

            if (fakedBlock != null && fakedBlock != net.minecraft.init.Blocks.air) {
                return new ItemStack(fakedBlock, 1, fakedMeta);
            }
        }

        // If no faked block, return null (can't pick)
        return null;

    }

    /**
     * On block activated - delegate to faked block
     * <p>
     * Delegates right-click interaction to the faked block
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        hellfirepvp.astralsorcery.common.tile.TileTranslucent tt = (hellfirepvp.astralsorcery.common.tile.TileTranslucent) world
            .getTileEntity(x, y, z);

        if (tt != null) {
            Block fakedBlock = tt.getFakedBlock();

            if (fakedBlock != null && fakedBlock != net.minecraft.init.Blocks.air) {
                try {
                    // Delegate activation to faked block
                    return fakedBlock.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
                } catch (Exception exc) {
                    hellfirepvp.astralsorcery.common.util.LogHelper.warn(
                        "[BlockTranslucentBlock] Failed to delegate activation to " + fakedBlock.getUnlocalizedName()
                            + ": "
                            + exc.getMessage());
                }
            }
        }

        // If no faked block or delegation failed, use default behavior
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);

    }

    /**
     * Create new tile entity
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileTranslucent();
    }

    // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
    //
    // ❌ addDestroyEffects(World, BlockPos, ParticleManager)
    // - 1.8+ method
    // - Plays break particles of faked block
    //
    // ❌ randomDisplayTick(IBlockState, World, BlockPos, Random)
    // - 1.8+ signature
    // - Creates white flare particles
    //
    // ❌ getSoundType(IBlockState, World, BlockPos, Entity)
    // - 1.8+ signature
    // - Returns faked block's sound type
    // - 1.7.10: Uses stepSound field
    //
    // ❌ canEntityDestroy(IBlockState, IBlockAccess, BlockPos, Entity)
    // - 1.8+ method
    // - Returns false
    //
    // ❌ isTranslucent(IBlockState)
    // - 1.8+ signature
    // - Returns true
    //
    // ❌ isSideSolid(IBlockState, IBlockAccess, BlockPos, EnumFacing)
    // - 1.8+ signature
    // - Returns false
    //
    // ❌ isFullCube(IBlockState)
    // - 1.8+ signature
    // - Returns false
    //
    // ❌ getBlockFaceShape()
    // - 1.8+ method
    //
    // ❌ getFakedStateTile(World, BlockPos)
    // - Uses IBlockState
    // - 1.7.10: Can't store IBlockState in TileEntity

    /**
     * NOTE: Camouflage system
     * <p>
     * Original version integrates with TileTranslucent to:
     * - Store faked IBlockState
     * - Display as faked block
     * - Delegate sound to faked block
     * - Delegate activation to faked block
     * - Play break particles of faked block
     * - Show white flare particles occasionally
     * <p>
     * In 1.7.10, this system needs adaptation:
     * - TileTranslucent stores block ID + metadata instead of IBlockState
     * - Sound delegation uses different API
     * - Particle effects need 1.7.10 implementation
     */
}
