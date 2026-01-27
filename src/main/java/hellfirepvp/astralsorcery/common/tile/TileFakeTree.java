/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import hellfirepvp.astralsorcery.common.item.tool.ItemChargedCrystalAxe;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktDualParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 20:34
 */
public class TileFakeTree extends TileEntityTick {

    private TickAction ta;
    private Block fakedState;

    private UUID playerEffectRef = null;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!getWorld().isRemote) {
            if (ticksExisted > 5 && ticksExisted % 4 == 0) {
                if (ta != null) {
                    ta.update(this);
                }
                if (fakedState == null || fakedState.equals(Blocks.air)) {
                    cleanUp();
                }
            }
        }
    }

    private void cleanUp() {
        // 1.7.10: use getWorld() instead of world
        if (fakedState != null) {
            getWorld().setBlock(this.xCoord, this.yCoord, this.zCoord, fakedState, 0, 3);
        } else {
            getWorld().setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    protected void onFirstTick() {}

    public void setupTile(BlockPos treeBeaconRef, Block fakedState) {
        this.ta = new TreeBeaconRef(treeBeaconRef);
        this.fakedState = fakedState;
        markForUpdate();
    }

    public void setupTile(EntityPlayer breakingPlayer, ItemStack usedAxe, Block fakedState) {
        this.ta = new PlayerHarvestRef(breakingPlayer, usedAxe);
        this.fakedState = fakedState;
        markForUpdate();
    }

    public void setPlayerEffectRef(UUID playerEffectRef) {
        this.playerEffectRef = playerEffectRef;
    }

    public UUID getPlayerEffectRef() {
        return playerEffectRef;
    }

    public Block getFakedState() {
        return fakedState;
    }

    @Nullable
    public BlockPos getReference() {
        return ta instanceof TreeBeaconRef ? ((TreeBeaconRef) ta).ref : null;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        int index = compound.getInteger("type");
        if (index == 0) {
            this.ta = new TreeBeaconRef(null);
            ta.read(compound);
        } else {
            this.ta = new ClearAction();
        }

        // 1.7.10: UUID helper methods don't exist, read manually from most/least significant bits
        if (compound.hasKey("playerEffectRefMost") && compound.hasKey("playerEffectRefLeast")) {
            this.playerEffectRef = new UUID(
                compound.getLong("playerEffectRefMost"),
                compound.getLong("playerEffectRefLeast"));
        } else {
            this.playerEffectRef = null;
        }

        if (compound.hasKey("Block") && compound.hasKey("Data")) {
            int data = compound.getInteger("Data");
            Block b = Block.getBlockFromName(compound.getString("Block"));
            if (b != null) {
                fakedState = b;
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (ta instanceof TreeBeaconRef) {
            compound.setInteger("type", 0);
        } else if (ta instanceof PlayerHarvestRef) {
            compound.setInteger("type", 1);
        }
        if (ta != null) {
            ta.write(compound);
        }
        if (fakedState != null) {
            // 1.7.10: Use Block.blockRegistry to get the registry name
            Object nameObj = net.minecraft.block.Block.blockRegistry.getNameForObject(fakedState);
            String name = nameObj != null ? nameObj.toString() : "";
            compound.setString("Block", name);
            compound.setInteger("Data", 0); // 1.7.10: Simplified, metadata is handled differently
        }

        // 1.7.10: UUID helper methods don't exist, write manually as most/least significant bits
        if (this.playerEffectRef != null) {
            compound.setLong("playerEffectRefMost", this.playerEffectRef.getMostSignificantBits());
            compound.setLong("playerEffectRefLeast", this.playerEffectRef.getLeastSignificantBits());
        }
    }

    @Override
    public void writeNetNBT(NBTTagCompound compound) {
        super.writeNetNBT(compound);

        compound.setInteger("type", 0);
    }

    private static interface TickAction {

        public void update(TileFakeTree tft);

        public void write(NBTTagCompound cmp);

        public void read(NBTTagCompound cmp);

    }

    private static class ClearAction implements TickAction {

        @Override
        public void update(TileFakeTree tft) {
            // 1.7.10: setBlockToAir takes x, y, z coordinates, not BlockPos
            tft.worldObj.setBlockToAir(
                tft.getPos()
                    .getX(),
                tft.getPos()
                    .getY(),
                tft.getPos()
                    .getZ());
        }

        @Override
        public void write(NBTTagCompound cmp) {}

        @Override
        public void read(NBTTagCompound cmp) {}

    }

    private static class PlayerHarvestRef implements TickAction {

        private EntityPlayer player;
        private ItemStack usedTool;

        private PlayerHarvestRef(EntityPlayer player, ItemStack usedAxe) {
            this.player = player;
            if (usedAxe != null && !(usedAxe == null || usedAxe.stackSize <= 0)) {
                this.usedTool = usedAxe.copy();
                // 1.7.10: EnchantmentHelper API differs - uses Map<Integer, Integer> not Map<Enchantment, Integer>
                java.util.Map<Integer, Integer> levels = EnchantmentHelper.getEnchantments(this.usedTool);
                int fortuneId = Enchantment.fortune.effectId;
                if (levels.containsKey(fortuneId)) {
                    levels.put(fortuneId, levels.get(fortuneId) + 2);
                } else {
                    levels.put(fortuneId, 2);
                }
                EnchantmentHelper.setEnchantments(levels, this.usedTool);
            } else {
                this.usedTool = null;
            }
        }

        @Override
        public void update(TileFakeTree tft) {
            if (tft.ticksExisted <= 10) return;
            if (player != null && player instanceof EntityPlayerMP
                && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)
                && tft.fakedState != null) {
                ArrayList<ItemStack> out = new ArrayList<>();
                harvestAndAppend(tft, out);
                Vector3 plPos = Vector3.atEntityCenter(player);
                for (ItemStack stack : out) {
                    // 1.7.10: addItemStackToInventory doesn't exist, use inventory manipulation
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        ItemUtils.dropItemNaturally(
                            player.worldObj,
                            plPos.getX() + rand.nextFloat() - rand.nextFloat(),
                            plPos.getY() + rand.nextFloat(),
                            plPos.getZ() + rand.nextFloat() - rand.nextFloat(),
                            stack);
                    }
                }
                PktDualParticleEvent ev = new PktDualParticleEvent(
                    PktDualParticleEvent.DualParticleEventType.CHARGE_HARVEST,
                    new Vector3(tft),
                    Vector3.atEntityCenter(player));
                if (usedTool != null && ((usedTool == null || usedTool.stackSize <= 0)
                    || !(usedTool.getItem() instanceof ItemChargedCrystalAxe))) {
                    ev.setAdditionalData(
                        Color.GRAY.brighter()
                            .getRGB());
                } else {
                    ev.setAdditionalData(Color.GREEN.getRGB());
                }
                // 1.7.10: use tft.worldObj instead of tft.world
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(tft.worldObj, tft.getPos(), 24));
            }
            // 1.7.10: setBlockToAir takes x, y, z coordinates, not BlockPos
            tft.worldObj.setBlockToAir(
                tft.getPos()
                    .getX(),
                tft.getPos()
                    .getY(),
                tft.getPos()
                    .getZ());
        }

        private void harvestAndAppend(TileFakeTree tft, ArrayList<ItemStack> out) {
            BlockDropCaptureAssist.startCapturing();
            try {
                // 1.7.10: harvestBlock signature is different - doesn't have IBlockState parameter
                tft.getFakedState()
                    .harvestBlock(
                        player.worldObj,
                        player,
                        tft.getPos()
                            .getX(),
                        tft.getPos()
                            .getY(),
                        tft.getPos()
                            .getZ(),
                        0);
            } finally {
                // 1.7.10: Explicitly use java.util.List to avoid conflict with java.awt.List
                java.util.List<ItemStack> captured = BlockDropCaptureAssist.getCapturedStacksAndStop();
                for (ItemStack stack : captured) {
                    if (stack != null && !(stack == null || stack.stackSize <= 0)) {
                        out.add(stack);
                    }
                }
            }
        }

        @Override
        public void write(NBTTagCompound cmp) {}

        @Override
        public void read(NBTTagCompound cmp) {}

    }

    private static class TreeBeaconRef implements TickAction {

        private BlockPos ref;

        private TreeBeaconRef(BlockPos ref) {
            this.ref = ref;
        }

        @Override
        public void update(TileFakeTree tft) {
            // 1.7.10: use tft.worldObj instead of tft.world
            if (MiscUtils.isChunkLoaded(tft.worldObj, new ChunkPos(ref))) {
                TileTreeBeacon beacon = MiscUtils.getTileAt(tft.worldObj, ref, TileTreeBeacon.class, true);
                if (beacon == null || beacon.isInvalid()) {
                    tft.cleanUp();
                }
            }
        }

        @Override
        public void write(NBTTagCompound cmp) {
            if (ref != null) {
                NBTHelper.writeBlockPosToNBT(ref, cmp);
            }
        }

        @Override
        public void read(NBTTagCompound cmp) {
            ref = NBTHelper.readBlockPosFromNBT(cmp);
        }
    }

}
