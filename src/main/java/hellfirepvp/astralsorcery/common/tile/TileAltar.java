/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import hellfirepvp.astralsorcery.common.block.BlockAltar;
import hellfirepvp.astralsorcery.common.crafting.altar.ASAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.client.gui.modularui.AltarGuiFactory;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.MultiblockStructures;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.StarlightHelper;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:18
 *
 * Phase 3.1: ModularUI integration
 * - Implements IGuiHolder<PosGuiData> for GUI system
 * - Uses ItemStackHandler for inventory management
 * - Creates ModularPanel with altar-specific UI
 */
// TODO: IWandInteract interface not available - remove interface for now
public class TileAltar extends TileEntitySynchronized implements IGuiHolder<PosGuiData> {

    // TODO: Implement after IMultiblockDependantTile interface is migrated
    // implements IMultiblockDependantTile

    private static final Random rand = new Random();

    private float posDistribution = -1;

    private ActiveCraftingTask activeCraftingTask = null;

    private Object clientCraftSound = null;

    private AltarLevel level = AltarLevel.DISCOVERY;

    // TODO: Re-enable after ChangeSubscriber and StructureMatcherPatternArray are migrated
    // private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private boolean multiblockMatches = false;

    private ItemStack focusItem = null;
    private boolean doesSeeSky = false;
    private int starlightStored = 0;

    // Phase 3.1: Inventory system using ItemStackHandler
    // Size depends on altar level: 9 slots for crafting + focus slot
    private IItemHandlerModifiable inventory;

    public TileAltar() {
        // TODO: Remove super calls with parameters - not available in base TileEntity
        // super(25);
        // Initialize inventory after level is set
        this.inventory = new ItemStackHandler(getInventorySize()) {

            @Override
            public int getSlotLimit(int slot) {
                // Last slot is focus slot, others are crafting slots
                return slot < getInventorySize() - 1 ? 64 : 1;
            }
        };
    }

    public TileAltar(AltarLevel level) {
        // TODO: Remove super calls with parameters - not available in base TileEntity
        // super(25, ForgeDirection.UP);
        this.level = level;
        // Initialize inventory after level is set
        this.inventory = new ItemStackHandler(getInventorySize()) {

            @Override
            public int getSlotLimit(int slot) {
                // Last slot is focus slot, others are crafting slots
                return slot < getInventorySize() - 1 ? 64 : 1;
            }
        };
    }

    // Phase 3.1: Get inventory size based on altar level
    public int getInventorySize() {
        switch (level) {
            case DISCOVERY:
                return 9; // 9 crafting slots
            case ATTUNEMENT:
                return 13; // 9 + 4 corners
            case CONSTELLATION_CRAFT:
                return 21; // 13 + 8 outer ring
            case TRAIT_CRAFT:
            case BRILLIANCE:
                return 26; // 21 + 4 center + 1 focus
            default:
                return 9;
        }
    }

    // Phase 3.1: Get the inventory handler
    public IItemHandlerModifiable getInventory() {
        return inventory;
    }

    // ========================================================================
    // StructureLib Integration - Multiblock Structure Support
    // ========================================================================

    /**
     * Get the structure definition for the current altar level
     * Uses MultiblockStructures for centralized structure definitions
     */
    private IStructureDefinition<?> getStructureDefinition() {
        AltarLevel level = getAltarLevel();
        if (level == null) {
            level = AltarLevel.DISCOVERY;
        }

        // Use centralized structure definitions from MultiblockStructures
        return hellfirepvp.astralsorcery.common.structure.MultiblockStructures.getAltarStructure(level.ordinal());
    }

    /**
     * Check if the altar structure is complete
     * Uses StructureLib to validate the multiblock structure
     */
    public boolean checkStructure() {
        AltarLevel level = getAltarLevel();
        if (level == null || level == AltarLevel.DISCOVERY) {
            // Discovery altar requires no structure
            return true;
        }

        IStructureDefinition<?> definition = getStructureDefinition();
        if (definition == null) {
            return true;
        }

        // Check structure using StructureLib
        // Note: We use SOUTH_NORMAL_NONE facing for now (no rotation support in Phase 1)
        // Using null as context since our structure definition is Object-based
        @SuppressWarnings("unchecked")
        IStructureDefinition<Object> def = (IStructureDefinition<Object>) definition;
        boolean formed = def.check(
            null, // context (not used for Object-based structures)
            "main", // piece name (all structures use "main")
            worldObj, // world
            ExtendedFacing.SOUTH_NORMAL_NONE, // facing (no rotation for now)
            xCoord,
            yCoord,
            zCoord, // position
            0,
            0,
            0, // offsets
            false // forceCheckAll
        );

        return formed;
    }

