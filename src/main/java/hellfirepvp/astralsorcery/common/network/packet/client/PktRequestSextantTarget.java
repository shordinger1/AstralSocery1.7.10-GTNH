/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.UISextantCache;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.ClientReplyPacket;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestSextantTarget
 * Created by HellFirePvP
 * Date: 08.06.2018 / 16:48
 */
public class PktRequestSextantTarget
    implements IMessageHandler<PktRequestSextantTarget, IMessage>, IMessage, ClientReplyPacket {

    private String regNameExpected = null;

    private BlockPos resultPos = null;
    private Integer resultDim = null;

    public PktRequestSextantTarget() {}

    public PktRequestSextantTarget(SextantFinder.TargetObject object) {
        this.regNameExpected = object.getRegistryName();
    }

    public PktRequestSextantTarget(SextantFinder.TargetObject to, @Nullable BlockPos result, Integer dimension) {
        this.regNameExpected = to.getRegistryName();
        this.resultPos = result;
        this.resultDim = dimension;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.regNameExpected = ByteBufUtils.readString(buf);
        this.resultPos = ByteBufUtils.readOptional(buf, new Function<ByteBuf, BlockPos>() {

            @Override
            public BlockPos apply(ByteBuf buf) {
                return ByteBufUtils.readPos(buf);
            }
        });
        this.resultDim = ByteBufUtils.readOptional(buf, new Function<ByteBuf, Integer>() {

            @Override
            public Integer apply(ByteBuf buf) {
                return buf.readInt();
            }
        });
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.regNameExpected);
        ByteBufUtils.writeOptional(buf, resultPos, new BiConsumer<ByteBuf, BlockPos>() {

            @Override
            public void accept(ByteBuf buf1, BlockPos pos) {
                ByteBufUtils.writePos(buf1, pos);
            }
        });
        ByteBufUtils.writeOptional(buf, resultDim, new BiConsumer<ByteBuf, Integer>() {

            @Override
            public void accept(ByteBuf buf1, Integer integer) {
                buf1.writeInt(integer);
            }
        });
    }

    @Override
    public IMessage onMessage(PktRequestSextantTarget pkt, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            // 1.7.10: Network packets are already handled on the main thread, no need for addScheduledTask
            SextantFinder.TargetObject to = SextantFinder.getByName(pkt.regNameExpected);
            if (to == null) return null;
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (!MiscUtils.isPlayerFakeMP(player)) {
                ItemStack heldStack = MiscUtils.getMainOrOffHand(
                    player,
                    ItemsAS.sextant,
                    (st) -> to.isSelectable(st, ResearchManager.getProgress(player, Side.SERVER)));
                if (heldStack == null) {
                    return null;
                }

                ExecutorService exec = Executors.newSingleThreadExecutor();
                try {
                    exec.invokeAll(Collections.singletonList(new java.util.concurrent.Callable<Void>() {

                        @Override
                        public Void call() {
                            BlockPos result = to.searchFor((WorldServer) player.worldObj, new BlockPos(player));

                            PktRequestSextantTarget target = new PktRequestSextantTarget(
                                to,
                                result,
                                player.worldObj.provider.dimensionId);
                            PacketChannel.CHANNEL.sendTo(target, player);
                            return null;
                        }
                    }), 5, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                    // No-Op, drop the task if it fails.
                } finally {
                    exec.shutdown();
                }
            }
        } else {
            handlePacketClient(pkt);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handlePacketClient(PktRequestSextantTarget pkt) {
        Minecraft.getMinecraft()
            .addScheduledTask(new Runnable() {

                @Override
                public void run() {
                    if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
                        return;
                    }
                    SextantFinder.TargetObject to = SextantFinder.getByName(pkt.regNameExpected);
                    if (to == null) return;
                    UISextantCache.addTarget(to, pkt.resultPos, pkt.resultDim);
                }
            });
    }

}
