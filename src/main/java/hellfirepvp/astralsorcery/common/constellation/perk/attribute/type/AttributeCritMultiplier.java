/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeCritMultiplier
 * Created by HellFirePvP
 * Date: 13.07.2018 / 19:22
 */
public class AttributeCritMultiplier extends PerkAttributeType {

    public AttributeCritMultiplier() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_CRIT_MULTIPLIER, true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onArrowCt(EntityJoinWorldEvent event) {
        // 1.7.10: Use event.entity instead of event.getEntity()
        if (event.entity instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.entity;
            if (!arrow.getIsCritical()) return; // No crit

            if (arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
                Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                float dmgMod = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
                dmgMod = AttributeEvent.postProcessModded(player, this, dmgMod);
                arrow.setDamage(arrow.getDamage() * dmgMod);
            }
        }
    }

    // CriticalHitEvent doesn't exist in 1.7.10 - melee critical hit multiplier enhancement not available
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void onCrit(CriticalHitEvent event) {
    // if (!event.isVanillaCritical() && event.getResult() != Event.Result.ALLOW) {
    // return; // No crit
    // }
    //
    // EntityPlayer player = event.entityPlayer;
    // Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
    // if (!hasTypeApplied(player, side)) {
    // return;
    // }
    //
    // float dmgMod = PerkAttributeHelper.getOrCreateMap(event.entityPlayer, side)
    // .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
    // dmgMod = AttributeEvent.postProcessModded(player, this, dmgMod);
    // event.setDamageModifier(event.getDamageModifier() * dmgMod);
    // }

}
