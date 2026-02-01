/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Ritual Pedestal - Ritual pedestal block
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
 * BlockRitualPedestal - Ritual pedestal (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Ritual pedestal for constellation rituals</li>
 * <li>Has TileEntity (TileRitualPedestal)</li>
 * <li>Part of multiblock ritual structures</li>
 * <li>Item display and interaction</li>
 * </ul>
 */
public class BlockRitualPedestal extends BlockContainer {

    public BlockRitualPedestal() {
        super(Material.rock);
        setHardness(3.0F);
        setResistance(20.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
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
     * Get render type - use TESR for pedestal rendering
     */
    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // TODO: Interact with pedestal (place/remove item)
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
        // TODO: Drop displayed item
        drops.add(new ItemStack(this, 1, 0));
        return drops;

    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileRitualPedestal();
    }
}
