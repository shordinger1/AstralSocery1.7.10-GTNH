/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.auxiliary.StarlightNetworkDebugHandler;
import hellfirepvp.astralsorcery.common.auxiliary.SwordSharpenHelper;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationDraconicEvolution;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.tool.wand.WandAugment;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktClearBlockStorageStack;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import hellfirepvp.astralsorcery.common.util.data.TimeoutList;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerEntity
 * Created by HellFirePvP
 * Date: 01.08.2017 / 20:28
 */
public class EventHandlerEntity {

    private static final Random rand = new Random();
    private static final Color discidiaWandColor = new Color(0x880100);

    public static int spawnSkipId = -1;
    public static TickTokenizedMap<WorldBlockPos, TickTokenizedMap.SimpleTickToken<Double>> spawnDenyRegions = new TickTokenizedMap<>(
        TickEvent.Type.SERVER);
    public static TimeoutList<EntityPlayer> invulnerabilityCooldown = new TimeoutList<>(null, TickEvent.Type.SERVER);
    public static TimeoutList<EntityPlayer> ritualFlight = new TimeoutList<>(player -> {
        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).theItemInWorldManager.getGameType()
            .isSurvivalOrAdventure()) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }, TickEvent.Type.SERVER);
    public static Map<Integer, EntityAttackStack> attackStack = new HashMap<>();

    @SubscribeEvent
    public void onTarget(LivingSetAttackTargetEvent event) {
        EntityLivingBase living = event.target;
        if (living != null && !living.isDead && living instanceof EntityPlayer) {
            if (invulnerabilityCooldown.contains((EntityPlayer) living)) {
                event.entityLiving.setRevengeTarget(null);
                if (event.entityLiving instanceof EntityLiving) {
                    ((EntityLiving) event.entityLiving).setAttackTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSleep(PlayerSleepInBedEvent event) {
        WorldSkyHandler wsh = ConstellationSkyHandler.getInstance()
            .getWorldHandler(event.entityPlayer.worldObj);
        if (wsh != null && wsh.dayOfSolarEclipse && wsh.solarEclipse) {
            if (event.result == null) {
                event.result = EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
            }
        }
    }

    // EntityAreaEffectCloud doesn't exist in 1.7.10 (introduced in 1.9)
    // This event handler is disabled for 1.7.10 compatibility
    /*
     * @SubscribeEvent
     * public void onSpawnDropCloud(EntityJoinWorldEvent event) {
     * if (event.getEntity() instanceof EntityAreaEffectCloud && MiscUtils.iterativeSearch(
     * ((EntityAreaEffectCloud) event.getEntity()).effects,
     * (pEffect) -> pEffect.getPotion()
     * .equals(RegistryPotions.potionDropModifier))
     * != null) {
     * event.setCanceled(true);
     * }
     * }
     */

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAttack(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        DamageSource source = event.source;
        if (source.getEntity() != null) {
            EntityLivingBase entitySource = null;
            if (source.getEntity() instanceof EntityLivingBase) {
                entitySource = (EntityLivingBase) source.getEntity();
            } else if (source.getEntity() instanceof EntityArrow) {
                Entity shooter = ((EntityArrow) source.getEntity()).shootingEntity;
                if (shooter != null && shooter instanceof EntityLivingBase) {
                    entitySource = (EntityLivingBase) shooter;
                }
            }
            if (entitySource != null) {
                WandAugment foundAugment = null;
                ItemStack stack = entitySource instanceof EntityPlayer
                    ? ((EntityPlayer) entitySource).getCurrentEquippedItem()
                    : null;
                if (!(stack == null || stack.stackSize <= 0) && stack.getItem() instanceof ItemWand) {
                    foundAugment = ItemWand.getAugment(stack);
                }
                // 1.7.10: No off-hand, skip offhand check
                if (foundAugment != null && foundAugment.equals(WandAugment.DISCIDIA)) {
                    EntityAttackStack attack = attackStack.get(entitySource.getEntityId());
                    if (attack == null) {
                        attack = new EntityAttackStack();
                        attackStack.put(entitySource.getEntityId(), attack);
                    }
                    EntityLivingBase entity = event.entityLiving;
                    float multiplier = attack.getAndUpdateMultipler(entity);
                    event.ammount = event.ammount * (1F + multiplier); // Note: typo "ammount" in 1.7.10
                    PktParticleEvent ev = new PktParticleEvent(
                        PktParticleEvent.ParticleEventType.DISCIDIA_ATTACK_STACK,
                        entity.posX,
                        entity.posY,
                        entity.posZ);
                    ev.setAdditionalData(multiplier);
                    PacketChannel.CHANNEL.sendToAllAround(
                        ev,
                        PacketChannel.pointFromPos(event.entityLiving.worldObj, new BlockPos(event.entityLiving), 64));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDeathInform(LivingDeathEvent event) {
        attackStack.remove(event.entityLiving.getEntityId());
    }

    // LivingDestroyBlockEvent doesn't exist in 1.7.10
    /*
     * @SubscribeEvent
     * public void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
     * if (event.entityLiving.isPotionActive(RegistryPotions.potionTimeFreeze)) {
     * event.setCanceled(true);
     * }
     * }
     */

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        if (event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote
            || !(event.entityLiving.worldObj instanceof WorldServer)) {
            return;
        }
        if (event.entityLiving instanceof EntityPlayer || !(event.entityLiving instanceof EntityLiving)) return;
        EntityLiving el = (EntityLiving) event.entityLiving;
        WorldServer ws = (WorldServer) el.worldObj;

        PotionEffect pe = el.getActivePotionEffect(RegistryPotions.potionDropModifier);
        if (pe != null) {
            // 1.7.10: removePotionEffect takes potion ID, not Potion object
            el.removePotionEffect(RegistryPotions.potionDropModifier.getId());
            int ampl = pe.getAmplifier();
            if (ampl == 0) {
                event.drops.clear();
            }
        }
    }

    // Just... do the clear.
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLeftClickBlock(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            ItemStack held = event.entityPlayer.getHeldItem();
            if (!event.world.isRemote && !(held == null || held.stackSize <= 0)
                && held.getItem() instanceof ItemBlockStorage) {
                ItemBlockStorage.tryClearContainerFor(event.entityPlayer);
            }
        }
    }

    // Send clear to server
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLeftClickAir(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            ItemStack held = event.entityPlayer.getHeldItem();
            if (!(held == null || held.stackSize <= 0) && held.getItem() instanceof ItemBlockStorage) {
                PacketChannel.CHANNEL.sendToServer(new PktClearBlockStorageStack());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickDebug(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            // 1.7.10: Use capabilities.isCreativeMode instead of isCreative()
            if (event.entityPlayer.capabilities.isCreativeMode && !event.world.isRemote) {
                if (StarlightNetworkDebugHandler.INSTANCE
                    .beginDebugFor(event.world, new BlockPos(event.x, event.y, event.z), event.entityPlayer)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDamage(LivingHurtEvent event) {
        EntityLivingBase living = event.entityLiving;
        if (living.worldObj.isRemote) return;

        if (!living.isDead && living instanceof EntityPlayer) {
            if (invulnerabilityCooldown.contains((EntityPlayer) living)) {
                event.setCanceled(true);
                return;
            }
        }

        DamageSource source = event.source;
        if (Mods.DRACONICEVOLUTION.isPresent()) {
            // 1.7.10: armorInventory[2] = chestplate slot
            ItemStack chest = living instanceof EntityPlayer ? ((EntityPlayer) living).inventory.armorInventory[2]
                : null;
            if (!(chest == null || chest.stackSize <= 0) && chest.getItem() instanceof ItemCape
                && ModIntegrationDraconicEvolution.isChaosDamage(source)) {
                if (living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.isCreativeMode) {
                    event.setCanceled(true);
                    return;
                }
                event.ammount = event.ammount * (1F - Config.capeChaosResistance);
                if (event.ammount <= 1E-4) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        lblIn: if (source.getEntity() != null) {
            EntityPlayer p;
            if (source.getEntity() instanceof EntityPlayer) {
                p = (EntityPlayer) source.getEntity();
            } else if (source.getEntity() instanceof EntityArrow) {
                Entity shooter = ((EntityArrow) source.getEntity()).shootingEntity;
                if (shooter != null && shooter instanceof EntityPlayer) {
                    p = (EntityPlayer) shooter;
                } else {
                    break lblIn;
                }
            } else {
                break lblIn;
            }
            ItemStack held = p.getHeldItem();
            if (SwordSharpenHelper.isSwordSharpened(held)) {
                // YEEEAAAA i know this flat multiplies all damage.. but w/e..
                // There's no great way to test for item here.
                event.ammount = event.ammount * (1 + ((float) Config.swordSharpMultiplier));
            }
        }
        EntityLivingBase entity = event.entityLiving;
        // 1.7.10: Only EntityPlayer has isUsingItem() in 1.7.10, not EntityLivingBase
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isUsingItem()) {
            ItemStack active = ((EntityPlayer) entity).getItemInUse();
            if (!(active == null || active.stackSize <= 0) && active.getItem() instanceof ItemWand) {
                WandAugment wa = ItemWand.getAugment(active);
                if (wa != null && wa.equals(WandAugment.ARMARA)) {
                    // 1.7.10: Use Potion.resistance.id and Potion.field_76444_x (absorption)
                    PotionEffect potion = new PotionEffect(net.minecraft.potion.Potion.resistance.id, 100, 0);
                    if (entity.isPotionApplicable(potion)) {
                        entity.addPotionEffect(potion);
                    }
                    potion = new PotionEffect(net.minecraft.potion.Potion.field_76444_x.id, 100, 1);
                    if (entity.isPotionApplicable(potion)) {
                        entity.addPotionEffect(potion);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onSpawnTest(LivingSpawnEvent.CheckSpawn event) {
        if (event.getResult() == Event.Result.DENY) return; // Already denied anyway.
        if (event.world.isRemote) return;
        // 1.7.10: isSpawner() method doesn't exist in CheckSpawn event
        // if (event.isSpawner()) return; // FINE, i'll allow spawners.

        EntityLivingBase toTest = event.entityLiving;
        if (spawnSkipId != -1 && toTest.getEntityId() == spawnSkipId) {
            return;
        }

        Vector3 at = Vector3.atEntityCorner(toTest);
        boolean mayDeny = Config.doesMobSpawnDenyDenyEverything
            || toTest.isCreatureType(EnumCreatureType.monster, false);
        if (mayDeny) {
            for (Map.Entry<WorldBlockPos, TickTokenizedMap.SimpleTickToken<Double>> entry : spawnDenyRegions
                .entrySet()) {
                if (!entry.getKey()
                    .getWorld()
                    .equals(toTest.worldObj)) continue;
                if (at.distance(entry.getKey()) <= entry.getValue()
                    .getValue()) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playDiscidiaStackAttackEffects(PktParticleEvent pkt) {
        Vector3 at = pkt.getVec();
        World w = Minecraft.getMinecraft().theWorld;
        EntityLivingBase found = null;
        if (w != null) {
            EntityLivingBase e = EntityUtils.selectClosest(
                w.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    AxisAlignedBB.getBoundingBox(
                        at.getX() - 0.5,
                        at.getY() - 0.5,
                        at.getZ() - 0.5,
                        at.getX() + 0.5,
                        at.getY() + 0.5,
                        at.getZ() + 0.5)),
                (ent) -> ent.getDistance(at.getX(), at.getY(), at.getZ()));
            if (e != null) {
                found = e;
            }
        }
        if (found != null) {
            // 1.7.10: use entity.boundingBox instead of getEntityBoundingBox()
            AxisAlignedBB box = found.boundingBox;
            for (int i = 0; i < 24; i++) {
                if (rand.nextFloat() < pkt.getAdditionalData()) {
                    Vector3 pos = new Vector3(
                        box.minX + ((box.maxX - box.minX) * rand.nextFloat()),
                        box.minY + ((box.maxY - box.minY) * rand.nextFloat()),
                        box.minZ + ((box.maxZ - box.minZ) * rand.nextFloat()));
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
                    p.setColor(discidiaWandColor)
                        .setMaxAge(25 + rand.nextInt(10));
                    p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                        .setAlphaMultiplier(1F);
                    p.gravity(0.004)
                        .scale(0.15F + rand.nextFloat() * 0.1F);
                    Vector3 motion = new Vector3();
                    MiscUtils.applyRandomOffset(motion, rand, 0.03F);
                    p.motion(motion.getX(), motion.getY(), motion.getZ());
                }
            }
        }
    }

    private static class EntityAttackStack {

        private static long stackMsDuration = 5000;

        private int entityStackId = -1;
        private long lastStackMs = 0;
        private int stack = 0;

        public float getMultiplier(Entity attackedEntity) {
            return getMultiplier(attackedEntity.getEntityId());
        }

        public float getMultiplier(int attackedEntityId) {
            if (entityStackId != attackedEntityId) {
                return 0F;
            }
            return (((float) stack) / ((float) Config.discidiaStackCap)) * Config.discidiaStackMultiplier;
        }

        public float getAndUpdateMultipler(Entity attackedEntity) {
            return getAndUpdateMultipler(attackedEntity.getEntityId());
        }

        public float getAndUpdateMultipler(int attackedEntityId) {
            if (attackedEntityId != entityStackId) {
                entityStackId = attackedEntityId;
                lastStackMs = System.currentTimeMillis();
                stack = 0;
            } else {
                long current = System.currentTimeMillis();
                long diff = current - lastStackMs;
                lastStackMs = current;
                if (diff < stackMsDuration) {
                    stack = WrapMathHelper.clamp(stack + 1, 0, Config.discidiaStackCap);
                } else {
                    stack = WrapMathHelper.clamp(stack - ((int) (diff / stackMsDuration)), 0, Config.discidiaStackCap);
                }
            }
            return (((float) stack) / ((float) Config.discidiaStackCap)) * Config.discidiaStackMultiplier;
        }

    }

}
