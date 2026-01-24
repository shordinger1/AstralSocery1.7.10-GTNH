/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.tile.TESRTranslucentBlock;
import hellfirepvp.astralsorcery.common.auxiliary.StorageNetworkHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.tile.TileOreGenerator;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerIO
 * Created by HellFirePvP
 * Date: 01.08.2017 / 18:45
 */
public class EventHandlerIO {

    public static List<TileOreGenerator> generatorQueue = Lists.newLinkedList();

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        World w = event.world;
        ConstellationSkyHandler.getInstance()
            .informWorldUnload(w);
        StorageNetworkHandler.clearHandler(w);
        if (w.isRemote) {
            clientUnload();
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientUnload() {
        AstralSorcery.proxy.scheduleClientside(() -> TESRTranslucentBlock.cleanUp());
    }

    @SubscribeEvent
    public void onSave(WorldEvent.Save event) {
        WorldCacheManager.getInstance()
            .doSave(event.world);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!event.world.isRemote) {
            Iterator<TileOreGenerator> iterator = generatorQueue.iterator();
            while (iterator.hasNext()) {
                TileOreGenerator gen = iterator.next();
                BlockPos at = gen.getPos();
                // 1.7.10: Chunk has xPosition and zPosition fields, not getPos()
                // 1.7.10: Chunk has chunkTileEntityMap field, not getTileEntityMap()
                ChunkPos cp = new ChunkPos(at);
                if (event.getChunk().xPosition == cp.x && event.getChunk().zPosition == cp.z) {
                    // 1.7.10: Need to convert BlockPos to ChunkPosition for the map key
                    net.minecraft.world.ChunkPosition chunkPos = new net.minecraft.world.ChunkPosition(
                        at.getX(),
                        at.getY(),
                        at.getZ());
                    event.getChunk().chunkTileEntityMap.put(chunkPos, gen);
                    iterator.remove();
                }
            }
        }
    }

}
