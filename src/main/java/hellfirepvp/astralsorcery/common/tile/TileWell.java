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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.WellLiquefaction;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 12:28
 */
public class TileWell extends TileReceiverBaseInventory implements IFluidHandler, IGuiHolder<PosGuiData> {

    private static final Random rand = new Random();
    private static final int MAX_CAPACITY = 2000;

    /** Active liquefaction recipe */
    private WellLiquefaction.LiquefactionEntry running = null;

    // 1.7.10: Using Forge's FluidTank instead of Capability system
    private final FluidTank tank;

    private double starlightBuffer = 0;
    private float posDistribution = -1;

    // ModularUI inventory handler - wraps the base inventory
    private final IItemHandlerModifiable itemHandlerWrapper;

    public TileWell() {
        super(1); // Inventory size 1 for catalyst
        // Initialize Forge FluidTank (TileEntity will be set when available)
        this.tank = new FluidTank(MAX_CAPACITY);
        // Create wrapper for ModularUI
        this.itemHandlerWrapper = new ItemStackHandler(1) {

            @Override
            public int getSlotLimit(int slot) {
                return 1; // Only 1 catalyst item
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                // Sync to base inventory
                TileWell.this.setInventorySlotContents(slot, stack);
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                // Read from base inventory
                return TileWell.this.getStackInSlot(slot);
            }
        };
    }

    protected ItemHandlerTile createNewItemHandler() {
        return new CatalystItemHandler(this);
    }

    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            // TODO: Re-enable after MiscUtils.canSeeSky is migrated
            // if (MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, false)) {
            if (worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) {
                // TODO: Re-enable after ConstellationSkyHandler is migrated
                // double sbDayDistribution =
                // ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world);
                // sbDayDistribution = 0.3 + (0.7 * sbDayDistribution);
                double sbDayDistribution = 0.3 + (0.7 * 0.5); // Placeholder

                int yLevel = yCoord;
                float dstr;
                if (yLevel > 120) {
                    dstr = 1F;
                } else {
                    dstr = yLevel / 120F;
                }
                if (posDistribution == -1) {
                    // TODO: Re-enable after SkyCollectionHelper is migrated
                    // posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, getPos());
                    posDistribution = 0.5F; // Placeholder
                }

                sbDayDistribution *= dstr;
                sbDayDistribution *= 1 + (1.2 * posDistribution);
                starlightBuffer += Math.max(0.0001, sbDayDistribution);
            }

            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if (stack != null && stack.stackSize > 0) {
                if (!worldObj.isAirBlock(xCoord, yCoord + 1, zCoord)) {
                    breakCatalyst();
                } else {
                    running = WellLiquefaction.getLiquefactionEntry(stack);

                    if (running != null) {
                        double gain = running.calculateProduction(starlightBuffer);
                        if (gain > 0 && tank.getFluidAmount() < MAX_CAPACITY) {
                            fillAndDiscardRest(running, gain);
                        }
                        starlightBuffer = 0;

                        // Check if catalyst should shatter
                        if (running.shouldShatter(rand)) {
                            breakCatalyst();
                            LogHelper.debug("Catalyst shattered in well at " + xCoord + ", " + yCoord + ", " + zCoord);
                        }
                    } else {
                        starlightBuffer = 0;
                    }
                }
            } else {
                starlightBuffer = 0;
            }

