/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import java.util.*;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.base.Plants;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.*;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key.KeyMantleFlight;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entities.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.migration.RayTraceResult;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktElytraCapeState;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.core.ASMCallHook;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerCapeEffects
 * Created by HellFirePvP
 * Date: 10.10.2017 / 00:34
 */
public class EventHandlerCapeEffects implements ITickHandler {

    private static final Random rand = new Random();
    public static EventHandlerCapeEffects INSTANCE = new EventHandlerCapeEffects();

    private static List<UUID> vicioMantleFlightPlayers = Lists.newArrayList();

    // Propagate player in tick for octans anti-knockback effect.
    public static EntityPlayer currentPlayerInTick = null;
    public static ItemStack currentStackInTick = null;

    // Prevent event overflow
    private static boolean discidiaChainingAttack = false;
    private static boolean evorsioChainingBreak = false;

    // To propagate elytra states
    private static boolean updateElytraBuffer = false;
    public static boolean inElytraCheck = false;

    private EventHandlerCapeEffects() {}

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        if (event.world.isRemote) return;
        if (evorsioChainingBreak) return;

        EntityPlayer pl = event.getPlayer();
        if (pl == null || !(pl instanceof EntityPlayerMP)) return;
        if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

        Block state = event.world.getBlock(event.x, event.y, event.z);
        ItemStack held = pl.getCurrentEquippedItem();

