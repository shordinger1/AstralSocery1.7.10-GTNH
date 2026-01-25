/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;
import hellfirepvp.astralsorcery.common.tile.TileOreGenerator;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureTreasureShrine
 * Created by HellFirePvP
 * Date: 20.07.2017 / 22:27
 */
public class StructureTreasureShrine extends StructureBlockArray {

    public StructureTreasureShrine() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;

        // BlockMarble metadata: RAW=0, CHISELED=1, BRICKS=2, ARCH=3, PILLAR=5, RUNED=4
        BlockStateCheck mrw = new BlockStateCheck.Meta(marble, 0); // RAW
        BlockStateCheck mru = new BlockStateCheck.Meta(marble, 4); // RUNED
        BlockStateCheck mpl = new BlockStateCheck.Meta(marble, 5); // PILLAR
        BlockStateCheck mbr = new BlockStateCheck.Meta(marble, 2); // BRICKS

        addBlockCube(marble, mrw, -4, 0, -4, 4, 8, 4);
        addAirCube(-3, 2, -3, 3, 7, 3);

        addBlock(0, 1, 0, marble, mru);
        addBlock(0, 2, 0, marble, mpl);
        addBlock(0, 3, 0, marble, mpl);
        addBlock(0, 4, 0, Blocks.stone);
        addBlock(0, 5, 0, marble, mpl);
        addBlock(0, 6, 0, marble, mpl);
        addBlock(0, 7, 0, marble, mru);

        addBlock(-1, 1, 1, Blocks.water);
        addBlock(0, 1, 1, Blocks.water);
        addBlock(1, 1, 1, Blocks.water);
        addBlock(1, 1, 0, Blocks.water);
        addBlock(1, 1, -1, Blocks.water);
        addBlock(0, 1, -1, Blocks.water);
        addBlock(-1, 1, -1, Blocks.water);
        addBlock(-1, 1, 0, Blocks.water);

        addBlock(2, 1, 0, Blocks.water);
        addBlock(3, 1, 0, Blocks.water);
        addBlock(-2, 1, 0, Blocks.water);
        addBlock(-3, 1, 0, Blocks.water);
        addBlock(0, 1, 2, Blocks.water);
        addBlock(0, 1, 3, Blocks.water);
        addBlock(0, 1, -2, Blocks.water);
        addBlock(0, 1, -3, Blocks.water);

        addBlock(2, 1, 1, marble, mbr);
        addBlock(3, 1, 1, marble, mbr);
        addBlock(2, 1, -1, marble, mbr);
        addBlock(3, 1, -1, marble, mbr);
        addBlock(-2, 1, 1, marble, mbr);
        addBlock(-3, 1, 1, marble, mbr);
        addBlock(-2, 1, -1, marble, mbr);
        addBlock(-3, 1, -1, marble, mbr);

        addBlock(1, 1, 2, marble, mbr);
        addBlock(1, 1, 3, marble, mbr);
        addBlock(-1, 1, 2, marble, mbr);
        addBlock(-1, 1, 3, marble, mbr);
        addBlock(1, 1, -2, marble, mbr);
        addBlock(1, 1, -3, marble, mbr);
        addBlock(-1, 1, -2, marble, mbr);
        addBlock(-1, 1, -3, marble, mbr);

        buildPillar(-3, -3, marble, mpl);
        buildPillar(3, -3, marble, mpl);
        buildPillar(3, 3, marble, mpl);
        buildPillar(-3, 3, marble, mpl);
    }

    private void buildPillar(int x, int z, Block marble, BlockStateCheck mpl) {
        for (int i = 0; i < 5; i++) {
            addBlock(x, 2 + i, z, marble, mpl);
        }
//        addBlock(x, 7, z, Blocks.prismarine); // 1.7.10 doesn't have sea_lantern, using prismarine as closest equivalent
    }

    @Override
    public Map<BlockPos, Block> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, Block> placed = super.placeInWorld(world, center);
        TileOreGenerator.createStructuralTile(world, center.add(0, 4, 0));
        return placed;
    }

}
