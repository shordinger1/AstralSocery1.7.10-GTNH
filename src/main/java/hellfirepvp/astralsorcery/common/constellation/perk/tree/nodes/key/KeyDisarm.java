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
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDisarm
 * Created by HellFirePvP
 * Date: 20.07.2018 / 18:08
 */
public class KeyDisarm extends KeyPerk {

    private float dropChance = 0.05F;

    public KeyDisarm(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                dropChance = cfg.getFloat(
                    "DropChance",
                    getConfigurationSection(),
                    dropChance,
                    0F,
                    1F,
                    "Defines the chance (in percent) per hit to make the attacked entity drop its armor.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.dropChance *= multiplier;
    }

    @SubscribeEvent
    public void onAttack(LivingHurtEvent event) {
        DamageSource source = event.source;
        if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, dropChance);
                float currentChance = WrapMathHelper.clamp(chance, 0F, 1F);
                // 1.7.10: Iterate armor slots manually [0-3] = boots, legs, chest, helmet
                for (int slotIdx = 0; slotIdx < 4; slotIdx++) {
                    if (rand.nextFloat() >= currentChance) {
                        continue;
                    }
                    EntityLivingBase attacked = event.entityLiving;
                    if (attacked instanceof EntityPlayer) {
                        ItemStack stack = ((EntityPlayer) attacked).inventory.armorInventory[slotIdx];
                        if (!(stack == null || stack.stackSize <= 0)) {
                            ((EntityPlayer) attacked).inventory.armorInventory[slotIdx] = null;
                            ItemUtils.dropItemNaturally(
                                attacked.worldObj,
                                attacked.posX,
                                attacked.posY,
                                attacked.posZ,
                                stack);
                            break;
                        }
                    }
                }
            }
        }
    }

}