            // TODO: Re-enable after LiquidStarlightChaliceHandler is migrated
            // if((ticksExisted % 100 == 0) && getHeldFluid() != null && getFluidAmount() > 100) {
            // int mb = Math.min(400, getFluidAmount());
            // FluidStack fluidStack = new FluidStack(getHeldFluid(), mb);
            // java.util.List<TileChalice> out = LiquidStarlightChaliceHandler.findNearbyChalicesWithSpaceFor(this,
            // fluidStack);
            // if(!out.isEmpty()) {
            // TileChalice target = out.get(rand.nextInt(out.size()));
            // LiquidStarlightChaliceHandler.doFluidTransfer(this, target, fluidStack.copy());
            // this.tank.drain(mb, true);
            // markForUpdate();
            // }
            // }
        } else {
            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if (stack != null && stack.stackSize > 0) {
                running = WellLiquefaction.getLiquefactionEntry(stack);

                if (running != null) {
                    // TODO: Re-enable client-side rendering effects
                    // Color color = running.catalystColor != null ? running.catalystColor : Color.WHITE;
                    // doCatalystEffect(color);
                }
            }
            // TODO: Re-enable after FluidLiquidStarlight is migrated
            // if(tank.getFluidAmount() > 0 && tank.getTankFluid() != null && tank.getTankFluid() instanceof
            // FluidLiquidStarlight) {
            // doStarlightEffect();
            // }
        }
    }

    // ========================================================================
    // ModularUI Implementation - Well GUI
    // ========================================================================

    /**
     * Build the Well GUI
     * Shows catalyst slot, fluid tank, and starlight collection status
     */
    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        // Register slot group for catalyst slot
        guiSyncManager.registerSlotGroup("catalyst", 1);
        guiSyncManager.bindPlayerInventory(guiData.getPlayer());

        // Sync fluid amount
        com.cleanroommc.modularui.value.sync.IntSyncValue fluidValue = new com.cleanroommc.modularui.value.sync.IntSyncValue(
            this::getFluidAmount,
            val -> {
                // Fluid amount is synced from tank directly
            });
        guiSyncManager.syncValue("fluid", fluidValue);

        // Sync starlight buffer
        com.cleanroommc.modularui.value.sync.IntSyncValue starlightValue = new com.cleanroommc.modularui.value.sync.IntSyncValue(
            () -> (int) starlightBuffer,
            val -> this.starlightBuffer = val);
        guiSyncManager.syncValue("starlight", starlightValue);

        // Create main panel
        ModularPanel panel = new ModularPanel("well_gui");
        panel.flex()
            .size(176, 166)
            .align(com.cleanroommc.modularui.utils.Alignment.Center);

        // Add title
        panel.child(
            IKey.str("Liquid Starlight Well")
                .asWidget()
                .pos(8, 6));

        // Add fluid tank progress bar
        panel.child(
            new ProgressWidget().progress(() -> (double) getFluidAmount() / (double) MAX_CAPACITY)
                .texture(com.cleanroommc.modularui.drawable.GuiTextures.PROGRESS_ARROW, 20)
                .pos(8, 20)
                .size(160, 10)
                .tooltip(
                    tooltip -> tooltip
                        .addLine("Liquid Starlight: " + getFluidAmount() + " / " + MAX_CAPACITY + " mB")));

        // Add starlight buffer indicator
        panel.child(
            new ProgressWidget().progress(() -> starlightBuffer / 100.0) // Normalize to 0-1 range
                .texture(com.cleanroommc.modularui.drawable.GuiTextures.PROGRESS_ARROW, 20)
                .pos(8, 34)
                .size(160, 5)
                .tooltip(tooltip -> tooltip.addLine("Starlight Buffer: " + (int) starlightBuffer)));

        // Add catalyst slot
        panel.child(
            new ItemSlot().slot(new ModularSlot(itemHandlerWrapper, 0))
                .pos(80, 50)
                .tooltip(tooltip -> {
                    tooltip.addLine("Catalyst Slot");
                    tooltip.addLine("Insert items to convert to liquid starlight");
                }));

        // Add player inventory
        panel.child(com.cleanroommc.modularui.widgets.SlotGroupWidget.playerInventory(7, true));

        return panel;
    }

    // ========================================================================
    // End ModularUI Implementation
    // ========================================================================

    public void breakCatalyst() {
        setInventorySlotContents(0, null);
        // TODO: Re-enable after network packet system is migrated
        // PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.WELL_CATALYST_BREAK,
        // getPos().getX(), getPos().getY(), getPos().getZ());
        // PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, getPos(), 32));
        // SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, getWorld(), getPos(), 1F, 1F);

        // 1.7.10 sound effect
        worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "step.stone", 1F, 1F);
    }

    // TODO: Re-enable after client-side rendering is migrated
    // @SideOnly(Side.CLIENT)
    // private void doStarlightEffect() {
    // if(rand.nextInt(3) == 0) {
    // EntityFXFacingParticle p = EffectHelper.genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 0.4,
    // getPos().getZ() + 0.5);
    // p.offset(0, getPercFilled() * 0.5, 0);
    // p.offset(rand.nextFloat() * 0.35 * (rand.nextBoolean() ? 1 : -1), 0, rand.nextFloat() * 0.35 *
    // (rand.nextBoolean() ? 1 : -1));
    // p.scale(0.16F).gravity(0.006).setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
    // }
    // }

    // TODO: Re-enable after client-side rendering is migrated
    // @SideOnly(Side.CLIENT)
    // private void doCatalystEffect(Color color) {
    // if(rand.nextInt(6) == 0) {
    // Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
    // if(rView == null) rView = Minecraft.getMinecraft().player;
    // if(rView.getDistanceSq(getPos()) > Config.maxEffectRenderDistanceSq) return;
    // EntityFXFacingParticle p = EffectHelper.genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 1.3,
    // getPos().getZ() + 0.5);
    // p.offset(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.1, rand.nextFloat() * 0.1 *
    // (rand.nextBoolean() ? 1 : -1));
    // p.scale(0.2F).gravity(-0.004).setAlphaMultiplier(1F).setColor(color);
    // }
    // }

    private void receiveStarlight(double amount) {
        this.starlightBuffer += amount;
    }

    /**
     * Fill tank with produced fluid
     */
    private void fillAndDiscardRest(WellLiquefaction.LiquefactionEntry entry, double gain) {
        if (tank.getFluid() == null) {
            tank.setFluid(new FluidStack(entry.producing.getFluid(), (int) gain));
        } else if (tank.getFluid()
            .isFluidEqual(entry.producing)) {
            tank.fill(new FluidStack(entry.producing.getFluid(), (int) gain), true);
        } else {
            // Different fluid - drain and replace
            tank.drain(tank.getFluidAmount(), true);
            tank.setFluid(new FluidStack(entry.producing.getFluid(), (int) gain));
        }
        markDirty();
    }

    // TODO: Re-enable after particle system is migrated
    // @SideOnly(Side.CLIENT)
    // public static void catalystBurst(PktParticleEvent event) {
    // BlockPos at = event.getVec().toBlockPos();
    // EffectHandler.getInstance().registerFX(new EntityFXCrystalBurst(rand.nextInt(), at.getX() + 0.5, at.getY() + 1.3,
    // at.getZ() + 0.5, 1.5F));
    // }

    // TODO: Re-enable after ITransmissionReceiver is migrated
    // @Nullable
    // @Override
    // public String getUnLocalizedDisplayName() {
    // return "tile.blockwell.name";
    // }
    //
    // @Override
    // @Nonnull
    // public ITransmissionReceiver provideEndpoint(BlockPos at) {
    // return new TransmissionReceiverWell(at);
    // }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    @Nullable
    public Fluid getHeldFluid() {
        FluidStack fluid = tank.getFluid();
        return fluid != null ? fluid.getFluid() : null;
    }

    public float getPercFilled() {
        return tank.getFluidAmount() / (float) MAX_CAPACITY;
    }

    // IFluidHandler implementation for 1.7.10
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        // Tank doesn't accept input from outside
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !tank.getFluid()
            .isFluidEqual(resource)) {
            return null;
        }
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack drained = tank.drain(maxDrain, doDrain);
        if (drained != null && doDrain) {
            // TODO: markForUpdate() not available in 1.7.10 TileEntity
            // markForUpdate();
        }
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false; // Tank doesn't accept input
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        FluidStack fluidStack = tank.getFluid();
        return fluidStack != null && fluidStack.getFluid() == fluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { new FluidTankInfo(tank.getFluid(), tank.getCapacity()) };
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        compound.setTag("tank", tankTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("tank")) {
            NBTTagCompound tankTag = compound.getCompoundTag("tank");
            tank.readFromNBT(tankTag);
        }
    }

    public static class CatalystItemHandler extends ItemHandlerTileFiltered {

        public CatalystItemHandler(TileReceiverBaseInventory inv) {
            super(inv);
        }

        @Override
        public int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
            if (toAdd == null || toAdd.stackSize <= 0) return true;
            return WellLiquefaction.canLiquefy(toAdd) && (existing == null || existing.stackSize <= 0);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return null; // Cannot extract catalyst
        }
    }

    // TODO: Re-enable after ITransmissionReceiver is migrated
    // public static class TransmissionReceiverWell extends SimpleTransmissionReceiver {
    //
    // public TransmissionReceiverWell(BlockPos thisPos) {
    // super(thisPos);
    // }
    //
    // @Override
    // public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
    // if(isChunkLoaded) {
    // TileWell tw = MiscUtils.getTileAt(world, getLocationPos(), TileWell.class, false);
    // if(tw != null) {
    // tw.receiveStarlight(amount);
    // }
    // }
    // }
    //
    // @Override
    // public TransmissionClassRegistry.TransmissionProvider getProvider() {
    // return new WellReceiverProvider();
    // }
    //
    // }
    //
    // public static class WellReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {
    //
    // @Override
    // public TransmissionReceiverWell provideEmptyNode() {
    // return new TransmissionReceiverWell(null);
    // }
    //
    // @Override
    // public String getIdentifier() {
    // return AstralSorcery.MODID + ":TransmissionReceiverWell";
    // }
    //
    // }

}
