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
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.BlockStateContainer;
import hellfirepvp.astralsorcery.common.migration.IStringSerializable;
import hellfirepvp.astralsorcery.common.migration.PropertyEnum;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBlackMarble
 * Created by HellFirePvP
 * Date: 21.10.2016 / 23:02
 */
public class BlockBlackMarble extends Block implements BlockCustomName, BlockVariants {

    public static PropertyEnum<BlackMarbleBlockType> BLACK_MARBLE_TYPE = PropertyEnum
        .create("marbletype", BlackMarbleBlockType.class);

    private BlockStateContainer blockState;

    public BlockBlackMarble() {
        super(Material.rock);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(Block.soundTypePiston);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.blockState = new BlockStateContainer(this, BLACK_MARBLE_TYPE);
    }

    // In 1.7.10, default state is represented by default metadata (0)
    // BlackMarbleBlockType.RAW has meta 0, so no special handling needed

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlackMarbleBlockType t : BlackMarbleBlockType.values()) {
            if (!t.obtainableInCreative()) continue;
            list.add(new ItemStack(this, 1, t.ordinal()));
        }
    }

    // In 1.7.10, getActualState doesn't exist - pillar state is determined by metadata
    // PILLAR (2), PILLAR_TOP (2), PILLAR_BOTTOM (2) all use meta 2
    // This logic would need to be handled during block placement or updates

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    // In 1.7.10, getLightOpacity() doesn't take state parameter
    // Use default behavior from Block class

    @Override
    public boolean isOpaqueCube() {
        // 1.7.10: No state parameter, always return true for base implementation
        // Pillar variations are handled by the block state wrapper
        return true;
    }

    public boolean isFullCube() {
        // 1.7.10: No state parameter, return true for base implementation
        return true;
    }

    public boolean isFullBlock() {
        // 1.7.10: No state parameter, return true for base implementation
        return true;
    }

    public boolean doesSideBlockRendering(int metadata, IBlockAccess world, int x, int y, int z, EnumFacing face) {
        BlackMarbleBlockType marbleType = BlackMarbleBlockType
            .values()[metadata >= BlackMarbleBlockType.values().length ? 0 : metadata];

        // Get offset for EnumFacing
        int dx = 0, dy = 0, dz = 0;
        switch (face) {
            case DOWN:
                dy = -1;
                break;
            case UP:
                dy = 1;
                break;
            case NORTH:
                dz = -1;
                break;
            case SOUTH:
                dz = 1;
                break;
            case WEST:
                dx = -1;
                break;
            case EAST:
                dx = 1;
                break;
        }

        Block other = world.getBlock(x + dx, y + dy, z + dz);
        if (marbleType == BlackMarbleBlockType.PILLAR) {
            return false;
        }
        if (marbleType == BlackMarbleBlockType.PILLAR_TOP) {
            return face == EnumFacing.UP;
        }
        if (marbleType == BlackMarbleBlockType.PILLAR_BOTTOM) {
            return face == EnumFacing.DOWN;
        }
        return true;
    }

    // In 1.7.10, isTopSolid() doesn't exist or take state parameter
    // Use default behavior

    @Override
    public String getIdentifierForMeta(int meta) {
        BlackMarbleBlockType mt = BlackMarbleBlockType.values()[meta >= BlackMarbleBlockType.values().length ? 0
            : meta];
        return mt.getName();
    }

    // In 1.7.10, getMetaFromState and getStateFromMeta don't exist
    // Metadata is handled directly

    @Override
    public List<Block> getValidStates() {
        List<Block> ret = new LinkedList<>();
        // In 1.7.10, all variants are the same block with different metadata
        // Return the block itself once for each variant type
        for (BlackMarbleBlockType type : BlackMarbleBlockType.values()) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    public String getStateName(int metadata) {
        BlackMarbleBlockType type = BlackMarbleBlockType.values()[metadata >= BlackMarbleBlockType.values().length ? 0
            : metadata];
        return type.getName();
    }

    public static enum BlackMarbleBlockType implements IStringSerializable {

        RAW(0),
        BRICKS(1),
        PILLAR(2),
        ARCH(3),
        CHISELED(4),
        ENGRAVED(5),
        RUNED(6),

        PILLAR_TOP(2),
        PILLAR_BOTTOM(2);

        private final int meta;

        private BlackMarbleBlockType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockBlackMarble, 1, meta);
        }

        // In 1.7.10, asBlock() just returns the block instance
        // Metadata is handled separately
        public Block asBlock() {
            return BlocksAS.blockBlackMarble;
        }

        public boolean isPillar() {
            return this == PILLAR_BOTTOM || this == PILLAR || this == PILLAR_TOP;
        }

        public boolean obtainableInCreative() {
            return this != PILLAR_TOP && this != PILLAR_BOTTOM;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
