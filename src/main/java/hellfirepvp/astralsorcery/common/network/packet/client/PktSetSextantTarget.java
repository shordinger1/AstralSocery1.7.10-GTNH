/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSetSextantTarget
 * Created by HellFirePvP
 * Date: 31.05.2018 / 10:14
 */
public class PktSetSextantTarget implements IMessage, IMessageHandler<PktSetSextantTarget, IMessage> {

    private String target;
    // 1.7.10: Removed EnumHand hand field - doesn't exist in 1.7.10

    public PktSetSextantTarget() {}

    public PktSetSextantTarget(SextantFinder.TargetObject target) {
        this.target = target.getRegistryName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.target = ByteBufUtils.readString(buf);
        // 1.7.10: Removed hand reading - doesn't exist in 1.7.10
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.target);
        // 1.7.10: Removed hand writing - doesn't exist in 1.7.10
    }

    @Override
    public IMessage onMessage(PktSetSextantTarget message, MessageContext ctx) {
        // 1.7.10: Network packets are already handled on the main thread, no need for addScheduledTask
        SextantFinder.TargetObject target = SextantFinder.getByName(message.target);
        if (target == null) {
            return null;
        }
        ItemStack held = ctx.getServerHandler().playerEntity.getCurrentEquippedItem();
        if ((held == null || held.stackSize <= 0) || !(held.getItem() instanceof ItemSextant)) {
            return null;
        }
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                // May be null; In that case, tell that to the client as well so it won't ask the server any longer.
                BlockPos result = target.searchFor((WorldServer) player.worldObj, new BlockPos(player));
                if (result != null) {
                    // 1.7.10: Run directly on this thread instead of using addScheduledTask
                    if (ResearchManager.useSextantTarget(target, player)) {
                        ItemSextant.setTarget(held, target);
                        ItemSextant.setCurrentTargetInformation(held, result, player.worldObj.provider.dimensionId);
                    } else {
                        AstralSorcery.log.warn(
                            "Could not set used sextant target for player " + player.getDisplayName()
                                + " - missing progress!");
                    }
                }
            }
        });
        tr.setName("SextantTargetFinder-Applying ThreadId=" + tr.getId());
        tr.start();
        return null;
    }
}
