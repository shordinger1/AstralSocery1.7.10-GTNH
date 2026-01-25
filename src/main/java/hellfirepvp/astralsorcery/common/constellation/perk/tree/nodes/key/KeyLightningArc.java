/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.entities.EntityTechnicalAmbient;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyLightningArc
 * Created by HellFirePvP
 * Date: 20.07.2018 / 22:22
 */
public class KeyLightningArc extends KeyPerk {

    private static boolean chainingDamage = false;

    private float arcChance = 0.6F;
    private float arcPercent = 0.75F;
    private int arcTicks = 3;

    private static float distanceSearch = 7F;
    private static int arcBaseChains = 3;

    public KeyLightningArc(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                arcChance = cfg.getFloat(
                    "Chance",
                    getConfigurationSection(),
                    arcChance,
                    0F,
                    1F,
                    "Sets the chance to spawn a damage-arc effect when an enemy is hit (value is in percent)");
                arcPercent = cfg.getFloat(
                    "DamagePercent",
                    getConfigurationSection(),
                    arcPercent,
                    0.1F,
                    64F,
                    "Defines the damage-multiplier which gets added to the damage dealt initially.");
                distanceSearch = cfg.getFloat(
                    "Distance",
                    getConfigurationSection(),
                    distanceSearch,
                    0.2F,
                    16F,
                    "Defines the distance for how far a single arc can jump/search for nearby entities");
                arcTicks = cfg.getInt(
                    "DamageTicks",
                    getConfigurationSection(),
                    arcTicks,
                    1,
                    128,
                    "Defines the amount of times an arc will repetitively chain between the mobs and deal damage after initially spawned/triggered");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.arcChance *= multiplier;
        this.arcPercent *= multiplier;
        this.arcTicks = WrapMathHelper.ceil(this.arcTicks * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(LivingHurtEvent event) {
        if (chainingDamage) return;

        DamageSource source = event.source;
        if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (side == Side.SERVER && prog.hasPerkEffect(this)) {
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, arcChance);
                if (rand.nextFloat() < chance) {
                    float dmg = event.ammount;
                    dmg = Math.max(WrapMathHelper.sqrt(dmg), 1.5F);
                    new RepetitiveArcEffect(player.worldObj, player, arcTicks, event.entityLiving.getEntityId(), dmg)
                        .fire();
                }
            }
        }
    }

    static class RepetitiveArcEffect {

        private World world;
        private EntityPlayer player;
        private int count;
        private int entityStartId;
        private float damage;

        public RepetitiveArcEffect(World world, EntityPlayer player, int count, int entityStartId, float damage) {
            this.world = world;
            this.player = player;
            this.count = count;
            this.entityStartId = entityStartId;
            this.damage = damage;
        }

        void fire() {
            if (player.isDead) {
                return;
            }

            Color c = new Color(0x0195FF);
            int chainTimes = Math.round(
                PerkAttributeHelper.getOrCreateMap(player, Side.SERVER)
                    .modifyValue(
                        player,
                        ResearchManager.getProgress(player, Side.SERVER),
                        AttributeTypeRegistry.ATTR_TYPE_ARC_CHAINS,
                        arcBaseChains));
            List<EntityLivingBase> visitedEntities = Lists.newArrayList();
            Entity start = world.getEntityByID(entityStartId);
            if (start != null && start instanceof EntityLivingBase && !start.isDead) {
                AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                    -distanceSearch,
                    -distanceSearch,
                    -distanceSearch,
                    distanceSearch,
                    distanceSearch,
                    distanceSearch);

                EntityLivingBase last = null;
                EntityLivingBase entity = (EntityLivingBase) start;
                while (entity != null && !entity.isDead && chainTimes > 0) {
                    visitedEntities.add(entity);
                    chainTimes--;

                    if (last != null) {
                        AstralSorcery.proxy.fireLightning(
                            entity.worldObj,
                            Vector3.atEntityCenter(last),
                            Vector3.atEntityCenter(entity),
                            c);
                        AstralSorcery.proxy.fireLightning(
                            entity.worldObj,
                            Vector3.atEntityCenter(entity),
                            Vector3.atEntityCenter(last),
                            c);
                    }
                    // 1.7.10: getEntitiesWithinAABB takes (Class<T>, AxisAlignedBB), only 2 arguments
                    List<EntityLivingBase> entities = entity.worldObj.getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        box.offset(entity.posX, entity.posY, entity.posZ));
                    entities.remove(entity);
                    if (last != null) {
                        entities.remove(last);
                    }
                    if (player != null) {
                        entities.remove(player);
                    }
                    entities.removeAll(visitedEntities);
                    Iterator<EntityLivingBase> it1 = entities.iterator();
                    while (it1.hasNext()) {
                        Entity e = it1.next();
                        if (e instanceof EntityTechnicalAmbient || e instanceof EntityFlare) {
                            it1.remove();
                        }
                    }
                    Iterator<EntityLivingBase> it2 = entities.iterator();
                    while (it2.hasNext()) {
                        Entity e = it2.next();
                        // 1.7.10: MiscUtils.canPlayerAttackServer takes EntityLivingBase, not Entity
                        if (e instanceof EntityLivingBase
                            && !MiscUtils.canPlayerAttackServer(player, (EntityLivingBase) e)) {
                            it2.remove();
                        }
                    }

                    if (!entities == null || entities.stackSize <= 0) {
                        EntityLivingBase tmpEntity = entity; // Final for lambda
                        // 1.7.10: getDistanceToEntity returns float, need to cast to double
                        EntityLivingBase closest = EntityUtils
                            .selectClosest(entities, (e) -> (double) e.getDistanceToEntity(tmpEntity));
                        if (closest != null && !closest.isDead) {
                            last = entity;
                            entity = closest;
                        } else {
                            entity = null;
                        }
                    } else {
                        entity = null;
                    }
                }

                if (visitedEntities.size() > 1) {
                    for (Entity e : visitedEntities) {
                        chainingDamage = true;
                        DamageUtil.attackEntityFrom(e, CommonProxy.dmgSourceStellar, damage, player);
                        chainingDamage = false;
                    }
                }
            }

            if (count > 0) {
                count--;
                AstralSorcery.proxy.scheduleDelayed(new Runnable() {

                    @Override
                    public void run() {
                        fire();
                    }
                }, 12);
            }
        }
    }
}
