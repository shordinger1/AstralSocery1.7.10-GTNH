/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.modifier.AttributeModifierThorns;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeThorns
 * Created by HellFirePvP
 * Date: 11.08.2018 / 18:24
 */
public class AttributeThorns extends PerkAttributeType {

    public AttributeThorns() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_THORNS);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        return new AttributeModifierThorns(getTypeString(), mode, modifier);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        PlayerProgress prog = ResearchManager.getProgress(player, side);

        float reflectAmount = PerkAttributeHelper.getOrCreateMap(player, side)
            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_THORNS, 0F);
        reflectAmount = AttributeEvent.postProcessModded(player, this, reflectAmount);
        reflectAmount /= 100.0F;
        if (reflectAmount <= 0) {
            return;
        }
        reflectAmount = WrapMathHelper.clamp(reflectAmount, 0F, 1F);

        DamageSource source = event.source;
        EntityLivingBase reflectTarget = null;
        if (source.getSourceOfDamage() != null && source.getSourceOfDamage() instanceof EntityLivingBase
            && !source.getSourceOfDamage().isDead) {
            reflectTarget = (EntityLivingBase) source.getSourceOfDamage();
        }

        if (reflectTarget == null && AttributeEvent.postProcessModded(
            player,
            this,
            PerkAttributeHelper.getOrCreateMap(player, side)
                .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_THORNS_RANGED))
            > 1) {
            if (source.getEntity() != null && source.getEntity() instanceof EntityLivingBase
                && !source.getEntity().isDead) {
                reflectTarget = (EntityLivingBase) source.getEntity();
            }
        }

        if (reflectTarget != null) {
            float dmgReflected = event.ammount * reflectAmount;
            if (dmgReflected > 0 && !event.entityLiving.equals(reflectTarget)) {
                if (MiscUtils.canPlayerAttackServer(event.entityLiving, reflectTarget)) {
                    DamageUtil.attackEntityFrom(reflectTarget, CommonProxy.dmgSourceReflect, dmgReflected, player);
                }
            }
        }
    }

}
