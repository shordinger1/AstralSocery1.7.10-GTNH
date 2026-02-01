/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Map Drawing Table - Constellation mapping table
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockMapDrawingTable - Constellation mapping table (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Table for drawing constellation maps</li>
 * <li>Has TileEntity (TileMapDrawingTable)</li>
 * <li>Part of constellation discovery</li>
 * <li>GUI for map drawing</li>
 * </ul>
 */
public class BlockMapDrawingTable extends BlockContainer {

    public BlockMapDrawingTable() {
        super(Material.wood);
        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeWood);
        setHarvestLevel("axe", 0);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public boolean isOpaqueCube() {
        return true;

    }

    public boolean renderAsNormalBlock() {
        return true;

    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // TODO: Open map drawing GUI
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
        drops.add(new ItemStack(this, 1, 0));
        return drops;

    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable();
    }
}
