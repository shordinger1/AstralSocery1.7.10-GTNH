/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Starlight Infuser - Starlight infusion device
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.TextureRegister;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockStarlightInfuser - Starlight infuser (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Infuses items with starlight</li>
 * <li>Has TileEntity (TileStarlightInfuser)</li>
 * <li>GUI for infusion</li>
 * <li>Part of item enhancement system</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * </ul>
 */
public class BlockStarlightInfuser extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconInfuser;

    public BlockStarlightInfuser() {
        super(Material.rock);
        setHardness(3.0F);
        setResistance(15.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Custom model
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false; // Custom rendering
    }

    /**
     * Set block bounds based on state
     * <p>
     * Infuser has height of 12 pixels (0.75 blocks) instead of full 1.0
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        // Height is 12/16 instead of full 1.0
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    /**
     * Get collision bounding box
     * <p>
     * Infuser has reduced height (12 pixels)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.75, z + 1);
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // TODO: Open infuser GUI
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
        return new hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser();
    }

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // Load texture from models/starlight_infuser/ directory
        iconInfuser = TextureRegister.registerModelIcon(reg, "starlight_infuser/starlight_infuser");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconInfuser;
    }
}
