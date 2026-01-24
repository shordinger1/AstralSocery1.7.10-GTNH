/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import hellfirepvp.astralsorcery.common.base.RockCrystalHandler;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.item.knowledge.ItemFragmentCapsule;
import hellfirepvp.astralsorcery.common.item.knowledge.ItemKnowledgeFragment;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.tool.wand.WandAugment;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktCraftingTableFix;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.world.util.WorldEventNotifier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerServer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:09
 */
public class EventHandlerServer {

    private static final Random rand = new Random();

    @SubscribeEvent
    public void attachPlague(AttachCapabilitiesEvent<Entity> event) {
        // if(event.getObject() instanceof EntityLivingBase) {
        // event.addCapability(SpellPlague.CAPABILITY_NAME, new SpellPlague.Provider());
        // }
    }

    /*
     * @SubscribeEvent
     * public void onHarvestSpeedCheck(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
     * EntityPlayer harvester = event.entityPlayer;
     * if (harvester != null) {
     * PlayerProgress prog = ResearchManager.getProgress(harvester, harvester.worldObj.isRemote ? Side.CLIENT :
     * Side.SERVER);
     * if (prog != null) {
     * Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
     * for (ConstellationPerk perk : perks.keySet()) {
     * if (!prog.isPerkActive(perk)) continue;
     * if (perk.mayExecute(ConstellationPerk.Target.PLAYER_HARVEST_SPEED)) {
     * BlockPos p = event.getLocationPos();
     * event.setNewSpeed(perk.onHarvestSpeed(harvester, event.getState(), (p == null || p.getY() < 0) ? null : p,
     * event.getNewSpeed()));
     * }
     * }
     * }
     * }
     * }
     * @SubscribeEvent
     * public void onHarvestTypeCheck(net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck event) {
     * EntityPlayer harvester = event.entityPlayer;
     * if (harvester != null) {
     * PlayerProgress prog = ResearchManager.getProgress(harvester, harvester.worldObj.isRemote ? Side.CLIENT :
     * Side.SERVER);
     * if (prog != null) {
     * Map<ConstellationPerk, Integer> perks = prog.getAppliedPerks();
     * for (ConstellationPerk perk : perks.keySet()) {
     * if (!prog.isPerkActive(perk)) continue;
     * if (perk.mayExecute(ConstellationPerk.Target.PLAYER_HARVEST_TYPE)) {
     * if(perk.onCanHarvest(harvester, harvester.getHeldItem(), event.getTargetBlock(), event.canHarvest())) {
     * event.setCanHarvest(true);
     * }
     * }
     * }
     * }
     * }
     * }
     */

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        EntityItem ei = event.getItem();
        if (ei.getEntityItem()
            .getItem() instanceof ItemFragmentCapsule
            || ei.getItem()
                .getItem() instanceof ItemKnowledgeFragment) {
            EntityPlayer pickingUp = event.entityPlayer;
            if (!pickingUp.worldObj.isRemote) {
                String playerName = ei.getOwner();
                if (playerName == null) {
                    playerName = ei.getThrower();
                }
                if (playerName != null && !playerName.equals(pickingUp.getName())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    // 1.7.10: PlayerContainerEvent.Open doesn't exist, PlayerOpenContainerEvent doesn't expose the container
    // This event is disabled for 1.7.10 compatibility
    // @SubscribeEvent
    // public void onContainerOpen(PlayerOpenContainerEvent event) {
    // // 1.7.10: Can't access the container from this event
    // }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDeath(LivingDeathEvent event) {
        if (event.getSource()
            .canHarmInCreative()) {
            return;
        }
        if (phoenixProtect(event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    private boolean phoenixProtect(EntityLivingBase entity) {
        PotionEffect pe = entity.getActivePotionEffect(RegistryPotions.potionCheatDeath);
        if (pe != null) {
            int level = pe.getAmplifier();
            phoenixEffects(entity, level);
            return true;
        }
        return false;
    }

    private void phoenixEffects(EntityLivingBase entity, int level) {
        entity.setHealth(Math.min(entity.getMaxHealth(), 6 + level * 2));
        entity.addPotionEffect(new PotionEffect(Potion.regeneration, 200, 2, false, false));
        entity.addPotionEffect(new PotionEffect(Potion.fireResistance, 500, 1, false, false));
        List<EntityLivingBase> others = entity.worldObj.getEntitiesWithinAABB(
            EntityLivingBase.class,
            entity.getEntityBoundingBox()
                .grow(3),
            (e) -> !e.isDead && e != entity);
        for (EntityLivingBase lb : others) {
            lb.setFire(16);
            lb.knockBack(entity, 2F, lb.posX - entity.posX, lb.posZ - entity.posZ);
        }
        PktParticleEvent ev = new PktParticleEvent(
            PktParticleEvent.ParticleEventType.PHOENIX_PROC,
            new Vector3(entity.posX, entity.posY, entity.posZ));
        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(entity.world, new BlockPos(entity), 32));

        // In 1.7.10, use MinecraftServer.getServer() instead of entity.getServer()
        MinecraftServer server = MinecraftServer.getServer();
        server.addScheduledTask(() -> entity.removePotionEffect(RegistryPotions.potionCheatDeath));
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        // 1.7.10: Use event.x, event.y, event.z directly instead of BlockPos
        Block at = event.world.getBlock(event.x, event.y, event.z);
        if (at instanceof BlockMachine) {
            if (((BlockMachine) at).handleSpecificActivateEvent(event)) {
                event.setCancellationResult(true);
                event.setCanceled(true);
                return;
            }
        }

        ItemStack hand = event.itemStack;
        if ((hand == null || hand.stackSize <= 0)) return;
        if (hand.getItem() instanceof ISpecialInteractItem) {
            ISpecialInteractItem i = (ISpecialInteractItem) hand.getItem();
            BlockPos pos = new BlockPos(event.x, event.y, event.z);
            if (i.needsSpecialHandling(event.world, pos, event.entityPlayer, hand)) {
                if (i.onRightClick(event.world, pos, event.entityPlayer, event.face, hand)) {
                    event.setCanceled(true);
                    event.setCancellationResult(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRightClickLast(PlayerInteractEvent.RightClickBlock event) {
        if (!event.world.isRemote) {
            BlockPos pos = new BlockPos(event.x, event.y, event.z);
            Block interacted = event.world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (interacted instanceof BlockWorkbench) {
                PktCraftingTableFix fix = new PktCraftingTableFix(pos);
                PacketChannel.CHANNEL.sendTo(fix, (EntityPlayerMP) event.entityPlayer);
            }
        }
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        // In 1.7.10, player doesn't have getServer() - this is server-side only
        ResearchManager.informCraftingGridCompletion(event.player, event.crafting);
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            ResearchManager.loadPlayerKnowledge((EntityPlayerMP) event.player);
            ResearchManager.savePlayerKnowledge((EntityPlayerMP) event.player);
        }
        if (Config.giveJournalFirst) {
            EntityPlayer pl = event.player;
            if (!ResearchManager.getProgress(pl)
                .didReceiveTome() && pl.inventory.addItemStackToInventory(new ItemStack(ItemsAS.journal))) {
                ResearchManager.setTomeReceived(pl);
            }
        }
    }

    @SubscribeEvent
    public void onLoad(WorldEvent.Load event) {
        event.world.addEventListener(new WorldEventNotifier());

        GameRules rules = event.world.getGameRules();
        if (!rules.hasRule(MiscUtils.GAMERULE_SKIP_SKYLIGHT_CHECK)) {
            rules.addGameRule(MiscUtils.GAMERULE_SKIP_SKYLIGHT_CHECK, "false", GameRules.ValueType.BOOLEAN_VALUE);
        }
    }

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        if (event.world.isRemote || !event.getChunk()
            .isTerrainPopulated()) return;
        if (!Loader.instance()
            .hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
            return; // Thanks BuildCraft.
        }
        BlockPos at = new BlockPos(event.x, event.y, event.z);
        WorldNetworkHandler.getNetworkHandler(event.world)
            .informBlockChange(at);
        if (event.getNewBlock()
            .equals(Blocks.CRAFTING_TABLE)) {
            if (!event.getOldBlock()
                .equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.world)
                    .attemptAutoLinkTo(at);
            }
        }
        if (event.getOldBlock()
            .equals(Blocks.CRAFTING_TABLE)) {
            if (!event.getNewBlock()
                .equals(Blocks.CRAFTING_TABLE)) {
                WorldNetworkHandler.getNetworkHandler(event.world)
                    .removeAutoLinkTo(at);
            }
        }
        if (event.getNewBlock()
            .equals(BlocksAS.blockAltar)) {
            if (!event.getOldBlock()
                .equals(BlocksAS.blockAltar)) {
                WorldNetworkHandler.getNetworkHandler(event.world)
                    .attemptAutoLinkTo(at);
            }
        }
        if (event.getOldBlock()
            .equals(BlocksAS.blockAltar)) {
            if (!event.getNewBlock()
                .equals(BlocksAS.blockAltar)) {
                WorldNetworkHandler.getNetworkHandler(event.world)
                    .removeAutoLinkTo(at);
            }
        }
        if (event.getOldBlock()
            .equals(BlocksAS.customOre)) {
            Block oldState = event.getOldState();
            if (oldState.getValue(BlockCustomOre.ORE_TYPE)
                .equals(BlockCustomOre.OreType.ROCK_CRYSTAL)) {
                RockCrystalHandler.INSTANCE.removeOre(event.world, new BlockPos(event.x, event.y, event.z), true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        if (event.world.isRemote) return;
        BlockPos at = new BlockPos(event.x, event.y, event.z);

        ItemStack heldStack = MiscUtils
            .getMainOrOffHand(event.entityPlayer, ItemsAS.wand, stack -> ItemWand.getAugment(stack) != null);
        if (heldStack != null && ItemWand.getAugment(heldStack.value) == WandAugment.EVORSIO) {
            if (rand.nextFloat() < Config.evorsioEffectChance) {
                World w = event.world;
                Block stateAt = w.getBlock(at);
                BlockPos playerPos = new BlockPos(event.entityPlayer);
                BlockArray foundBlocks = BlockDiscoverer.searchForBlocksAround(
                    w,
                    at,
                    2,
                    ((world, pos,
                        state) -> (pos.getY() >= playerPos.getY() && state.equals(stateAt)
                            && state.getBlockHardness(world, pos) >= 0
                            && world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) == null
                            && !world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())
                            && world.getBlock(pos.getX(), pos.getY(), pos.getZ())
                                .canHarvestBlock(world, pos, event.entityPlayer))));
                for (BlockPos pos : foundBlocks.getPattern()
                    .keySet()) {
                    Block atState = w.getBlock(pos);
                    if (w.setBlock(pos.getX(), pos.getY(), pos.getZ(), BlocksAS.blockFakeTree, 0, 3)) {
                        TileFakeTree tt = MiscUtils.getTileAt(w, pos, TileFakeTree.class, true);
                        if (tt != null) {
                            tt.setupTile(event.entityPlayer, event.entityPlayer.getCurrentEquippedItem(), atState);
                        } else {
                            w.setBlock(pos.getX(), pos.getY(), pos.getZ(), atState, 0, 3);
                        }
                    }
                }
                if (foundBlocks.getPattern()
                    .containsKey(at)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && !event.isSilkTouching()) {
            ItemStack main = event.getHarvester()
                .getCurrentEquippedItem();
            if (!(main == null || main.stackSize <= 0)) {
                if (EnchantmentHelper.getEnchantmentLevel(EnchantmentsAS.enchantmentScorchingHeat, main) > 0) {
                    int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune, main);
                    ArrayList<ItemStack> newStacks = new ArrayList<>();
                    Iterator<ItemStack> iterator = event.getDrops()
                        .iterator();
                    while (iterator.hasNext()) {
                        ItemStack stack = iterator.next();
                        if ((stack == null || stack.stackSize <= 0)) {
                            continue;
                        }
                        ItemStack out = FurnaceRecipes.smelting()
                            .getSmeltingResult(stack);
                        if ((out == null || out.stackSize <= 0)) {
                            continue;
                        }
                        ItemStack furnaced = ItemUtils.copyStackWithSize(out, 1);
                        iterator.remove();
                        newStacks.add(furnaced);
                        furnaced.onCrafting(event.world, event.getHarvester(), 1);
                        FMLCommonHandler.instance()
                            .firePlayerSmeltedEvent(event.getHarvester(), furnaced);
                        if (fortuneLvl > 0 && !(out.getItem() instanceof ItemBlock)) {
                            for (int i = 0; i < fortuneLvl; i++) {
                                if (rand.nextFloat() < 0.5F) {
                                    newStacks.add(ItemUtils.copyStackWithSize(out, 1));
                                }
                            }
                        }
                    }
                    event.getDrops()
                        .addAll(newStacks);
                }
            }
        }
    }

}
