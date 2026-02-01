/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Telescope block - Constellation viewing device
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.TileEntityGuiFactory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * BlockTelescope - Celestial telescope (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Telescope for constellation viewing</li>
 * <li>Has TileEntity (TileTelescope)</li>
 * <li>Right-click to open GUI</li>
 * <li>Can rotate to view different constellations</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * <li>Material.rock (stone base)</li>
 * <li>No IBlockState - simple block</li>
 * </ul>
 */
public class BlockTelescope extends BlockContainer {

    public BlockTelescope() {
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

    /**
     * On block activated - open telescope GUI or rotate
     * <p>
     * Right-click: Open GUI
     * Shift+Right-click: Rotate clockwise
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileTelescope)) {
            return false;
        }

        TileTelescope telescope = (TileTelescope) te;

        // Shift+Right-click: Rotate telescope
        if (player.isSneaking()) {
            if (!world.isRemote) {
                TileTelescope.TelescopeRotation current = telescope.getRotation();
                TileTelescope.TelescopeRotation newRotation = current.nextClockWise();
                telescope.setRotation(newRotation);
                LogHelper.info("[BlockTelescope] Rotated telescope at " + x + "," + y + "," + z +
                    " from " + current + " to " + newRotation);
            }
            return true;
        }

        // Normal Right-click: Open GUI
        if (world.isRemote) {
            return true;
        }

        LogHelper.info("[BlockTelescope] Opening telescope GUI at " + x + "," + y + "," + z);
        TileEntityGuiFactory.INSTANCE.open(player, telescope);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Could set rotation based on player facing here if needed
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this, 1, 0));
        return drops;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTelescope();
    }

}
