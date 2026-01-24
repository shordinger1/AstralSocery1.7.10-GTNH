/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.change;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureIntegrityObserver
 * Created by HellFirePvP
 * Date: 02.12.2018 / 11:45
 */
public class StructureIntegrityObserver {

    public static final StructureIntegrityObserver INSTANCE = new StructureIntegrityObserver();

    private StructureIntegrityObserver() {}

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        World world = event.world;
        if (world.isRemote || !event.getChunk()
            .isTerrainPopulated()) {
            return;
        }

        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChunkPos ch = event.getChunk()
            .getPos();
        BlockPos pos = event.getPos();
        Block oldS = event.getOldState();
        Block newS = event.getNewState();

        List<ChangeSubscriber<?>> subscribers = buf.getSubscribers(ch);
        for (ChangeSubscriber<?> subscriber : subscribers) {
            if (subscriber.observes(pos)) {
                LogCategory.STRUCTURE_MATCH.info(() -> "Adding change at " + pos + " for " + subscriber.getRequester());
                subscriber.addChange(pos, oldS, newS);
                buf.markDirty();
            }
        }

        if (oldS instanceof BlockStructureObserver) {
            LogCategory.STRUCTURE_MATCH.info(() -> "Testing removal for subscriber at " + pos);
            if (((BlockStructureObserver) oldS).removeWithNewState(world, pos, oldS, newS)) {
                LogCategory.STRUCTURE_MATCH.info(() -> "Removing subscriber at " + pos);
                buf.removeSubscriber(pos);
            }
        }
    }

}
