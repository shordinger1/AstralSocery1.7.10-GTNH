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
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.modifier.AttributeModifierLifeLeech;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeLifeLeech
 * Created by HellFirePvP
 * Date: 01.08.2018 / 21:04
 */
public class AttributeLifeLeech extends PerkAttributeType {

    public AttributeLifeLeech() {
        super(AttributeTypeRegistry.ATTR_TYPE_ATTACK_LIFE_LEECH);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        return new AttributeModifierLifeLeech(getTypeString(), mode, modifier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(LivingHurtEvent event) {
        // 1.7.10: Use event.source instead of event.getSource()
        DamageSource source = event.source;
        // 1.7.10: Use source.getEntity() instead of source.getTrueSource()
        if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            if (side == Side.SERVER && hasTypeApplied(player, side)) {
                float leechPerc = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(
                        player,
                        ResearchManager.getProgress(player, side),
                        AttributeTypeRegistry.ATTR_TYPE_ATTACK_LIFE_LEECH,
                        0F);
                leechPerc /= 100.0F;
                leechPerc = AttributeEvent.postProcessModded(player, this, leechPerc);
                if (leechPerc > 0) {
                    // 1.7.10: Use event.ammount instead of event.getAmount()
                    float leech = event.ammount * leechPerc;
                    if (leech > 0) {
                        player.heal(leech);
                    }
                }
            }
        }
    }

}
