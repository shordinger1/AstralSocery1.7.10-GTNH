/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyRampage
 * Created by HellFirePvP
 * Date: 29.07.2018 / 14:01
 */
public class KeyRampage extends KeyPerk {

    private int duration = 100;
    private float chance = 0.35F;

    public KeyRampage(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                duration = cfg.getInt(
                    "Duration",
                    getConfigurationSection(),
                    duration,
                    10,
                    40_000,
                    "Defines the duration of the rampage in ticks.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.duration = WrapMathHelper.ceil(this.duration * multiplier);
        this.chance *= multiplier;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // Monitoring outcome after all other mods might've cancelled this
    public void onEntityDeath(LivingDeathEvent event) {
        DamageSource source = event.source;
        if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (side == Side.SERVER && prog.hasPerkEffect(this)) {
                float ch = chance;
                ch = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, ch);
                if (rand.nextFloat() < ch) {

                    int dur = duration;
                    dur = Math.round(
                        PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_RAMPAGE_DURATION, dur));
                    dur = Math.round(
                        PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, dur));
                    if (dur > 0) {
                        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), dur, 1, false));
                        player.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), dur, 1, false));
                        player.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), dur, 1, false));
                    }
                }
            }
        }
    }

}
