/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Marble slab block - decorative slab
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.TextureRegister;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockMarbleSlab - Marble slab (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Half slab (bottom placement)</li>
 * <li>Single variant (BRICKS)</li>
 * <li>Translucent (lightOpacity 0)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>No PropertyEnum - uses metadata bit 8 for half/top</li>
 * <li>No EnumBlockHalf - uses metadata bit 8 instead</li>
 * <li>Simplified to single variant (BRICKS)</li>
 * <li>BlockSlab constructor: (boolean isDouble, Material material)</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had PropertyEnum<EnumType> with multiple variants.
 * This version only implements BRICKS variant for simplicity.
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // Register half slab
 * GameRegistry.registerBlock(blockMarbleSlab, "blockMarbleSlab");
 *
 * // Register double slab
 * GameRegistry.registerBlock(blockMarbleDoubleSlab, "blockMarbleDoubleSlab");
 * </pre>
 */
public class BlockMarbleSlab extends BlockSlab {

    /**
     * Constructor - half slab
     */
    public BlockMarbleSlab() {
        super(false, Material.rock); // false = not double slab

        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(soundTypeStone);
        setLightOpacity(0);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    /**
     * Get the item dropped when block is broken
     */
    @Override
    public Item getItemDropped(int meta, java.util.Random rand, int fortune) {
        // TODO: Return Item.getItemFromBlock(BlocksAS.blockMarbleSlab)
        // When blockMarbleSlab is properly registered in BlocksAS
        return Item.getItemFromBlock(this);
    }

    /**
     * Get damage value for dropped item
     */
    @Override
    public int damageDropped(int meta) {
        return meta & 7; // Lower 3 bits - variant
    }

    /**
     * Get sub blocks for creative tab
     */
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Only one variant (BRICKS)
        list.add(new ItemStack(item, 1, 0));
    }

    /**
     * Get the unlocalized name with metadata
     * Uses unlocalized base name for single variant
     */
    @Override
    public String func_150002_b(int meta) {
        return getUnlocalizedName();
    }

    /**
     * Get the icon for rendering
     * Returns the registered slab icon
     */
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }

    /**
     * Register block icons
     * Uses marble bricks texture
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = TextureRegister.registerBlockIcon(reg, "marble_bricks");
    }

    /**
     * Get variant type from metadata
     */
    public static EnumType getTypeFromMeta(int meta) {
        return EnumType.byMetadata(meta & 7);
    }

    /**
     * Is this a top slab?
     */
    public static boolean isTopSlab(int meta) {
        return (meta & 8) != 0;
    }

    /**
     * Enum for slab variants
     */
    public static enum EnumType {

        /**
         * Bricks variant - only implemented variant
         */
        BRICKS(0);

        private final int meta;

        EnumType(int meta) {
            this.meta = meta;
        }

        public int getMetadata() {
            return meta;
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }
    }
}
