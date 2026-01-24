/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AevitasRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:45
 */
public class AevitasRootPerk extends RootPerk {

    private static final int trackLength = 20;
    private Map<UUID, Queue<BlockPos>> plInteractMap = new HashMap<>();
    private Map<UUID, Deque<Block>> plDimReturns = new HashMap<>();

    public AevitasRootPerk(int x, int y) {
        super("aevitas", Constellations.aevitas, x, y);
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        if (side == Side.SERVER) {
            plInteractMap.remove(player.getUniqueID());
            plDimReturns.remove(player.getUniqueID());
        }
    }

    @Override
    public void clearCaches(Side side) {
        super.clearCaches(side);

        if (side == Side.SERVER) {
            plInteractMap.clear();
            plDimReturns.clear();
        }
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {
        EntityPlayer player = event.player;
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        if (!plDimReturns.containsKey(player.getUniqueID())) {
            plDimReturns.put(player.getUniqueID(), new LinkedList<>());
        }
        Deque<Block> dim = plDimReturns.get(player.getUniqueID());
        while (dim.size() >= trackLength) {
            dim.pollLast();
        }
        float used = 0;
        Block placedBlock = event.world.getBlock(event.x, event.y, event.z);
        for (Block placed : dim) {
            if (MiscUtils.matchStateExact(placedBlock, placed)) {
                used++;
            }
        }
        float same;
        if (dim.size() <= 0) {
            same = 1F;
        } else {
            same = 0.4F + (1F - (used / trackLength)) * 0.6F;
        }
        dim.addFirst(placedBlock);

        BlockPos pos = new BlockPos(event.x, event.y, event.z);
        if (!plInteractMap.containsKey(player.getUniqueID())) {
            plInteractMap.put(player.getUniqueID(), new ArrayDeque<>(trackLength));
        }
        Queue<BlockPos> tracked = plInteractMap.get(player.getUniqueID());
        if (!tracked.contains(pos)) {
            tracked.add(pos);

            float xp = Math.max(placedBlock.getBlockHardness(event.world, pos.getX(), pos.getY(), pos.getZ()) / 20F, 1);
            xp *= expMultiplier;
            xp *= same;
            xp = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, xp);
            xp = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, xp);
            xp = AttributeEvent.postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, xp);

            float expGain = xp;
            LogCategory.PERKS
                .info(() -> "Grant " + expGain + " exp to " + player.getCommandSenderName() + " (Aevitas)");

            ResearchManager.modifyExp(player, expGain);
        }

    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        if (!plInteractMap.containsKey(player.getUniqueID())) {
            plInteractMap.put(player.getUniqueID(), new ArrayDeque<>(trackLength));
        }
        Queue<BlockPos> tracked = plInteractMap.get(player.getUniqueID());
        if (tracked.contains(new BlockPos(event.x, event.y, event.z))) {
            return;
        }
        while (tracked.size() >= trackLength) {
            tracked.poll();
        }
        tracked.add(new BlockPos(event.x, event.y, event.z));
    }

}
