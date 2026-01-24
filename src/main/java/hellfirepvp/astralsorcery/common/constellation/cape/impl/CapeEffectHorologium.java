/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopController;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopZone;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectHorologium
 * Created by HellFirePvP
 * Date: 17.10.2017 / 23:38
 */
public class CapeEffectHorologium extends CapeArmorEffect {

    private static float effectRange = 20F;
    private static int duration = 180;

    private static int cooldown = 1000;
    private static float chanceProc = 0.8F;

    public CapeEffectHorologium(NBTTagCompound cmp) {
        super(cmp, "horologium");
    }

    public void onHurt(EntityPlayer player) {
        // 1.7.10: No CooldownTracker, cooldown not applied
        if (rand.nextFloat() < chanceProc) {
            TimeStopController.freezeWorldAt(
                TimeStopZone.EntityTargetController.allExcept(player),
                player.worldObj,
                new BlockPos(player),
                false,
                effectRange,
                duration);
        }
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.horologium;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        float perc = 0.35F; // 1.7.10: No CooldownTracker, always show higher percentage
        playConstellationCapeSparkles(pl, perc);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        effectRange = cfg.getFloat(
            getKey() + "FreezeRange",
            getConfigurationSection(),
            effectRange,
            4F,
            64F,
            "Defines the range of the time-freeze effect");
        duration = cfg.getInt(
            getKey() + "Duration",
            getConfigurationSection(),
            duration,
            40,
            50_000,
            "Defines the duration of the time-freeze bubble");

        cooldown = cfg.getInt(
            getKey() + "Cooldown",
            getConfigurationSection(),
            cooldown,
            40,
            70_000,
            "Defines the cooldown for the time-freeze effect after it triggered (should be longer than duration!)");
        chanceProc = cfg.getFloat(
            getKey() + "TriggerChance",
            getConfigurationSection(),
            chanceProc,
            0F,
            1F,
            "Defines the chance for the time-freeze effect to trigger when being hit");
    }

}
