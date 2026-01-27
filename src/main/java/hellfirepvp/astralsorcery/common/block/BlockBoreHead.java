/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileBore;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBoreHead
 * Created by HellFirePvP
 * Date: 07.11.2017 / 20:22
 */
public class BlockBoreHead extends Block implements BlockCustomName, BlockVariants {

    public static final PropertyEnum<TileBore.BoreType> BORE_TYPE = PropertyEnum
        .create("type", TileBore.BoreType.class);

    private BlockStateContainer blockState;

    public BlockBoreHead() {
        super(Material.iron);
        setHarvestLevel("pickaxe", 2);
        setHardness(10F);
        setResistance(15F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, BORE_TYPE);
    }

//     public IBlockState getDefaultState() {
//         return this.blockState.getBaseState()
//             .withProperty(BORE_TYPE, TileBore.BoreType.LIQUID);
//     }

//     protected void setDefaultState(IBlockState state) {
//         // In 1.7.10, default state is tracked separately
//     }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (TileBore.BoreType bt : TileBore.BoreType.values()) {
            list.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 3; // Render type 3 = MODEL in 1.7.10
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        TileBore.BoreType bt = TileBore.BoreType.values()[WrapMathHelper
            .clamp(meta, 0, TileBore.BoreType.values().length - 1)];
        return bt.getName();
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (TileBore.BoreType type : TileBore.BoreType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        TileBore.BoreType bt = TileBore.BoreType.values()[WrapMathHelper
            .clamp(metadata, 0, TileBore.BoreType.values().length - 1)];
        return bt.getName();
    }
}
