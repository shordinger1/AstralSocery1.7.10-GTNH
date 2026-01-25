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

import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureAncientShrine
 * Created by HellFirePvP
 * Date: 02.08.2016 / 10:24
 */
public class StructureAncientShrine extends StructureBlockArray {

    public StructureAncientShrine() {
        load();
    }

    private void load() {
        Block m = BlocksAS.blockMarble;

        // BlockMarble metadata: RAW=0, CHISELED=1, BRICKS=2, ARCH=3, PILLAR=5, RUNED=4, ENGRAVED=6
        BlockStateCheck mRaw = new BlockStateCheck.Meta(m, 0);   // RAW
        BlockStateCheck mBrick = new BlockStateCheck.Meta(m, 2); // BRICKS
        BlockStateCheck mChisel = new BlockStateCheck.Meta(m, 1); // CHISELED
        BlockStateCheck mPillar = new BlockStateCheck.Meta(m, 5); // PILLAR
        BlockStateCheck mArch = new BlockStateCheck.Meta(m, 3);  // ARCH
        BlockStateCheck mRuned = new BlockStateCheck.Meta(m, 4); // RUNED
        BlockStateCheck mEngraved = new BlockStateCheck.Meta(m, 6); // ENGRAVED

        addBlockCube(m, mRaw, -7, 0, -7, 7, 0, 7);
        addAirCube(-7, 1, -7, 7, 11, 7);

        addBlockCube(m, mRaw, -9, 0, -9, -5, 0, -5);
        addBlockCube(m, mRaw, 9, 0, 9, 5, 0, 5);
        addBlockCube(m, mRaw, -9, 0, 9, -5, 0, 5);
        addBlockCube(m, mRaw, 9, 0, -9, 5, 0, -5);
        addAirCube(-9, 1, -9, -5, 6, -5);
        addAirCube(9, 1, 9, 5, 6, 5);
        addAirCube(-9, 1, 9, -5, 6, 5);
        addAirCube(9, 1, -9, 5, 6, -5);

        addBlockCube(m, mRaw, -6, -1, -6, 6, -7, 6);
        addAirCube(-4, -1, -4, 4, -5, 4);

        addBlockCube(m, mBrick, -6, 1, -6, 6, 1, 6);
        addBlockCube(m, mBrick, -8, 1, -8, -6, 1, -6);
        addBlockCube(m, mBrick, -8, 1, 8, -6, 1, 6);
        addBlockCube(m, mBrick, 8, 1, -8, 6, 1, -6);
        addBlockCube(m, mBrick, 8, 1, 8, 6, 1, 6);

        addAirCube(-2, 1, -2, 2, 1, 2);

        addAirCube(-3, 1, 1, -3, 1, -1);
        addAirCube(3, 1, 1, 3, 1, -1);
        addAirCube(1, 1, -3, -1, 1, -3);
        addAirCube(1, 1, 3, -1, 1, 3);

        addAirCube(-1, -6, -1, 1, -6, 1);

        addBlockCube(m, mBrick, -2, 10, -2, 2, 10, 2);

        addBlock(0, 0, 0, Blocks.glowstone);
        addBlock(1, 0, 0, m, mChisel);
        addBlock(-1, 0, 0, m, mChisel);
        addBlock(0, 0, 1, m, mChisel);
        addBlock(0, 0, -1, m, mChisel);

        addBlock(-5, -2, 0, Blocks.water);
        addBlock(5, -2, 0, Blocks.water);
        addBlock(0, -2, -5, Blocks.water);
        addBlock(0, -2, 5, Blocks.water);

        addAir(2, -6, 0);
        addAir(3, -6, 0);
        addAir(4, -6, 0);
        addAir(-2, -6, 0);
        addAir(-3, -6, 0);
        addAir(-4, -6, 0);
        addAir(0, -6, 2);
        addAir(0, -6, 3);
        addAir(0, -6, 4);
        addAir(0, -6, -2);
        addAir(0, -6, -3);
        addAir(0, -6, -4);

        addBlock(5, -6, 0, m, mArch);
        addBlock(-5, -6, 0, m, mArch);
        addBlock(0, -6, 5, m, mArch);
        addBlock(0, -6, -5, m, mArch);
        addBlock(2, -6, 1, m, mArch);
        addBlock(3, -6, 1, m, mArch);
        addBlock(4, -6, 1, m, mArch);
        addBlock(-2, -6, 1, m, mArch);
        addBlock(-3, -6, 1, m, mArch);
        addBlock(-4, -6, 1, m, mArch);
        addBlock(2, -6, -1, m, mArch);
        addBlock(3, -6, -1, m, mArch);
        addBlock(4, -6, -1, m, mArch);
        addBlock(-2, -6, -1, m, mArch);
        addBlock(-3, -6, -1, m, mArch);
        addBlock(-4, -6, -1, m, mArch);
        addBlock(1, -6, 2, m, mArch);
        addBlock(1, -6, 3, m, mArch);
        addBlock(1, -6, 4, m, mArch);
        addBlock(1, -6, -2, m, mArch);
        addBlock(1, -6, -3, m, mArch);
        addBlock(1, -6, -4, m, mArch);
        addBlock(-1, -6, 2, m, mArch);
        addBlock(-1, -6, 3, m, mArch);
        addBlock(-1, -6, 4, m, mArch);
        addBlock(-1, -6, -2, m, mArch);
        addBlock(-1, -6, -3, m, mArch);
        addBlock(-1, -6, -4, m, mArch);

        addBlock(-3, -5, -3, m, mRuned);
        addBlock(-3, -4, -3, m, mPillar);
        addBlock(-3, -3, -3, m, mPillar);
        addBlock(-3, -2, -3, m, mPillar);
        addBlock(-3, -1, -3, m, mEngraved);
        addBlock(-3, -5, 3, m, mRuned);
        addBlock(-3, -4, 3, m, mPillar);
        addBlock(-3, -3, 3, m, mPillar);
        addBlock(-3, -2, 3, m, mPillar);
        addBlock(-3, -1, 3, m, mEngraved);
        addBlock(3, -5, -3, m, mRuned);
        addBlock(3, -4, -3, m, mPillar);
        addBlock(3, -3, -3, m, mPillar);
        addBlock(3, -2, -3, m, mPillar);
        addBlock(3, -1, -3, m, mEngraved);
        addBlock(3, -5, 3, m, mRuned);
        addBlock(3, -4, 3, m, mPillar);
        addBlock(3, -3, 3, m, mPillar);
        addBlock(3, -2, 3, m, mPillar);
        addBlock(3, -1, 3, m, mEngraved);

        addBlock(-5, -5, -3, m, mPillar);
        addBlock(-5, -4, -3, m, mPillar);
        addBlock(-5, -3, -3, m, mPillar);
        addBlock(-5, -2, -3, m, mPillar);
        addBlock(-5, -1, -3, m, mChisel);
        addBlock(-3, -5, -5, m, mPillar);
        addBlock(-3, -4, -5, m, mPillar);
        addBlock(-3, -3, -5, m, mPillar);
        addBlock(-3, -2, -5, m, mPillar);
        addBlock(-3, -1, -5, m, mChisel);
        addBlock(5, -5, -3, m, mPillar);
        addBlock(5, -4, -3, m, mPillar);
        addBlock(5, -3, -3, m, mPillar);
        addBlock(5, -2, -3, m, mPillar);
        addBlock(5, -1, -3, m, mChisel);
        addBlock(3, -5, -5, m, mPillar);
        addBlock(3, -4, -5, m, mPillar);
        addBlock(3, -3, -5, m, mPillar);
        addBlock(3, -2, -5, m, mPillar);
        addBlock(3, -1, -5, m, mChisel);
        addBlock(-5, -5, 3, m, mPillar);
        addBlock(-5, -4, 3, m, mPillar);
        addBlock(-5, -3, 3, m, mPillar);
        addBlock(-5, -2, 3, m, mPillar);
        addBlock(-5, -1, 3, m, mChisel);
        addBlock(-3, -5, 5, m, mPillar);
        addBlock(-3, -4, 5, m, mPillar);
        addBlock(-3, -3, 5, m, mPillar);
        addBlock(-3, -2, 5, m, mPillar);
        addBlock(-3, -1, 5, m, mChisel);
        addBlock(5, -5, 3, m, mPillar);
        addBlock(5, -4, 3, m, mPillar);
        addBlock(5, -3, 3, m, mPillar);
        addBlock(5, -2, 3, m, mPillar);
        addBlock(5, -1, 3, m, mChisel);
        addBlock(3, -5, 5, m, mPillar);
        addBlock(3, -4, 5, m, mPillar);
        addBlock(3, -3, 5, m, mPillar);
        addBlock(3, -2, 5, m, mPillar);
        addBlock(3, -1, 5, m, mChisel);

        addBlock(-7, 2, -7, m, mBrick);
        addBlock(-7, 3, -7, m, mPillar);
        addBlock(-7, 4, -7, m, mPillar);
        addBlock(-7, 5, -7, m, mChisel);
        addBlock(7, 2, -7, m, mBrick);
        addBlock(7, 3, -7, m, mPillar);
        addBlock(7, 4, -7, m, mPillar);
        addBlock(7, 5, -7, m, mChisel);
        addBlock(-7, 2, 7, m, mBrick);
        addBlock(-7, 3, 7, m, mPillar);
        addBlock(-7, 4, 7, m, mPillar);
        addBlock(-7, 5, 7, m, mChisel);
        addBlock(7, 2, 7, m, mBrick);
        addBlock(7, 3, 7, m, mPillar);
        addBlock(7, 4, 7, m, mPillar);
        addBlock(7, 5, 7, m, mChisel);

        addBlock(5, 2, 0, m, mBrick);
        addBlock(5, 3, 0, m, mPillar);
        addBlock(5, 4, 0, m, mPillar);
        addBlock(5, 5, 0, m, mPillar);
        addBlock(5, 6, 0, m, mChisel);
        addBlock(5, 7, 0, m, mPillar);
        addBlock(5, 8, 0, m, mChisel);
        addBlock(-5, 2, 0, m, mBrick);
        addBlock(-5, 3, 0, m, mPillar);
        addBlock(-5, 4, 0, m, mPillar);
        addBlock(-5, 5, 0, m, mPillar);
        addBlock(-5, 6, 0, m, mChisel);
        addBlock(-5, 7, 0, m, mPillar);
        addBlock(-5, 8, 0, m, mChisel);
        addBlock(0, 2, 5, m, mBrick);
        addBlock(0, 3, 5, m, mPillar);
        addBlock(0, 4, 5, m, mPillar);
        addBlock(0, 5, 5, m, mPillar);
        addBlock(0, 6, 5, m, mChisel);
        addBlock(0, 7, 5, m, mPillar);
        addBlock(0, 8, 5, m, mChisel);
        addBlock(0, 2, -5, m, mBrick);
        addBlock(0, 3, -5, m, mPillar);
        addBlock(0, 4, -5, m, mPillar);
        addBlock(0, 5, -5, m, mPillar);
        addBlock(0, 6, -5, m, mChisel);
        addBlock(0, 7, -5, m, mPillar);
        addBlock(0, 8, -5, m, mChisel);

        addBlock(5, 2, 5, m, mRuned);
        addBlock(5, 3, 5, m, mPillar);
        addBlock(5, 4, 5, m, mPillar);
        addBlock(5, 5, 5, m, mPillar);
        addBlock(5, 6, 5, Blocks.glowstone);
        addBlock(-5, 2, 5, m, mRuned);
        addBlock(-5, 3, 5, m, mPillar);
        addBlock(-5, 4, 5, m, mPillar);
        addBlock(-5, 5, 5, m, mPillar);
        addBlock(-5, 6, 5, Blocks.glowstone);
        addBlock(5, 2, -5, m, mRuned);
        addBlock(5, 3, -5, m, mPillar);
        addBlock(5, 4, -5, m, mPillar);
        addBlock(5, 5, -5, m, mPillar);
        addBlock(5, 6, -5, Blocks.glowstone);
        addBlock(-5, 2, -5, m, mRuned);
        addBlock(-5, 3, -5, m, mPillar);
        addBlock(-5, 4, -5, m, mPillar);
        addBlock(-5, 5, -5, m, mPillar);
        addBlock(-5, 6, -5, Blocks.glowstone);

        addBlock(5, 6, 4, m, mArch);
        addBlock(5, 6, 3, m, mArch);
        addBlock(5, 6, 2, m, mArch);
        addBlock(5, 6, 1, m, mArch);
        addBlock(5, 6, -1, m, mArch);
        addBlock(5, 6, -2, m, mArch);
        addBlock(5, 6, -3, m, mArch);
        addBlock(5, 6, -4, m, mArch);
        addBlock(-5, 6, 4, m, mArch);
        addBlock(-5, 6, 3, m, mArch);
        addBlock(-5, 6, 2, m, mArch);
        addBlock(-5, 6, 1, m, mArch);
        addBlock(-5, 6, -1, m, mArch);
        addBlock(-5, 6, -2, m, mArch);
        addBlock(-5, 6, -3, m, mArch);
        addBlock(-5, 6, -4, m, mArch);
        addBlock(4, 6, 5, m, mArch);
        addBlock(3, 6, 5, m, mArch);
        addBlock(2, 6, 5, m, mArch);
        addBlock(1, 6, 5, m, mArch);
        addBlock(-1, 6, 5, m, mArch);
        addBlock(-2, 6, 5, m, mArch);
        addBlock(-3, 6, 5, m, mArch);
        addBlock(-4, 6, 5, m, mArch);
        addBlock(4, 6, -5, m, mArch);
        addBlock(3, 6, -5, m, mArch);
        addBlock(2, 6, -5, m, mArch);
        addBlock(1, 6, -5, m, mArch);
        addBlock(-1, 6, -5, m, mArch);
        addBlock(-2, 6, -5, m, mArch);
        addBlock(-3, 6, -5, m, mArch);
        addBlock(-4, 6, -5, m, mArch);

        addBlock(4, 1, 4, m, mRaw);
        addBlock(3, 1, 4, m, mRaw);
        addBlock(4, 1, 3, m, mRaw);
        addBlock(-4, 1, 4, m, mRaw);
        addBlock(-3, 1, 4, m, mRaw);
        addBlock(-4, 1, 3, m, mRaw);
        addBlock(4, 1, -4, m, mRaw);
        addBlock(3, 1, -4, m, mRaw);
        addBlock(4, 1, -3, m, mRaw);
        addBlock(-4, 1, -4, m, mRaw);
        addBlock(-3, 1, -4, m, mRaw);
        addBlock(-4, 1, -3, m, mRaw);

        addBlock(4, 6, 4, m, mBrick);
        addBlock(4, 7, 4, m, mBrick);
        addBlock(3, 7, 3, m, mBrick);
        addBlock(3, 8, 3, m, mBrick);
        addBlock(-4, 6, 4, m, mBrick);
        addBlock(-4, 7, 4, m, mBrick);
        addBlock(-3, 7, 3, m, mBrick);
        addBlock(-3, 8, 3, m, mBrick);
        addBlock(4, 6, -4, m, mBrick);
        addBlock(4, 7, -4, m, mBrick);
        addBlock(3, 7, -3, m, mBrick);
        addBlock(3, 8, -3, m, mBrick);
        addBlock(-4, 6, -4, m, mBrick);
        addBlock(-4, 7, -4, m, mBrick);
        addBlock(-3, 7, -3, m, mBrick);
        addBlock(-3, 8, -3, m, mBrick);

        addBlock(2, 8, 3, m, mBrick);
        addBlock(2, 9, 3, m, mBrick);
        addBlock(3, 8, 2, m, mBrick);
        addBlock(3, 9, 2, m, mBrick);
        addBlock(2, 9, 2, m, mRuned);
        addBlock(-2, 8, 3, m, mBrick);
        addBlock(-2, 9, 3, m, mBrick);
        addBlock(-3, 8, 2, m, mBrick);
        addBlock(-3, 9, 2, m, mBrick);
        addBlock(-2, 9, 2, m, mRuned);
        addBlock(2, 8, -3, m, mBrick);
        addBlock(2, 9, -3, m, mBrick);
        addBlock(3, 8, -2, m, mBrick);
        addBlock(3, 9, -2, m, mBrick);
        addBlock(2, 9, -2, m, mRuned);
        addBlock(-2, 8, -3, m, mBrick);
        addBlock(-2, 9, -3, m, mBrick);
        addBlock(-3, 8, -2, m, mBrick);
        addBlock(-3, 9, -2, m, mBrick);
        addBlock(-2, 9, -2, m, mRuned);

        addBlock(1, 9, 3, m, mBrick);
        addBlock(0, 9, 3, m, mBrick);
        addBlock(-1, 9, 3, m, mBrick);
        addBlock(1, 9, -3, m, mBrick);
        addBlock(0, 9, -3, m, mBrick);
        addBlock(-1, 9, -3, m, mBrick);
        addBlock(3, 9, 1, m, mBrick);
        addBlock(3, 9, 0, m, mBrick);
        addBlock(3, 9, -1, m, mBrick);
        addBlock(-3, 9, 1, m, mBrick);
        addBlock(-3, 9, 0, m, mBrick);
        addBlock(-3, 9, -1, m, mBrick);

        addAir(2, 10, 2);
        addAir(-2, 10, 2);
        addAir(2, 10, -2);
        addAir(-2, 10, -2);

        addBlock(0, 1, 0, m, mPillar);
        addBlock(0, 2, 0, m, mPillar);
        addBlock(0, 3, 0, m, mChisel);
        addBlock(0, 4, 0, Blocks.water);

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

        addBlock(4, -5, -4, Blocks.chest);
        addTileCallback(new BlockPos(4, -5, -4), lootCallback);

        addBlock(-4, -5, 4, Blocks.chest);
        addTileCallback(new BlockPos(-4, -5, 4), lootCallback);

        addBlock(0, -3, 0, BlocksAS.collectorCrystal);
        addTileCallback(new BlockPos(0, -3, 0), new TileEntityCallback() {

            @Override
            public boolean isApplicable(TileEntity te) {
                return te instanceof TileCollectorCrystal;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                if (te instanceof TileCollectorCrystal) {
                    ((TileCollectorCrystal) te).onPlace(
                        MiscUtils.getRandomEntry(ConstellationRegistry.getMajorConstellations(), STATIC_RAND),
                        null,
                        CrystalProperties.getMaxRockProperties(),
                        null,
                        BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL);
                }
            }
        });
    }

}
