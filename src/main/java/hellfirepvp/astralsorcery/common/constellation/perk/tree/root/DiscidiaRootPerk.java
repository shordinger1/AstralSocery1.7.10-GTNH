/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DiscidiaRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:11
 */
public class DiscidiaRootPerk extends RootPerk {

    public DiscidiaRootPerk(int x, int y) {
        super("discidia", Constellations.discidia, x, y);
    }

    // Measure actual outcome of dmg
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingHurtEvent event) {
        Side side = event.entityLiving.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        DamageSource ds = event.source;
        EntityPlayer player = null;
        if (ds.getSourceOfDamage() != null && ds.getSourceOfDamage() instanceof EntityPlayer) {
            player = (EntityPlayer) ds.getSourceOfDamage();
        }
        if (player == null && ds.getEntity() != null && ds.getEntity() instanceof EntityPlayer) {
            player = (EntityPlayer) ds.getEntity();
        }
        if (player != null) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                float dmgDealt = event.ammount;
                dmgDealt *= 0.09F;
                dmgDealt *= expMultiplier;
                dmgDealt = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, dmgDealt);
                dmgDealt = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, dmgDealt);
                dmgDealt = AttributeEvent
                    .postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, dmgDealt);

                float expGain = dmgDealt;
                EntityPlayer logPlayer = player;
                LogCategory.PERKS
                    .info(() -> "Grant " + expGain + " exp to " + logPlayer.getCommandSenderName() + " (Discidia)");

                ResearchManager.modifyExp(player, expGain);
            }
        }
    }

}
