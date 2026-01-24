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
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

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
            if (originalBreakDirection != null && (out == null || out.stackSize <= 0)) {
                faces.remove(originalBreakDirection);
                faces.remove(originalBreakDirection.getOpposite());
            }
            Collections.shuffle(faces);
            for (EnumFacing face : faces) {
                BlockPos at = offset.offset(face);
                if (out.getPattern()
                    .containsKey(at)) {
                    continue;
                }
                Block test = world.getBlock(at);
                if (MiscUtils.matchStateExact(match, test) && addCheck.isStateValid(world, at, test)) {
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

        BlockPos.PooledMutableBlockPos offset = BlockPos.PooledMutableBlockPos.retain();
        for (int xx = -cubeSize; xx <= cubeSize; xx++) {
            for (int zz = -cubeSize; zz <= cubeSize; zz++) {
                for (int yy = -cubeSize; yy <= cubeSize; yy++) {
                    offset.setPos(origin.getX() + xx, origin.getY() + yy, origin.getZ() + zz);
                    if (getWorld().isBlockLoaded(offset)) {
                        Block atState = world.getBlock(offset);
                        if (match.isStateValid(world, offset, atState)) {
                            out.addBlock(new BlockPos(offset), atState);
                        }
                    }
                }
            }
        }
        offset.release();
        return out;
    }

    public static BlockArray discoverBlocksWithSameStateAroundLimited(Map<Block, Integer> stateLimits, World world,
        BlockPos origin, boolean onlyExposed, int cubeSize, int limit, boolean searchCorners) {
        Block testState = world.getBlock(origin);

        BlockArray foundResult = new BlockArray();
        foundResult.addBlock(origin, testState);
        List<BlockPos> visited = new LinkedList<>();

        Deque<BlockPos> searchNext = new LinkedList<>();
        searchNext.addFirst(origin);

        while (!(searchNext == null || searchNext.stackSize <= 0)) {
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
                                    Block current = world.getBlock(search);
                                    if (MiscUtils.matchStateExact(current, testState)) {
                                        foundResult.addBlock(search, current);
                                        searchNext.add(search);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing face : EnumFacing.values()) {
                        BlockPos search = offsetPos.offset(face);
                        if (visited.contains(search)) continue;
                        if (getCubeDistance(search, origin) > cubeSize) continue;
                        if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                        visited.add(search);

                        if (!onlyExposed || isExposedToAir(world, search)) {
                            Block current = world.getBlock(search);
                            if (MiscUtils.matchStateExact(current, testState)) {
                                foundResult.addBlock(search, current);
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
        foundResult.addBlock(origin, world.getBlock(origin));
        List<BlockPos> visited = new LinkedList<>();

        Deque<BlockPos> searchNext = new LinkedList<>();
        searchNext.addFirst(origin);

        while (!(searchNext == null || searchNext.stackSize <= 0)) {
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
                                    Block current = world.getBlock(search);
                                    if (MiscUtils.getMatchingState(states, current) != null) {
                                        foundResult.addBlock(search, current);
                                        searchNext.add(search);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing face : EnumFacing.values()) {
                        BlockPos search = offsetPos.offset(face);
                        if (visited.contains(search)) continue;
                        if (getCubeDistance(search, origin) > cubeSize) continue;
                        if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                        visited.add(search);

                        if (!onlyExposed || isExposedToAir(world, search)) {
                            Block current = world.getBlock(search);
                            if (MiscUtils.getMatchingState(states, current) != null) {
                                foundResult.addBlock(search, current);
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
        Block toMatch = world.getBlock(origin);
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
        return (int) WrapMathHelper
            .absMax(WrapMathHelper.absMax(p1.getX() - p2.getX(), p1.getY() - p2.getY()), p1.getZ() - p2.getZ());
    }

    public static boolean isExposedToAir(World world, BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos offset = pos.offset(face);
            if (getWorld().isAirBlock(offset) || world.getBlock(offset)
                .getBlock()
                .isReplaceable(world, offset)) return true;
        }
        return false;
    }

}
