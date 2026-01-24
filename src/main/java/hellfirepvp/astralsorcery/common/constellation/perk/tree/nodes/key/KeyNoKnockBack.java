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
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyNoKnockBack
 * Created by HellFirePvP
 * Date: 23.11.2018 / 20:13
 */
public class KeyNoKnockBack extends KeyPerk {

    public KeyNoKnockBack(String name, int x, int y) {
        super(name, x, y);
    }

    // 1.7.10: LivingKnockBackEvent doesn't exist. Use LivingAttackEvent as alternative.
    // This prevents the attack entirely, which also prevents knockback.
    @SubscribeEvent
    public void onKnockBack(LivingAttackEvent event) {
        EntityLivingBase attacked = event.entityLiving;
        if (attacked instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacked;
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                event.setCanceled(true);
            }
        }
    }

}
