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
        // TODO: Implement full login synchronization
        // ResearchManager.sendInitClientKnowledge(p);
        // CelestialGatewaySystem.instance.syncTo(p);
        // SyncDataHolder.syncAllDataTo(p);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        EntityPlayer player = e.player;

        // TODO: Implement PlayerChargeHandler
        // PlayerChargeHandler.INSTANCE.informDisconnect(player);
        // EventHandlerEntity.attackStack.remove(e.player.getEntityId());
    }

}
