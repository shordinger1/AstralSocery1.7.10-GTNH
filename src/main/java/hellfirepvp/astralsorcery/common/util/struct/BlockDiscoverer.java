/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.struct;

import java.util.*;
import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDiscoverer
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:09
 */
public class BlockDiscoverer {

    public static BlockArray discoverBlocksWithSameStateAroundChain(World world, BlockPos origin, Block match,
        int length, @Nullable EnumFacing originalBreakDirection, BlockStateCheck.WorldSpecific addCheck) {
        BlockArray out = new BlockArray();

        BlockPos offset = new BlockPos(origin);
        lbl: while (length > 0) {
            List<EnumFacing> faces = new ArrayList<>();
            Collections.addAll(faces, EnumFacing.values());
            // 1.7.10: BlockArray uses isEmpty() not stackSize
            if (originalBreakDirection != null && (out == null || out.isEmpty())) {
                // 1.7.10: EnumFacing doesn't have opposite() method, use ordinal arithmetic
                faces.remove(originalBreakDirection);
                EnumFacing opposite = EnumFacing.values()[originalBreakDirection.ordinal() ^ 1];
                faces.remove(opposite);
            }
            Collections.shuffle(faces);
            for (EnumFacing face : faces) {
                // 1.7.10: BlockPos.offset() expects ForgeDirection, not EnumFacing
                BlockPos at = new BlockPos(
                    offset.getX() + face.getFrontOffsetX(),
                    offset.getY() + face.getFrontOffsetY(),
                    offset.getZ() + face.getFrontOffsetZ());
                if (out.getPattern()
                    .containsKey(at)) {
                    continue;
                }
                Block test = world.getBlock(at.posX, at.posY, at.posZ);
                // 1.7.10: isStateValid takes 4 parameters (world, pos, block, metadata)
                int metadata = world.getBlockMetadata(at.posX, at.posY, at.posZ);
                if (MiscUtils.matchStateExact(match, test) && addCheck.isStateValid(world, at, test, metadata)) {
                    // 1.7.10: addBlock takes 4 parameters (x, y, z, block)
                    out.addBlock(at.getX(), at.getY(), at.getZ(), test);
                    length--;
                    offset = at;
                    continue lbl;
                }
            }
            break;
        }

        return out;
    }

    public static BlockArray searchForBlocksAround(World world, BlockPos origin, int cubeSize, BlockStateCheck match) {
        return searchForBlocksAround(world, origin, cubeSize, BlockStateCheck.WorldSpecific.wrap(match));
    }

    public static BlockArray searchForBlocksAround(World world, BlockPos origin, int cubeSize,
        BlockStateCheck.WorldSpecific match) {
        BlockArray out = new BlockArray();

        // 1.7.10: PooledMutableBlockPos doesn't exist, use regular BlockPos
        BlockPos offset = new BlockPos(0, 0, 0);
        for (int xx = -cubeSize; xx <= cubeSize; xx++) {
            for (int zz = -cubeSize; zz <= cubeSize; zz++) {
                for (int yy = -cubeSize; yy <= cubeSize; yy++) {
                    // 1.7.10: Create new BlockPos manually
                    offset = new BlockPos(origin.getX() + xx, origin.getY() + yy, origin.getZ() + zz);
                    // 1.7.10: Use blockExists instead of isBlockLoaded, and pass World
                    if (world.blockExists(offset.getX(), offset.getY(), offset.getZ())) {
                        Block atState = world.getBlock(offset.getX(), offset.getY(), offset.getZ());
                        // 1.7.10: isStateValid takes 4 parameters (world, pos, block, metadata)
                        int meta = world.getBlockMetadata(offset.getX(), offset.getY(), offset.getZ());
                        if (match.isStateValid(world, offset, atState, meta)) {
                            // 1.7.10: addBlock takes individual coordinates, not BlockPos
                            out.addBlock(offset.getX(), offset.getY(), offset.getZ(), atState);
                        }
                    }
                }
            }
        }
        return out;
    }

