/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Flare light block - Colored light source block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.TextureRegister;
import hellfirepvp.astralsorcery.common.base.AstralBaseBlock;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockFlareLight - Colored light source (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>16 color variants (dye colors)</li>
 * <li>Material: AIR (pass-through)</li>
 * <li>Light level: 15 (maximum)</li>
 * <li>Unbreakable (setBlockUnbreakable)</li>
 * <li>No collision</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - uses metadata (0-15 for 16 dye colors)</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>No EnumDyeColor - uses metadata with string array</li>
 * <li>Colors from ItemDye.field_150923_a</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // Light level 15, all 16 colors
 * // Metadata 0-15 maps to dye colors
 * // 0=black, 1=red, 2=green, ..., 15=white
 * </pre>
 * <p>
 * <b>NOTE:</b> Original version had:
 * <ul>
 * <li>PropertyEnum&lt;EnumDyeColor&gt; COLOR - 1.8+ system</li>
 * <li>Material.AIR - available in 1.7.10</li>
 * <li>isAir(IBlockState, IBlockAccess, BlockPos) - 1.8+ signature</li>
 * <li>causesSuffocation(IBlockState) - 1.8+ method</li>
 * </ul>
 */
public class BlockFlareLight extends AstralBaseBlock {

    /** Color names from ItemDye */
    public static final String[] DYE_COLORS = { "black", // 0
        "red", // 1
        "green", // 2
        "brown", // 3
        "blue", // 4
        "purple", // 5
        "cyan", // 6
        "silver", // 7
        "gray", // 8
        "pink", // 9
        "lime", // 10
        "yellow", // 11
        "lightBlue", // 12
        "magenta", // 13
        "orange", // 14
        "white" // 15
    };

    @SideOnly(Side.CLIENT)
    private IIcon iconLight;

    /**
     * Constructor
     */
    public BlockFlareLight() {
        super(Material.air); // AIR material - pass-through

        setLightLevel(1.0F); // Light level 15 (maximum)
        setBlockUnbreakable();
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.QUARTZ
        // Using default air color
    }

    /**
     * Get damage value for dropped item
     */
    public int damageDropped(int meta) {
        return meta; // Return metadata to preserve color
    }

    /**
     * Get sub blocks for creative tab
     */
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Add all 16 color variants
        for (int i = 0; i < 16; i++) {
            list.add(new ItemStack(item, 1, i));
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
     * Can this block provide power?
     */
    public boolean canProvidePower() {
        return false;

    }
    /**
     * Is this block air?
     * <p>
     * In 1.7.10, this method doesn't exist in Block class.
     * This block acts like air for gameplay purposes.
     */
    // NOTE: Original had isAir(IBlockState, IBlockAccess, BlockPos)
    // 1.7.10 doesn't have this method
    // The block behaves like air due to Material.air and isOpaqueCube()=false

    /**
     * Does this block cause suffocation?
     */
    // NOTE: Original had causesSuffocation(IBlockState)
    // 1.7.10 doesn't have this method
    // Players won't suffocate due to Material.air

    /**
     * Get the item dropped
     * Returns the item itself (this block)
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);

    }

    /**
     * Quantity dropped
     */
    public int quantityDropped(Random rand) {
        return 1;

        // NOTE: Original version had these methods that don't exist or are different in 1.7.10:
        //
        // ❌ getStateFromMeta(int), getMetaFromState(IBlockState)
        // - 1.8+ methods
        // - 1.7.10 uses metadata directly
        //
        // ❌ createBlockState()
        // - 1.8+ method
        // - 1.7.10 doesn't use BlockStateContainer
        //
        // ❌ isAir(IBlockState, IBlockAccess, BlockPos)
        // - 1.8+ method
        // - 1.7.10: Material.air handles this
        //
        // ❌ causesSuffocation(IBlockState)
        // - 1.8+ method
        // - 1.7.10: Material.air handles this

        // NOTE: 1.7.10 metadata layout:
        // 0-15: Dye colors (16 variants)
        // 0 = black
        // 1 = red
        // 2 = green
        // ...
        // 15 = white
    }

    /**
     * Get color name from metadata
     * Convenience method for external use
     *
     * @param meta Block metadata (0-15)
     * @return Color name (e.g., "red", "blue")
     */
    public static String getColorName(int meta) {
        if (meta < 0 || meta >= DYE_COLORS.length) {
            meta = 11; // Default to yellow
        }
        return DYE_COLORS[meta];
    }

    /**
     * Get color array
     * For use in registration and rendering
     *
     * @return Array of 16 color names
     */
    public static String[] getColorArray() {
        return DYE_COLORS.clone();
    }

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconLight = TextureRegister.registerBlockIcon(reg, "core_edge");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconLight;
    }
}
