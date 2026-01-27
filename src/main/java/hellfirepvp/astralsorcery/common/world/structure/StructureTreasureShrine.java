/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.world.WorldGenAttributeCommon.StructureQuery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureTreasureShrine
 * Created by HellFirePvP
 * Date: 22.07.2017 / 17:21
 */
public class StructureTreasureShrine extends WorldGenAttributeStructure {

    public StructureTreasureShrine() {
        super(2, 20, "treasureShrine", new StructureQuery() {

            @Override
            public StructureBlockArray getStructure() {
                return MultiBlockArrays.treasureShrine;
            }
        }, StructureGenBuffer.StructureType.TREASURE, true);
        this.cfgEntry.setMinY(10);
        this.cfgEntry.setMaxY(40);
        this.idealDistance = 192;
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        CaveAdjacencyInformation information = validatePosition(pos, world);
        if (information != null) {
            generateAsSubmergedStructure(world, pos);
            // 1.7.10: Convert EnumFacing to ForgeDirection for offset()
            ForgeDirection dir = toForgeDirection(information.direction);
            BlockPos offsetPos = pos.add(0, 3, 0)
                .offset(dir, 4);
            world.setBlockToAir(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            world.setBlockToAir(
                offsetPos.getX(),
                offsetPos.up()
                    .getY(),
                offsetPos.getZ());

            // 1.7.10: Use metadata directly instead of withProperty()
            int metaRuned = BlockMarble.MarbleBlockType.RUNED.ordinal();
            int metaRaw = BlockMarble.MarbleBlockType.RAW.ordinal();
            for (int i = 0; i < information.tunnelDistance; i++) {
                offsetPos = offsetPos.offset(dir);

                world.setBlockToAir(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                world.setBlockToAir(
                    offsetPos.getX(),
                    offsetPos.up()
                        .getY(),
                    offsetPos.getZ());

                // Down
                world.setBlock(
                    offsetPos.down()
                        .getX(),
                    offsetPos.down()
                        .getY(),
                    offsetPos.down()
                        .getZ(),
                    BlocksAS.blockMarble,
                    metaRaw,
                    3);
                // Up 2
                world.setBlock(
                    offsetPos.up(2)
                        .getX(),
                    offsetPos.up(2)
                        .getY(),
                    offsetPos.up(2)
                        .getZ(),
                    BlocksAS.blockMarble,
                    metaRaw,
                    3);

                // Up + rotateY
                ForgeDirection rotateY = rotateY(dir);
                BlockPos upRotateY = offsetPos.up()
                    .offset(rotateY);
                world.setBlock(upRotateY.getX(), upRotateY.getY(), upRotateY.getZ(), BlocksAS.blockMarble, metaRaw, 3);

                // rotateY
                BlockPos rotateYPos = offsetPos.offset(rotateY);
                world.setBlock(
                    rotateYPos.getX(),
                    rotateYPos.getY(),
                    rotateYPos.getZ(),
                    BlocksAS.blockMarble,
                    metaRuned,
                    3);

                // Up + rotateYCCW
                ForgeDirection rotateYCCW = rotateYCCW(dir);
                BlockPos upRotateYCCW = offsetPos.up()
                    .offset(rotateYCCW);
                world.setBlock(
                    upRotateYCCW.getX(),
                    upRotateYCCW.getY(),
                    upRotateYCCW.getZ(),
                    BlocksAS.blockMarble,
                    metaRaw,
                    3);

                // rotateYCCW
                BlockPos rotateYCCWPos = offsetPos.offset(rotateYCCW);
                world.setBlock(
                    rotateYCCWPos.getX(),
                    rotateYCCWPos.getY(),
                    rotateYCCWPos.getZ(),
                    BlocksAS.blockMarble,
                    metaRuned,
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

        BiomeGenBase b = world.getBiomeGenForCoords(pos.getX(), pos.getZ());
        // 1.7.10: Use getTypesForBiome() which returns Type[], not Collection
        BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(b);
        if (types.length <= 0) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (cfgEntry.getTypes()
                .contains(t)) {
                applicable = true;
                break;
            }
        }
        return applicable;
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        BlockPos initial = new BlockPos(chX * 16 + 8, 0, chZ * 16 + 8);
        // 1.7.10: ChunkGeneratorSettings doesn't exist, skip stronghold check
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
        // 1.7.10: PooledMutableBlockPos doesn't exist, use manual coordinate tracking
        for (int xx = -4; xx <= 4; xx++) {
            for (int zz = -4; zz <= 4; zz++) {
                for (int yy = 0; yy <= 8; yy++) {
                    Block at = world.getBlock(pos.getX() + xx, pos.getY() + yy, pos.getZ() + zz);
                    // 1.7.10: Use isOpaqueCube() instead of isFullCube()
                    if (!at.isOpaqueCube()) {
                        return null;
                    }
                }
            }
        }
        // 1.7.10: EnumFacing.HORIZONTALS doesn't exist, iterate manually
        EnumFacing[] horizontals = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST,
            EnumFacing.WEST };
        for (EnumFacing face : horizontals) {
            ForgeDirection dir = toForgeDirection(face);
            BlockPos offsetPos = pos.add(0, 3, 0)
                .offset(dir, 4);
            for (int n = 1; n < 4; n++) {
                BlockPos testAt = offsetPos.offset(dir, n);
                // 1.7.10: Use world parameter instead of getWorld()
                if (world.isAirBlock(testAt.getX(), testAt.getY(), testAt.getZ()) && world.isAirBlock(
                    testAt.getX(),
                    testAt.up()
                        .getY(),
                    testAt.getZ())) {
                    return new CaveAdjacencyInformation(face, n);
                }
            }
        }
        return null;
    }

    // 1.7.10: Helper methods for direction rotation
    private ForgeDirection rotateY(ForgeDirection dir) {
        switch (dir) {
            case NORTH:
                return ForgeDirection.WEST;
            case EAST:
                return ForgeDirection.NORTH;
            case SOUTH:
                return ForgeDirection.EAST;
            case WEST:
                return ForgeDirection.SOUTH;
            default:
                return dir;
        }
    }

    private ForgeDirection rotateYCCW(ForgeDirection dir) {
        switch (dir) {
            case NORTH:
                return ForgeDirection.EAST;
            case EAST:
                return ForgeDirection.SOUTH;
            case SOUTH:
                return ForgeDirection.WEST;
            case WEST:
                return ForgeDirection.NORTH;
            default:
                return dir;
        }
    }

    private ForgeDirection toForgeDirection(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return ForgeDirection.NORTH;
            case SOUTH:
                return ForgeDirection.SOUTH;
            case EAST:
                return ForgeDirection.EAST;
            case WEST:
                return ForgeDirection.WEST;
            case UP:
                return ForgeDirection.UP;
            case DOWN:
                return ForgeDirection.DOWN;
            default:
                return ForgeDirection.UNKNOWN;
        }
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
