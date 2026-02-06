/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * World Illuminator - World lighting device
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
 * BlockWorldIlluminator - World lighting device (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Device for illuminating areas</li>
 * <li>Has TileEntity (TileWorldIlluminator)</li>
 * <li>Places BlockFlareLight automatically</li>
 * <li>Wireless range-based lighting</li>
 * </ul>
 */
public class BlockWorldIlluminator extends BlockContainer {

    public BlockWorldIlluminator() {
        super(Material.rock);
        setHardness(2.0F);
        setResistance(15.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    public boolean isOpaqueCube() {
        return true;

    }

    public boolean renderAsNormalBlock() {
        return true;

    }

    /**
     * On block activated - open GUI or toggle illuminator
     * 1.7.10: Removed EnumHand (off-hand is 1.9+ feature)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        hellfirepvp.astralsorcery.common.tile.TileWorldIlluminator illuminator =
            (hellfirepvp.astralsorcery.common.tile.TileWorldIlluminator) world.getTileEntity(x, y, z);

        if (illuminator != null) {
            // Sneak + right-click: Toggle illuminator
            if (player.isSneaking()) {
                illuminator.setPlayerPlaced();
                hellfirepvp.astralsorcery.common.util.LogHelper.info("[BlockWorldIlluminator] Toggled illuminator at " + x + "," + y + "," + z);
            } else {
                // Normal right-click: Open GUI
                TileEntityGuiFactory.INSTANCE.open(player, illuminator);
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
        return new hellfirepvp.astralsorcery.common.tile.TileWorldIlluminator();
    }
}
