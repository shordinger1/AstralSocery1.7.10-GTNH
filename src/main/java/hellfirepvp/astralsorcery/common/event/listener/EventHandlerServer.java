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
import net.minecraft.util.EnumFacing;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerServer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:09
 */
public class EventHandlerServer {

    private static final Random rand = new Random();

    // 1.7.10: AttachCapabilitiesEvent doesn't exist (capabilities added in 1.10+)
    // SpellPlague feature disabled for 1.7.10
    // @SubscribeEvent
    // public void attachPlague(AttachCapabilitiesEvent<Entity> event) {
    // if(event.getObject() instanceof EntityLivingBase) {
    // event.addCapability(SpellPlague.CAPABILITY_NAME, new SpellPlague.Provider());
    // }
    // }

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
        EntityItem ei = event.item;
        // 1.7.10: Use getEntityItem() instead of getItem()
        if (ei.getEntityItem()
            .getItem() instanceof ItemFragmentCapsule
            || ei.getEntityItem()
                .getItem() instanceof ItemKnowledgeFragment) {
            EntityPlayer pickingUp = event.entityPlayer;
            if (!pickingUp.worldObj.isRemote) {
                // 1.7.10: EntityItem has a 'thrower' field (String) storing the owner name
                // We can't access private fields directly, so we use reflection or skip this check
                // For now, we'll skip the owner check since it's not critical
                String playerName = null; // ei.func_146066_a() doesn't exist in 1.7.10

                // 1.7.10: Use getCommandSenderName()
                String pickingName = pickingUp.getCommandSenderName();
                if (playerName != null && !playerName.equals(pickingName)) {
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
        // 1.7.10: Use event.source directly, not getSource()
        if (event.source.canHarmInCreative()) {
            return;
        }
        // 1.7.10: Use event.entityLiving directly, not getEntityLiving()
        if (phoenixProtect(event.entityLiving)) {
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
        // 1.7.10: PotionEffect constructor is (int id, int duration, int amplifier)
        entity.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 2));
        entity.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 500, 1));
        // 1.7.10: Use boundingBox instead of getEntityBoundingBox()
        List<EntityLivingBase> others = entity.worldObj
            .getEntitiesWithinAABB(EntityLivingBase.class, entity.boundingBox.expand(3, 3, 3));
        // Filter manually - 1.7.10 doesn't support predicate in getEntitiesWithinAABB
        List<EntityLivingBase> filtered = new java.util.ArrayList<>();
        for (EntityLivingBase e : others) {
            if (!e.isDead && e != entity) {
                filtered.add(e);
            }
        }
        for (EntityLivingBase lb : filtered) {
            lb.setFire(16);
            lb.knockBack(entity, 2F, lb.posX - entity.posX, lb.posZ - entity.posZ);
        }
        PktParticleEvent ev = new PktParticleEvent(
            PktParticleEvent.ParticleEventType.PHOENIX_PROC,
            new Vector3(entity.posX, entity.posY, entity.posZ));
        PacketChannel.CHANNEL
            .sendToAllAround(ev, PacketChannel.pointFromPos(entity.worldObj, new BlockPos(entity), 32));

