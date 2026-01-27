/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraftforge.common.BiomeDictionary;

import com.google.common.collect.Lists;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureFinder
 * Created by HellFirePvP
 * Date: 25.02.2018 / 15:29
 */
public class StructureFinder {

    public static final String STRUCT_VILLAGE = "Village";
    public static final String STRUCT_STRONGHOLD = "Stronghold";
    public static final String STRUCT_MASNION = "Mansion";
    public static final String STRUCT_MONUMENT = "Monument";
    public static final String STRUCT_MINESHAFT = "Mineshaft";
    public static final String STRUCT_TEMPLE = "Temple";
    public static final String STRUCT_ENDCITY = "EndCity";
    public static final String STRUCT_FORTRESS = "Fortress";

    private StructureFinder() {}

    @Nullable
    public static BlockPos tryFindClosestAstralSorceryStructure(WorldServer world, BlockPos playerPos,
        StructureGenBuffer.StructureType searchKey) {
        StructureGenBuffer buffer = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_GEN);
        return buffer.getClosest(searchKey, playerPos);
    }

    @Nullable
    public static BlockPos tryFindClosestVanillaStructure(WorldServer world, BlockPos playerPos, String searchKey) {
        // 1.7.10: Structure finding API is very different and not easily accessible
        // MapGenStructure doesn't have a simple getNearestStructurePos method like in 1.12+
        // This functionality is disabled for 1.7.10
        return null;
    }

    @Nullable
    public static BlockPos tryFindClosestBiomeType(WorldServer world, BlockPos playerPos,
        BiomeDictionary.Type biomeType) {
        BiomeGenBase[] fitting = BiomeDictionary.getBiomesForType(biomeType);
        if ((fitting == null || fitting.length == 0)) {
            return null;
        }
        List<BiomeGenBase> fittingList = Lists.newArrayList(fitting);
        WorldChunkManager gen = world.getWorldChunkManager();
        for (int reach = 64; reach < 2112; reach += 128) {
            ChunkPosition closest = gen
                .findBiomePosition(playerPos.getX(), playerPos.getZ(), reach, fittingList, new Random(world.getSeed()));
            if (closest != null) {
                return new BlockPos(closest.chunkPosX, 0, closest.chunkPosZ);
            }
        }
        return null;
    }

}
