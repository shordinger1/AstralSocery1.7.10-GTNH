/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Structural block - Multiblock structure helper blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
 * BlockStructural - Multiblock structure helper blocks (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Invisible structural blocks for multiblock structures</li>
 * <li>TELESCOPE_STRUCT variant for telescope multiblock</li>
 * <li>Delegates most interactions to supported block</li>
 * <li>Auto-removes if support block disappears</li>
 * <li>Has TileEntity for structure connection data</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Material: Use glass instead of BARRIER</li>
 * <li>No PropertyEnum - uses metadata (0=TELESCOPE_STRUCT)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>Invisible render type: -1</li>
 * <li>Delegates to supported block via metadata</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had many 1.8+ features:
 * <ul>
 * <li>PropertyEnum&lt;BlockType&gt; - 1.8+ system</li>
 * <li>addDestroyEffects(World, BlockPos, ParticleManager) - 1.8+ method</li>
 * <li>addHitEffects - 1.8+ method</li>
 * <li>getBoundingBox(IBlockState, IBlockAccess, BlockPos) - 1.8+ signature</li>
 * <li>All IBlockState parameters - use metadata instead</li>
 * <li>neighborChanged(IBlockState, World, BlockPos, Block, BlockPos) - 1.9+ signature</li>
 * </ul>
 * <p>
 * <b>Delegation System:</b>
 * Most methods delegate to the supported block (TELESCOPE → BlockMachine.TELESCOPE)
 * This provides consistent interaction - the structural block acts like its supported block.
 */
public class BlockStructural extends BlockContainer implements BlockCustomName, BlockVariants {

    /** Prevent infinite recursion in effects */
    private static boolean effectLoop = false;

    /** Icon for item rendering */
    @SideOnly(Side.CLIENT)
    private IIcon iconStructural;

    /** Variant names */
    private static final String[] VARIANT_NAMES = { "telescope_struct" };

    /**
     * Enum for block types
     */
    public static enum BlockType {

        /** Telescope structure block */
        TELESCOPE_STRUCT(0);

        private final int meta;

        BlockType(int meta) {
            this.meta = meta;
        }

        public int getMetadata() {
            return meta;
        }

        /**
         * Get block type from metadata
         */
        public static BlockType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }

        /**
         * Get the name of this type
         */
        public String getName() {
            return name().toLowerCase();
        }

        // TODO: When BlockMachine is implemented:
        // /**
        // * Get the supported block state
        // * This is the block that this structural block delegates to
        // */
        // public IBlockState getSupportedState() {
        // switch (this) {
        // case TELESCOPE_STRUCT:
        // return BlocksAS.blockMachine.getDefaultState()
        // .withProperty(BlockMachine.MACHINE_TYPE, BlockMachine.MachineType.TELESCOPE);
        // default:
        // return null;
        // }
        // }
    }

    /**
     * Constructor
     */
    public BlockStructural() {
        super(Material.glass); // 1.7.10: Use glass instead of BARRIER

        setBlockUnbreakable();
        setStepSound(soundTypeGlass);

        // NOTE: Not in creative tab - internal use only
    }

    /**
     * Register block icons
     * NOTE: Block is invisible (renderType -1), texture only used for item display
     * Using marble_bricks as placeholder since this block is internal-only
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconStructural = TextureRegister.registerBlockIcon(reg, "marble_bricks");
    }

    /**
     * Get icon for rendering
     * Returns invisible icon for block, but item will have icon
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconStructural;
    }

    /**
     * Get sub blocks for creative tab (not used - internal block)
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Add all variants (though not normally in creative tab)
        for (BlockType bt : BlockType.values()) {
            list.add(new ItemStack(item, 1, bt.ordinal()));
        }
    }

    /**
     * Get damage value for dropped item
     */
    public int damageDropped(int meta) {
        return meta;

    }

    /**
     * Get the item dropped
     * <p>
     * TODO: When BlockMachine is implemented, drop the telescope item
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        BlockType type = BlockType.byMetadata(meta);
        switch (type) {
            case TELESCOPE_STRUCT:
                // TODO: When BlockMachine.MachineType.TELESCOPE is implemented
                // return BlockMachine.MachineType.TELESCOPE.getItem();
                return Item.getItemFromBlock(this);
            default:
                return Item.getItemFromBlock(this);
        }
    }

    /**
     * Get drops
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        BlockType type = BlockType.byMetadata(metadata);

        switch (type) {
            case TELESCOPE_STRUCT:
                // TODO: When BlockMachine is implemented
                // drops.add(BlockMachine.MachineType.TELESCOPE.asStack());
                drops.add(new ItemStack(this, 1, metadata));
                break;
            default:
                break;
        }

        return drops;

    }

    /**
     * On block activated - delegate to supported block
     * <p>
     * TODO: Implement when BlockMachine is available
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        int meta = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.byMetadata(meta);

        switch (type) {
            case TELESCOPE_STRUCT:
                // TODO: When BlockMachine is implemented
                // Block supportedBlock = BlockType.TELESCOPE_STRUCT.getSupportedState().getBlock();
                // return supportedBlock.onBlockActivated(world, x, y - 1, z, player, side, hitX, hitY, hitZ);
                return false;
            default:
                break;
        }

        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);

    }

    /**
     * On neighbor block change - auto-remove if support disappears
     * <p>
     * TELESCOPE_STRUCT removes itself if block below is air.
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        int meta = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.byMetadata(meta);

        switch (type) {
            case TELESCOPE_STRUCT:
                // Remove if block below is air
                if (world.isAirBlock(x, y - 1, z)) {
                    world.setBlockToAir(x, y, z);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Get render type - invisible
     */
    public int getRenderType() {
        return -1; // Invisible
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
     * Pick block - delegate to supported block
     * <p>
     * TODO: Implement when BlockMachine is available
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.byMetadata(meta);

        switch (type) {
            case TELESCOPE_STRUCT:
                // TODO: When BlockMachine is implemented
                // return BlockType.TELESCOPE_STRUCT.getSupportedState().getBlock()
                // .getPickBlock(target, world, x, y - 1, z);
                return new ItemStack(this, 1, meta);
            default:
                return new ItemStack(this, 1, meta);
        }
    }

    /**
     * Get block hardness - delegate to supported block
     * <p>
     * TODO: Implement when BlockMachine is available
     */
    public float getBlockHardness(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        BlockType type = BlockType.byMetadata(meta);

        switch (type) {
            case TELESCOPE_STRUCT:
                // TODO: When BlockMachine is implemented
                // return BlockType.TELESCOPE_STRUCT.getSupportedState().getBlock()
                // .getBlockHardness(world, x, y - 1, z);
                return super.getBlockHardness(world, x, y, z);
            default:
                return super.getBlockHardness(world, x, y, z);
        }
    }

    /**
     * Get explosion resistance - delegate to supported block
     * <p>
     * TODO: Implement when BlockMachine is available
     */
    public float getExplosionResistance(Entity entity) {
        // TODO: Implement delegation when BlockMachine is available
        return super.getExplosionResistance(entity);

    }

    /**
     * Create new tile entity
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileStructuralConnector();
    }

    public String getIdentifierForMeta(int meta) {
        BlockType type = BlockType.byMetadata(meta);
        return type.getName();

        // BlockVariants implementation
    }

    public String[] getVariantNames() {
        return VARIANT_NAMES;

    }

    public String getStateName(int metadata) {
        return BlockType.byMetadata(metadata)
            .getName();

        // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
        //
        // ❌ addDestroyEffects(World, BlockPos, ParticleManager)
        // - 1.8+ method
        //
        // ❌ addHitEffects(IBlockState, World, RayTraceResult, ParticleManager)
        // - 1.8+ method
        //
        // ❌ getBoundingBox(IBlockState, IBlockAccess, BlockPos)
        // - 1.8+ signature
        // - 1.7.10 uses setBlockBounds() / getCollisionBoundingBoxFromPool()
        //
        // ❌ neighborChanged(IBlockState, World, BlockPos, Block, BlockPos)
        // - 1.9+ signature
        // - 1.7.10 uses onNeighborBlockChange(World, int, int, int, Block)
        //
        // ❌ getSoundType(IBlockState, World, BlockPos, Entity)
        // - 1.8+ signature
        // - 1.7.10 uses stepSound field
        //
        // ❌ getDrops(NonNullList, IBlockAccess, BlockPos, IBlockState, int)
        // - 1.8+ signature
        //
        // ❌ getStateFromMeta(int), getMetaFromState(IBlockState)
        // - 1.8+ methods
        // - 1.7.10 uses metadata directly
        //
        // ❌ isTranslucent(), isFullCube(), isNormalCube()
        // - 1.8+ methods
        //
        // ❌ isSideSolid(IBlockState, IBlockAccess, BlockPos, EnumFacing)
        // - 1.8+ signature
        //
        // ❌ getBlockFaceShape()
        // - 1.8+ method

        // NOTE: Special collision box for TELESCOPE_STRUCT
        // Original: (0, -1, 0) to (1, 1, 1) - extends downward
        // In 1.7.10, this would require:
        // @Override
        // public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        // int meta = world.getBlockMetadata(x, y, z);
        // if (meta == BlockType.TELESCOPE_STRUCT.getMetadata()) {
        // setBlockBounds(0, -1, 0, 1, 1, 1);
        // } else {
        // setBlockBounds(0, 0, 0, 1, 1, 1);
        // }
        // }
        //
        // TODO: Implement when collision is needed

        /**
         * NOTE: Delegation System
         * <p>
         * This block delegates most interactions to its supported block.
         * This provides a consistent user experience - interacting with the
         * structural block feels like interacting with the main block.
         * <p>
         * Example: TELESCOPE_STRUCT delegates to BlockMachine.TELESCOPE
         * - Right-click opens telescope GUI
         * - Break drops telescope item
         * - Hardness matches telescope
         * - Sound matches telescope
         */
    }
}
