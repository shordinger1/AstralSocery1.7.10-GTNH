/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Custom ore block - rock crystal and starmetal
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.TextureRegister;
import hellfirepvp.astralsorcery.common.base.AstralBaseBlock;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockCustomOre - Custom ore blocks (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Two variants: ROCK_CRYSTAL, STARMETAL</li>
 * <li>Security harvest system (player distance check)</li>
 * <li>Fortune-compatible drops</li>
 * <li>High hardness and resistance</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - uses metadata (0=ROCK_CRYSTAL, 1=STARMETAL)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>No BlockRenderLayer - 1.7.10 uses different rendering system</li>
 * <li>harvestBlock() signature is different</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had harvestBlock() with IBlockState and TileEntity parameters.
 * In 1.7.10, the signature is different - we'll use a simplified version.
 * <p>
 * <b>Security System:</b>
 * <ul>
 * <li>allowCrystalHarvest config - if true, anyone can harvest</li>
 * <li>Distance check - player must be within 10 blocks</li>
 * <li>Real player check - fake players (machines) cannot harvest</li>
 * </ul>
 */
public class BlockCustomOre extends AstralBaseBlock {

    /** Config flag to allow crystal harvest without restrictions */
    public static boolean allowCrystalHarvest = false;

    private static final Random rand = new Random();

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    /**
     * Enum for ore types
     */
    public static enum OreType {

        /** Rock crystal ore */
        ROCK_CRYSTAL(0),
        /** Starmetal ore */
        STARMETAL(1);

        private final int meta;

        OreType(int meta) {
            this.meta = meta;
        }

        public int getMetadata() {
            return meta;
        }

        /**
         * Get ore type from metadata
         */
        public static OreType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }

        public String getName() {
            return name().toLowerCase();
        }
    }

    /**
     * Constructor
     */
    public BlockCustomOre() {
        super(Material.rock);

        setHardness(3.0F);
        setResistance(25.0F); // High resistance
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 3); // Diamond tier
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.GRAY
        // Using default rock color
    }

    // Return base unlocalizedName - parent class handles "tile." prefix

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
        // Add both variants
        list.add(new ItemStack(item, 1, 0)); // ROCK_CRYSTAL
        list.add(new ItemStack(item, 1, 1)); // STARMETAL
    }

    /**
     * Drop items when block is broken
     */
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        int meta = world.getBlockMetadata(x, y, z);
        OreType type = OreType.byMetadata(meta);

        switch (type) {
            case ROCK_CRYSTAL:
                // TODO: When ItemRockCrystalBase is implemented
                // drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                // For now, drop the block item itself
                drops.add(new ItemStack(this, 1, meta));

                // Fortune bonus: 50% chance per level to drop extra
                for (int i = 0; i < fortune; i++) {
                    if (rand.nextBoolean()) {
                        // TODO: drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                        drops.add(new ItemStack(this, 1, meta));
                    }
                }
                break;

            case STARMETAL:
                // TODO: When starmetal item is implemented
                drops.add(new ItemStack(this, 1, meta));
                break;

            default:
                break;
        }

        return drops;

    }

    /**
     * Get the item dropped
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        // TODO: Return actual item when implemented
        return Item.getItemFromBlock(this);

    }

    /**
     * Quantity dropped (base amount)
     */
    public int quantityDropped(Random rand) {
        return 1;

    }

    /**
     * Check if this ore can be harvested by the player
     * <p>
     * Security check to prevent automated mining:
     * - If allowCrystalHarvest is true, always allow
     * - Otherwise, check if player is real and within range
     *
     * @param world  The world
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param z      Z coordinate
     * @param player The player harvesting
     * @return true if harvesting is allowed
     */
    public boolean canHarvest(World world, int x, int y, int z, EntityPlayer player) {
        // If config allows, anyone can harvest
        if (allowCrystalHarvest) {
            return true;
        }

        // Check if real player (not fake MP player)
        if (!isRealPlayer(player)) {
            return false;
        }

        // Check distance (within 10 blocks)
        double dist = player.getDistanceSq(x + 0.5, y + 0.5, z + 0.5);
        if (dist > 100) { // 10^2 = 100
            return false;
        }

        return true;
    }

    /**
     * Check if player is a real player
     * <p>
     * Real player = EntityPlayerMP instance
     * Fake player = fake player from machines/mods
     *
     * @param player The player to check
     * @return true if player is EntityPlayerMP
     */
    private boolean isRealPlayer(EntityPlayer player) {
        // In 1.7.10, real players are EntityPlayerMP
        // Fake players are usually a different class
        return player instanceof EntityPlayerMP;
    }

    /**
     * Check safety of harvesting position
     * <p>
     * Checks if there are any real players nearby
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if safe to harvest
     */
    private boolean checkSafety(World world, int x, int y, int z) {
        // Check for nearby players
        // TODO: Implement player scanning
        // For now, return true (assume safe)
        return true;
    }

    // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
    //
    // ❌ harvestBlock(World, EntityPlayer, BlockPos, IBlockState, TileEntity, ItemStack)
    // - 1.7.10 signature is different
    // - Use canHarvest() + onBlockHarvested() instead
    //
    // ❌ breakBlock(World, BlockPos, IBlockState)
    // - 1.7.10 signature is breakBlock(World, int, int, int)
    //
    // ❌ getBlockLayer() - 1.7.10 doesn't have this
    // - Rendering is handled differently in 1.7.10
    //
    // ❌ getDrops(NonNullList, IBlockAccess, BlockPos, IBlockState, int)
    // - 1.7.10 signature is getDrops(World, int, int, int, int)
    //
    // ❌ getStateFromMeta/meta, createBlockState
    // - 1.7.10 doesn't use IBlockState system

    /**
     * Alternative to harvestBlock - called when block is harvested
     * Override this in subclasses if needed
     */

    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        // TODO: When RockCrystalHandler is implemented
        // OreType type = OreType.byMetadata(world.getBlockMetadata(x, y, z));
        // if (type == OreType.ROCK_CRYSTAL) {
        // RockCrystalHandler.INSTANCE.removeOre(world, x, y, z, true);
        // }

        // NOTE: Metadata layout in 1.7.10:
        // 0 = ROCK_CRYSTAL
        // 1 = STARMETAL
        // Additional metadata bits (2-15) currently unused
    }

    /**
     * Get ore type from metadata
     * Convenience method
     *
     * @param meta Block metadata
     * @return OreType
     */
    public static OreType getTypeFromMeta(int meta) {
        return OreType.byMetadata(meta);
    }

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[2];
        icons[0] = TextureRegister.registerBlockIcon(reg, "ore_rockcrystal");
        icons[1] = TextureRegister.registerBlockIcon(reg, "ore_starmetal");
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
