/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Observatory - Celestial observation device
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

import com.cleanroommc.modularui.factory.TileEntityGuiFactory;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;

/**
 * BlockObservatory - Celestial observatory (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Observatory for constellation viewing</li>
 * <li>Has TileEntity (TileObservatory)</li>
 * <li>Multiblock structure</li>
 * <li>GUI for constellation observation</li>
 * </ul>
 */
public class BlockObservatory extends BlockContainer {

    public BlockObservatory() {
        super(Material.rock);
        setHardness(3.5F);
        setResistance(20.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Not a full block
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false; // Special rendering
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true; // Client side
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileObservatory) {
            TileObservatory observatory = (TileObservatory) te;
            // Open ModularUI GUI
            com.cleanroommc.modularui.factory.TileEntityGuiFactory.INSTANCE.open(player, observatory);
            return true;
        }
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
        return new hellfirepvp.astralsorcery.common.tile.TileObservatory();
    }
}
