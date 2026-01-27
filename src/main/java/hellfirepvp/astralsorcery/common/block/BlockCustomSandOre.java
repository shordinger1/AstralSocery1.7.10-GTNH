/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCustomSandOre
 * Created by HellFirePvP
 * Date: 17.08.2016 / 13:07
 */
public class BlockCustomSandOre extends BlockFalling implements BlockCustomName, BlockVariants {

    private static final Random rand = new Random();

    public BlockCustomSandOre() {
        super(Material.sand);
        setHardness(0.5F);
        setStepSound(Block.soundTypeSand);
        setHarvestLevel("shovel", 1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (OreType t : OreType.values()) {
            list.add(new ItemStack(this, 1, t.ordinal()));
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        OreType type = OreType.byMetadata(metadata);
        switch (type) {
            case AQUAMARINE:
                int f = fortune + 3;
                int i = rand.nextInt(f * 2) - 1;
                if (i < 0) {
                    i = 0;
                }
                for (int j = 0; j < (i + 1); j++) {
                    drops.add(ItemCraftingComponent.MetaType.AQUAMARINE.asStack());
                }
                break;
        }
        return drops;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public int getRenderType() {
        return 0; // Standard render type
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        OreType ot = OreType.byMetadata(meta);
        return ot.getName();
    }

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (OreType type : OreType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        OreType type = OreType.byMetadata(metadata);
        return type.getName();
    }

//    @Override
//     public List<IBlockState> getValidStates() {
//         List<IBlockState> ret = new LinkedList<>();
//         for (OreType type : OreType.values()) {
//             ret.add(new IBlockState(this, type.ordinal()));
//         }
//         return ret;
//     }

//    @Override
//     public String getStateName(IBlockState state) {
//         OreType type = OreType.byMetadata(state.getMetadata());
//         return type.getName();
//     }

    public enum OreType implements IStringSerializable {

        AQUAMARINE(0);

        private final int meta;

        OreType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.customSandOre, 1, meta);
        }

        public int getMeta() {
            return meta;
        }

        public static OreType byMetadata(int meta) {
            OreType[] values = values();
            return meta >= 0 && meta < values.length ? values[meta] : values[0];
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
