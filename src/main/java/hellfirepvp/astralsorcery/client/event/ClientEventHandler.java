/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Client-side event handler
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Client-side event handler for Astral Sorcery
 */
public class ClientEventHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Client tick logic
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        // Render tick logic
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote) return;

        Entity entity = event.entity;
        // LogHelper.debug("Entity joined client world: " + entity.getName());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!event.world.isRemote) return;
        // Chunk load logic
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (!event.world.isRemote) return;
        // Chunk unload logic
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.world.isRemote) return;
        LogHelper.debug("Client world loaded: Dimension " + event.world.provider.dimensionId);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!event.world.isRemote) return;
        LogHelper.debug("Client world unloaded: Dimension " + event.world.provider.dimensionId);
    }

    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        // LogHelper.info("Connected to server: " + event.manager.getRemoteAddress());
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        LogHelper.info("Disconnected from server");
    }

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) { // 0 = blocks
            LogHelper.info("=== TextureStitchEvent.Pre: Starting block texture registration ===");
            // Manually trigger BlockTextureRegister
            hellfirepvp.astralsorcery.client.util.BlockTextureRegister.registerAll(event.map);
            LogHelper.info("=== TextureStitchEvent.Pre: Block texture registration complete ===");
        }
    }

    protected Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    protected net.minecraft.world.World getWorld() {
        return getMinecraft().theWorld;
    }

    protected net.minecraft.entity.player.EntityPlayer getPlayer() {
        return getMinecraft().thePlayer;
    }

    protected boolean isInGame() {
        return getPlayer() != null && getWorld() != null;
    }
}
