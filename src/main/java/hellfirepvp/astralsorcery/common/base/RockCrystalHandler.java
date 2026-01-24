/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.*;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RockCrystalHandler
 * Created by HellFirePvP
 * Date: 05.04.2019 / 19:12
 */
public class RockCrystalHandler {

    private static final String DATA_KEY = AstralSorcery.MODID + "_RockCrystalData";
    public static RockCrystalHandler INSTANCE = new RockCrystalHandler();

    // Runtime cache of RockCrystalPositions per chunk
    private final Map<World, Map<ChunkPos, RockCrystalPositions>> cache = new WeakHashMap<>();

    private RockCrystalHandler() {}

    public List<BlockPos> collectPositions(World world, ChunkPos center, int chunkRadius) {
        List<BlockPos> out = new LinkedList<>();
        for (int xx = -chunkRadius; xx <= chunkRadius; xx++) {
            for (int zz = -chunkRadius; zz <= chunkRadius; zz++) {
                ChunkPos other = new ChunkPos(center.x + xx, center.z + zz);
                if (MiscUtils.isChunkLoaded(world, other)) {
                    RockCrystalPositions positions = getPositions(world, other);
                    if (positions != null) {
                        out.addAll(positions.crystalPositions);
                    }
                }
            }
        }
        return out;
    }

    public boolean addOre(World world, BlockPos pos, boolean force) {
        ChunkPos ch = new ChunkPos(pos);
        if (force || MiscUtils.isChunkLoaded(world, ch)) {
            return this.addOre(world.getChunkFromChunkCoords(ch.x, ch.z), pos);
        }
        return false;
    }

    public boolean addOre(Chunk chunk, BlockPos pos) {
        RockCrystalPositions positions = getPositions(chunk);
        if (positions != null) {
            if (positions.crystalPositions.add(pos)) {
                // 1.7.10: Chunk.markDirty() doesn't exist - WorldSavedData handles saving
                // chunk.markDirty();
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean removeOre(World world, BlockPos pos, boolean force) {
        ChunkPos ch = new ChunkPos(pos);
        if (force || MiscUtils.isChunkLoaded(world, ch)) {
            return this.removeOre(world.getChunkFromChunkCoords(ch.x, ch.z), pos);
        }
        return false;
    }

    public boolean removeOre(Chunk chunk, BlockPos pos) {
        RockCrystalPositions positions = getPositions(chunk);
        if (positions != null) {
            if (positions.crystalPositions.remove(pos)) {
                // 1.7.10: Chunk.markDirty() doesn't exist - WorldSavedData handles saving
                // chunk.markDirty();
                return true;
            }
            return false;
        }
        return false;
    }

    @Nullable
    private RockCrystalPositions getPositions(Chunk chunk) {
        return getPositions(chunk.worldObj, new ChunkPos(chunk.xPosition, chunk.zPosition));
    }

    @Nullable
    private RockCrystalPositions getPositions(World world, ChunkPos pos) {
        Map<ChunkPos, RockCrystalPositions> worldCache = cache.get(world);
        if (worldCache == null) {
            worldCache = new HashMap<>();
            cache.put(world, worldCache);
        }

        RockCrystalPositions positions = worldCache.get(pos);
        if (positions == null) {
            // Try to load from saved data
            RockCrystalSavedData savedData = (RockCrystalSavedData) world.perWorldStorage
                .loadData(RockCrystalSavedData.class, DATA_KEY);
            if (savedData != null) {
                positions = savedData.getPositions(pos);
            }

            if (positions == null) {
                positions = new RockCrystalPositions();
            }
            worldCache.put(pos, positions);
        }
        return positions;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote) return;
        // Clear cache for this world
        cache.remove(event.world);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) return;
        // Clear cache for this world
        cache.remove(event.world);
    }

    public static class RockCrystalPositions {

        private Set<BlockPos> crystalPositions = new HashSet<>();

        public NBTTagCompound serializeNBT() {
            NBTTagCompound cmp = new NBTTagCompound();

            NBTTagList posList = new NBTTagList();
            for (BlockPos exactPos : this.crystalPositions) {
                NBTTagCompound tag = new NBTTagCompound();
                NBTHelper.writeBlockPosToNBT(exactPos, tag);
                posList.appendTag(tag);
            }

            cmp.setTag("posList", posList);
            return cmp;
        }

        public void deserializeNBT(NBTTagCompound nbt) {
            this.crystalPositions.clear();

            NBTTagList entries = nbt.getTagList("posList", 10);
            for (int j = 0; j < entries.tagCount(); j++) {
                NBTTagCompound tag = entries.getCompoundTagAt(j);
                this.crystalPositions.add(NBTHelper.readBlockPosFromNBT(tag));
            }
        }
    }

    /**
     * Saved data class for persisting rock crystal data to disk.
     */
    public static class RockCrystalSavedData extends WorldSavedData {

        private final Map<ChunkPos, RockCrystalPositions> positions = new HashMap<>();

        public RockCrystalSavedData(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            positions.clear();
            NBTTagList list = nbt.getTagList("chunks", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int cx = tag.getInteger("x");
                int cz = tag.getInteger("z");
                ChunkPos pos = new ChunkPos(cx, cz);
                RockCrystalPositions crystalPos = new RockCrystalPositions();
                crystalPos.deserializeNBT(tag.getCompoundTag("data"));
                positions.put(pos, crystalPos);
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<ChunkPos, RockCrystalPositions> entry : positions.entrySet()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("x", entry.getKey().x);
                tag.setInteger("z", entry.getKey().z);
                tag.setTag(
                    "data",
                    entry.getValue()
                        .serializeNBT());
                list.appendTag(tag);
            }
            nbt.setTag("chunks", list);
        }

        @Nullable
        public RockCrystalPositions getPositions(ChunkPos pos) {
            return positions.get(pos);
        }

        public void setPositions(ChunkPos pos, RockCrystalPositions data) {
            positions.put(pos, data);
            markDirty();
        }

        public static RockCrystalSavedData get(World world) {
            RockCrystalSavedData data = (RockCrystalSavedData) world.perWorldStorage
                .loadData(RockCrystalSavedData.class, DATA_KEY);
            if (data == null) {
                data = new RockCrystalSavedData(DATA_KEY);
                world.perWorldStorage.setData(DATA_KEY, data);
            }
            return data;
        }
    }

}
