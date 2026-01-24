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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyAreaOfEffect
 * Created by HellFirePvP
 * Date: 24.11.2018 / 21:50
 */
public class KeyAreaOfEffect extends KeyAddEnchantment {

    private static boolean inSweepAttack = false;

    public KeyAreaOfEffect(String name, int x, int y) {
        super(name, x, y);
        // addEnchantment(Enchantments.SWEEPING, 2); // SWEEPING doesn't exist in 1.7.10
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDmg(LivingHurtEvent event) {
        if (inSweepAttack) {
            return;
        }

        DamageSource source = event.source;
        if (source instanceof EntityDamageSourceIndirect && source.getEntity() != null
            && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                EntityLivingBase attacked = event.entityLiving;
                // Sweeping edge enchantment doesn't exist in 1.7.10, using fallback value
                float sweepPerc = getSweepingDamageRatio(player);
                if (sweepPerc > 0) {
                    sweepPerc = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, sweepPerc);
                    float toApply = event.ammount * sweepPerc;
                    inSweepAttack = true;
                    try {
                        for (EntityLivingBase target : attacked.worldObj
                            .getEntitiesWithinAABB(EntityLivingBase.class, attacked.boundingBox.expand(1, 0.25, 1))) {
                            if (MiscUtils.canPlayerAttackServer(player, target)) {
                                DamageUtil.attackEntityFrom(target, source, toApply);
                            }
                        }
                    } finally {
                        inSweepAttack = false;
                    }
                }
            }
        }
    }

    /**
     * 1.7.10 compatibility: Sweeping edge enchantment doesn't exist.
     * Returns 0 by default, subclasses can override if they have custom sweep mechanics.
     */
    protected float getSweepingDamageRatio(EntityPlayer player) {
        return 0F;
    }

}
