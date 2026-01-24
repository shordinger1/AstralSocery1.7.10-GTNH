/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRitualLink
 * Created by HellFirePvP
 * Date: 06.01.2017 / 02:40
 */
public class BlockRitualLink extends BlockContainer {

    private static final AxisAlignedBB box = AxisAlignedBB
        .getBoundingBox(6D / 16D, 2D / 16D, 6D / 16D, 10D / 16D, 14D / 16D, 10D / 16D);

    public BlockRitualLink() {
        super(Material.rock);
        setHardness(3.0F);
        setStepSound(Block.soundTypeGlass);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 2);
        setLightLevel(0.6F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(6F / 16F, 2F / 16F, 6F / 16F, 10F / 16F, 14F / 16F, 10F / 16F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return box;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return box;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRitualLink();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

}
