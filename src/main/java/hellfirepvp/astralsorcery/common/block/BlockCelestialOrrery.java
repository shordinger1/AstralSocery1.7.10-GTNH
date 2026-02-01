/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Celestial Orrery - Advanced starlight device
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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * BlockCelestialOrrery - Celestial orrery device (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Advanced starlight observation device</li>
 * <li>Has TileEntity (TileCelestialOrrery)</li>
 * <li>Part of constellation discovery</li>
 * <li>Multiblock structure</li>
 * <li>GUI for viewing constellations</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * <li>Complex multiblock structure</li>
 * </ul>
 */
public class BlockCelestialOrrery extends BlockContainer {

    /** Icon for the orrery */
    @SideOnly(Side.CLIENT)
    private IIcon iconOrrery;

    public BlockCelestialOrrery() {
        super(Material.rock);
        setHardness(3.0F);
        setResistance(20.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    /**
     * Register block icons
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconOrrery = IconHelper.registerBlockIconsFromConfig(reg, "blockcelestialorrery")[0];
    }

    /**
     * Get icon for rendering
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconOrrery;
    }

    public boolean isOpaqueCube() {
        return true;

    }

    public boolean renderAsNormalBlock() {
        return true;

    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // TODO: Open orrery GUI
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
        return new hellfirepvp.astralsorcery.common.tile.TileCelestialOrrery();
    }
}
