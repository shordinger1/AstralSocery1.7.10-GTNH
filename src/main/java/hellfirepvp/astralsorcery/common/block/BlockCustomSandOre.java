/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Custom sand ore block - aquamarine in sand
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * BlockCustomSandOre - Aquamarine sand ore (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Single variant: AQUAMARINE</li>
 * <li>Gravity affected (inherits BlockFalling)</li>
 * <li>Fortune-friendly drops</li>
 * <li>Drops aquamarine crafting component</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - simplified to single variant</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>No NonNullList - uses ArrayList</li>
 * <li>Inherits BlockFalling for gravity</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had PropertyEnum<OreType> with AQUAMARINE.
 * This version uses metadata 0 for the single variant.
 * <p>
 * <b>Drops:</b>
 * <ul>
 * <li>Fortune 0: 1-2 aquamarine</li>
 * <li>Fortune 1: 1-3 aquamarine</li>
 * <li>Fortune 2: 1-5 aquamarine</li>
 * <li>Fortune 3: 1-7 aquamarine</li>
 * </ul>
 */
public class BlockCustomSandOre extends BlockFalling {

    private static final Random rand = new Random();

    @SideOnly(Side.CLIENT)
    private IIcon iconOre;

    /**
     * Constructor
     */
    public BlockCustomSandOre() {
        super(Material.sand);

        setHardness(0.5F);
        setStepSound(soundTypeSand);
        setHarvestLevel("shovel", 1); // Shovel harvest level 1
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor for sand
        // Using default sand color
    }

    /**
     * Get damage value for dropped item
     */
    public int damageDropped(int meta) {
        return meta; // Return metadata as-is
    }

    /**
     * Get quantity dropped
     */
    public int quantityDropped(Random rand) {
        return 1; // Base drop
    }

    /**
     * Get quantity dropped with fortune bonus
     */
    public int quantityDroppedWithBonus(int fortune, Random rand) {
        // Fortune calculation: 1 + rand(fortune * 2 - 1)
        // Example:
        // fortune 0: 1 + rand(-1 to -1) = 1 + 0 = 1 (but min 0, so 1)
        // fortune 1: 1 + rand(0 to 1) = 1 to 2
        // fortune 2: 1 + rand(0 to 3) = 1 to 4
        // fortune 3: 1 + rand(0 to 5) = 1 to 6
        int bonus = fortune * 2 - 1;
        if (bonus < 0) {
            bonus = 0;
        }
        int extra = rand.nextInt(bonus + 1);
        return 1 + extra;

    }

    /**
     * Get the item dropped
     * TODO: Return aquamarine crafting component when implemented
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        // TODO: When ItemCraftingComponent.MetaType.AQUAMARINE is implemented
        // return ItemCraftingComponent.MetaType.AQUAMARINE.getItem();
        // For now, return the item of this block
        return Item.getItemFromBlock(this);

    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

        // Calculate drop count based on fortune
        int count = quantityDroppedWithBonus(fortune, rand);

        // TODO: When ItemCraftingComponent is implemented
        // drops.add(new ItemStack(ItemCraftingComponent.MetaType.AQUAMARINE.getItem(), count, 0));
        // For now, drop the block item
        drops.add(new ItemStack(this, count, 0));

        return drops;

    }

    /**
     * Get sub blocks for creative tab
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Only one variant (AQUAMARINE)
        list.add(new ItemStack(item, 1, 0));

        // NOTE: Original version had these methods that don't exist in 1.7.10:
        // - getMetaFromState(IBlockState) - 1.7.10 doesn't use IBlockState
        // - getStateFromMeta(int) - 1.7.10 doesn't use IBlockState
        // - createBlockState() - 1.7.10 doesn't use BlockStateContainer
        // - damageDropped(IBlockState) - 1.7.10 uses damageDropped(int meta)

        // NOTE: 1.7.10 metadata layout:
        // Only 1 variant, so metadata is always 0
    }

    /**
     * Internal enum for ore types
     * Currently only AQUAMARINE is implemented
     */
    public static enum OreType {

        /**
         * Aquamarine ore in sand
         */
        AQUAMARINE(0);

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
                return AQUAMARINE;
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
        iconOre = IconHelper.registerBlockIconsFromConfig(reg, "blockcustomsandore")[0];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconOre;
    }
}
