/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import cpw.mods.fml.common.gameevent.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerServer
 * Created by HellFirePvP
 * Date: 01.08.2017 / 20:28
 */
public class EventHandlerServer {

    // Placeholder for server-specific events
    // This can be expanded as needed for 1.7.10 server-side handling

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // Server-side logout handling if needed
    }

}
