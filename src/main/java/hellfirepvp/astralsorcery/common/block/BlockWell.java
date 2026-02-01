/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Well - Starlight well block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.TileEntityGuiFactory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * BlockWell - Starlight well (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Starlight well for liquid collection</li>
 * <li>Has TileEntity (TileWell)</li>
 * <li>Part of liquid starlight system</li>
 * <li>GUI for liquid management</li>
 * </ul>
 */
public class BlockWell extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconWell;

    public BlockWell() {
        super(Material.rock);
        setHardness(2.5F);
        setResistance(15.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // Set block bounds (similar to cauldron - hollow container)
        // Well is approximately full block height but hollow inside
        // Values are 0-16 (block coordinates)
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Well is hollow
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false; // Custom rendering
    }

    /**
     * Get render type - use TESR for custom rendering
     */
    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    /**
     * Add collision boxes for well shape
     * <p>
     * Well has base (5 pixels high) and 4 walls (11 pixels high)
     * Players can step into the well
     */
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB entityBox,
        List<AxisAlignedBB> collidingBoxes, Entity entity) {
        // Well base (bottom section) - 5 pixels high
        AxisAlignedBB baseBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 0D,
            (double) z + 1D / 16D,
            (double) x + 15D / 16D,
            (double) y + 5D / 16D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(baseBB)) {
            collidingBoxes.add(baseBB);
        }

        // Well walls (top section) - 4 walls, 11 pixels high
        AxisAlignedBB northBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 5D / 16D,
            (double) z + 1D / 16D,
            (double) x + 2D / 16D,
            (double) y + 1D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(northBB)) {
            collidingBoxes.add(northBB);
        }

        AxisAlignedBB westBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 5D / 16D,
            (double) z + 1D / 16D,
            (double) x + 15D / 16D,
            (double) y + 1D,
            (double) z + 2D / 16D);
        if (entityBox.intersectsWith(westBB)) {
            collidingBoxes.add(westBB);
        }

        AxisAlignedBB southBB = AxisAlignedBB.getBoundingBox(
            (double) x + 14D / 16D,
            (double) y + 5D / 16D,
            (double) z + 1D / 16D,
            (double) x + 15D / 16D,
            (double) y + 1D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(southBB)) {
            collidingBoxes.add(southBB);
        }

        AxisAlignedBB eastBB = AxisAlignedBB.getBoundingBox(
            (double) x + 1D / 16D,
            (double) y + 5D / 16D,
            (double) z + 14D / 16D,
            (double) x + 15D / 16D,
            (double) y + 1D,
            (double) z + 15D / 16D);
        if (entityBox.intersectsWith(eastBB)) {
            collidingBoxes.add(eastBB);
        }
    }

    /**
     * Comparator output - based on liquid starlight fill level
     * Returns 0-15 redstone signal based on well fill percentage
     */
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileWell well = (TileWell) world.getTileEntity(x, y, z);
        if (well != null) {
            // Get fill percentage from TileWell
            // In 1.7.10 simplified version: check if well has liquid
            // TODO: When TileWell fluid system is implemented:
            // float percentage = well.getPercFilled();
            // return MathHelper.ceiling_float_int(percentage * 15F);

            // Simplified version for now
            // Return signal based on whether well has any fluid
            return well.getHeldFluid() != null ? 15 : 0;
        }
        return 0;
    }

    /**
     * Has comparator output
     */
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Open well GUI using ModularUI
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileWell) {
            TileWell well = (TileWell) te;
            if (!world.isRemote) {
                // Open ModularUI GUI on server side
                TileEntityGuiFactory.INSTANCE.open(player, well);
            }
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
        return new TileWell();

        // TODO: Implement well system
        // - Liquid starlight collection
        // - GUI system
        // - Liquid management
    }

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // For TESR blocks, we need a simple icon for the item in hand/inventory
        // Register all block textures and store reference for quick access
        hellfirepvp.astralsorcery.client.util.BlockTextureRegister.registerAll(reg);
        iconWell = reg.registerIcon("astralsorcery:blocks/well");
        LogHelper.info("[BlockWell] Registered icon: " + iconWell.getIconName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconWell;
    }
}
