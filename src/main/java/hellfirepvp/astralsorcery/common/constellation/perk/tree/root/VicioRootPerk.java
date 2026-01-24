/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.PlayerActivityManager;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VicioRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 14:27
 */
public class VicioRootPerk extends RootPerk implements IPlayerTickPerk {

    private static final StatBase WALK_STAT = StatList.distanceWalkedStat;
    private static final StatBase FLY_STAT = StatList.distanceFlownStat;

    private Map<StatBase, Map<UUID, Integer>> moveTrackMap = new HashMap<>();

    public VicioRootPerk(int x, int y) {
        super("vicio", Constellations.vicio, x, y);
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        if (side == Side.SERVER) {
            if (!this.moveTrackMap.containsKey(WALK_STAT)) {
                this.moveTrackMap.put(WALK_STAT, new HashMap<>());
            }
            this.moveTrackMap.get(WALK_STAT)
                .remove(player.getUniqueID());
            if (!this.moveTrackMap.containsKey(FLY_STAT)) {
                this.moveTrackMap.put(FLY_STAT, new HashMap<>());
            }
            this.moveTrackMap.get(FLY_STAT)
                .remove(player.getUniqueID());
        }
    }

    @Override
    public void clearCaches(Side side) {
        super.clearCaches(side);

        if (side == Side.SERVER) {
            this.moveTrackMap.clear();
        }
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER && player instanceof EntityPlayerMP) {
            UUID uuid = player.getUniqueID();
            StatisticsFile statFile = ((EntityPlayerMP) player).func_147099_x();
            int walked = statFile.writeStat(WALK_STAT);
            int flown = statFile.writeStat(FLY_STAT);

            if (!this.moveTrackMap.containsKey(WALK_STAT)) {
                this.moveTrackMap.put(WALK_STAT, new HashMap<>());
            }
            Map<UUID, Integer> walkMap = this.moveTrackMap.get(WALK_STAT);
            if (!walkMap.containsKey(uuid)) {
                walkMap.put(uuid, walked);
            }
            int lastWalked = walkMap.get(uuid);

            if (!this.moveTrackMap.containsKey(FLY_STAT)) {
                this.moveTrackMap.put(FLY_STAT, new HashMap<>());
            }
            Map<UUID, Integer> flyMap = this.moveTrackMap.get(FLY_STAT);
            if (!flyMap.containsKey(uuid)) {
                flyMap.put(uuid, flown);
            }
            int lastFly = flyMap.get(uuid);

            float added = 0;

            if (walked > lastWalked) {
                added += Math.min(walked - lastWalked, 500F);
                if (added >= 500F) {
                    added = 500F;
                }
                this.moveTrackMap.get(WALK_STAT)
                    .put(uuid, walked);
            }
            if (flown > lastFly) {
                added += Math.min(flown - lastFly, 500F);
                added *= 0.4F;
                this.moveTrackMap.get(FLY_STAT)
                    .put(uuid, flown);
            }

            if (!PlayerActivityManager.INSTANCE.isPlayerActiveServer(player)) {
                return;
            }

            if (added > 0) {
                PlayerProgress prog = ResearchManager.getProgress(player, side);

                added *= 0.025F;
                added *= expMultiplier;
                added = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, added);
                added = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, added);
                added = AttributeEvent.postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, added);

                float xpGain = added;
                LogCategory.PERKS
                    .info(() -> "Grant " + xpGain + " exp to " + player.getCommandSenderName() + " (Vicio)");

                ResearchManager.modifyExp(player, xpGain);
            }
        }
    }

}
