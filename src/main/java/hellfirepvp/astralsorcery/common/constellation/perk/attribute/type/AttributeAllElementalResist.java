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

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeAllElementalResist
 * Created by HellFirePvP
 * Date: 13.07.2018 / 23:34
 */
public class AttributeAllElementalResist extends PerkAttributeType {

    public AttributeAllElementalResist() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST, true);
    }

    @SubscribeEvent
    public void onDamageTaken(LivingHurtEvent event) {
        // 1.7.10: LivingHurtEvent has direct field access instead of getter methods
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }
        // 1.7.10: Use event.source instead of event.getSource()
        DamageSource ds = event.source;
        if (isMaybeElementalDamage(ds)) {
            float multiplier = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
            multiplier -= 1F;
            multiplier = AttributeEvent.postProcessModded(player, this, multiplier);
            multiplier = 1F - WrapMathHelper.clamp(multiplier, 0F, 1F);
            // 1.7.10: Use event.ammount field instead of getAmount()/setAmount()
            event.ammount = event.ammount * multiplier;
        }
    }

    private boolean isMaybeElementalDamage(DamageSource source) {
        // "Magic" is often used for any kinds of damages... poison for example
        if (source.isFireDamage() || source.isMagicDamage()) {
            return true;
        }
        String key = source.getDamageType();
        if (key == null) {
            return false;
        }
        key = key.toLowerCase();
        return key.contains("fire") || key.contains("heat")
            || key.contains("lightning")
            || key.contains("cold")
            || key.contains("freez")
            || key.contains("discharg")
            || key.contains("electr")
            || key.contains("froze")
            || key.contains("ice");
    }

}
