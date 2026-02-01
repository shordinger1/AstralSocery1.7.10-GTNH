/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Tree Beacon - Patreon supporter beacon
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * BlockTreeBeacon - Patreon tree beacon (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Patreon supporter beacon block</li>
 * <li>Has TileEntity (TileTreeBeacon)</li>
 * <li>Displays particle effects</li>
 * <li>Links to player account</li>
 * <li>Visual beacon effect</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * <li>Patreon-only feature</li>
 * <li>Not in creative tab</li>
 * </ul>
 */
public class BlockTreeBeacon extends BlockContainer {

    public BlockTreeBeacon() {
        super(Material.glass);
        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(1.0F); // Full brightness
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
        return null; // No drops
    }

    public int quantityDropped(Random rand) {
        return 0;

    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileTreeBeacon();
    }
}
