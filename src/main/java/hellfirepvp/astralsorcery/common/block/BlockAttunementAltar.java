/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Attunement Altar - Advanced celestial altar
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
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * BlockAttunementAltar - Attunement altar (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Advanced altar for constellation attunement</li>
 * <li>Has TileEntity (TileAttunementAltar)</li>
 * <li>Higher tier than BlockAltar</li>
 * <li>Complex crafting rituals</li>
 * <li>Part of progression system</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * <li>Similar to BlockAltar but more complex</li>
 * </ul>
 */
public class BlockAttunementAltar extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconAltar;

    public BlockAttunementAltar() {
        super(Material.rock);
        setHardness(4.0F);
        setResistance(25.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 2); // Iron tier
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public boolean isOpaqueCube() {
        return false; // Custom model

    }

    public boolean renderAsNormalBlock() {
        return false; // Custom rendering
    }

    /**
     * Get render type - use TESR for custom rendering
     */
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    // ========== Texture Registration ==========

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // For TESR blocks, we need a simple icon for the item in hand/inventory
        iconAltar = reg.registerIcon("astralsorcery:blocks/attunement_altar");
        LogHelper.info("[BlockAttunementAltar] Registered icon: " + iconAltar.getIconName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconAltar;
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Handle attunement interaction
        TileAttunementAltar tile = (TileAttunementAltar) world.getTileEntity(x, y, z);
        if (tile != null) {
            tile.onRightClick(player);
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
        return new TileAttunementAltar();
    }
    // TODO: Implement advanced altar system
    // - GUI system
    // - Advanced recipes
    // - Constellation attunement
    // - Progression requirements
}
