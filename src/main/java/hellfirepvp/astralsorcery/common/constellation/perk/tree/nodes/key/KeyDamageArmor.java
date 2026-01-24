/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDamageArmor
 * Created by HellFirePvP
 * Date: 23.11.2018 / 20:17
 */
public class KeyDamageArmor extends KeyPerk {

    private final float dmgPercentPerArmor;

    public KeyDamageArmor(String name, int x, int y, float dmgPercent) {
        super(name, x, y);
        this.dmgPercentPerArmor = dmgPercent;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDmg(LivingHurtEvent event) {
        EntityLivingBase attacked = event.entityLiving;
        if (attacked instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacked;
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                int armorPieces = 0;
                // In 1.7.10, iterate over armor slots manually
                for (int i = 0; i < 4; i++) {
                    ItemStack armor = player.inventory.armorItemInSlot(i);
                    if (!(armor == null || armor.stackSize <= 0)) {
                        armorPieces++;
                    }
                }
                if (armorPieces == 0) {
                    return;
                }

                float dmg = event.ammount;
                dmg *= ((dmgPercentPerArmor * armorPieces) * PerkAttributeHelper.getOrCreateMap(player, side)
                    .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT));
                event.ammount = Math.max(event.ammount - dmg, 0);

                int armorDmg = WrapMathHelper.ceil(dmg * 1.3F);
                // In 1.7.10, iterate over armor slots manually
                for (int i = 0; i < 4; i++) {
                    ItemStack stack = player.inventory.armorItemInSlot(i);
                    if (stack != null) {
                        stack.damageItem(armorDmg, player);
                    }
                }
            }
        }
    }

}
