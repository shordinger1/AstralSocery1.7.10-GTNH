/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Event handler for mod events
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import hellfirepvp.astralsorcery.common.block.BlockAltar;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.NetworkWrapper;
import hellfirepvp.astralsorcery.common.network.packet.PacketNBT;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Main event handler for Astral Sorcery
 *
 * Handles both Forge and FML events on the common side.
 * Based on TST ServerEvent.java
 */
public class EventHandler {

    /**
     * Called when a player logs in
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.worldObj.isRemote) {
            return; // Don't process on client
        }

        EntityPlayer player = event.player;
        LogHelper.debug("Player logged in: " + player.getCommandSenderName());

        // Register PlayerProgressProperties (attaches progress to player)
        hellfirepvp.astralsorcery.common.data.research.PlayerProgressProperties.register(player);
        LogHelper.debug("Registered PlayerProgressProperties for " + player.getCommandSenderName());

        // Load player research progress from extended properties
        PlayerProgress progress = hellfirepvp.astralsorcery.common.data.research.PlayerProgressProperties
            .getProgress(player);

        // Also cache in ResearchManager
        ResearchManager.getProgress(player);

        // Send progress to client
        if (progress != null) {
            sendResearchProgress(player, progress);
            LogHelper.info(
                "Loaded research progress for " + player.getCommandSenderName()
                    + " - Tier: "
                    + progress.getTierReached());
        }
    }

    /**
     * Called when a player logs out
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player.worldObj.isRemote) {
            return; // Don't process on client
        }

        LogHelper.debug("Player logged out: " + event.player.getCommandSenderName());

        // Player data is automatically saved by PlayerProgressProperties
        // No need to manually save here
    }

    /**
     * Called when a player respawns
     */
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player.worldObj.isRemote) {
            return; // Don't process on client
        }

        EntityPlayer player = event.player;
        LogHelper.debug("Player respawned: " + player.getCommandSenderName());

        // Preserve research progress on respawn
        PlayerProgress progress = ResearchManager.getProgress(player);
        if (progress != null) {
            sendResearchProgress(player, progress);
        }
    }

    /**
     * Called when an entity joins the world
     */
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity.worldObj.isRemote) {
            return; // Don't process on client
        }

        // Handle entity spawn logic here
        // TODO: Initialize AS entities if needed
    }

    /**
     * Called when a block is broken
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.world.isRemote) {
            return; // Don't process on client
        }

        // Handle special block break logic
        if (event.block instanceof BlockAltar) {
            // Notify altar TileEntity of break
            // This is also called from BlockAltar.breakBlock
            // But this event allows us to cancel it if needed
            LogHelper.debug("Altar block broken at " + event.x + ", " + event.y + ", " + event.z);
        }
    }

    /**
     * Called when a block is placed
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.world.isRemote) {
            return; // Don't process on client
        }

        // Handle special block placement logic
        if (event.block instanceof BlockAltar) {
            LogHelper.debug("Altar block placed at " + event.x + ", " + event.y + ", " + event.z);

            // Initialize altar TileEntity
            // This is also called from BlockAltar.onBlockAdded
            // But this event allows us to cancel it if needed
        }
    }

    /**
     * Called when a chunk is loaded
     */
    @SubscribeEvent
    public void onChunkLoad(WorldEvent.Load event) {
        if (event.world.isRemote) {
            return; // Don't process on client
        }

        // Handle chunk load logic here
        // TODO: Initialize any chunk-specific data
    }

    /**
     * Called when a chunk is unloaded
     */
    @SubscribeEvent
    public void onChunkUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            return; // Don't process on client
        }

        // Handle chunk unload logic here
        // TODO: Clean up any chunk-specific data
    }

    /**
     * World tick event
     * Called every world tick (20 times per second)
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote) {
            return; // Don't process on client
        }

        if (event.phase == TickEvent.Phase.END) {
            // Handle end-of-tick logic
            // This is called after all other tile entities have ticked
            tickWorldEnd(event.world);
        }
    }

    /**
     * Server tick event
     * Called every server tick
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Handle server-wide end-of-tick logic
            // This is good for periodic tasks
        }
    }

    /**
     * Handle world tick end logic
     */
    private void tickWorldEnd(net.minecraft.world.World world) {
        // Periodic tasks (every 5 seconds = 100 ticks)
        long totalTime = world.getTotalWorldTime();
        if (totalTime % 100 == 0) {
            // Player data is automatically saved by PlayerProgressProperties
            // No need for manual periodic saves
        }
    }

    /**
     * Send research progress to client
     */
    private void sendResearchProgress(EntityPlayer player, PlayerProgress progress) {
        NBTTagCompound data = new NBTTagCompound();
        progress.store(data);

        // Send progress data to client
        if (NetworkWrapper.isInitialized()) {
            NetworkWrapper
                .sendTo(new PacketNBT("research_progress", data), (net.minecraft.entity.player.EntityPlayerMP) player);
            LogHelper.debug("Sent research progress to " + player.getCommandSenderName());
        }
    }
}
