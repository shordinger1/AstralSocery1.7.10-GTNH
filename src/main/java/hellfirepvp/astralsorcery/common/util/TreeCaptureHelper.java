/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeCaptureHelper
 * Created by HellFirePvP
 * Date: 11.11.2016 / 19:58
 */
public class TreeCaptureHelper {

    public static TreeCaptureHelper eventInstance = new TreeCaptureHelper();

    public static List<WorldBlockPos> oneTimeCatches = Lists.newLinkedList();

    private static List<WeakReference<TreeWatcher>> watchers = Lists.newLinkedList();
    private static Map<WeakReference<TreeWatcher>, List<WorldBlockPos>> cachedEntries = Maps.newHashMap();

    private TreeCaptureHelper() {}

    @SubscribeEvent
    public void onTreeGrowth(SaplingGrowTreeEvent event) {
        // 1.7.10: SaplingGrowTreeEvent has x, y, z fields instead of getPos()
        BlockPos pos = new BlockPos(event.x, event.y, event.z);
        LogCategory.TREE_BEACON
            .info(() -> "Captured tree growth at " + pos + " in dim " + event.world.provider.dimensionId);
        WorldBlockPos worldPos = new WorldBlockPos(event.world, pos);
        if (oneTimeCatches.contains(worldPos)) {
            LogCategory.TREE_BEACON.info(() -> "Expected growth at " + worldPos + " - skipping!");
            oneTimeCatches.remove(worldPos);
            return;
        }

        // 1.7.10: List uses isEmpty(), not stackSize
        if (watchers == null || watchers.isEmpty()) return;
        Iterator<WeakReference<TreeWatcher>> iterator = watchers.iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> watch = iterator.next();
            TreeWatcher watcher = watch.get();
            if (watcher == null) {
                LogCategory.TREE_BEACON.info(() -> "A TreeWatcher timed out (no additional information)");
                iterator.remove();
                continue;
            }
            if (watcher.watches(worldPos)) {
                LogCategory.TREE_BEACON.info(
                    () -> "TreeWatcher at " + watcher.center
                        + " watches "
                        + worldPos
                        + " - with squared radius: "
                        + watcher.watchRadiusSq
                        + " (real: "
                        + Math.sqrt(watcher.watchRadiusSq)
                        + ")");
                addWatch(watch, worldPos);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    public static void offerWeakWatcher(TreeWatcher watcher) {
        Iterator<WeakReference<TreeWatcher>> iterator = watchers.iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> w = iterator.next();
            TreeWatcher other = w.get();
            if (other == null) {
                iterator.remove();
                continue;
            }
            if (other.equals(watcher)) return;
        }
        LogCategory.TREE_BEACON.info(() -> "New watcher offered and added at " + watcher.center);
        watchers.add(new WeakReference<>(watcher));
    }

    @Nonnull
    public static List<WorldBlockPos> getAndClearCachedEntries(@Nullable TreeWatcher watcher) {
        if (watcher == null) return Lists.newArrayList();
        // 1.7.10: List uses isEmpty(), not stackSize
        if (watchers == null || watchers.isEmpty()) return Lists.newArrayList();
        Iterator<WeakReference<TreeWatcher>> iterator = watchers.iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> itW = iterator.next();
            TreeWatcher watch = itW.get();
            if (watch == null) {
                LogCategory.TREE_BEACON.info(() -> "A TreeWatcher timed out (no additional information)");
                iterator.remove();
                continue;
            }
            if (watcher.equals(watch)) {
                List<WorldBlockPos> pos = cachedEntries.get(itW);
                cachedEntries.remove(itW);
                LogCategory.TREE_BEACON.info(
                    () -> "Fetched " + (pos == null ? 0 : pos.size())
                        + " cached, captured positions for watcher at "
                        + watcher.center);
                return pos == null ? Lists.newArrayList() : pos;
            }
        }
        return Lists.newArrayList();
    }

    private void addWatch(WeakReference<TreeWatcher> watch, WorldBlockPos pos) {
        if (!cachedEntries.containsKey(watch)) {
            cachedEntries.put(watch, Lists.newLinkedList());
        }
        List<WorldBlockPos> entries = cachedEntries.get(watch);
        entries.add(pos);

        LogCategory.TREE_BEACON.info(() -> "Captured " + pos + " - TreeWatcher in total watches " + entries.size());

        Iterator<WeakReference<TreeWatcher>> iterator = cachedEntries.keySet()
            .iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> wrT = iterator.next();
            if (wrT.get() == null) {
                LogCategory.TREE_BEACON.info(() -> "An empty TreeWatcher was removed from the entry cache");
                iterator.remove();
            }
        }
    }

    public static class TreeWatcher {

        private final int dimId;
        private final BlockPos center;
        private final double watchRadiusSq;

        // 1.7.10: TileEntity has getWorldObj() method, worldObj is protected
        public TreeWatcher(TileEntity te, double watchRadius) {
            this(te.getWorldObj(), new BlockPos(te.xCoord, te.yCoord, te.zCoord), watchRadius);
        }

        public TreeWatcher(World world, BlockPos center, double watchRadius) {
            this(world.provider.dimensionId, center, watchRadius);
        }

        public TreeWatcher(int dimId, BlockPos center, double watchRadius) {
            this.dimId = dimId;
            this.center = center;
            this.watchRadiusSq = watchRadius * watchRadius;
        }

        public boolean watches(WorldBlockPos pos) {
            if (pos.getWorld().provider.dimensionId != dimId) return false;
            // 1.7.10: BlockPos doesn't have distanceSq(), calculate manually
            double dx = center.getX() - pos.getX();
            double dy = center.getY() - pos.getY();
            double dz = center.getZ() - pos.getZ();
            double distanceSq = dx * dx + dy * dy + dz * dz;
            return distanceSq <= watchRadiusSq;
        }
    }

}
