/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Ritual Link - Ritual link block
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * BlockRitualLink - Ritual link (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Invisible ritual linking block</li>
 * <li>Has TileEntity (TileRitualLink)</li>
 * <li>Connects ritual components</li>
 * <li>Not in creative tab</li>
 * </ul>
 */
public class BlockRitualLink extends BlockContainer {

    public BlockRitualLink() {
        super(Material.glass);
        setBlockUnbreakable();
        setResistance(6000001.0F);
        setCreativeTab(null); // Not in creative tab
    }

    public int getRenderType() {
        return -1; // Invisible
    }

    public boolean isOpaqueCube() {
        return false;

    }

    public boolean renderAsNormalBlock() {
        return false;

    }

    public Item getItemDropped(int meta, Random rand, int fortune) {
        return null;

    }

    public int quantityDropped(Random rand) {
        return 0;

    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileRitualLink();
    }
}
