/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;
import hellfirepvp.astralsorcery.common.tile.TileStructController;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallRuin
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:07
 */
public class StructureSmallRuin extends StructureBlockArray {

    public StructureSmallRuin() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;

        // BlockMarble metadata: RAW=0, CHISELED=1, BRICKS=2, ARCH=3, PILLAR=5
        BlockStateCheck mrw = new BlockStateCheck.Meta(marble, 0); // RAW
        BlockStateCheck mch = new BlockStateCheck.Meta(marble, 1); // CHISELED
        BlockStateCheck mar = new BlockStateCheck.Meta(marble, 3); // ARCH
        BlockStateCheck mbr = new BlockStateCheck.Meta(marble, 2); // BRICKS

        // 1.7.10 stairs use metadata for facing: 0=EAST, 1=WEST, 2=SOUTH, 3=NORTH
        BlockStateCheck stairsSouth = new BlockStateCheck.Meta(BlocksAS.blockMarbleStairs, 2); // SOUTH
        BlockStateCheck stairsNorth = new BlockStateCheck.Meta(BlocksAS.blockMarbleStairs, 3); // NORTH

        addBlock(0, 3, 0, BlocksAS.blockPortalNode);
        addTileCallback(new BlockPos(0, 3, 0), new TileEntityCallback() {

            @Override
            public boolean isApplicable(TileEntity te) {
                return te instanceof TileStructController;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                if (te instanceof TileStructController) {
                    ((TileStructController) te).setType(TileStructController.StructType.GATE);
                }
            }
        });

        addBlock(1, 0, 0, marble, mrw);
        addBlock(1, 0, 1, marble, mrw);
        addBlock(1, 0, 2, marble, mrw);
        addBlock(1, 0, 3, marble, mrw);
        addBlock(2, 0, 2, marble, mrw);
        addBlock(0, 0, 2, marble, mrw);
        addBlock(-1, 0, 2, marble, mrw);
        addBlock(0, 0, 3, marble, mrw);
        addBlock(-1, 0, 3, marble, mrw);
        addBlock(0, 0, 4, marble, mrw);
        addBlock(-1, 0, 4, marble, mrw);
        addBlock(0, 0, 5, marble, mrw);

        addBlock(0, 0, -2, marble, mrw);
        addBlock(0, 0, -3, marble, mrw);
        addBlock(0, 0, -4, marble, mrw);
        addBlock(0, 0, -5, marble, mrw);
        addBlock(0, 0, -6, marble, mrw);
        addBlock(1, 0, -1, marble, mrw);
        addBlock(1, 0, -2, marble, mbr);
        addBlock(1, 0, -3, marble, mrw);
        addBlock(1, 0, -4, marble, mrw);
        addBlock(2, 0, -2, marble, mrw);
        addBlock(2, 0, -3, marble, mrw);
        addBlock(-1, 0, -3, marble, mbr);
        addBlock(-1, 0, -4, marble, mrw);
        addBlock(-2, 0, -4, marble, mrw);

        addBlock(0, 1, 1, BlocksAS.blockMarbleStairs, stairsSouth);
        addBlock(0, 1, -1, BlocksAS.blockMarbleStairs, stairsNorth);

        addBlock(0, 1, -2, marble, mrw);
        addBlock(0, 1, -3, marble, mrw);
        addBlock(0, 1, -4, marble, mrw);
        addBlock(1, 1, -2, marble, mrw);
        addBlock(-1, 1, -2, marble, mrw);
        addBlock(1, 1, 0, marble, mar);
        addBlock(1, 1, -1, marble, mar);
        addBlock(1, 1, -3, BlocksAS.blockMarbleStairs, stairsSouth);

        addBlock(-1, 1, 1, marble, mar);
        addBlock(-1, 1, 2, marble, mrw);
        addBlock(0, 1, 2, marble, mrw);
        addBlock(1, 1, 2, marble, mbr);
        addBlock(1, 1, 3, BlocksAS.blockMarbleStairs, stairsNorth);
        addBlock(0, 1, 3, marble, mrw);

        addBlock(0, 2, 2, marble, mch);
        addBlock(0, 3, 2, marble, mrw);
        addBlock(0, 2, -2, marble, mrw);
        addBlock(0, 3, -2, marble, mch);

        addBlock(0, 4, 2, BlocksAS.blockMarbleStairs, stairsNorth);
        addBlock(0, 4, -2, BlocksAS.blockMarbleStairs, stairsSouth);
    }

}
