/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerIO
 * Created by HellFirePvP
 * Date: 01.08.2017 / 18:45
 */
public class EventHandlerIO {

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        World w = event.world;
        // ConstellationSkyHandler.getInstance().informWorldUnload(w); // TODO: Implement
        if (w.isRemote) {
            clientUnload();
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientUnload() {
        // AstralSorcery.proxy.scheduleClientside(TESRTranslucentBlock::cleanUp); // TODO: Implement
    }

    @SubscribeEvent
    public void onSave(WorldEvent.Save event) {
        // TODO: Implement world save handling
    }

}
