/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Celestial Collector Crystal Block - Enhanced starlight collector
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * Celestial Collector Crystal Block
 * <p>
 * An enhanced starlight collector made with celestial crystal.
 * <p>
 * Features:
 * - Collects starlight more efficiently than regular collector
 * - Larger starlight capacity
 * - Can connect to more constellation lenses
 * - Emits more light
 * - Supports constellation-specific effects (TODO)
 * <p>
 * Compared to BlockCollectorCrystal:
 * - Higher collection efficiency
 * - Better range for starlight collection
 * - Supports more lens connections
 * - Enhanced particle effects (TODO)
 * <p>
 * TODO:
 * - Implement enhanced collection logic
 * - Implement constellation-specific collection
 * - Implement glow effect
 * - Implement TileEntity with enhanced properties
 * - Implement network visualization
 * - Link with ItemCelestialCrystal properties
 */
public class BlockCelestialCollectorCrystal extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconCrystal;

    public BlockCelestialCollectorCrystal() {
        super(Material.glass);

        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeGlass);
        setHarvestLevel("pickaxe", 1); // Stone tier
        setLightLevel(0.8F); // Emits more light than regular collector
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        // Use celestial crystal texture
        iconCrystal = IconHelper.registerIcon(register, "crystal_celestial");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconCrystal;

    }

    public boolean isOpaqueCube() {
        return false; // Not a full block - has transparency
    }

    public boolean renderAsNormalBlock() {
        return false; // Special rendering
    }

    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);

    }

    public int quantityDropped(Random rand) {
        return 1;

    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileCelestialCollectorCrystal();
    }
    /**
     * NOTE: Enhanced starlight network integration
     * <p>
     * Original version:
     * - Extends BlockStarlightNetwork
     * - Higher collection range
     * - More lens connection points
     * - Better transmission efficiency
     * <p>
     * In 1.7.10:
     * - TODO: Implement BlockStarlightNetwork base class
     * - TODO: Implement TileCelestialCollectorCrystal
     * - TODO: Implement enhanced collection logic
     * - TODO: Implement network visualization
     * - TODO: Implement constellation-specific bonuses
     */
}