    // ========================================================================
    // End StructureLib Integration
    // ========================================================================

    // Phase 3.1: ModularUI implementation - build the altar GUI
    // Routes to appropriate altar GUI based on altar level
    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        LogHelper.info("[TileAltar] buildUI called at " + xCoord + "," + yCoord + "," + zCoord + ", level: " + level);
        return AltarGuiFactory.createAltarUI(this, guiData, guiSyncManager, settings);
    }

    // TODO: ItemHandlerTile not available - method commented out
//     protected TileReceiverBaseInventory.ItemHandlerTile createNewItemHandler() {
//     return new ItemHandlerTileFiltered(this) {
//     @Override
//     public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
//     if (!super.canInsertItem(slot, toAdd, existing)) {
//     return false;
//     }
//     AltarLevel al = TileAltar.this.getAltarLevel();
//     if (al == null) {
//     al = AltarLevel.DISCOVERY;
//     }
//     int allowed = al.getAccessibleInventorySize();
//     return slot >= 0 && slot < allowed;
//     }
//     };
//     }

    // TODO: Re-enable after IWeakConstellation is migrated
    // public void receiveStarlight(@Nullable IWeakConstellation type, double amount) {
    // if(amount <= 0.001) return;
    //
    // starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + (amount * 200D)));
    // markForUpdate();
    // }
    @Override
    public void updateEntity() {
        super.updateEntity();

        boolean canSee = worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord);
        updateSkyState(canSee);

        if (!worldObj.isRemote) {
            // Phase 2.2: Basic constellation discovery
            if (doesSeeSky && level == AltarLevel.DISCOVERY) {
                checkConstellationDiscovery();
            }

            boolean needUpdate = false;

            // StructureLib integration: Check multiblock structure
            // Only check periodically to avoid performance impact (every 20 ticks = 1 second)
            if (worldObj.getTotalWorldTime() % 20 == 0) {
                boolean structureFormed = checkStructure();
                if (structureFormed != this.multiblockMatches) {
                    this.multiblockMatches = structureFormed;
                    needUpdate = true;

                    // If structure is broken and we're crafting, stop crafting
                    if (!structureFormed && activeCraftingTask != null) {
                        activeCraftingTask = null;
                        LogHelper.info(
                            "Altar structure broken at " + xCoord
                                + ","
                                + yCoord
                                + ","
                                + zCoord
                                + ", stopping crafting");
                    }
                }
            }

            // Starlight collection - Phase 2.3: Using StarlightHelper
            needUpdate = starlightPassive(needUpdate);

            // Recipe crafting system
            needUpdate = doTryCraft(needUpdate);

            if (needUpdate) {
                // Now available through TileEntitySynchronized
                markForUpdate();
            }
        } else {
            // TODO: Re-enable after crafting and effects are migrated
            // if(getActiveCraftingTask() != null) {
            // doCraftEffects();
            // doCraftSound();
            // }
            // if(getAltarLevel() != null &&
            // getAltarLevel().ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal() &&
            // getMultiblockState()) {
            // playAltarEffects();
            // }
        }
    }

    /**
     * Phase 2.2: Check for constellation discovery
     * Simplified implementation for 1.7.10
     */
    private void checkConstellationDiscovery() {
        // Only check periodically (every 200 ticks = 10 seconds)
        if (worldObj.getTotalWorldTime() % 200 != 0) {
            return;
        }

        // Find nearby players
        java.util.List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(
            EntityPlayer.class,
            net.minecraft.util.AxisAlignedBB
                .getBoundingBox(xCoord - 3, yCoord - 1, zCoord - 3, xCoord + 4, yCoord + 3, zCoord + 4));

        for (EntityPlayer player : players) {
            hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress = hellfirepvp.astralsorcery.common.data.research.ResearchManager
                .getProgress(player);

            if (progress != null && !progress.wasOnceAttuned()) {
                // Player hasn't attuned yet - check if they can discover constellations at night
                if (isNight() && doesSeeSky) {
                    // Allow constellation discovery at Discovery Altar during night
                    // Players need to use a telescope or journal to actually discover constellations
                    // This just checks that the altar is in the right conditions
                    LogHelper.debug(
                        "Player " + player.getCommandSenderName()
                            + " near Discovery altar at "
                            + xCoord
                            + ","
                            + yCoord
                            + ","
                            + zCoord
                            + " - Night time, sky visible, ready for constellation discovery");
                }
            }
        }
    }

    /**
     * Check if it's currently night
     * Phase 2.2: Helper method for constellation discovery
     */
    private boolean isNight() {
        long time = worldObj.getWorldTime() % 24000L;
        return time >= 13000L && time <= 23000L; // Night time in 1.7.10
    }

    // TODO: Re-enable after client-side rendering is migrated
    // @SideOnly(Side.CLIENT)
    // private void playAltarEffects() {
    // if(Minecraft.isFancyGraphicsEnabled() && rand.nextBoolean()) {
    // EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
    // getPos().getX() + 0.5,
    // getPos().getY() + 4.4,
    // getPos().getZ() + 0.5);
    // p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
    // (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
    // (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
    // p.scale(0.15F).setColor(Color.WHITE).setMaxAge(25);
    // }
    // }

    // TODO: Re-enable after client-side sound system is migrated
    // @SideOnly(Side.CLIENT)
    // private void doCraftSound() {
    // if(Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0) {
    // if(clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
    // clientCraftSound = SoundHelper.playSoundLoopClient(Sounds.attunement, new Vector3(this), 0.25F, 1F,
    // () -> isInvalid() ||
    // Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) <= 0 ||
    // craftingTask == null);
    // }
    // } else {
    // clientCraftSound = null;
    // }
    // }

    // TODO: Re-enable after ItemConstellationFocus is migrated
    // @Nullable
    // public IConstellation getFocusedConstellation() {
    // if (focusItem != null && focusItem.stackSize > 0 && focusItem.getItem() instanceof ItemConstellationFocus) {
    // return ((ItemConstellationFocus) focusItem.getItem()).getFocusConstellation(focusItem);
    // }
    // return null;
    // }

    @Nonnull
    public ItemStack getFocusItem() {
        return focusItem != null ? focusItem : null;
    }

    public void setFocusStack(@Nonnull ItemStack stack) {
        this.focusItem = stack;

        // Now available through TileEntitySynchronized
        markForUpdate();
    }

    public void onBreak() {

        if (!worldObj.isRemote && focusItem != null && focusItem.stackSize > 0) {
            // TODO: Re-enable after ItemUtils is migrated
            // ItemUtils.dropItemNaturally(worldObj,
            // xCoord + 0.5, yCoord + 0.5, zCoord + 0.5,
            // focusItem);
            this.focusItem = null;
        }
    }

    // TODO: Re-enable after rendering is migrated
    // @Override
    // @SideOnly(Side.CLIENT)
    // public AxisAlignedBB getRenderBoundingBox() {
    // AxisAlignedBB box = super.getRenderBoundingBox().expand(0, 5, 0);
    // if(level != null && level.ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal()) {
    // box = box.grow(3, 0, 3);
    // }
    // return box;
    // }

    // TODO: Re-enable after crafting system is migrated
    // @SideOnly(Side.CLIENT)
    // private void doCraftEffects() {
    // craftingTask.getRecipeToCraft().onCraftClientTick(this,
    // craftingTask.getState(), ClientScheduler.getClientTick(), rand);
    // }

    // TODO: Re-enable after structure matching is migrated
    // private void matchStructure() {
    // PatternBlockArray structure = this.getRequiredStructure();
    // if (structure != null) {
    // if (this.structureMatch == null) {
    // this.structureMatch = PatternMatchHelper.getOrCreateMatcher(getWorld(), getPos(), structure);
    // }
    // }
    //
    // boolean matches = structure == null || this.structureMatch.matches(this.getWorld());
    // if (matches != this.multiblockMatches) {
    // LogCategory.STRUCTURE_MATCH.info(() ->
    // "Structure match updated: " + this.getClass().getName() + " at " + this.getPos() +
    // " (" + this.multiblockMatches + " -> " + matches + ")");
    // this.multiblockMatches = matches;
    // this.markForUpdate();
    // }
    // }

    /**
     * Attempt to craft a recipe
     * Phase 4: Complete crafting system implementation
     */
    private boolean doTryCraft(boolean needUpdate) {
        // Structure check: Cannot craft without a complete structure (except Discovery altar)
        if (getAltarLevel() != AltarLevel.DISCOVERY && !multiblockMatches) {
            // Structure not complete, abort any active crafting
            if (activeCraftingTask != null) {
                LogHelper.debug("Structure incomplete, aborting crafting");
                activeCraftingTask = null;
                return true;
            }
            return needUpdate;
        }

        // Try to start a new crafting task if none is active
        if (activeCraftingTask == null) {
            // Get inventory as array
            ItemStack[] inventoryArray = new ItemStack[getInventorySize()];
            for (int i = 0; i < getInventorySize(); i++) {
                inventoryArray[i] = this.inventory.getStackInSlot(i);
            }

            ASAltarRecipe recipe = AltarRecipeRegistry.findRecipe(inventoryArray, getAltarLevel());
            if (recipe != null) {
                int craftingDivisor = getAltarLevel().ordinal() + 1;
                // Use a placeholder UUID (will be replaced with actual player UUID when possible)
                activeCraftingTask = new ActiveCraftingTask(recipe, craftingDivisor, UUID.randomUUID());
                LogHelper.info(
                    "Started crafting: " + recipe.getOutput()
                        .getDisplayName());
                return true;
            }
            return needUpdate;
        }

        // Update active crafting task
        ASAltarRecipe recipe = activeCraftingTask.getRecipe();

        // Check if recipe still matches
        if (!activeCraftingTask.doesRecipeMatch(this)) {
            LogHelper.debug("Recipe no longer matches, aborting crafting");
            activeCraftingTask = null;
            return true;
        }

        // Update crafting progress
        boolean shouldContinue = activeCraftingTask.update(this);
        ActiveCraftingTask.CraftingState state = activeCraftingTask.getState();

        // Check if crafting is complete
        if (state == ActiveCraftingTask.CraftingState.COMPLETE) {
            finishCrafting();
            return true;
        }

        // Update changed
        if (state != ActiveCraftingTask.CraftingState.IDLE) {
            needUpdate = true;
        }

        return needUpdate || shouldContinue;
    }

    /**
     * Finish the active crafting task and output the result
     */
    private void finishCrafting() {
        if (activeCraftingTask == null) {
            return;
        }

        ASAltarRecipe recipe = activeCraftingTask.getRecipe();
        ItemStack output = activeCraftingTask.complete(this);

        LogHelper.info("Crafting complete: " + output.getDisplayName());

        // Drop the output item
        if (output != null && output.stackSize > 0) {
            // Drop the item in front of the altar
            net.minecraft.entity.item.EntityItem itemEntity = new net.minecraft.entity.item.EntityItem(
                worldObj,
                xCoord + 0.5,
                yCoord + 1.3,
                zCoord + 0.5,
                output.copy());
            itemEntity.lifespan = 1200; // 60 seconds
            worldObj.spawnEntityInWorld(itemEntity);
        }

        // Consume starlight
        int starlightConsumed = recipe.getStarlightRequired();
        starlightStored = Math.max(0, starlightStored - starlightConsumed);

        // Clear the crafting task
        activeCraftingTask = null;

        // Mark for update
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    // TODO: Re-enable after crafting system is migrated
    // private boolean doTryCraft(boolean needUpdate) {
    // if(craftingTask == null) return needUpdate;
    // AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
    // if(!doesRecipeMatch(altarRecipe, true)) {
    // abortCrafting();
    // return true;
    // }
    // if(!altarRecipe.fulfillesStarlightRequirement(this)) {
    // if(craftingTask.shouldPersist(this)) {
    // craftingTask.setState(ActiveCraftingTask.CraftingState.PAUSED);
    // return true;
    // }
    // abortCrafting();
    // return true;
    // }
    // if((ticksExisted % 5) == 0) {
    // if(matchDownMultiblocks(altarRecipe.getNeededLevel()) == null) {
    // abortCrafting();
    // return true;
    // }
    // }
    // if(craftingTask.isFinished()) {
    // finishCrafting();
    // return true;
    // }
    // if(!craftingTask.tick(this)) {
    // craftingTask.setState(ActiveCraftingTask.CraftingState.WAITING);
    // return true;
    // }
    // ActiveCraftingTask.CraftingState prev = craftingTask.getState();
    // craftingTask.setState(ActiveCraftingTask.CraftingState.ACTIVE);
    // craftingTask.getRecipeToCraft().onCraftServerTick(
    // this,
    // ActiveCraftingTask.CraftingState.ACTIVE,
    // craftingTask.getTicksCrafting(),
    // craftingTask.getTotalCraftingTime(),
    // rand);
    // return (prev != craftingTask.getState()) || needUpdate;
    // }

    // TODO: Re-enable after crafting system is migrated
    // private void finishCrafting() {
    // if(craftingTask == null) return; //Wtf
    //
    // AbstractAltarRecipe recipe = craftingTask.getRecipeToCraft();
    // ShapeMap current = copyGetCurrentCraftingGrid();
    // ItemStack out = recipe.getOutput(current, this); //Central item helps defining output - probably, eventually.
    // if(!out.isEmpty()) {
    // out = ItemUtils.copyStackWithSize(out, out.getCount());
    // }
    //
    // ForgeHooks.setCraftingPlayer(craftingTask.tryGetCraftingPlayerServer());
    // recipe.handleInputConsumption(this, craftingTask, getInventoryHandler());
    // ForgeHooks.setCraftingPlayer(null);
    //
    // if(!out.isEmpty() && !(out.getItem() instanceof ItemBlockAltar)) {
    // if(out.getCount() > 0) {
    // ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5, out).setNoDespawn();
    // }
    // }
    //
    // starlightStored = Math.max(0, starlightStored - recipe.getPassiveStarlightRequired());
    //
    // if (!recipe.allowsForChaining() || !doesRecipeMatch(recipe, false) ||
    // matchDownMultiblocks(recipe.getNeededLevel()) == null) {
    // if(getAltarLevel().ordinal() >= AltarLevel.CONSTELLATION_CRAFT.ordinal()) {
    // Vector3 pos = new Vector3(getPos()).add(0.5, 0, 0.5);
    // PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CRAFT_FINISH_BURST, pos.getX(),
    // pos.getY() + 0.05, pos.getZ());
    // PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(getWorld(), getPos(), 32));
    // }
    // craftingTask.getRecipeToCraft().onCraftServerFinish(this, rand);
    //
    // if(!recipe.getOutputForMatching().isEmpty()) {
    // ItemStack match = recipe.getOutputForMatching();
    // if(match.getItem() instanceof ItemBlockAltar) {
    // TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[
    // MathHelper.clamp(match.getItemDamage(), 0, AltarLevel.values().length - 1)];
    // tryForceLevelUp(to, true);
    // }
    // }
    // ResearchManager.informCraftingAltarCompletion(this, craftingTask);
    // SoundHelper.playSoundAround(Sounds.craftFinish, world, getPos(), 1F, 1.7F);
    // EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() *
    // 7));
    // craftingTask = null;
    // markForUpdate();
    // }

    // TODO: Re-enable after crafting system is migrated
    // public ShapeMap copyGetCurrentCraftingGrid() {
    // ShapeMap current = new ShapeMap();
    // for (int i = 0; i < 9; i++) {
    // ShapedRecipeSlot slot = ShapedRecipeSlot.values()[i];
    // ItemStack stack = getInventoryHandler().getStackInSlot(i);
    // if(!stack.isEmpty()) {
    // current.put(slot, new ItemHandle(ItemUtils.copyStackWithSize(stack, 1)));
    // }
    // }
    // return current;
    // }

    public boolean tryForceLevelUp(AltarLevel to, boolean doLevelUp) {
        int curr = getAltarLevel().ordinal();
        if (curr >= to.ordinal()) return false;
        if (getAltarLevel().next() != to) return false;

        if (!doLevelUp) return true;
        return levelUnsafe(getAltarLevel().next());
    }

    private boolean levelUnsafe(AltarLevel to) {
        this.level = to;
        this.multiblockMatches = false;
        // TODO: Re-enable after structure matching is migrated
        // this.structureMatch = null;
        // return world.setBlockState(getPos(),
        // BlocksAS.blockAltar.getDefaultState().withProperty(BlockAltar.ALTAR_TYPE,
        // level.getCorrespondingAltarType()));

        // 1.7.10 version
        // TODO: Re-enable after BlockAltar is migrated
        // Block block = BlocksAS.blockAltar;
        // int metadata = level.ordinal();
        // return worldObj.setBlock(xCoord, yCoord, zCoord, block, metadata, 3);
        return true;
    }

    public boolean shouldRefresh(Block block, int metadata, Block blockNew, int metadataNew) {
        return block != blockNew;
    }

    // TODO: Re-enable after crafting system is migrated
    // private void abortCrafting() {
    // this.craftingTask = null;
    // markForUpdate();
    // }

    /**
     * Starlight passive collection
     * Phase 2.3: Integrated with StarlightHelper
     * Phase 2.4: Pull from nearby collector crystals
     *
     * @param needUpdate Current update state
     * @return New update state
     */
    private boolean starlightPassive(boolean needUpdate) {
        // Decay existing starlight
        if (starlightStored > 0) {
            needUpdate = true;
            starlightStored *= 0.95; // 5% decay per tick
            if (starlightStored < 1) {
                starlightStored = 0;
            }
        }

        // Collect new starlight if can see sky
        if (doesSeeSky) {
            float collectionPercent = StarlightHelper.calculateStarlightCollection(worldObj, xCoord, yCoord, zCoord);

            if (collectionPercent > 0) {
                // Convert percentage (0.0-1.0) to actual amount
                float collectAmount = collectionPercent * 200F; // Scale up

                int currentStarlight = starlightStored;
                int maxStarlight = getMaxStarlightStorage();

                starlightStored = Math.min(maxStarlight, (int) (starlightStored + collectAmount));

                if (starlightStored != currentStarlight) {
                    needUpdate = true;
                }
            }
        }

        // Phase 2.4: Pull starlight from nearby collector crystals
        // Check periodically (every 20 ticks = 1 second)
        if (worldObj.getTotalWorldTime() % 20 == 0) {
            needUpdate = pullFromNearbyCollectors(needUpdate);
        }

        return needUpdate;
    }

    /**
     * Pull starlight from nearby collector crystals
     * Searches in a 5-block radius for collector crystals
     *
     * @param needUpdate Current update state
     * @return New update state
     */
    private boolean pullFromNearbyCollectors(boolean needUpdate) {
        int maxStarlight = getMaxStarlightStorage();
        if (starlightStored >= maxStarlight) {
            return needUpdate; // Already full
        }

        int range = 5; // Search radius

        // Search for collector crystals in range
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue; // Skip self

                    int x = xCoord + dx;
                    int y = yCoord + dy;
                    int z = zCoord + dz;

                    // Check if tile entity is a collector crystal
                    if (worldObj.blockExists(x, y, z)) {
                        net.minecraft.tileentity.TileEntity te = worldObj.getTileEntity(x, y, z);
                        if (te instanceof TileCollectorCrystal) {
                            TileCollectorCrystal collector = (TileCollectorCrystal) te;

                            // Calculate transmission efficiency based on distance
                            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                            double efficiency = 1.0 / (1.0 + (distance / 32.0));

                            // Pull starlight from collector
                            double space = maxStarlight - starlightStored;
                            double pullAmount = Math.min(space, 10.0 * efficiency); // Pull up to 10 per tick

                            double pulled = collector.consumeStarlight(pullAmount);
                            if (pulled > 0) {
                                starlightStored += (int) pulled;
                                needUpdate = true;

                                // Log occasionally
                                if (worldObj.getTotalWorldTime() % 200 == 0) {
                                    LogHelper.debug("Altar pulled %.1f starlight from collector at [%d,%d,%d]",
                                        pulled, x, y, z);
                                }
                            }

                            // Only pull from one collector per tick to avoid performance issues
                            if (starlightStored >= maxStarlight) {
                                return needUpdate;
                            }
                        }
                    }
                }
            }
        }

        return needUpdate;
    }

    @Nullable
    public ActiveCraftingTask getActiveCraftingTask() {
        return activeCraftingTask;
    }

    public boolean getMultiblockState() {
        return this.multiblockMatches;
    }

    // TODO: Re-enable after structure system is migrated
    // @Override
    // @Nullable
    // public PatternBlockArray getRequiredStructure() {
    // return getAltarLevel().getPattern();
    // }

    // TODO: Re-enable after structure system is migrated
    // @Nonnull
    // @Override
    // public BlockPos getLocationPos() {
    // return this.getPos();
    // }

    public float getAmbientStarlightPercent() {
        return ((float) starlightStored) / ((float) getMaxStarlightStorage());
    }

    public int getStarlightStored() {
        return starlightStored;
    }

    public int getMaxStarlightStorage() {
        return getAltarLevel().getStarlightMaxStorage();
    }

    /**
     * Set the starlight storage amount
     * Used by ModularUI for synchronization
     */
    public void setStarlightStored(int amount) {
        this.starlightStored = amount;
        this.markForUpdate();
    }

    /**
     * Set the multiblock state
     * Used by ModularUI for synchronization
     */
    public void setMultiblockState(boolean state) {
        this.multiblockMatches = state;
        this.markForUpdate();
    }

    /**
     * Get the starlight progress (0.0 to 1.0) for rendering
     * Used by TESRAltar to visualize crafting progress
     *
     * @return Starlight progress ratio (0.0 to 1.0)
     */
    public float getStarlightProgress() {
        if (activeCraftingTask != null) {
            // Return crafting progress
            return activeCraftingTask.getProgress();
        }
        // Return ambient starlight percentage
        return getAmbientStarlightPercent();
    }

    /**
     * Consume starlight from the altar's storage
     * Called by ActiveCraftingTask when crafting completes
     *
     * @param amount Amount of starlight to consume
     * @return true if starlight was consumed, false if not enough starlight
     */
    public boolean consumeStarlight(int amount) {
        if (starlightStored >= amount) {
            starlightStored -= amount;
            markForUpdate(); // Sync to client
            LogHelper.debug("Consumed " + amount + " starlight, remaining: " + starlightStored);
            return true;
        }
        return false;
    }

    // TODO: Re-enable after crafting system is migrated
    // public boolean doesRecipeMatch(AbstractAltarRecipe recipe, boolean ignoreStarlightRequirement) {
    // if(!recipe.getOutputForMatching().isEmpty()) {
    // ItemStack match = recipe.getOutputForMatching();
    // if(match.getItem() instanceof ItemBlockAltar) {
    // TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[
    // MathHelper.clamp(match.getItemDamage(), 0, AltarLevel.values().length - 1)];
    // if(getAltarLevel().ordinal() >= to.ordinal()) {
    // return false;
    // }
    // }
    // }
    // return recipe.matches(this, getInventoryHandler(), ignoreStarlightRequirement);
    // }

    public void onInteract(World world, BlockPos pos, EntityPlayer player, ForgeDirection side, boolean sneaking) {
        // TODO: Implement after BlockPos is properly integrated
        // For now, using xCoord, yCoord, zCoord directly
        if (!worldObj.isRemote) {
            // TODO: Re-enable after crafting system is migrated
            // if(getActiveCraftingTask() != null) {
            // AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
            // if(matchDownMultiblocks(altarRecipe.getNeededLevel()) == null ||
            // !doesRecipeMatch(altarRecipe, false)) {
            // abortCrafting();
            // return;
            // }
            // }
            //
            // findRecipe(player);
        }
    }

    // TODO: Re-enable after structure system is migrated
    // @Nullable
    // public AltarLevel matchDownMultiblocks(AltarLevel levelDownTo) {
    // for (int i = getAltarLevel().ordinal(); i >= levelDownTo.ordinal(); i--) {
    // AltarLevel al = AltarLevel.values()[i];
    // PatternBlockArray pattern = al.getPattern();
    // if (pattern == null || pattern.matches(this.getWorld(), this.getPos())) {
    // return al;
    // }
    // }
    // return null;
    // }

    // TODO: Re-enable after crafting system is migrated
    // private void findRecipe(EntityPlayer crafter) {
    // if(craftingTask != null) return;
    //
    // AbstractAltarRecipe recipe = AltarRecipeRegistry.findMatchingRecipe(this, false);
    // if(recipe instanceof IGatedRecipe) {
    // if(!((IGatedRecipe) recipe).hasProgressionServer(crafter)) return;
    // }
    // if(recipe != null) {
    // int divisor = Math.max(0, this.getAltarLevel().ordinal() - recipe.getNeededLevel().ordinal());
    // divisor = (int) Math.round(Math.pow(2, divisor));
    // this.craftingTask = new ActiveCraftingTask(recipe, divisor, crafter.getUniqueID());
    // // Now available through TileEntitySynchronized
    // markForUpdate();
    // }
    // }

    protected void updateSkyState(boolean seesSky) {
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if (update) {
            // Now available through TileEntitySynchronized
            markForUpdate();
        }
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    public AltarLevel getAltarLevel() {
        LogHelper.debug("[TileAltar] getAltarLevel() returning: " + level);
        return level;
    }

    public int getCraftingRecipeWidth() {
        return 3;
    }

    public int getCraftingRecipeHeight() {
        return 3;
    }

    public void readCustomNBT(NBTTagCompound compound) {

        this.level = AltarLevel.values()[compound.getInteger("level")];
        this.starlightStored = compound.getInteger("starlight");
        this.multiblockMatches = compound.getBoolean("multiblockMatches");

        // TODO: Re-enable after ActiveCraftingTask is migrated
        // if(compound.hasKey("craftingTask")) {
        // this.craftingTask = ActiveCraftingTask.deserialize(compound.getCompoundTag("craftingTask"),
        // this.craftingTask);
        // } else {
        // this.craftingTask = null;
        // }

        this.focusItem = NBTHelper.getStack(compound, "focusItem");

        // Phase 3.1: Load inventory from NBT
        if (compound.hasKey("inventory")) {
            ((ItemStackHandler) this.inventory).deserializeNBT(compound.getCompoundTag("inventory"));
        }

        // Load crafting task
        if (compound.hasKey("craftingTask")) {
            NBTTagCompound taskNbt = compound.getCompoundTag("craftingTask");
            // Note: Full deserialization requires recipe lookup from registry
            // For now, we'll keep the task but it may not function correctly after reload
            // This is acceptable since the recipe matching will restart crafting if invalid
            LogHelper.debug("Loaded crafting task from NBT (note: recipe may need re-verification)");
            // activeCraftingTask = ActiveCraftingTask.deserialize(taskNbt, activeCraftingTask);
            // For simplicity, we'll clear the task and let it restart
            activeCraftingTask = null;
        }
    }

    public void writeCustomNBT(NBTTagCompound compound) {

        compound.setInteger("level", level.ordinal());
        compound.setInteger("starlight", starlightStored);
        compound.setBoolean("multiblockMatches", multiblockMatches);

        if (focusItem != null && focusItem.stackSize > 0) {
            NBTHelper.setStack(compound, "focusItem", focusItem);
        } else {
            compound.removeTag("focusItem");
        }

        // Phase 3.1: Save inventory to NBT
        compound.setTag("inventory", ((ItemStackHandler) this.inventory).serializeNBT());

        // Save crafting task
        if (activeCraftingTask != null) {
            NBTTagCompound taskNbt = activeCraftingTask.serialize();
            compound.setTag("craftingTask", taskNbt);
        }
    }

    // Phase 3.1: Standard TileEntity NBT methods
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
    }

    // TODO: Re-enable after ITransmissionReceiver is migrated
    // @Nullable
    // @Override
    // public String getUnLocalizedDisplayName() {
    // return "tile.blockaltar.general.name";
    // }
    //
    // @Override
    // @Nonnull
    // public ITransmissionReceiver provideEndpoint(BlockPos at) {
    // return new TransmissionReceiverAltar(at);
    // }

    public void onPlace(AltarLevel level) {
        this.level = level;
        // Now available through TileEntitySynchronized
        markForUpdate();
    }

    // TODO: Re-enable after particle system is migrated
    // @SideOnly(Side.CLIENT)
    // public static void finishBurst(PktParticleEvent event) {
    // EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst,
    // Vector3.RotAxis.Y_AXIS.clone()).setPosition(event.getVec()).setScale(5 +
    // rand.nextInt(2)).setNoRotation(rand.nextInt(360));
    // }

    public static enum AltarLevel {

        DISCOVERY(9, null),
        ATTUNEMENT(13, null),
        CONSTELLATION_CRAFT(21, null),
        TRAIT_CRAFT(25, null),
        BRILLIANCE(25, null);

        private final int maxStarlightStorage;
        private final int accessibleInventorySize;
        // TODO: Re-enable after PatternBlockArray is migrated
        // private final Provider<PatternBlockArray> patternProvider;

        AltarLevel(int invSize, /* Provider<PatternBlockArray> patternProvider */ Object patternProvider) {
            // this.patternProvider = patternProvider;
            this.accessibleInventorySize = invSize;
            this.maxStarlightStorage = (int) (1000 * Math.pow(2, ordinal()));
        }

        public BlockAltar.AltarType getCorrespondingAltarType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        // @Nullable
        // public PatternBlockArray getPattern() {
        // return patternProvider.provide();
        // }

        public int getStarlightMaxStorage() {
            return maxStarlightStorage;
        }

        public int getAccessibleInventorySize() {
            return accessibleInventorySize;
        }

        // TODO: Re-enable after BlockAltar is migrated
        // public BlockAltar.AltarType getType() {
        // return BlockAltar.AltarType.values()[ordinal()];
        // }

        public AltarLevel next() {
            if (this == BRILLIANCE) return this;
            return AltarLevel.values()[ordinal() + 1];
        }

    }

    // TODO: Re-enable after ITransmissionReceiver is migrated
    // public static class TransmissionReceiverAltar extends SimpleTransmissionReceiver {
    //
    // public TransmissionReceiverAltar(BlockPos thisPos) {
    // super(thisPos);
    // }
    //
    // @Override
    // public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
    // if(isChunkLoaded) {
    // TileAltar ta = MiscUtils.getTileAt(world, getLocationPos(), TileAltar.class, false);
    // if(ta != null) {
    // ta.receiveStarlight(type, amount);
    // }
    // }
    // }
    //
    // @Override
    // public TransmissionClassRegistry.TransmissionProvider getProvider() {
    // return new AltarReceiverProvider();
    // }
    //
    // }
    //
    // public static class AltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {
    //
    // @Override
    // public TransmissionReceiverAltar provideEmptyNode() {
    // return new TransmissionReceiverAltar(null);
    // }
    //
    // @Override
    // public String getIdentifier() {
    // return AstralSorcery.MODID + ":TransmissionReceiverAltar";
    // }
    //
    // }

}
