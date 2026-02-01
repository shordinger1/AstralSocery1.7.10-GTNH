/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Collector crystal block - Starlight collector
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * BlockCollectorCrystal - Starlight collector crystal (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Collects starlight and transfers to adjacent blocks</li>
 * <li>Has TileEntity (TileCollectorCrystal)</li>
 * <li>Crystal appearance with glow effect</li>
 * <li>Part of starlight network</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>BlockContainer with TileEntity</li>
 * <li>No IBlockState - uses metadata directly</li>
 * <li>Part of BlockStarlightNetwork system</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Complex starlight network block
 * <ul>
 * <li>Requires BlockStarlightNetwork base class</li>
 * <li>Integrates with starlight transmission system</li>
 * <li>Has crystal properties</li>
 * <li>Network visualization and rendering</li>
 * </ul>
 */
public class BlockCollectorCrystal extends BlockContainer {

    /**
     * Constructor
     */
    public BlockCollectorCrystal() {
        super(Material.glass);

        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeGlass);
        setHarvestLevel("pickaxe", 1); // Stone tier
        setLightLevel(0.5F); // Emits light
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // NOTE: 1.7.10 doesn't have MapColor.QUARTZ
        // Using default glass color
    }

    /**
     * Is this a full block?
     */
    public boolean isOpaqueCube() {
        return false;

    }

    /**
     * Does this block render normally?
     */
    public boolean renderAsNormalBlock() {
        return false;

    }

    /**
     * Get the item dropped
     */
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);

    }

    /**
     * Quantity dropped
     */
    public int quantityDropped(Random rand) {
        return 1;

    }

    /**
     * Create new tile entity
     */
    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal();
    }
}
