/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import hellfirepvp.astralsorcery.common.base.RockCrystalHandler;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LegacyDataMigration
 * Created by HellFirePvP
 * Date: 05.04.2019 / 19:05
 */
public class LegacyDataMigration {

    /**
     * Simple callback interface for 1.7.10 (Java 6/7 compatibility)
     * java.util.function.Consumer doesn't exist in Java 7
     */
    public interface StringCallback {
        void accept(String message);
    }

    public static void migrateRockCrystalData(StringCallback msgOut) {
        for (WorldServer world : DimensionManager.getWorlds()) {

            RockCrystalBuffer data = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
            Map<ChunkPos, List<BlockPos>> crystalData = data.getCrystalPositions();
            int totalChunkCount = crystalData.size();

            if (totalChunkCount > 0) {
                msgOut.accept("Migrating rock crystal data for dimension " + world.provider.dimensionId);
                msgOut.accept(totalChunkCount + " chunks of crystals found!");

                // 1.7.10: DimensionManager.keepDimensionLoaded doesn't exist
                // In 1.7.10, dimension loading works differently

                int chunkCount = 0;
                int migrated = 0;
                int failed = 0;
                Iterator<List<BlockPos>> iterator = crystalData.values()
                    .iterator();
                while (iterator.hasNext()) {
                    List<BlockPos> positionList = iterator.next();
                    chunkCount++;

                    int failedThisChunk = 0;
                    for (BlockPos position : positionList) {
                        if (RockCrystalHandler.INSTANCE.addOre(world, position, true)) {
                            migrated++;
                        } else {
                            failed++;
                            failedThisChunk++;
                        }
                    }
                    if (failedThisChunk == 0) {
                        iterator.remove();
                    }

                    if (chunkCount % 200 == 0) {
                        msgOut.accept("Migrated " + chunkCount + "/" + totalChunkCount + " chunks...");
                    }

                    // 1.7.10: queueUnloadAll and tick don't exist on IChunkProvider
                    // These methods were added in later versions
                }

                msgOut.accept(
                    "Migrated " + migrated + " entries successfully. " + failed + " entries failed to be transferred!");
            }

            data.markDirty();
        }
    }

}
