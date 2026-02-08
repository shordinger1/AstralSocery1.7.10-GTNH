/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Gem crystals block - Growing crystal formations
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockGemCrystals - Gem crystal formations (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>5 growth stages: STAGE_0, STAGE_1, STAGE_2_SKY, STAGE_2_DAY, STAGE_2_NIGHT</li>
 * <li>Has TileEntity (TileGemCrystals)</li>
 * <li>Emits light (level 0.3)</li>
 * <li>Needs solid block below</li>
 * <li>Drops based on growth stage</li>
 * <li>Particle effects on break</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * <li>No PropertyEnum - uses metadata (0-4 for stages)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>Collision box based on growth stage</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had:
 * <ul>
 * <li>getBoundingBox(IBlockState, IBlockAccess, BlockPos) - different collision per stage</li>
 * <li>breakBlock() - sends particle packet (GEM_CRYSTAL_BURST)</li>
 * <li>neighborChanged() - checks if block below is solid</li>
 * <li>TileGemCrystals with growth logic</li>
 * </ul>
 * <p>
 * <b>Variants:</b>
 *
 * <pre>
 * 0 = STAGE_0      - Stage 0 (white, height 0.375, no drop)
 * 1 = STAGE_1      - Stage 1 (white, height 0.5, no drop)
 * 2 = STAGE_2_SKY  - Sky crystal (blue, height 0.5625, drops sky gem)
 * 3 = STAGE_2_DAY  - Day crystal (orange, height 0.5625, drops day gem)
 * 4 = STAGE_2_NIGHT- Night crystal (gray, height 0.5625, drops night gem)
 * </pre>
 */
public class BlockGemCrystals extends BlockContainer implements BlockCustomName, BlockVariants {

    /**
     * Variant names
     */
    private static final String[] VARIANT_NAMES = { "stage_0", // 0
        "stage_1", // 1
        "stage_2_sky", // 2
        "stage_2_day", // 3
        "stage_2_night" // 4
    };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    /**
     * Enum for crystal growth stages
     */
    public static enum CrystalStage {

        /**
         * Stage 0 - small white crystal
         */
        STAGE_0(0),
        /**
         * Stage 1 - medium white crystal
         */
        STAGE_1(1),
        /**
         * Stage 2 - Sky crystal (blue)
         */
        STAGE_2_SKY(2),
        /**
         * Stage 2 - Day crystal (orange)
         */
        STAGE_2_DAY(3),
        /**
         * Stage 2 - Night crystal (gray)
         */
        STAGE_2_NIGHT(4);

        private final int meta;

        CrystalStage(int meta) {
            this.meta = meta;
        }

        public int getMetadata() {
            return meta;
        }

        /**
         * Is this a mature stage (drops item)?
         */
        public boolean isMature() {
            return this == STAGE_2_SKY || this == STAGE_2_DAY || this == STAGE_2_NIGHT;
        }

        /**
         * Get crystal stage from metadata
         */
        public static CrystalStage byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }

        /**
         * Get the name of this stage
         */
        public String getName() {
            return name().toLowerCase();
        }
    }

    /**
     * Constructor
     */
    public BlockGemCrystals() {
        super(Material.rock);

        setHardness(2.0F);
        setResistance(20.0F);
        setStepSound(soundTypeGlass);
        setHarvestLevel("pickaxe", 2); // Iron tier
        setLightLevel(0.3F); // Emits light
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.QUARTZ
        // Using default rock color
    }

    /**
     * Get damage value for dropped item
     */
    public int damageDropped(int meta) {
        return meta; // Return metadata to preserve variant
    }

    /**
     * Get sub blocks for creative tab
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Add all mature variants to creative
        list.add(new ItemStack(item, 1, CrystalStage.STAGE_2_SKY.getMetadata()));
        list.add(new ItemStack(item, 1, CrystalStage.STAGE_2_DAY.getMetadata()));
        list.add(new ItemStack(item, 1, CrystalStage.STAGE_2_NIGHT.getMetadata()));
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
     * Get collision box based on growth stage
     * STAGE_0: (0.25, 0, 0.25) to (0.75, 0.375, 0.75)
     * STAGE_1: (0.25, 0, 0.25) to (0.75, 0.5, 0.75)
     * STAGE_2_*: (0.25, 0, 0.25) to (0.75, 0.5625, 0.75)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        CrystalStage stage = CrystalStage.byMetadata(meta);

        double minY = 0.0;
        double maxY;

        switch (stage) {
            case STAGE_0:
                maxY = 0.375;
                break;
            case STAGE_1:
                maxY = 0.5;
                break;
            case STAGE_2_SKY:
            case STAGE_2_DAY:
            case STAGE_2_NIGHT:
                maxY = 0.5625;
                break;
            default:
                maxY = 0.375;
                break;
        }

        return AxisAlignedBB.getBoundingBox(0.25, minY, 0.25, 0.75, maxY, 0.75);
    }

    /**
     * Get selected bounding box (for block outline)
     */
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /**
     * Can place block at this position
     * Checks if block below is solid
     */
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // Check if block below is solid
        if (y > 0 && !world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
            return false;
        }
        return super.canPlaceBlockAt(world, x, y, z);
    }

    /**
     * On neighbor block change
     * Drops item and removes self if block below is not solid
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        // Check if block below is solid
        if (y > 0 && !world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
            // Drop self and remove
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
            return;
        }
        super.onNeighborBlockChange(world, x, y, z, neighbor);
    }

    /**
     * Break block
     * <p>
     * TODO: Send particle packet to clients
     */
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        // TODO: Send particle packet (GEM_CRYSTAL_BURST) to nearby players
        super.breakBlock(world, x, y, z, blockBroken, meta);
    }

    /**
     * Get drops based on growth stage
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        CrystalStage stage = CrystalStage.byMetadata(metadata);

        if (stage.isMature()) {
            // TODO: When gem items are implemented
            // switch (stage) {
            // case STAGE_2_SKY:
            // drops.add(new ItemStack(ItemsAS.skyGem, 1, 0));
            // break;
            // case STAGE_2_DAY:
            // drops.add(new ItemStack(ItemsAS.dayGem, 1, 0));
            // break;
            // case STAGE_2_NIGHT:
            // drops.add(new ItemStack(ItemsAS.nightGem, 1, 0));
            // break;
            // }
            // For now, drop the block itself
            drops.add(new ItemStack(this, 1, metadata));
        }

        return drops;

    }

    /**
     * Get the item dropped
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        CrystalStage stage = CrystalStage.byMetadata(meta);
        if (stage.isMature()) {
            // TODO: Return actual gem item
            return Item.getItemFromBlock(this);
        }
        return null; // No drop for immature stages
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
        return new hellfirepvp.astralsorcery.common.tile.TileGemCrystals();
    }

    public String getIdentifierForMeta(int meta) {
        return CrystalStage.byMetadata(meta)
            .getName();

        // BlockVariants implementation
    }

    public String[] getVariantNames() {
        return VARIANT_NAMES;

    }

    public String getStateName(int metadata) {
        return CrystalStage.byMetadata(metadata)
            .getName();

        // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
        //
        // ❌ getBoundingBox(IBlockState, IBlockAccess, BlockPos)
        // - 1.8+ signature
        // - STAGE_0: (0.25, 0, 0.25) to (0.75, 0.375, 0.75)
        // - STAGE_1: (0.25, 0, 0.25) to (0.75, 0.5, 0.75)
        // - STAGE_2_*: (0.25, 0, 0.25) to (0.75, 0.5625, 0.75)
        //
        // ❌ neighborChanged(IBlockState, World, BlockPos, Block, BlockPos)
        // - 1.9+ signature
        // - Checks if block below is solid
        //
        // ❌ getDrops(NonNullList, IBlockAccess, BlockPos, IBlockState, int)
        // - 1.8+ signature
        //
        // ❌ getStateFromMeta(int), getMetaFromState(IBlockState)
        // - 1.8+ methods
    }

    /**
     * NOTE: Growth system
     * <p>
     * Original version integrates with:
     * - TileGemCrystals - stores growth data
     * - Growth stages - STAGE_0 → STAGE_1 → STAGE_2 (branching to 3 types)
     * - Collision box changes with growth
     * - Particle effects on break
     * - Different drops based on final stage
     * <p>
     * In 1.7.10 simplified version:
     * - Metadata determines stage
     * - TODO: Implement TileGemCrystals for growth logic
     * - TODO: Implement collision box changes
     * - TODO: Implement particle effects
     */

    // ========== Texture Registration ==========
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // Register all block textures
        hellfirepvp.astralsorcery.client.util.BlockTextureRegister.registerAll(reg);
        // Initialize icons array for gem stages
        icons = new IIcon[5];
        icons[0] = hellfirepvp.astralsorcery.client.util.BlockTextureRegister.getGemStage(0); // Stage 0
        icons[1] = hellfirepvp.astralsorcery.client.util.BlockTextureRegister.getGemStage(1); // Stage 1
        icons[2] = hellfirepvp.astralsorcery.client.util.BlockTextureRegister.getGemStage(2); // Stage 2 Sky
        icons[3] = hellfirepvp.astralsorcery.client.util.BlockTextureRegister.getGemStage(3); // Stage 2 Day
        icons[4] = hellfirepvp.astralsorcery.client.util.BlockTextureRegister.getGemStage(4); // Stage 2 Night
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= icons.length) {
            meta = 0;
        }
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        return getIcon(side, meta);
    }
}
