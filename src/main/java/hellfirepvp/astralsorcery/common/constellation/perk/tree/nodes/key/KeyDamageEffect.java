/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDamageEffect
 * Created by HellFirePvP
 * Date: 24.11.2018 / 21:27
 */
public class KeyDamageEffect extends KeyPerk {

    private static final Random rand = new Random();
    private static final float baseApplyChance = 0.04F;

    public KeyDamageEffect(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamageResult(LivingHurtEvent event) {
        DamageSource source = event.source;
        if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                EntityLivingBase attacked = event.entityLiving;
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, baseApplyChance);
                if (rand.nextFloat() < chance) {
                    switch (rand.nextInt(3)) {
                        case 0:
                            attacked.setFire(100);
                            break;
                        case 1:
                            // 1.7.10: PotionEffect constructor takes (potionID, duration, amplifier, isAmbient)
                            attacked.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1, false));
                            break;
                        case 2:
                            // 1.7.10: Potion.digSpeed in 1.12.2 is Potion.moveSpeed in 1.7.10
                            attacked.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 100, 1, false));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

}
