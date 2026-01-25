/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHealEvent;

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
 * Class: KeyCleanseBadPotions
 * Created by HellFirePvP
 * Date: 24.11.2018 / 15:39
 */
public class KeyCleanseBadPotions extends KeyPerk {

    private static final Random rand = new Random();

    public KeyCleanseBadPotions(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHeal(LivingHealEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (entity instanceof EntityPlayer && !entity.worldObj.isRemote) {
            EntityPlayer player = (EntityPlayer) entity;
            List<PotionEffect> badEffects = new ArrayList<>();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (Potion.potionTypes[effect.getPotionID()].isBadEffect()) {
                    badEffects.add(effect);
                }
            }
            if (badEffects == null || badEffects.stackSize <= 0) {
                return;
            }
            PotionEffect effect = badEffects.get(rand.nextInt(badEffects.size()));
            PlayerProgress prog = ResearchManager.getProgress(player, Side.SERVER);
            if (prog.hasPerkEffect(this)) {
                float inclChance = 0.1F;
                inclChance = PerkAttributeHelper.getOrCreateMap(player, Side.SERVER)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, inclChance);
                float chance = getChance(event.amount) * inclChance;
                if (rand.nextFloat() < chance) {
                    player.removePotionEffect(Potion.potionTypes[effect.getPotionID()].id);
                }
            }
        }
    }

    private float getChance(float healed) {
        if (healed <= 0) {
            return 0;
        }
        float chance = ((3F / (healed * -0.66666667F)) + 5F) / 5F;
        return WrapMathHelper.clamp(chance, 0F, 1F);
    }

}