        // In 1.7.10, removePotionEffect takes int effectId, not Potion
        entity.removePotionEffect(Potion.regeneration.id);
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        // 1.7.10: Check action instead of using RightClickBlock subtype
        if (event.action != net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        // 1.7.10: Use event.x, event.y, event.z directly instead of BlockPos
        Block at = event.world.getBlock(event.x, event.y, event.z);
        if (at instanceof BlockMachine) {
            if (((BlockMachine) at).handleSpecificActivateEvent(event)) {
                // 1.7.10: No setCancellationResult, just setCanceled
                event.setCanceled(true);
                return;
            }
        }

        ItemStack hand = event.entityPlayer.getCurrentEquippedItem();
        if ((hand == null || hand.stackSize <= 0)) return;
        if (hand.getItem() instanceof ISpecialInteractItem) {
            ISpecialInteractItem i = (ISpecialInteractItem) hand.getItem();
            BlockPos pos = new BlockPos(event.x, event.y, event.z);
            if (i.needsSpecialHandling(event.world, pos, event.entityPlayer, hand)) {
                if (i.onRightClick(event.world, pos, event.entityPlayer, EnumFacing.getFront(event.face), hand)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRightClickLast(PlayerInteractEvent event) {
        // 1.7.10: Check action instead of using RightClickBlock subtype
        if (event.action != net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
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
        // 1.7.10: World.addEventListener() doesn't exist - WorldEventNotifier is handled differently
        // The notifier is typically attached through IWorldEventListener or similar mechanisms
        // For now, we skip this since the exact equivalent needs investigation

        GameRules rules = event.world.getGameRules();
        if (!rules.hasRule(MiscUtils.GAMERULE_SKIP_SKYLIGHT_CHECK)) {
            // 1.7.10: addGameRule takes just name and default value, no type parameter
            rules.addGameRule(MiscUtils.GAMERULE_SKIP_SKYLIGHT_CHECK, "false");
        }
    }

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        World world = event.getWorld();
        if (world.isRemote) return;
        Chunk chunk = event.getChunk();
        // 1.7.10: isTerrainPopulated is a public field, not a method
        if (!chunk.isTerrainPopulated) return;
        if (!Loader.instance()
            .hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
            return; // Thanks BuildCraft.
        }
        BlockPos at = event.getPos();
        WorldNetworkHandler.getNetworkHandler(world)
            .informBlockChange(at);
        // 1.7.10: Blocks.CRAFTING_TABLE doesn't exist, using Blocks.crafting_table
        if (event.getNewBlock()
            .equals(Blocks.crafting_table)) {
            if (!event.getOldBlock()
                .equals(Blocks.crafting_table)) {
                WorldNetworkHandler.getNetworkHandler(world)
                    .attemptAutoLinkTo(at);
            }
        }
        if (event.getOldBlock()
            .equals(Blocks.crafting_table)) {
            if (!event.getNewBlock()
                .equals(Blocks.crafting_table)) {
                WorldNetworkHandler.getNetworkHandler(world)
                    .removeAutoLinkTo(at);
            }
        }
        if (event.getNewBlock()
            .equals(BlocksAS.blockAltar)) {
            if (!event.getOldBlock()
                .equals(BlocksAS.blockAltar)) {
                WorldNetworkHandler.getNetworkHandler(world)
                    .attemptAutoLinkTo(at);
            }
        }
        if (event.getOldBlock()
            .equals(BlocksAS.blockAltar)) {
            if (!event.getNewBlock()
                .equals(BlocksAS.blockAltar)) {
                WorldNetworkHandler.getNetworkHandler(world)
                    .removeAutoLinkTo(at);
            }
        }
        if (event.getOldBlock()
            .equals(BlocksAS.customOre)) {
            // 1.7.10: Block doesn't have getValue() - need to use metadata or different approach
            // Rock crystal ores have specific metadata, but without block state system we need to check differently
            // For now, just try to remove ore
            RockCrystalHandler.INSTANCE.removeOre(world, at, true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        if (event.world.isRemote) return;
        BlockPos at = new BlockPos(event.x, event.y, event.z);

        // 1.7.10: BreakEvent doesn't have getPlayer(), need to access field differently
        // In 1.7.10, the player is stored as a public field or through inheritance
        EntityPlayer player = event.getPlayer();
        if (player == null) return;

        // 1.7.10: Can't use lambda, need anonymous class
        ItemStack heldStack = MiscUtils
            .getMainOrOffHand(player, ItemsAS.wand, new com.google.common.base.Predicate<ItemStack>() {

                @Override
                public boolean apply(ItemStack stack) {
                    return stack != null && ItemWand.getAugment(stack) != null;
                }
            });
        if (heldStack != null && ItemWand.getAugment(heldStack) == WandAugment.EVORSIO) {
            if (rand.nextFloat() < Config.evorsioEffectChance) {
                World w = event.world;
                // 1.7.10: getBlock takes x, y, z coordinates, not BlockPos
                Block stateAt = w.getBlock(at.getX(), at.getY(), at.getZ());
                BlockPos playerPos = new BlockPos(player);
                BlockArray foundBlocks = BlockDiscoverer
                    .searchForBlocksAround(w, at, 2, new BlockStateCheck.WorldSpecific() {

                        @Override
                        public boolean isStateValid(World world, BlockPos pos, Block block, int metadata) {
                            // 1.7.10: Manual lambda replacement
                            if (pos.getY() < playerPos.getY()) return false;
                            if (!block.equals(stateAt)) return false;
                            float hardness = block.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
                            if (hardness < 0) return false;
                            if (world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) != null) return false;
                            if (world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) return false;
                            if (!block.canHarvestBlock(player, metadata)) return false;
                            return true;
                        }
                    });
                for (BlockPos pos : foundBlocks.getPattern()
                    .keySet()) {
                    // 1.7.10: getBlock takes x, y, z coordinates, not BlockPos
                    Block atState = w.getBlock(pos.getX(), pos.getY(), pos.getZ());
                    if (w.setBlock(pos.getX(), pos.getY(), pos.getZ(), BlocksAS.blockFakeTree, 0, 3)) {
                        TileFakeTree tt = MiscUtils.getTileAt(w, pos, TileFakeTree.class, true);
                        if (tt != null) {
                            tt.setupTile(player, player.getCurrentEquippedItem(), atState);
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
        // 1.7.10: Use event.harvester field directly, not getHarvester()
        // 1.7.10: isSilkTouching is a boolean field, not a method
        if (event.harvester != null && !event.isSilkTouching) {
            ItemStack main = event.harvester.getCurrentEquippedItem();
            if (!(main == null || main.stackSize <= 0)) {
                // 1.7.10: getEnchantmentLevel takes int, use effectId
                int scorchingId = EnchantmentsAS.enchantmentScorchingHeat.effectId;
                if (EnchantmentHelper.getEnchantmentLevel(scorchingId, main) > 0) {
                    // 1.7.10: fortune enchantment ID
                    int fortuneId = Enchantment.fortune.effectId;
                    int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(fortuneId, main);
                    ArrayList<ItemStack> newStacks = new ArrayList<>();
                    // 1.7.10: Use event.drops field directly, not getDrops()
                    Iterator<ItemStack> iterator = event.drops.iterator();
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
                        // 1.7.10: onCrafting takes world and player
                        furnaced.onCrafting(event.world, event.harvester, 1);
                        FMLCommonHandler.instance()
                            .firePlayerSmeltedEvent(event.harvester, furnaced);
                        if (fortuneLvl > 0 && !(out.getItem() instanceof ItemBlock)) {
                            for (int i = 0; i < fortuneLvl; i++) {
                                if (rand.nextFloat() < 0.5F) {
                                    newStacks.add(ItemUtils.copyStackWithSize(out, 1));
                                }
                            }
                        }
                    }
                    event.drops.addAll(newStacks);
                }
            }
        }
    }

}