        CapeEffectPelotrio pel = ItemCape.getCapeEffect(pl, Constellations.pelotrio);
        if (pel != null) {
            // 1.7.10: Check if block requires pickaxe tool
            int metadata = event.world.getBlockMetadata(event.x, event.y, event.z);
            String harvestTool = state.getHarvestTool(metadata);
            boolean isPickaxe = "pickaxe".equalsIgnoreCase(harvestTool);
            if (!isPickaxe && !state.getMaterial()
                .isToolNotRequired()) {
                isPickaxe = Items.diamond_pickaxe.func_150897_b(state);
            }
            if (isPickaxe && !(held == null || held.stackSize <= 0)
                && held.getItem()
                    .getToolClasses(held)
                    .contains("pickaxe")) {
                if (rand.nextFloat() < pel.getChanceSpawnPick()) {
                    BlockPos at = new BlockPos(pl).add(0, 1, 0);
                    EntitySpectralTool esp = new EntitySpectralTool(
                        event.world,
                        at,
                        new ItemStack(Items.diamond_pickaxe),
                        EntitySpectralTool.ToolTask.createPickaxeTask());
                    event.world.spawnEntityInWorld(esp);
                    return;
                }
            }
            // 1.7.10: Simplified wood/leaves check
            boolean isWood = state == net.minecraft.init.Blocks.log || state == net.minecraft.init.Blocks.log2;
            boolean isLeaves = state == net.minecraft.init.Blocks.leaves || state == net.minecraft.init.Blocks.leaves2;
            if ((isWood || isLeaves) && !(held == null || held.stackSize <= 0)
                && held.getItem()
                    .getToolClasses(held)
                    .contains("axe")) {
                if (rand.nextFloat() < pel.getChanceSpawnAxe()) {
                    BlockPos at = new BlockPos(pl).add(0, 1, 0);
                    EntitySpectralTool esp = new EntitySpectralTool(
                        event.world,
                        at,
                        new ItemStack(Items.diamond_axe),
                        EntitySpectralTool.ToolTask.createLogTask());
                    event.world.spawnEntityInWorld(esp);
                }
            }
        }
        CapeEffectEvorsio ev = ItemCape.getCapeEffect(pl, Constellations.evorsio);
        ItemStack heldItem = pl.getHeldItem();
        if (ev != null && !(heldItem == null || heldItem.stackSize <= 0)
            && !heldItem.getItem()
                .getToolClasses(heldItem)
                .isEmpty()
            && !pl.isSneaking()) {
            evorsioChainingBreak = true;
            try {
                RayTraceResult rtr = MiscUtils.rayTraceLook(pl);
                if (rtr != null) {
                    int faceHit = rtr.sideHit;
                    if (faceHit >= 0 && faceHit < 6) {
                        EnumFacing enumFace = EnumFacing.getFront(faceHit);
                        BlockPos pos = new BlockPos(event.x, event.y, event.z);
                        // 1.7.10: Convert EnumFacing to ForgeDirection for Evorsio methods
                        net.minecraftforge.common.util.ForgeDirection dir = net.minecraftforge.common.util.ForgeDirection.getOrientation(
                            enumFace.ordinal());
                        if (enumFace == EnumFacing.UP || enumFace == EnumFacing.DOWN) {
                            ev.breakBlocksPlaneHorizontal((EntityPlayerMP) pl, dir, event.world, pos);
                        } else {
                            ev.breakBlocksPlaneVertical((EntityPlayerMP) pl, dir, event.world, pos);
                        }
                    }
                }
            } finally {
                evorsioChainingBreak = false;
            }
        }
    }

    @SubscribeEvent
    public void playerUpdatePre(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            currentPlayerInTick = event.player;
            // 1.7.10: armorInventory[2] = chestplate slot
            currentStackInTick = event.player.inventory.armorInventory[2];
        } else {
            currentPlayerInTick = null;
            currentStackInTick = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onHurt(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        if (event.entityLiving != null && event.entityLiving instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) event.entityLiving;
            CapeEffectDiscidia cd = ItemCape.getCapeEffect(pl, Constellations.discidia);
            if (cd != null) {
                cd.writeLastAttackDamage(event.ammount);
            }
            CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
            if (ca != null) {
                if (ca.shouldPreventDamage(event.source, false)) {
                    event.setCanceled(true);
                    return;
                }
            }
            CapeEffectBootes bo = ItemCape.getCapeEffect(pl, Constellations.bootes);
            if (bo != null && event.source.getSourceOfDamage() != null) {
                Entity source = event.source.getSourceOfDamage();
                if (source instanceof EntityLivingBase) {
                    bo.onPlayerDamagedByEntity(pl, (EntityLivingBase) source);
                }
            }
            if (event.source.isFireDamage()) {
                CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
                if (cf != null) {
                    cf.healFor(pl, event.ammount);
                    float mul = cf.getDamageMultiplier();
                    if (mul <= 0) {
                        event.setCanceled(true);
                    } else {
                        event.ammount = event.ammount * mul;
                    }
                }
            } else {
                CapeEffectHorologium horo = ItemCape.getCapeEffect(pl, Constellations.horologium);
                if (horo != null) {
                    horo.onHurt(pl);
                }
            }
        }
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (event.entity.worldObj.isRemote) return;

        DamageSource ds = event.source;
        if (ds.getSourceOfDamage() != null && ds.getSourceOfDamage() instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) ds.getSourceOfDamage();
            if (!(pl instanceof EntityPlayerMP)) return;
            if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

            CapeEffectEvorsio ev = ItemCape.getCapeEffect(pl, Constellations.evorsio);
            if (ev != null) {
                ev.deathAreaDamage(ds, event.entityLiving);
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (discidiaChainingAttack) return;
        if (event.entity.worldObj.isRemote) return;

        DamageSource ds = event.source;
        if (ds.getSourceOfDamage() != null && ds.getSourceOfDamage() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) ds.getSourceOfDamage();
            if (!(attacker instanceof EntityPlayerMP)) return;
            if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) attacker)) return;

            CapeEffectDiscidia cd = ItemCape.getCapeEffect(attacker, Constellations.discidia);
            if (cd != null) {
                double added = cd.getLastAttackDamage();

                discidiaChainingAttack = true;
                try {
                    DamageUtil
                        .attackEntityFrom(event.entityLiving, CommonProxy.dmgSourceStellar, (float) (added / 2.0F));
                    DamageUtil.attackEntityFrom(
                        event.entityLiving,
                        DamageSource.causePlayerDamage(attacker),
                        (float) (added / 2.0F));
                } finally {
                    discidiaChainingAttack = false;
                }
            }
            CapeEffectPelotrio pel = ItemCape.getCapeEffect(attacker, Constellations.pelotrio);
            ItemStack held = attacker.getHeldItem();
            if (pel != null && !(held == null || held.stackSize <= 0) && rand.nextFloat() < pel.getChanceSpawnSword()) {
                BlockPos at = new BlockPos(attacker).add(0, 1, 0);
                EntitySpectralTool esp = new EntitySpectralTool(
                    attacker.worldObj,
                    at,
                    new ItemStack(Items.diamond_sword),
                    EntitySpectralTool.ToolTask.createAttackTask());
                attacker.worldObj.spawnEntityInWorld(esp);
            }
        }
    }

    /**
     * 1.7.10: PlayerEvent.BreakSpeed hook
     */
    @SubscribeEvent
    public void onWaterBreak(PlayerEvent.BreakSpeed event) {
        EntityPlayer pl = event.entityPlayer;
        if (pl.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(pl)) {
            // Normally the break speed would be divided by 5 here in the actual logic.
            CapeEffectOctans ceo = ItemCape.getCapeEffect(pl, Constellations.octans);
            if (ceo != null) {
                // Revert speed back to what we think is original.
                // Might stack with others that implement it the same way.
                event.newSpeed = event.originalSpeed * 5;
            }
        }
    }

    @ASMCallHook
    public static float getWaterSlowDown(float oldSlowDown, EntityLivingBase base) {
        if (oldSlowDown < 1 && base instanceof EntityPlayer) {
            CapeEffectOctans ceo = ItemCape.getCapeEffect((EntityPlayer) base, Constellations.octans);
            if (ceo != null) {
                oldSlowDown = 0.95F; // Make sure it's not setting it to > 1 by itself.
            }
        }
        return oldSlowDown;
    }

    private void tickFornaxMelting(EntityPlayer pl) {
        if (pl.isBurning()) {
            CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
            if (cf != null) {
                cf.attemptMelt(pl);
            }
        }
    }

    private void tickAevitasEffect(EntityPlayer pl) {
        CapeEffectAevitas cd = ItemCape.getCapeEffect(pl, Constellations.aevitas);
        if (cd != null) {
            float potency = cd.getPotency();
            float range = cd.getRange();
            if (rand.nextFloat() < potency) {
                World w = pl.worldObj;
                AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(-range, -range, -range, range, range, range);
                bb = bb.offset(pl.posX, pl.posY, pl.posZ);
                // 1.7.10: Use IEntitySelector instead of EntitySelectors
                IEntitySelector selector = new IEntitySelector() {

                    @Override
                    public boolean isEntityApplicable(Entity entity) {
                        return entity.isEntityAlive();
                    }
                };
                List<EntityPlayer> players = w.selectEntitiesWithinAABB(EntityPlayer.class, bb, selector);
                for (EntityPlayer player : players) {
                    if (rand.nextFloat() <= cd.getFeedChancePerCycle()) {
                        player.heal(cd.getHealPerCycle());
                        player.getFoodStats()
                            .addStats(cd.getFoodLevelPerCycle(), cd.getFoodSaturationLevelPerCycle());
                    }
                }
            }
            if (rand.nextFloat() < cd.getTurnChance()) {
                int x = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                int y = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                int z = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                // 1.7.10: Entity doesn't have getPosition(), use BlockPos constructor
                BlockPos at = new BlockPos(pl).add(x, y, z);
                Block state = pl.worldObj.getBlock(at.getX(), at.getY(), at.getZ());
                if (Plants.matchesAny(state)) {
                    state = Plants.getAnyRandomState();
                    if (pl.worldObj.setBlock(at.getX(), at.getY(), at.getZ(), state, 0, 3)) {
                        PktParticleEvent ev = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                            at);
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(pl.worldObj, at, 16));
                    }
                } else {
                    CropHelper.GrowablePlant growable = CropHelper.wrapPlant(pl.worldObj, at);
                    if (growable != null) {
                        growable.tryGrow(pl.worldObj, rand);
                        PktParticleEvent ev = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                            at);
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(pl.worldObj, at, 16));
                    }
                }
            }
        }
    }

    private void tickArmaraWornEffect(EntityPlayer pl) {
        CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
        if (ca != null) {
            ca.wornTick();
        }
    }

    @SideOnly(Side.CLIENT)
    private void tickVicioClientEffect(EntityPlayer player) {
        if (player instanceof EntityPlayerSP) {
            EntityPlayerSP spl = (EntityPlayerSP) player;
            // 1.7.10: Check for KeyMantleFlight perk
            boolean hasFlightPerk = ResearchManager.getProgress(spl, Side.CLIENT)
                .hasPerkEffect((AbstractPerk p) -> p instanceof KeyMantleFlight);
            // 1.7.10: isInLava() doesn't exist, use isInsideOfMaterial(Material.lava)
            if (spl.movementInput.jump && !hasFlightPerk
                && !spl.onGround
                && spl.motionY < -0.5
                && !spl.capabilities.isFlying
                && !spl.isInWater()
                && !spl.isInsideOfMaterial(Material.lava)) {
                // 1.7.10: No Elytra, always send flying
                PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.setFlying());
                PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFallDistance());
            } else { // 1.7.10: Removed isElytraFlying check, always handle fall distance
                if (spl.capabilities.isFlying || hasFlightPerk
                    || spl.onGround
                    || spl.isInWater()
                    || spl.isInsideOfMaterial(Material.lava)) {
                    PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFlying());
                } else {
                    Vector3 mov = new Vector3(spl.motionX, 0, spl.motionZ);
                    if (mov.length() <= 0.4F && spl.motionY > 0.4F) {
                        PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFlying());
                    }
                }
            }
        }
    }

    private void tickOctansEffect(EntityPlayer pl) {
        CapeEffectOctans ceo = ItemCape.getCapeEffect(pl, Constellations.octans);
        if (ceo != null && pl.isInsideOfMaterial(Material.water)) {
            if (pl.getAir() < 300) {
                pl.setAir(300);
            }
            ceo.onWaterHealTick(pl);
        }
    }

    private void tickBootesEffect(EntityPlayer pl) {
        CapeEffectBootes ceo = ItemCape.getCapeEffect(pl, Constellations.bootes);
        if (ceo != null) {
            ceo.onPlayerTick(pl);
        }
    }

    private void tickVicioEffect(EntityPlayer pl) {
        if (!(pl instanceof EntityPlayerMP)) {
            return;
        }
        PlayerProgress prog = ResearchManager.getProgress(pl, Side.SERVER);
        // 1.7.10: Check for KeyMantleFlight perk
        if (!prog.hasPerkEffect((AbstractPerk p) -> p instanceof KeyMantleFlight)) {
            if (vicioMantleFlightPlayers.contains(pl.getUniqueID())) {
                if (pl.capabilities.isCreativeMode) {
                    pl.capabilities.allowFlying = true;
                } else {
                    pl.capabilities.allowFlying = false;
                    pl.capabilities.isFlying = false;
                }
                pl.sendPlayerAbilities();
                vicioMantleFlightPlayers.remove(pl.getUniqueID());
            }
            return;
        }

        CapeEffectVicio ceo = ItemCape.getCapeEffect(pl, Constellations.vicio);
        if (ceo != null) {
            if (!vicioMantleFlightPlayers.contains(pl.getUniqueID())) {
                vicioMantleFlightPlayers.add(pl.getUniqueID());
            }
            if (!pl.capabilities.allowFlying) {
                pl.capabilities.allowFlying = true;
                pl.sendPlayerAbilities();
            }
        } else if (vicioMantleFlightPlayers.contains(pl.getUniqueID())) {
            if (pl.capabilities.isCreativeMode) {
                pl.capabilities.allowFlying = true;
            } else {
                pl.capabilities.allowFlying = false;
                pl.capabilities.isFlying = false;
            }
            pl.sendPlayerAbilities();
            vicioMantleFlightPlayers.remove(pl.getUniqueID());
        }
    }

    @ASMCallHook
    public static void updateElytraEventPre(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            CapeEffectVicio vic = ItemCape.getCapeEffect((EntityPlayer) entity, Constellations.vicio);
            if (vic != null) {
                updateElytraBuffer = entity.getFlag(7);
                inElytraCheck = true;
            }
        }
    }

    @ASMCallHook
    public static void updateElytraEventPost(EntityLivingBase entity) {
        inElytraCheck = false;
        if (entity instanceof EntityPlayer && updateElytraBuffer) {
            CapeEffectVicio vic = ItemCape.getCapeEffect((EntityPlayer) entity, Constellations.vicio);
            if (vic != null) {
                boolean current = entity.getFlag(7);
                // So the state from true before has now changed to false.
                // We need to check if the item not being an elytra is responsible for that.
                if (!current) {
                    if (!((EntityPlayer) entity).onGround && !entity.isRiding()) {
                        entity.setFlag(7, true);
                    }
                }

                // Vector3 mV = new Vector3(entity.motionX, entity.motionY, entity.motionZ).normalize().multiply(0.65F);
                // entity.motionX += mV.getX() * 0.1D + (mV.getX() * 1.5D - entity.motionX) * 0.5D;
                // entity.motionY += mV.getY() * 0.1D + (mV.getY() * 1.5D - entity.motionY) * 0.5D;
                // entity.motionZ += mV.getZ() * 0.1D + (mV.getZ() * 1.5D - entity.motionZ) * 0.5D;
                entity.motionX *= 1.006F;
                entity.motionY *= 1.006F;
                entity.motionZ *= 1.006F;
            }
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        switch (type) {
            case PLAYER:
                EntityPlayer pl = (EntityPlayer) context[0];
                Side side = (Side) context[1];
                if (side == Side.SERVER) {
                    if (!(pl instanceof EntityPlayerMP)) return;
                    if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

                    tickAevitasEffect(pl);
                    tickFornaxMelting(pl);
                    tickArmaraWornEffect(pl);
                    tickOctansEffect(pl);
                    tickBootesEffect(pl);
                    tickVicioEffect(pl);
                } else if (side == Side.CLIENT) {
                    CapeArmorEffect cae = ItemCape.getCapeEffect(pl);
                    if (cae != null) {
                        cae.playActiveParticleTick(pl);
                    }
                    CapeEffectVicio vic = ItemCape.getCapeEffect(pl, Constellations.vicio);
                    if (vic != null) {
                        tickVicioClientEffect(pl);
                    }
                    CapeEffectLucerna luc = ItemCape.getCapeEffect(pl, Constellations.lucerna);
                    if (luc != null) {
                        luc.playClientHighlightTick(pl);
                    }
                    CapeEffectMineralis min = ItemCape.getCapeEffect(pl, Constellations.mineralis);
                    if (min != null) {
                        min.playClientHighlightTick(pl);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Cape-EventHandler";
    }

}
