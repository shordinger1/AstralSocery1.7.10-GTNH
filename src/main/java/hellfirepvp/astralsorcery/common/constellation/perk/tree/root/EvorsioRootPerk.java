/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
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
 * Class: EvorsioRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:41
 */
public class EvorsioRootPerk extends RootPerk {

    public EvorsioRootPerk(int x, int y) {
        super("evorsio", Constellations.evorsio, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;
        if (player != null && player instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (!prog.hasPerkEffect(this)) {
                return;
            }

            Block broken = event.world.getBlock(event.x, event.y, event.z);
            World world = event.world;
            BlockPos pos = new BlockPos(event.x, event.y, event.z);
            float gainedExp;
            try {
                gainedExp = broken.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
            } catch (Exception exc) {
                gainedExp = 0.5F;
            }
            if (gainedExp <= 0) {
                return; // Unbreakable lol. you're not getting exp for that.
            }
            gainedExp *= 0.15F;
            gainedExp *= expMultiplier;
            gainedExp = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, gainedExp);
            gainedExp = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, gainedExp);
            gainedExp = (float) Math.sqrt(gainedExp);
            gainedExp = AttributeEvent
                .postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, gainedExp);

            float xpGain = gainedExp;
            LogCategory.PERKS.info(() -> "Grant " + xpGain + " exp to " + player.getCommandSenderName() + " (Evorsio)");

            ResearchManager.modifyExp(player, xpGain);
        }
    }

}
