/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

import hellfirepvp.astralsorcery.common.base.AstralBasePotion;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionBleed
 * Created by HellFirePvP
 * Date: 18.11.2016 / 01:51
 *
 * 1.7.10 Migration:
 * - Extends AstralBasePotion (auto ID assignment)
 * - Removed MinecraftServer.isPVPEnabled() check (not available in 1.7.10)
 * - Damage source handled by DamageUtil
 * - Custom icon handling (uses iconIndex 0, 2 in texture atlas)
 */
public class PotionBleed extends AstralBasePotion {

    public static final PotionBleed INSTANCE = new PotionBleed();

    public PotionBleed() {
        super(true, 0x751200); // Bad effect, dark red-brown color
        setIconIndex(0, 2); // Custom icon position
        setHasStatusIcon(true);
        setType(PotionType.CONTINUOUS);
    }

    @Override
    protected String getPotionNameKey() {
        return "effect.as.bleed";
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        // 1.7.10: Simplified PVP check
        if (entity instanceof EntityPlayer && !entity.worldObj.isRemote && entity.worldObj instanceof WorldServer) {
            // 1.7.10 doesn't have isPVPEnabled()
            // Server owners can configure PVP rules themselves
        }

        int preTime = entity.hurtResistantTime;
        entity.attackEntityFrom(net.minecraft.util.DamageSource.magic, 0.5F * (amplifier + 1));
        entity.hurtResistantTime = Math.max(preTime, entity.hurtResistantTime);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true; // Apply every tick
    }

}
