/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Prism - Starlight splitting prism
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockPrism - Starlight splitting prism (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Prism for splitting starlight beam</li>
 * <li>6 directional variants</li>
 * <li>Has TileEntity (TileCrystalPrismLens)</li>
 * <li>Part of BlockStarlightNetwork</li>
 * </ul>
 */
public class BlockPrism extends BlockContainer {

    private static final String[] VARIANT_NAMES = { "down", "up", "north", "south", "west", "east" };

    public BlockPrism() {
        super(Material.glass);
        setHardness(3.0F);
        setResistance(12.0F);
        setStepSound(soundTypeGlass);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public int damageDropped(int meta) {
        return meta;

    }

    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 6; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Get render type - use TESR for prism rendering
     */
    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // TODO: Prism interaction
        return false;

    }

    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);

    }

    public int quantityDropped(Random rand) {
        return 1;

    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this, 1, metadata));
        return drops;

    }

    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        // TODO: Return item with crystal properties
        return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));

    }

    public net.minecraft.tileentity.TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileCrystalPrismLens();
    }
}
