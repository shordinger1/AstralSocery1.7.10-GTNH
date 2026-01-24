/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IBlockState;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyBool;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarbleSlab
 * Created by HellFirePvP
 * Date: 05.07.2017 / 18:47
 */
public class BlockMarbleSlab extends BlockSlab {

    public static final PropertyEnum<EnumType> MARBLE_TYPE = PropertyEnum.create("marbletype", EnumType.class);
    public static final PropertyBool HALF = PropertyBool.create("half");

    private final BlockStateContainer blockState;
    private final boolean isDouble;

    public BlockMarbleSlab() {
        this(false); // Default to single slab
    }

    public BlockMarbleSlab(boolean isDouble) {
        super(isDouble, Material.rock);
        this.isDouble = isDouble;
        this.blockState = new BlockStateContainer(this, MARBLE_TYPE, HALF);
        IBlockState state = this.blockState.getBaseState();
        if (!isDouble) {
            state = state.withProperty(HALF, false); // false = BOTTOM
        }
        setDefaultState(state.withProperty(MARBLE_TYPE, EnumType.BRICKS));
        setStepSound(Block.soundTypePiston);
        setLightOpacity(0);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    public IBlockState getDefaultState() {
        IBlockState state = this.blockState.getBaseState();
        if (!isDouble) {
            state = state.withProperty(HALF, false);
        }
        return state.withProperty(MARBLE_TYPE, EnumType.BRICKS);
    }

    protected void setDefaultState(IBlockState state) {
        // In 1.7.10, default state is tracked separately
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(BlocksAS.blockMarbleSlab);
    }

    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName() + "." + EnumType.BRICKS.getName();
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumType type : EnumType.values()) {
            list.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public String func_150002_b(int meta) {
        // In 1.7.10 BlockSlab, this returns the full block name for the slab
        return EnumType.byMetadata(meta & 7)
            .getName();
    }

    public static enum EnumType implements IStringSerializable {

        BRICKS;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }
    }

}
