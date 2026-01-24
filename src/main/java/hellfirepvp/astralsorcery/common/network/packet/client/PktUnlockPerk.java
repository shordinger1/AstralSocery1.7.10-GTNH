/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.ClientReplyPacket;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUnlockPerk
 * Created by HellFirePvP
 * Date: 12.12.2016 / 13:07
 */
public class PktUnlockPerk implements IMessage, IMessageHandler<PktUnlockPerk, PktUnlockPerk>, ClientReplyPacket {

    private AbstractPerk perk;

    private boolean serverAccept = false;

    public PktUnlockPerk() {}

    public PktUnlockPerk(boolean serverAccepted, AbstractPerk perk) {
        this.serverAccept = serverAccepted;
        this.perk = perk;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.serverAccept = buf.readBoolean();
        AbstractPerk perk = PerkTree.PERK_TREE.getPerk(ByteBufUtils.readResourceLocation(buf));
        if (perk != null) {
            this.perk = perk;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(serverAccept);
        ByteBufUtils.writeResourceLocation(buf, perk.getRegistryName());
    }

    @Override
    public PktUnlockPerk onMessage(PktUnlockPerk message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            // 1.7.10: Network packets are already handled on the main thread, no need for addScheduledTask
            EntityPlayerMP pl = ctx.getServerHandler().playerEntity;
            if (pl != null) {
                if (message.perk != null) {
                    AbstractPerk perk = message.perk;
                    PlayerProgress prog = ResearchManager.getProgress(pl, ctx.side);
                    if (!prog.hasPerkUnlocked(perk) && prog.isValid()) {
                        if (perk.mayUnlockPerk(prog, pl) && ResearchManager.applyPerk(pl, message.perk)) {
                            PacketChannel.CHANNEL.sendTo(new PktUnlockPerk(true, message.perk), pl);
                        }
                    }
                }
            }
        } else {
            recUnlockResultClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void recUnlockResultClient(PktUnlockPerk message) {
        if (message.serverAccept) {
            AbstractPerk perk = message.perk;
            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            if (current instanceof GuiJournalPerkTree) {
                // In 1.7.10, Minecraft doesn't have addScheduledTask, execute directly
                ((GuiJournalPerkTree) current).playUnlockAnimation(perk);
            }
        }
    }

}
