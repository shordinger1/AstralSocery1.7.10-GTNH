/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

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
 * Class: AttributeProjectileAttackDamage
 * Created by HellFirePvP
 * Date: 14.07.2018 / 13:11
 */
public class AttributeProjectileAttackDamage extends PerkAttributeType {

    public AttributeProjectileAttackDamage() {
        super(AttributeTypeRegistry.ATTR_TYPE_PROJ_DAMAGE, true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onProjDamage(LivingHurtEvent event) {
        if (event.source.isProjectile()) {
            DamageSource source = event.source;
            if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) source.getEntity();
                Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                float amt = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), event.ammount);
                amt = AttributeEvent.postProcessModded(player, this, amt);
                event.ammount = amt;
            }
        }
    }

}
