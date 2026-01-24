/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

import cpw.mods.fml.common.gameevent.TickEvent;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChain;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.tile.base.TileNetwork;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightNetworkDebugHandler
 * Created by HellFirePvP
 * Date: 20.05.2018 / 17:52
 */
public class StarlightNetworkDebugHandler implements ITickHandler {

    public static final StarlightNetworkDebugHandler INSTANCE = new StarlightNetworkDebugHandler();

    private Map<UUID, Tuple> playerAwaitingDebugMode = new HashMap<>();

    private StarlightNetworkDebugHandler() {}

    public void awaitDebugInteraction(EntityPlayer player, Runnable timeoutRunnable) {
        playerAwaitingDebugMode.put(player.getUniqueID(), new Tuple(400, timeoutRunnable));
    }

    public boolean beginDebugFor(World world, BlockPos pos, EntityPlayer player) {
        if (!playerAwaitingDebugMode.containsKey(player.getUniqueID())) {
            return false;
        }

        WorldNetworkHandler wnh = WorldNetworkHandler.getNetworkHandler(world);
        TransmissionWorldHandler twh = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(world);
        TileEntity te = MiscUtils.getTileAt(world, pos, TileEntity.class, false);
        IPrismTransmissionNode tr = wnh.getTransmissionNode(pos);

        player.addChatMessage(new ChatComponentText("§aPrinting debug for..."));
        player.addChatMessage(new ChatComponentText("§aWorld-ID:§c " + world.provider.dimensionId));
        player.addChatMessage(new ChatComponentText("§aPos:§c " + pos.toString()));
        player.addChatMessage(
            new ChatComponentText(
                "§aTile found:§c " + (te == null ? "null"
                    : te.getClass()
                        .getName())));

        if (twh == null) {
            player.addChatMessage(
                new ChatComponentText(
                    "§cWorld is missing a starlight-transmission handler! Is this world not ticking?"));
        }

        if (te != null) {
            player.addChatMessage(new ChatComponentText("§aIs Network-Tile:§c " + (te instanceof TileNetwork)));
            player.addChatMessage(
                new ChatComponentText("§aIs Starlight-Transmission-Tile:§c " + (te instanceof IStarlightTransmission)));
        }
        player.addChatMessage(new ChatComponentText("§aIs Transmission-Node present:§c " + (tr != null)));
        if (tr != null) {
            player.addChatMessage(
                new ChatComponentText(
                    "§aFull Transmission-Node class:§c " + tr.getClass()
                        .getName()));
            player.addChatMessage(
                new ChatComponentText(
                    "§aInternal Transmission-Node position:§c " + tr.getLocationPos()
                        .toString()));

            List<BlockPos> sources = tr.getSources();
            player.addChatMessage(new ChatComponentText("§aTransmission-Node Network-Source-Positions:"));
            if (sources.isEmpty()) {
                player.addChatMessage(new ChatComponentText("§cNONE"));
            }
            for (BlockPos sPos : sources) {
                player.addChatMessage(new ChatComponentText("§c" + sPos.toString()));
            }
            List<NodeConnection<IPrismTransmissionNode>> next = tr.queryNext(wnh);
            player.addChatMessage(new ChatComponentText("§aTransmission-Node next links:"));
            if ((next == null || next.size() <= 0)) {
                player.addChatMessage(new ChatComponentText("§cNONE"));
            }
            for (NodeConnection<IPrismTransmissionNode> nextNode : next) {
                player.addChatMessage(
                    new ChatComponentText(
                        "§c" + nextNode.getTo() + "§a - canSee/connected:§c " + nextNode.canConnect()));
            }

            if (tr instanceof ITransmissionSource) {
                IIndependentStarlightSource source = wnh.getSourceAt(tr.getLocationPos());
                if (source != null) {
                    player.addChatMessage(
                        new ChatComponentText(
                            "§aFound starlight source:§c " + source.getClass()
                                .getName()));

                    if (twh != null) {
                        TransmissionChain chain = twh.getSourceChain(source);
                        if (chain == null) {
                            player.addChatMessage(
                                new ChatComponentText("§cStarlight source does not have a transmission chain!"));
                        } else {
                            player.addChatMessage(
                                new ChatComponentText(
                                    "§aAmount of nodes this source provides starlight for:§c "
                                        + chain.getEndpointsNodes()
                                            .size()));
                            player.addChatMessage(
                                new ChatComponentText(
                                    "§aAmount of normal blocks this source provides starlight for:§c "
                                        + chain.getUncheckedEndpointsBlock()
                                            .size()));
                            player.addChatMessage(
                                new ChatComponentText(
                                    "§aInvolved chunks in this transmission-chain:§c " + chain.getInvolvedChunks()
                                        .size()));
                        }
                    }
                } else {
                    player.addChatMessage(
                        new ChatComponentText("§cTransmission-Source-Node is missing starlight source!"));
                }
            }
        }
        if (twh != null) {
            Collection<TransmissionChain> chains = twh.getTransmissionChains();
            for (TransmissionChain ch : chains) {
                if (ch.getUncheckedEndpointsBlock()
                    .contains(pos)) {
                    player.addChatMessage(
                        new ChatComponentText(
                            "§aFound TransmissionChain transmitting starlight to this block from "
                                + (ch.getSourceNode() == null ? "null"
                                    : ch.getSourceNode()
                                        .getLocationPos()
                                        .toString())
                                + "!"));
                }
            }
        }

        playerAwaitingDebugMode.remove(player.getUniqueID());
        return true;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        for (UUID plUUID : playerAwaitingDebugMode.keySet()) {
            Tuple cd = playerAwaitingDebugMode.get(plUUID);
            cd = new Tuple((Integer) cd.getFirst() - 1, cd.getSecond());
            if ((Integer) cd.getFirst() <= 0) {
                playerAwaitingDebugMode.remove(plUUID);
                ((Runnable) cd.getSecond()).run();
            } else {
                playerAwaitingDebugMode.put(plUUID, cd);
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Starlight Network Debug Handler";
    }
}
