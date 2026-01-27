/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.awt.*;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.PositionedLoopSound;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IGatedRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.change.ChangeSubscriber;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:18
 */
public class TileAltar extends TileReceiverBaseInventory implements IWandInteract, IMultiblockDependantTile {

    private static final Random rand = new Random();

    private float posDistribution = -1;

    private ActiveCraftingTask craftingTask = null;
    private Object clientCraftSound = null;

    private AltarLevel level = AltarLevel.DISCOVERY;
    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private boolean multiblockMatches = false;

    private ItemStack focusItem = null;
    private boolean doesSeeSky = false;
    private int starlightStored = 0;

    public TileAltar() {
        super(25);
    }

    public TileAltar(AltarLevel level) {
        super(25, EnumFacing.UP);
        this.level = level;
    }

    @Override
    protected ItemHandlerTile createNewItemHandler() {
        return new ItemHandlerTileFiltered(this) {

            @Override
            public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
                if (!super.canInsertItem(slot, toAdd, existing)) {
                    return false;
                }
                AltarLevel al = TileAltar.this.getAltarLevel();
                if (al == null) {
                    al = AltarLevel.DISCOVERY;
                }
                int allowed = al.getAccessibleInventorySize();
                return slot >= 0 && slot < allowed;
            }
        };
    }

    public void receiveStarlight(@Nullable IWeakConstellation type, double amount) {
        if (amount <= 0.001) return;

        starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + (amount * 200D)));
        markForUpdate();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if ((ticksExisted & 15) == 0) {
            updateSkyState(MiscUtils.canSeeSky(this.worldObj, this.getPos(), true, this.doesSeeSky));
        }

        if (!getWorld().isRemote) {
            boolean needUpdate = false;

            matchStructure();

            needUpdate = starlightPassive(needUpdate);
            needUpdate = doTryCraft(needUpdate);

            if (needUpdate) {
                markForUpdate();
            }
        } else {
            if (getActiveCraftingTask() != null) {
                doCraftEffects();
                doCraftSound();
            }
            if (getAltarLevel() != null && getAltarLevel().ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal()
                && getMultiblockState()) {
                playAltarEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playAltarEffects() {
        if (Minecraft.isFancyGraphicsEnabled() && rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 4.4, getPos().getZ() + 0.5);
            p.motion(
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.15F)
                .setColor(Color.WHITE)
                .setMaxAge(25);
        }
    }

    @SideOnly(Side.CLIENT)
    private void doCraftSound() {
        if (Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0) {
            if (clientCraftSound == null || ((PositionedLoopSound) clientCraftSound).hasStoppedPlaying()) {
                clientCraftSound = SoundHelper.playSoundLoopClient(
                    Sounds.attunement,
                    new Vector3(this),
                    0.25F,
                    1F,
                    () -> isInvalid() || Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) <= 0
                        || craftingTask == null);
            }
        } else {
            clientCraftSound = null;
        }
    }

    @Nullable
    public IConstellation getFocusedConstellation() {
        if (!(focusItem == null || focusItem.stackSize <= 0) && focusItem.getItem() instanceof ItemConstellationFocus) {
            return ((ItemConstellationFocus) focusItem.getItem()).getFocusConstellation(focusItem);
        }
        return null;
    }

    @Nonnull
    public ItemStack getFocusItem() {
        return focusItem;
    }

    public void setFocusStack(@Nonnull ItemStack stack) {
        this.focusItem = stack;
        markForUpdate();
    }

    @Override
    public void onBreak() {
        super.onBreak();

        if (!getWorld().isRemote && !(focusItem == null || focusItem.stackSize <= 0)) {
            // 1.7.10: use getWorld() and getPos() instead of world, pos
            ItemUtils.dropItemNaturally(
                getWorld(),
                getPos().getX() + 0.5,
                getPos().getY() + 0.5,
                getPos().getZ() + 0.5,
                focusItem);
            this.focusItem = null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        // 1.7.10: use expand() instead of grow()
        AxisAlignedBB box = super.getRenderBoundingBox().expand(0, 5, 0);
        if (level != null && level.ordinal() >= AltarLevel.TRAIT_CRAFT.ordinal()) {
            box = box.expand(3, 0, 3);
        }
        return box;
    }

    @SideOnly(Side.CLIENT)
    private void doCraftEffects() {
        craftingTask.getRecipeToCraft()
            .onCraftClientTick(this, craftingTask.getState(), ClientScheduler.getClientTick(), rand);
    }

    private void matchStructure() {
        PatternBlockArray structure = this.getRequiredStructure();
        if (structure != null) {
            if (this.structureMatch == null) {
                this.structureMatch = PatternMatchHelper.getOrCreateMatcher(getWorld(), getPos(), structure);
            }
        }

        boolean matches = structure == null || this.structureMatch.matches(this.worldObj);
        if (matches != this.multiblockMatches) {
            LogCategory.STRUCTURE_MATCH.info(
                () -> "Structure match updated: " + this.getClass()
                    .getName() + " at " + this.getPos() + " (" + this.multiblockMatches + " -> " + matches + ")");
            this.multiblockMatches = matches;
            this.markForUpdate();
        }
    }

    private boolean doTryCraft(boolean needUpdate) {
        if (craftingTask == null) return needUpdate;
        AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
        if (!doesRecipeMatch(altarRecipe, true)) {
            abortCrafting();
            return true;
        }
        if (!altarRecipe.fulfillesStarlightRequirement(this)) {
            if (craftingTask.shouldPersist(this)) {
                craftingTask.setState(ActiveCraftingTask.CraftingState.PAUSED);
                return true;
            }
            abortCrafting();
            return true;
        }
        if ((ticksExisted % 5) == 0) {
            if (matchDownMultiblocks(altarRecipe.getNeededLevel()) == null) {
                abortCrafting();
                return true;
            }
        }
        if (craftingTask.isFinished()) {
            finishCrafting();
            return true;
        }
        if (!craftingTask.tick(this)) {
            craftingTask.setState(ActiveCraftingTask.CraftingState.WAITING);
            return true;
        }
        ActiveCraftingTask.CraftingState prev = craftingTask.getState();
        craftingTask.setState(ActiveCraftingTask.CraftingState.ACTIVE);
        craftingTask.getRecipeToCraft()
            .onCraftServerTick(
                this,
                ActiveCraftingTask.CraftingState.ACTIVE,
                craftingTask.getTicksCrafting(),
                craftingTask.getTotalCraftingTime(),
                rand);
        return (prev != craftingTask.getState()) || needUpdate;
    }

    private void finishCrafting() {
        if (craftingTask == null) return; // Wtf

        AbstractAltarRecipe recipe = craftingTask.getRecipeToCraft();
        ShapeMap current = copyGetCurrentCraftingGrid();
        ItemStack out = recipe.getOutput(current, this); // Central item helps defining output - probably, eventually.
        if (!(out == null || out.stackSize <= 0)) {
            out = ItemUtils.copyStackWithSize(out, out.stackSize);
        }

        // 1.7.10: setCraftingPlayer may not be available or needed
        // ForgeHooks.setCraftingPlayer(craftingTask.tryGetCraftingPlayerServer());
        recipe.handleInputConsumption(this, craftingTask, getInventoryHandler());
        // ForgeHooks.setCraftingPlayer(null);

        if (!(out == null || out.stackSize <= 0) && !(out.getItem() instanceof ItemBlockAltar)) {
            if (out.stackSize > 0) {
                // 1.7.10: use getWorld() and getPos() instead of world, pos
                // Note: setNoDespawn() not available in 1.7.10, using default despawn behavior
                ItemUtils
                    .dropItem(getWorld(), getPos().getX() + 0.5, getPos().getY() + 1.3, getPos().getZ() + 0.5, out);
            }
        }

        starlightStored = Math.max(0, starlightStored - recipe.getPassiveStarlightRequired());

        if (!recipe.allowsForChaining() || !doesRecipeMatch(recipe, false)
            || matchDownMultiblocks(recipe.getNeededLevel()) == null) {
            if (getAltarLevel().ordinal() >= AltarLevel.CONSTELLATION_CRAFT.ordinal()) {
                Vector3 pos = new Vector3(getPos()).add(0.5, 0, 0.5);
                PktParticleEvent ev = new PktParticleEvent(
                    PktParticleEvent.ParticleEventType.CRAFT_FINISH_BURST,
                    pos.getX(),
                    pos.getY() + 0.05,
                    pos.getZ());
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(getWorld(), getPos(), 32));
            }
            craftingTask.getRecipeToCraft()
                .onCraftServerFinish(this, rand);

            // 1.7.10: ItemStack doesn't have isEmpty(), use stackSize check
            ItemStack matchFor = recipe.getOutputForMatching();
            if (!(matchFor == null || matchFor.stackSize <= 0)) {
                ItemStack match = matchFor;
                if (match.getItem() instanceof ItemBlockAltar) {
                    TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[WrapMathHelper
                        .clamp(match.getItemDamage(), 0, AltarLevel.values().length - 1)];
                    tryForceLevelUp(to, true);
                }
            }
            ResearchManager.informCraftingAltarCompletion(this, craftingTask);
            // 1.7.10: use getWorld() and Vector3 instead of BlockPos
            SoundHelper.playSoundAround(Sounds.craftFinish, getWorld(), new Vector3(this), 1F, 1.7F);
            EntityFlare.spawnAmbient(
                getWorld(),
                new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
            craftingTask = null;
        }
        markForUpdate();
    }

    public ShapeMap copyGetCurrentCraftingGrid() {
        ShapeMap current = new ShapeMap();
        for (int i = 0; i < 9; i++) {
            ShapedRecipeSlot slot = ShapedRecipeSlot.values()[i];
            ItemStack stack = getInventoryHandler().getStackInSlot(i);
            if (!(stack == null || stack.stackSize <= 0)) {
                current.put(slot, new ItemHandle(ItemUtils.copyStackWithSize(stack, 1)));
            }
        }
        return current;
    }

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
        this.structureMatch = null;
        // 1.7.10: Use metadata instead of properties, and getWorld() instead of world
        // AltarLevel ordinal maps directly to metadata: 0=DISCOVERY, 1=ATTUNEMENT, etc.
        int metadata = to.ordinal();
        return getWorld().setBlock(this.xCoord, this.yCoord, this.zCoord, BlocksAS.blockAltar, metadata, 3);
    }

    // shouldRefresh not available in 1.7.10 - removed for compatibility

    private void abortCrafting() {
        this.craftingTask = null;
        markForUpdate();
    }

    private boolean starlightPassive(boolean needUpdate) {
        if (starlightStored > 0) needUpdate = true;
        starlightStored *= 0.95;

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(getWorld());
        if (doesSeeSky() && handle != null) {
            int yLevel = getPos().getY();
            if (yLevel > 40) {
                float collect = 160;

                float dstr;
                if (yLevel > 120) {
                    dstr = 1F + ((yLevel - 120) / 272F);
                } else {
                    dstr = (yLevel - 20) / 100F;
                }

                if (posDistribution == -1) {
                    // 1.7.10: use getWorld() and getPos() instead of world, pos
                    posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(getWorld(), getPos());
                }

                collect *= dstr;
                collect *= (0.6 + (0.4 * posDistribution));
                collect *= 0.2 + (0.8 * ConstellationSkyHandler.getInstance()
                    .getCurrentDaytimeDistribution(getWorld()));

                starlightStored = Math.min(getMaxStarlightStorage(), (int) (starlightStored + collect));
                return true;
            }
        }
        return needUpdate;
    }

    @Nullable
    public ActiveCraftingTask getActiveCraftingTask() {
        return craftingTask;
    }

    public boolean getMultiblockState() {
        return this.multiblockMatches;
    }

    @Override
    @Nullable
    public PatternBlockArray getRequiredStructure() {
        return getAltarLevel().getPattern();
    }

    @Nonnull
    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    public float getAmbientStarlightPercent() {
        return ((float) starlightStored) / ((float) getMaxStarlightStorage());
    }

    public int getStarlightStored() {
        return starlightStored;
    }

    public int getMaxStarlightStorage() {
        return getAltarLevel().getStarlightMaxStorage();
    }

    public boolean doesRecipeMatch(AbstractAltarRecipe recipe, boolean ignoreStarlightRequirement) {
        // 1.7.10: ItemStack doesn't have isEmpty(), use stackSize check
        ItemStack matchFor = recipe.getOutputForMatching();
        if (!(matchFor == null || matchFor.stackSize <= 0)) {
            if (matchFor.getItem() instanceof ItemBlockAltar) {
                TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[WrapMathHelper
                    .clamp(matchFor.getItemDamage(), 0, AltarLevel.values().length - 1)];
                if (getAltarLevel().ordinal() >= to.ordinal()) {
                    return false;
                }
            }
        }
        return recipe.matches(this, getInventoryHandler(), ignoreStarlightRequirement);
    }

    @Override
    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneaking) {
        if (!getWorld().isRemote) {
            if (getActiveCraftingTask() != null) {
                AbstractAltarRecipe altarRecipe = craftingTask.getRecipeToCraft();
                if (matchDownMultiblocks(altarRecipe.getNeededLevel()) == null
                    || !doesRecipeMatch(altarRecipe, false)) {
                    abortCrafting();
                    return;
                }
            }

            findRecipe(player);
        }
    }

    @Nullable
    public AltarLevel matchDownMultiblocks(AltarLevel levelDownTo) {
        for (int i = getAltarLevel().ordinal(); i >= levelDownTo.ordinal(); i--) {
            AltarLevel al = AltarLevel.values()[i];
            PatternBlockArray pattern = al.getPattern();
            if (pattern == null || pattern.matches(this.worldObj, this.getPos())) {
                return al;
            }
        }
        return null;
    }

    private void findRecipe(EntityPlayer crafter) {
        if (craftingTask != null) return;

        AbstractAltarRecipe recipe = AltarRecipeRegistry.findMatchingRecipe(this, false);
        if (recipe instanceof IGatedRecipe) {
            if (!((IGatedRecipe) recipe).hasProgressionServer(crafter)) return;
        }
        if (recipe != null) {
            int divisor = Math.max(
                0,
                this.getAltarLevel()
                    .ordinal()
                    - recipe.getNeededLevel()
                        .ordinal());
            divisor = (int) Math.round(Math.pow(2, divisor));
            this.craftingTask = new ActiveCraftingTask(recipe, divisor, crafter.getUniqueID());
            markForUpdate();
        }
    }

    protected void updateSkyState(boolean seesSky) {
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if (update) {
            markForUpdate();
        }
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    public AltarLevel getAltarLevel() {
        return level;
    }

    public int getCraftingRecipeWidth() {
        return 3;
    }

    public int getCraftingRecipeHeight() {
        return 3;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.level = AltarLevel.values()[compound.getInteger("level")];
        this.starlightStored = compound.getInteger("starlight");
        this.multiblockMatches = compound.getBoolean("multiblockMatches");

        if (compound.hasKey("craftingTask")) {
            this.craftingTask = ActiveCraftingTask
                .deserialize(compound.getCompoundTag("craftingTask"), this.craftingTask);
        } else {
            this.craftingTask = null;
        }

        this.focusItem = null;
        if (compound.hasKey("focusItem")) {
            // 1.7.10: Use loadItemStackFromNBT() instead of ItemStack(NBTTagCompound) constructor
            NBTTagCompound focusTag = compound.getCompoundTag("focusItem");
            this.focusItem = ItemStack.loadItemStackFromNBT(focusTag);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("level", level.ordinal());
        compound.setInteger("starlight", starlightStored);
        compound.setBoolean("multiblockMatches", multiblockMatches);

        if (!(focusItem == null || focusItem.stackSize <= 0)) {
            // 1.7.10: Lambda takes single parameter for tag
            NBTHelper.setAsSubTag(compound, "focusItem", (tag1) -> focusItem.writeToNBT(tag1));
        }

        if (craftingTask != null) {
            compound.setTag("craftingTask", craftingTask.serialize());
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockaltar.general.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverAltar(at);
    }

    public void onPlace(AltarLevel level) {
        this.level = level;
        markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    public static void finishBurst(PktParticleEvent event) {
        EffectHandler.getInstance()
            .textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone())
            .setPosition(event.getVec())
            .setScale(5 + rand.nextInt(2))
            .setNoRotation(rand.nextInt(360));
    }

    public static enum AltarLevel {

        // 1.7.10: Cast to PatternBlockArray since MultiBlockArrays fields are Object type
        DISCOVERY(9, (Provider<PatternBlockArray>) () -> null),
        ATTUNEMENT(13, (Provider<PatternBlockArray>) () -> (PatternBlockArray) MultiBlockArrays.patternAltarAttunement),
        CONSTELLATION_CRAFT(21,
            (Provider<PatternBlockArray>) () -> (PatternBlockArray) MultiBlockArrays.patternAltarConstellation),
        TRAIT_CRAFT(25, (Provider<PatternBlockArray>) () -> (PatternBlockArray) MultiBlockArrays.patternAltarTrait),
        BRILLIANCE(25, (Provider<PatternBlockArray>) () -> null);

        private final int maxStarlightStorage;
        private final int accessibleInventorySize;
        private final Provider<PatternBlockArray> patternProvider;

        AltarLevel(int invSize, Provider<PatternBlockArray> patternProvider) {
            this.patternProvider = patternProvider;
            this.accessibleInventorySize = invSize;
            this.maxStarlightStorage = (int) (1000 * Math.pow(2, ordinal()));
        }

        public BlockAltar.AltarType getCorrespondingAltarType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        @Nullable
        public PatternBlockArray getPattern() {
            return patternProvider.provide();
        }

        public int getStarlightMaxStorage() {
            return maxStarlightStorage;
        }

        public int getAccessibleInventorySize() {
            return accessibleInventorySize;
        }

        public BlockAltar.AltarType getType() {
            return BlockAltar.AltarType.values()[ordinal()];
        }

        public AltarLevel next() {
            if (this == BRILLIANCE) return this;
            return AltarLevel.values()[ordinal() + 1];
        }

    }

    public static class TransmissionReceiverAltar extends SimpleTransmissionReceiver {

        public TransmissionReceiverAltar(BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if (isChunkLoaded) {
                TileAltar ta = MiscUtils.getTileAt(world, getLocationPos(), TileAltar.class, false);
                if (ta != null) {
                    ta.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new AltarReceiverProvider();
        }

    }

    public static class AltarReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverAltar provideEmptyNode() {
            return new TransmissionReceiverAltar(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverAltar";
        }

    }

}
