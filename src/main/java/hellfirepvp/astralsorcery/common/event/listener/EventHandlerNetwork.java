/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktFinalizeLogin;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerNetwork
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:10
 */
public class EventHandlerNetwork {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        EntityPlayerMP p = (EntityPlayerMP) e.player;
        // 1.7.10: Use getCommandSenderName() instead of getName()
        AstralSorcery.log.info(
            "[Astral Sorcery] Waiting for server synchronization on login for " + p.getCommandSenderName() + "...");
        AstralSorcery.proxy.scheduleDelayed(() -> {
            AstralSorcery.log
                .info("[Astral Sorcery] Synchronizing baseline information to " + p.getCommandSenderName());
            ResearchManager.sendInitClientKnowledge(p);
            CelestialGatewaySystem.instance.syncTo(p);
            SyncDataHolder.syncAllDataTo(p);

            PacketChannel.CHANNEL.sendTo(new PktFinalizeLogin(), p);
        });
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        EntityPlayer player = e.player;

        PlayerChargeHandler.INSTANCE.informDisconnect(player);
        EventHandlerEntity.attackStack.remove(e.player.getEntityId());
        // ResearchManager.logoutResetClient(player);
    }

}
