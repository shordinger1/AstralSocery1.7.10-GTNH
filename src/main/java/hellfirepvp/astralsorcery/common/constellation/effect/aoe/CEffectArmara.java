/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.controller.orbital.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.controller.orbital.OrbitalPropertiesRitualArmara;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.entities.EntityTechnicalAmbient;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerEntity;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectArmara
 * Created by HellFirePvP
 * Date: 03.01.2017 / 14:49
 */
public class CEffectArmara extends CEffectEntityCollect<EntityMob> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;
    public static int protectionRange = 32;

    private int rememberedTimeout = 0;
    public static int potionAmplifier = 0;

    public CEffectArmara(@Nullable ILocatable origin) {
        super(
            origin,
            Constellations.armara,
            "armara",
            protectionRange,
            EntityMob.class,
            new com.google.common.base.Predicate<EntityMob>() {

                @Override
                public boolean apply(EntityMob entity) {
                    return !entity.isDead && !(entity instanceof EntityTechnicalAmbient);
                }
            });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
        boolean extendedEffects) {
        if (pedestal.getTicksExisted() % 20 == 0) {
            OrbitalPropertiesRitualArmara luc = new OrbitalPropertiesRitualArmara();
            OrbitalEffectController ctrl = EffectHandler.getInstance()
                .orbital(luc, luc, luc);
            ctrl.setOffset(new Vector3(pos).add(0.5, 0.5, 0.5));
            ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.7);
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(20 + rand.nextInt(20));
        }
        ItemStack socket = pedestal.getCatalystCache();
        if (!(socket == null || socket.stackSize <= 0) && socket.getItem() instanceof ItemTunedCrystalBase) {
            IMinorConstellation trait = ItemTunedCrystalBase.getTrait(socket);
            ConstellationEffectProperties prop = provideProperties(0);
            prop.modify(trait);
            if (prop.isCorrupted()) {
                return;
            }
        }
        List<Entity> projectiles = world.getEntitiesWithinAABB(
            Entity.class,
            AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1)
                .offset(pos.getX(), pos.getY(), pos.getZ())
                .expand(protectionRange, protectionRange, protectionRange));
        if (!projectiles == null || projectiles.stackSize <= 0) {
            for (Entity e : projectiles) {
                if (!e.isDead && !(e instanceof EntityTechnicalAmbient)) {
                    if (e instanceof IProjectile) {
                        double xRatio = (pos.getX() + 0.5) - e.posX;
                        double zRatio = (pos.getZ() + 0.5) - e.posZ;
                        float f = WrapMathHelper.sqrt((float) (xRatio * xRatio + zRatio * zRatio));
                        e.motionX /= 2.0D;
                        e.motionZ /= 2.0D;
                        e.motionX -= xRatio / f * 0.4;
                        e.motionZ -= zRatio / f * 0.4;
                        ((IProjectile) e).setThrowableHeading(e.motionX, e.motionY, e.motionZ, 1F, 0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified,
        @Nullable IMinorConstellation possibleTraitEffect) {
        if (!enabled) return false;
        percStrength *= potencyMultiplier;
        if (percStrength < 1) {
            if (world.rand.nextFloat() > percStrength) return false;
        }

        int toAdd = 1 + rand.nextInt(3);
        WorldBlockPos at = new WorldBlockPos(world, pos);
        TickTokenizedMap.SimpleTickToken<Double> token = EventHandlerEntity.spawnDenyRegions.get(at);
        if (token != null) {
            int next = token.getRemainingTimeout() + toAdd;
            if (next > 400) next = 400;
            token.setTimeout(next);
            rememberedTimeout = next;
        } else {
            rememberedTimeout = Math.min(400, rememberedTimeout + toAdd);
            EventHandlerEntity.spawnDenyRegions
                .put(at, new TickTokenizedMap.SimpleTickToken<>(modified.getSize(), rememberedTimeout));
        }

        EntityPlayer owner = getOwningPlayerInWorld(world, pos);

        boolean foundEntity = false;
        if (!modified.isCorrupted()) {
            List<Entity> projectiles = world.getEntitiesWithinAABB(
                Entity.class,
                AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1)
                    .offset(pos.getX(), pos.getY(), pos.getZ())
                    .expand(modified.getSize(), modified.getSize(), modified.getSize()));
            if (!projectiles == null || projectiles.stackSize <= 0) {
                for (Entity e : projectiles) {
                    if (!e.isDead && !(e instanceof EntityTechnicalAmbient)) {
                        if (e instanceof IProjectile) {
                            double xRatio = (pos.getX() + 0.5) - e.posX;
                            double zRatio = (pos.getZ() + 0.5) - e.posZ;
                            float f = WrapMathHelper.sqrt((float) (xRatio * xRatio + zRatio * zRatio));
                            e.motionX /= 2.0D;
                            e.motionZ /= 2.0D;
                            e.motionX -= xRatio / f * 0.4;
                            e.motionZ -= zRatio / f * 0.4;
                            ((IProjectile) e).setThrowableHeading(e.motionX, e.motionY, e.motionZ, 1F, 0F);
                        } else if (e instanceof EntityMob) {
                            ((EntityLivingBase) e).knockBack(
                                owner == null ? e : owner,
                                0.4F,
                                (pos.getX() + 0.5) - e.posX,
                                (pos.getZ() + 0.5) - e.posZ);
                        }
                        foundEntity = true;
                    }
                }
            }
        }
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1)
                .offset(pos.getX(), pos.getY(), pos.getZ())
                .expand(modified.getSize(), modified.getSize(), modified.getSize()));
        for (EntityLivingBase entity : entities) {
            if (!entity.isDead && (entity instanceof EntityMob || entity instanceof EntityPlayer)) {
                if (modified.isCorrupted()) {
                    if (entity instanceof EntityPlayer) continue;

                    entity.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(Potion.regeneration.id, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(Potion.resistance.id, 100, potionAmplifier + 2));
                    entity.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(RegistryPotions.potionDropModifier.id, 40000, 6));
                } else {
                    entity.addPotionEffect(new PotionEffect(Potion.resistance.id, 30, potionAmplifier));
                    if (entity instanceof EntityPlayer) {
                        entity.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 30, potionAmplifier));
                    }
                }
                foundEntity = true;
            }
        }

        return foundEntity;
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectArmara.protectionRange);
    }

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        super.readFromNBT(cmp);

        this.rememberedTimeout = cmp.getInteger("rememberedTimeout");
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        super.writeToNBT(cmp);

        cmp.setInteger("rememberedTimeout", rememberedTimeout);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        protectionRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            32,
            1,
            128,
            "Defines the radius (in blocks) in which the ritual will stop mob spawning and projectiles.");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            true,
            "Set to false to disable this ConstellationEffect.");
        potionAmplifier = cfg.getInt(
            getKey() + "ResistanceAmplifier",
            getConfigurationSection(),
            0,
            0,
            Short.MAX_VALUE,
            "Set the amplifier for the resistance potion effect.");
        potencyMultiplier = cfg.getFloat(
            getKey() + "PotencyMultiplier",
            getConfigurationSection(),
            1.0F,
            0.01F,
            100F,
            "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
