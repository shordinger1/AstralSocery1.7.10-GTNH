/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAttunementFrame
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:36
 */
public class MultiblockAttunementFrame extends PatternBlockArray {

    public MultiblockAttunementFrame() {
        super(new ResourceLocation(AstralSorcery.MODID, "pattern_attunement_frame"));
        load();
    }

    private void load() {
        // 1.7.10: Use metadata instead of withProperty
        Block mar = BlocksAS.blockMarble; // ARCH type has metadata 3
        Block mbl = BlocksAS.blockBlackMarble; // RAW type has metadata 0

        addBlock(0, 0, 0, BlocksAS.attunementAltar);
        // addBlock(0, 1, 0, BlocksAS.blockStructural.getDefaultState().withProperty(BlockStructural.BLOCK_TYPE,
        // BlockStructural.BlockType.ATTUNEMENT_ALTAR_STRUCT));

        addBlockCube(mar, -7, -1, -8, 7, -1, -8);
        addBlockCube(mar, -7, -1, 8, 7, -1, 8);
        addBlockCube(mar, -8, -1, -7, -8, -1, 7);
        addBlockCube(mar, 8, -1, -7, 8, -1, 7);

        addBlockCube(mbl, -7, -1, -7, 7, -1, 7);

        pillarAt(-8, -0, -8);
        pillarAt(-8, -0, 8);
        pillarAt(8, -0, -8);
        pillarAt(8, -0, 8);

        addBlock(-9, -1, -9, mar);
        addBlock(-9, -1, -8, mar);
        addBlock(-9, -1, -7, mar);
        addBlock(-8, -1, -9, mar);
        addBlock(-7, -1, -9, mar);

        addBlock(-9, -1, 9, mar);
        addBlock(-9, -1, 8, mar);
        addBlock(-9, -1, 7, mar);
        addBlock(-8, -1, 9, mar);
        addBlock(-7, -1, 9, mar);

        addBlock(9, -1, -9, mar);
        addBlock(9, -1, -8, mar);
        addBlock(9, -1, -7, mar);
        addBlock(8, -1, -9, mar);
        addBlock(7, -1, -9, mar);

        addBlock(9, -1, 9, mar);
        addBlock(9, -1, 8, mar);
        addBlock(9, -1, 7, mar);
        addBlock(8, -1, 9, mar);
        addBlock(7, -1, 9, mar);
    }

    private void pillarAt(int x, int y, int z) {
        // 1.7.10: Use metadata instead of withProperty
        Block mru = BlocksAS.blockMarble; // RUNED has metadata 6
        Block mpl = BlocksAS.blockMarble; // PILLAR has metadata 2
        Block mch = BlocksAS.blockMarble; // CHISELED has metadata 4

        addBlock(x, y, z, mru);
        addBlock(x, y + 1, z, mpl);
        addBlock(x, y + 2, z, mpl);
        addBlock(x, y + 3, z, mpl);
        addBlock(x, y + 4, z, mch);
    }

    @Override
    public Map<BlockPos, Block> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, Block> placed = super.placeInWorld(world, center);
        // 1.7.10: setBlockToAir takes (x, y, z)
        if (world.setBlockToAir(center.getX(), center.getY(), center.getZ())) {
            placed.remove(center);
        }
        // 1.7.10: BlockPos doesn't have offset(), create new BlockPos manually
        BlockPos up = new BlockPos(center.getX(), center.getY() + 1, center.getZ());
        if (world.setBlockToAir(up.getX(), up.getY(), up.getZ())) {
            placed.remove(up);
        }
        return placed;
    }
}
