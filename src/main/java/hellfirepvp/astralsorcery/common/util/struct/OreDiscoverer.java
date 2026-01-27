/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.struct;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreDiscoverer
 * Created by HellFirePvP
 * Date: 12.03.2017 / 23:30
 */
public class OreDiscoverer {

    public static BlockArray startSearch(World world, Vector3 position, int xzLimit) {
        xzLimit = WrapMathHelper.clamp(xzLimit, 0, 32);
        BlockPos originPos = position.toBlockPos();
        BlockArray out = new BlockArray();
        List<Block> successfulOres = new ArrayList<>(12);
        try {
            for (int xx = -xzLimit; xx <= xzLimit; xx++) {
                for (int zz = -xzLimit; zz <= xzLimit; zz++) {
                    int chunkX = (originPos.getX() + xx) >> 4;
                    int chunkZ = (originPos.getZ() + zz) >> 4;
                    Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
                    // 1.7.10: getTopFilledSegment() doesn't exist, use getActualHeight() or fixed value
                    int highest = 256; // Maximum world height in 1.7.10
                    for (int y = 0; y < highest; y++) {
                        // 1.7.10: Use absolute coordinates
                        int absX = originPos.getX() + xx;
                        int absZ = originPos.getZ() + zz;
                        // 1.7.10: Chunk.getBlock takes x, y, z coordinates
                        Block at = c.getBlock(absX, y, absZ);
                        if (successfulOres.contains(at)) {
                            out.addBlock(new BlockPos(absX, y, absZ), at);
                        } else if (isOre(at)) {
                            out.addBlock(new BlockPos(absX, y, absZ), at);
                            successfulOres.add(at);
                        }
                    }
                }
            }
        } finally {
            // Empty finally block for resource cleanup (if needed in future)
        }
        return out;
    }

    private static boolean isOre(Block state) {
        if (state instanceof BlockOre) { // WELL that's easy enough.
            return true;
        }
        ItemStack blockStack = ItemUtils.createBlockStack(state);
        return !(blockStack == null || blockStack.stackSize <= 0) && ItemUtils.hasOreNamePart(blockStack, "ore");
    }

}
