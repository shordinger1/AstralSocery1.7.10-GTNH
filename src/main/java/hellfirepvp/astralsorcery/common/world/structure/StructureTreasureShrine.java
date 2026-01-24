/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import java.util.Collection;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureTreasureShrine
 * Created by HellFirePvP
 * Date: 22.07.2017 / 17:21
 */
public class StructureTreasureShrine extends WorldGenAttributeStructure {

    public StructureTreasureShrine() {
        super(
            2,
            20,
            "treasureShrine",
            () -> MultiBlockArrays.treasureShrine,
            StructureGenBuffer.StructureType.TREASURE,
            true);
        this.cfgEntry.setMinY(10);
        this.cfgEntry.setMaxY(40);
        this.idealDistance = 192;
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        CaveAdjacencyInformation information = validatePosition(pos, world);
        if (information != null) { // Which i'd expect
            generateAsSubmergedStructure(world, pos);
            BlockPos offsetPos = pos.add(0, 3, 0)
                .offset(information.direction, 4);
            world.setBlockToAir(offsetPos);
            world.setBlockToAir(offsetPos.up());
            Block mru = BlocksAS.blockMarble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
            Block mrw = BlocksAS.blockMarble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
            for (int i = 0; i < information.tunnelDistance; i++) {
                offsetPos = offsetPos.offset(information.direction);

                world.setBlockToAir(offsetPos);
                world.setBlockToAir(offsetPos.up());

                world.setBlock(
                    offsetPos.down()
                        .getX(),
                    offsetPos.down()
                        .getY(),
                    offsetPos.down()
                        .getZ(),
                    mrw,
                    0,
                    3);
                world.setBlock(
                    offsetPos.up(2)
                        .getX(),
                    offsetPos.up(2)
                        .getY(),
                    offsetPos.up(2)
                        .getZ(),
                    mrw,
                    0,
                    3);

                world.setBlock(
                    offsetPos.up()
                        .offset(information.direction.rotateY())
                        .getX(),
                    offsetPos.up()
                        .offset(information.direction.rotateY())
                        .getY(),
                    offsetPos.up()
                        .offset(information.direction.rotateY())
                        .getZ(),
                    mrw,
                    0,
                    3);
                world.setBlock(
                    offsetPos.offset(information.direction.rotateY())
                        .getX(),
                    offsetPos.offset(information.direction.rotateY())
                        .getY(),
                    offsetPos.offset(information.direction.rotateY())
                        .getZ(),
                    mru,
                    0,
                    3);
                world.setBlock(
                    offsetPos.up()
                        .offset(information.direction.rotateYCCW())
                        .getX(),
                    offsetPos.up()
                        .offset(information.direction.rotateYCCW())
                        .getY(),
                    offsetPos.up()
                        .offset(information.direction.rotateYCCW())
                        .getZ(),
                    mrw,
                    0,
                    3);
                world.setBlock(
                    offsetPos.offset(information.direction.rotateYCCW())
                        .getX(),
                    offsetPos.offset(information.direction.rotateYCCW())
                        .getY(),
                    offsetPos.offset(information.direction.rotateYCCW())
                        .getZ(),
                    mru,
                    0,
                    3);
            }
            getBuffer(world).markStructureGeneration(pos, StructureGenBuffer.StructureType.TREASURE);
        }
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        if (!isApplicableWorld(world)) return false;
        if (!isApplicableBiome(world, pos)) return false;
        return true;
    }

    private boolean isApplicableWorld(World world) {
        if (cfgEntry.shouldIgnoreDimensionSpecifications()) return true;

        Integer dimId = world.provider.dimensionId;
        if (cfgEntry.getApplicableDimensions()
            .isEmpty()) return false;
        for (Integer dim : cfgEntry.getApplicableDimensions()) {
            if (dim.equals(dimId)) return true;
        }
        return false;
    }

    private boolean isApplicableBiome(World world, BlockPos pos) {
        if (cfgEntry.shouldIgnoreBiomeSpecifications()) return true;

        Biome b = world.getBiomeGenForCoords(pos.getX(), pos.getZ());
        Collection<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
        if (types.isEmpty()) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (cfgEntry.getTypes()
                .contains(t)) applicable = true;
        }
        return applicable;
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        BlockPos initial = new BlockPos(chX * 16 + 8, 0, chZ * 16 + 8);
        if (world instanceof WorldServer) {
            try {
                ChunkGeneratorSettings settings = ChunkGeneratorSettings.Factory.jsonToFactory(
                    world.getWorldInfo()
                        .getGeneratorOptions())
                    .build();
                if (settings.useStrongholds) {
                    BlockPos blockpos = ((WorldServer) world).getChunkProvider()
                        .getNearestStructurePos(world, "Stronghold", initial, false);
                    if (blockpos != null) {
                        double xDst = blockpos.getX() - initial.getX();
                        double zDst = blockpos.getZ() - initial.getZ();
                        float flatDst = WrapMathHelper.sqrt(xDst * xDst + zDst * zDst);
                        if (flatDst <= 20) {
                            return null;
                        }
                    }
                }
            } catch (Exception ignored) {} // Well, then we just don't care about generating into strongholds *shrugs*
        }
        for (int i = 0; i < 15; i++) {
            BlockPos pos = initial.add(
                rand.nextInt(16),
                this.cfgEntry.getMinY() + rand.nextInt(this.cfgEntry.getMaxY() - this.cfgEntry.getMinY()),
                rand.nextInt(16));
            CaveAdjacencyInformation information = validatePosition(pos, world);
            if (information != null) {
                return pos;
            }
        }
        return null;
    }

    @Nullable
    private CaveAdjacencyInformation validatePosition(BlockPos pos, World world) {
        BlockPos.PooledMutableBlockPos move = BlockPos.PooledMutableBlockPos.retain();
        for (int xx = -4; xx <= 4; xx++) {
            for (int zz = -4; zz <= 4; zz++) {
                for (int yy = 0; yy <= 8; yy++) {
                    move.setPos(pos.getX() + xx, pos.getY() + yy, pos.getZ() + zz);
                    Block at = world.getBlock(move.getX(), move.getY(), move.getZ());
                    if (!at.isFullCube()) {
                        move.release();
                        return null;
                    }
                }
            }
        }
        move.release();
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos offsetPos = pos.add(0, 3, 0)
                .offset(face, 4);
            for (int n = 1; n < 4; n++) {
                BlockPos testAt = offsetPos.offset(face, n);
                if (getWorld().isAirBlock(testAt) && getWorld().isAirBlock(testAt.up())) {
                    return new CaveAdjacencyInformation(face, n);
                }
            }
        }
        return null;
    }

    private static class CaveAdjacencyInformation {

        private final EnumFacing direction;
        private final int tunnelDistance;

        private CaveAdjacencyInformation(EnumFacing direction, int tunnelDistance) {
            this.direction = direction;
            this.tunnelDistance = tunnelDistance;
        }

    }

}
