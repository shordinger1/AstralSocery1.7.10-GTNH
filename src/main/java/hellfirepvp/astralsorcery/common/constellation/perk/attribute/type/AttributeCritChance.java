/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.modifier.AttributeModifierCritChance;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeCritChance
 * Created by HellFirePvP
 * Date: 13.07.2018 / 19:06
 */
public class AttributeCritChance extends PerkAttributeType {

    public AttributeCritChance() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_CRIT_CHANCE);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        return new AttributeModifierCritChance(getTypeString(), mode, modifier);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onArrowCt(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.entity;
            if (arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
                Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }
                float critChance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 0F);
                critChance = AttributeEvent.postProcessModded(player, this, critChance);
                critChance /= 100.0F;
                if (critChance >= rand.nextFloat()) {
                    arrow.setIsCritical(true);
                }
            }
        }
    }

    // CriticalHitEvent doesn't exist in 1.7.10 - melee critical hit enhancement not available
    // @SubscribeEvent(priority = EventPriority.LOW)
    // public void onCrit(CriticalHitEvent event) {
    // if (event.isVanillaCritical() || event.getResult() == Event.Result.ALLOW) {
    // return;
    // }
    // EntityPlayer player = event.entityPlayer;
    // Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
    // if (!hasTypeApplied(player, side)) {
    // return;
    // }
    //
    // float critChance = PerkAttributeHelper.getOrCreateMap(player, side)
    // .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 0F);
    // critChance = AttributeEvent.postProcessModded(player, this, critChance);
    // critChance /= 100.0F;
    // if (critChance >= rand.nextFloat()) {
    // event.setResult(Event.Result.ALLOW);
    // }
    // }

}