    public static BlockArray discoverBlocksWithSameStateAroundLimited(Map<Block, Integer> stateLimits, World world,
        BlockPos origin, boolean onlyExposed, int cubeSize, int limit, boolean searchCorners) {
        Block testState = world.getBlock(origin.posX, origin.posY, origin.posZ);

        BlockArray foundResult = new BlockArray();
        // 1.7.10: addBlock takes individual coordinates, not BlockPos
        foundResult.addBlock(origin.getX(), origin.getY(), origin.getZ(), testState);
        List<BlockPos> visited = new LinkedList<>();

        Deque<BlockPos> searchNext = new LinkedList<>();
        searchNext.addFirst(origin);

        // 1.7.10: Collection uses isEmpty() not stackSize
        while (!(searchNext == null || searchNext.isEmpty())) {
            Deque<BlockPos> currentSearch = searchNext;
            searchNext = new LinkedList<>();

            for (BlockPos offsetPos : currentSearch) {
                if (searchCorners) {
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int yy = -1; yy <= 1; yy++) {
                            for (int zz = -1; zz <= 1; zz++) {
                                BlockPos search = offsetPos.add(xx, yy, zz);
                                if (visited.contains(search)) continue;
                                if (getCubeDistance(search, origin) > cubeSize) continue;
                                if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                                visited.add(search);

                                if (!onlyExposed || isExposedToAir(world, search)) {
                                    Block current = world.getBlock(search.getX(), search.getY(), search.getZ());
                                    if (MiscUtils.matchStateExact(current, testState)) {
                                        // 1.7.10: addBlock takes individual coordinates, not BlockPos
                                        foundResult.addBlock(search.getX(), search.getY(), search.getZ(), current);
                                        searchNext.add(search);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing face : EnumFacing.values()) {
                        // 1.7.10: offset() expects ForgeDirection, calculate manually
                        BlockPos search = new BlockPos(
                            offsetPos.getX() + face.getFrontOffsetX(),
                            offsetPos.getY() + face.getFrontOffsetY(),
                            offsetPos.getZ() + face.getFrontOffsetZ());
                        if (visited.contains(search)) continue;
                        if (getCubeDistance(search, origin) > cubeSize) continue;
                        if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                        visited.add(search);

                        if (!onlyExposed || isExposedToAir(world, search)) {
                            Block current = world.getBlock(search.getX(), search.getY(), search.getZ());
                            if (MiscUtils.matchStateExact(current, testState)) {
                                // 1.7.10: addBlock takes individual coordinates, not BlockPos
                                foundResult.addBlock(search.getX(), search.getY(), search.getZ(), current);
                                searchNext.add(search);
                            }
                        }
                    }
                }
            }
        }

        return foundResult;
    }

    public static BlockArray discoverBlocksWithSameStateAround(List<Block> states, World world, BlockPos origin,
        boolean onlyExposed, int cubeSize, int limit, boolean searchCorners) {
        BlockArray foundResult = new BlockArray();
        // 1.7.10: addBlock takes individual coordinates, not BlockPos
        foundResult.addBlock(
            origin.getX(),
            origin.getY(),
            origin.getZ(),
            world.getBlock(origin.posX, origin.posY, origin.posZ));
        List<BlockPos> visited = new LinkedList<>();

        Deque<BlockPos> searchNext = new LinkedList<>();
        searchNext.addFirst(origin);

        // 1.7.10: Collection uses isEmpty() not stackSize
        while (!(searchNext == null || searchNext.isEmpty())) {
            Deque<BlockPos> currentSearch = searchNext;
            searchNext = new LinkedList<>();

            for (BlockPos offsetPos : currentSearch) {
                if (searchCorners) {
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int yy = -1; yy <= 1; yy++) {
                            for (int zz = -1; zz <= 1; zz++) {
                                BlockPos search = offsetPos.add(xx, yy, zz);
                                if (visited.contains(search)) continue;
                                if (getCubeDistance(search, origin) > cubeSize) continue;
                                if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                                visited.add(search);

                                if (!onlyExposed || isExposedToAir(world, search)) {
                                    Block current = world.getBlock(search.getX(), search.getY(), search.getZ());
                                    if (MiscUtils.getMatchingState(states, current) != null) {
                                        // 1.7.10: addBlock takes individual coordinates, not BlockPos
                                        foundResult.addBlock(search.getX(), search.getY(), search.getZ(), current);
                                        searchNext.add(search);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing face : EnumFacing.values()) {
                        // 1.7.10: offset() expects ForgeDirection, calculate manually
                        BlockPos search = new BlockPos(
                            offsetPos.getX() + face.getFrontOffsetX(),
                            offsetPos.getY() + face.getFrontOffsetY(),
                            offsetPos.getZ() + face.getFrontOffsetZ());
                        if (visited.contains(search)) continue;
                        if (getCubeDistance(search, origin) > cubeSize) continue;
                        if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                        visited.add(search);

                        if (!onlyExposed || isExposedToAir(world, search)) {
                            Block current = world.getBlock(search.getX(), search.getY(), search.getZ());
                            if (MiscUtils.getMatchingState(states, current) != null) {
                                // 1.7.10: addBlock takes individual coordinates, not BlockPos
                                foundResult.addBlock(search.getX(), search.getY(), search.getZ(), current);
                                searchNext.add(search);
                            }
                        }
                    }
                }
            }
        }

        return foundResult;
    }

    public static BlockArray discoverBlocksWithSameStateAround(World world, BlockPos origin, boolean onlyExposed,
        int cubeSize, int limit, boolean searchCorners) {
        Block toMatch = world.getBlock(origin.posX, origin.posY, origin.posZ);
        return discoverBlocksWithSameStateAround(
            Lists.newArrayList(toMatch),
            world,
            origin,
            onlyExposed,
            cubeSize,
            limit,
            searchCorners);
    }

    public static int getCubeDistance(BlockPos p1, BlockPos p2) {
        // 1.7.10: absMax doesn't exist, calculate manually using Math.max
        int dx = Math.abs(p1.getX() - p2.getX());
        int dy = Math.abs(p1.getY() - p2.getY());
        int dz = Math.abs(p1.getZ() - p2.getZ());
        int maxD = Math.max(dx, dy);
        return Math.max(maxD, dz);
    }

    public static boolean isExposedToAir(World world, BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            // 1.7.10: offset() expects ForgeDirection, calculate manually
            BlockPos offset = new BlockPos(
                pos.getX() + face.getFrontOffsetX(),
                pos.getY() + face.getFrontOffsetY(),
                pos.getZ() + face.getFrontOffsetZ());
            // 1.7.10: Check isAirBlock on world, and simplify Block check
            if (world.isAirBlock(offset.getX(), offset.getY(), offset.getZ())
                || world.getBlock(offset.getX(), offset.getY(), offset.getZ())
                    .isReplaceable(world, offset.getX(), offset.getY(), offset.getZ()))
                return true;
        }
        return false;
    }

}
