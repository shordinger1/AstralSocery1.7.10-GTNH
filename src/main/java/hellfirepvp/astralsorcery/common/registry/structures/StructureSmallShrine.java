/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.IBlockAccess;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallShrine
 * Created by HellFirePvP
 * Date: 07.01.2017 / 16:41
 */
public class StructureSmallShrine extends StructureBlockArray {

    public StructureSmallShrine() {
        load();
    }

    private void load() {
        Block m = BlocksAS.blockMarble;

        // BlockMarble metadata: RAW=0, CHISELED=1, BRICKS=2, ARCH=3, PILLAR=5
        BlockStateCheck mRaw = new BlockStateCheck.Meta(m, 0);
        BlockStateCheck mBrick = new BlockStateCheck.Meta(m, 2);
        BlockStateCheck mChisel = new BlockStateCheck.Meta(m, 1);
        BlockStateCheck mPillar = new BlockStateCheck.Meta(m, 5);

        addBlockCube(m, mRaw, -4, 0, -4, 4, 0, 4);
        addAirCube(-4, 1, -4, 4, 5, 4);
        addBlockCube(m, mBrick, -3, 1, -3, 3, 1, 3);
        addAirCube(-1, 1, -1, 1, 1, 1);

        addBlock(0, 1, 0, m, mPillar);
        addBlock(0, 2, 0, m, mPillar);
        // addBlock(0, 3, 0, Blocks.SEA_LANTERN);
        addBlock(0, 4, 0, Blocks.water);

        addBlock(2, 2, 2, m, mPillar);
        addBlock(2, 3, 2, m, mPillar);
        addBlock(2, 4, 2, m, mPillar);
        addBlock(2, 5, 2, m, mChisel);

        addBlock(2, 2, -2, m, mPillar);
        addBlock(2, 3, -2, m, mPillar);
        addBlock(2, 4, -2, m, mPillar);
        addBlock(2, 5, -2, m, mChisel);

        addBlock(-2, 2, 2, m, mPillar);
        addBlock(-2, 3, 2, m, mPillar);
        addBlock(-2, 4, 2, m, mPillar);
        addBlock(-2, 5, 2, m, mChisel);

        addBlock(-2, 2, -2, m, mPillar);
        addBlock(-2, 3, -2, m, mPillar);
        addBlock(-2, 4, -2, m, mPillar);
        addBlock(-2, 5, -2, m, mChisel);

        TileEntityCallback lootCallback = new TileEntityCallback() {

            @Override
            public boolean isApplicable(TileEntity te) {
                return te instanceof TileEntityChest;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                // 1.7.10: setLootTable doesn't exist, loot tables are filled when chest is opened
                // ((TileEntityChest) te).setLootTable(LootTableUtil.LOOT_TABLE_SHRINE, STATIC_RAND.nextLong());
            }
        };

        addBlock(2, 1, -2, Blocks.chest);
        addTileCallback(new BlockPos(2, 1, -2), lootCallback);

    }

}
