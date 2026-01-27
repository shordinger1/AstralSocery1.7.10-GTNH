/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBreakAssist
 * Created by HellFirePvP
 * Date: 26.07.2017 / 16:19
 */
public class BlockBreakAssist {

    private static final Map<Integer, TickTokenizedMap<BlockPos, BreakEntry>> breakMap = new HashMap<>();

    public static BreakEntry addProgress(World world, BlockPos pos, float expectedHardness, float percStrength) {
        TickTokenizedMap<BlockPos, BreakEntry> map = breakMap.get(world.provider.dimensionId);
        if (map == null) {
            map = new TickTokenizedMap<>(TickEvent.Type.SERVER);
            TickManager.getInstance()
                .register(map);
            breakMap.put(world.provider.dimensionId, map);
        }

        BreakEntry breakProgress = map.get(pos);
        if (breakProgress == null) {
            // 1.7.10: Get block and metadata separately
            Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            int metadata = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            breakProgress = new BreakEntry(expectedHardness, world, pos, block, metadata);
            map.put(pos, breakProgress);
        }

        breakProgress.breakProgress -= percStrength;
        breakProgress.idleTimeout = 0;
        return breakProgress;
    }

    @SideOnly(Side.CLIENT)
    public static void blockBreakAnimation(PktPlayEffect pktPlayEffect) {
        // 1.7.10: Can't use Block.getStateById(), particles don't need full block state
        // The data field contains block ID and metadata encoded
        int blockId = pktPlayEffect.data >> 4; // Upper bits = block ID
        Block block = Block.getBlockById(blockId);
        if (block != null) {
            // 1.7.10: playBlockBreakParticles only takes BlockPos and Block
            RenderingUtils.playBlockBreakParticles(pktPlayEffect.pos, block);
        }
    }

    public static class BreakEntry
        implements TickTokenizedMap.TickMapToken<Float>, CEffectPositionListGen.CEffectGenListEntry {

        private float breakProgress;
        private final World world;
        private BlockPos pos;
        private Block expectedBlock;
        private int expectedMetadata; // 1.7.10: Store metadata instead of state

        private int idleTimeout;

        public BreakEntry(World world) {
            this.world = world;
        }

        public BreakEntry(@Nonnull Float value, World world, BlockPos at, Block expectedBlock, int metadata) {
            // 1.7.10: Store block and metadata separately
            this.breakProgress = value;
            this.world = world;
            this.pos = at;
            this.expectedBlock = expectedBlock;
            this.expectedMetadata = metadata;
        }

        @Override
        public int getRemainingTimeout() {
            return (breakProgress <= 0 || idleTimeout >= 20) ? 0 : 1;
        }

        @Override
        public void tick() {
            idleTimeout++;
        }

        @Override
        public void onTimeout() {
            if (breakProgress > 0) return;

            // 1.7.10: Check block and metadata
            Block nowBlock = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            int nowMetadata = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            if (nowBlock == expectedBlock && nowMetadata == expectedMetadata) {
                MiscUtils.breakBlockWithoutPlayer((WorldServer) world, pos, nowBlock, true, true, true);
            }
        }

        @Override
        public Float getValue() {
            return breakProgress;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            this.breakProgress = nbt.getFloat("breakProgress");
            this.pos = NBTHelper.readBlockPosFromNBT(nbt);
            // 1.7.10: Read block ID and metadata separately
            int blockId = nbt.getInteger("expectedBlockId");
            this.expectedBlock = Block.getBlockById(blockId);
            if (this.expectedBlock == null) {
                this.expectedBlock = Blocks.air;
            }
            this.expectedMetadata = nbt.getInteger("expectedMetadata");
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setFloat("breakProgress", this.breakProgress);
            NBTHelper.writeBlockPosToNBT(this.pos, nbt);
            // 1.7.10: Write block ID and metadata separately
            nbt.setInteger("expectedBlockId", Block.getIdFromBlock(this.expectedBlock));
            nbt.setInteger("expectedMetadata", this.expectedMetadata);
        }

    }
}
