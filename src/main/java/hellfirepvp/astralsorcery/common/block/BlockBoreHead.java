/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Bore head block - Drilling machine head component
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseBlock;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * BlockBoreHead - Bore machine head (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>2 variants: LIQUID, SOLID</li>
 * <li>Must be placed below BlockBore</li>
 * <li>High hardness and resistance</li>
 * <li>Part of multibore drilling machine</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - uses metadata (0=LIQUID, 1=SOLID)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>Placement restrictions: must be below BlockBore</li>
 * </ul>
 * <p>
 * <b>Variants:</b>
 * 
 * <pre>
 * 0 = LIQUID  - Liquid mining bore head
 * 1 = SOLID   - Solid mining bore head
 * </pre>
 */
public class BlockBoreHead extends AstralBaseBlock implements BlockCustomName, BlockVariants {

    /** Icons for bore head variants */
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    /**
     * Variant names
     */
    private static final String[] VARIANT_NAMES = { "liquid", "solid" };

    /**
     * Enum for bore head types
     */
    public static enum BoreType {

        /**
         * Liquid mining bore head
         */
        LIQUID(0),
        /**
         * Solid mining bore head
         */
        SOLID(1);

        private final int meta;

        BoreType(int meta) {
            this.meta = meta;
        }

        public int getMetadata() {
            return meta;
        }

        /**
         * Get bore type from metadata
         */
        public static BoreType byMetadata(int meta) {
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
    }

    /**
     * Constructor
     */
    public BlockBoreHead() {
        super(Material.iron);

        setHardness(10.0F);
        setResistance(15.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 2); // Iron tier
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.GOLD specifically
        // Using default iron color
    }

    /**
     * Register block icons
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = IconHelper.registerBlockIconsFromConfig(reg, "blockborehead");
    }

    /**
     * Get icon for rendering
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int index = meta % icons.length;
        return icons[index];
    }

    /**
     * Get damage value for dropped item
     */
    public int damageDropped(int meta) {
        return meta;

    }

    /**
     * Get sub blocks for creative tab
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Add both variants
        for (BoreType type : BoreType.values()) {
            list.add(new ItemStack(item, 1, type.getMetadata()));
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
     * Can place block at this position
     * <p>
     * Bore head must be placed below BlockBore
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // First check base conditions
        if (!super.canPlaceBlockAt(world, x, y, z)) {
            return false;
        }

        // Check if block above is BlockBore
        int yAbove = y + 1;
        if (yAbove >= world.getHeight()) {
            return false; // Above world height
        }

        Block blockAbove = world.getBlock(x, yAbove, z);
        // Check if it's BlockBore (compare by class or check BlocksAS reference)
        if (blockAbove instanceof BlockBore) {
            return true; // Valid: BlockBore above
        }

        return false; // Invalid: No BlockBore above
    }

    /**
     * On neighbor change - check if BlockBore above still exists
     * <p>
     * Breaks if BlockBore above is removed
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);

        if (world.isRemote) return;

        // Check if BlockBore above still exists
        int yAbove = y + 1;
        Block blockAbove = world.getBlock(x, yAbove, z);

        if (!(blockAbove instanceof BlockBore)) {
            // BlockBore removed, break self
            world.func_147480_a(x, y, z, true); // breakBlock + drop items
            hellfirepvp.astralsorcery.common.util.LogHelper
                .info("[BlockBoreHead] Breaking bore head at " + x + "," + y + "," + z + " - BlockBore above removed");
        }
    }

    /**
     * On block activated
     * <p>
     * NOTE: Full interaction with BlockBore requires:
     * - TileBore implementation with drilling logic
     * - Bore head metadata tracking (LIQUID/SOLID)
     * - GUI for bore configuration
     * - Automation logic for mining
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // For now, just return false (no interaction)
        // TODO: When TileBore is implemented, open GUI or interact with bore
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);

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
     * Get drops
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this, 1, metadata));
        return drops;
    }
    // BlockCustomName implementation

    public String getIdentifierForMeta(int meta) {
        return BoreType.byMetadata(meta)
            .getName();
    }
    // BlockVariants implementation

    public String[] getVariantNames() {
        return VARIANT_NAMES;

    }

    public String getStateName(int metadata) {
        return BoreType.byMetadata(metadata)
            .getName();

        // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
        //
        // ❌ canPlaceBlockOnSide(World, BlockPos, EnumFacing)
        // - 1.8+ method
        // - Checks: side==DOWN, block above is BlockBore
        //
        // ❌ isSideSolid(IBlockState, IBlockAccess, BlockPos, EnumFacing)
        // - 1.8+ signature
        // - Returns false
        //
        // ❌ getStateFromMeta(int), getMetaFromState(IBlockState)
        // - 1.8+ methods
        // - 1.7.10 uses metadata directly
    }
    /**
     * NOTE: Placement restriction system
     * <p>
     * Original version requires:
     * - Must be placed on BOTTOM face (clicking from below)
     * - Block above must be BlockBore
     * - Prevents incorrect placement
     * <p>
     * In 1.7.10 simplified version:
     * - TODO: Implement canPlaceBlockAt() check
     * - TODO: Check if block at (x, y+1, z) is BlockBore
     */
}
