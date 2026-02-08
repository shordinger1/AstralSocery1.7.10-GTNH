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
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.WellLiquefaction;
import hellfirepvp.astralsorcery.common.util.block.PrecisionFluidTank;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 12:28
 */
public class TileWell extends TileReceiverBaseInventory implements IFluidHandler {

    private static final Random rand = new Random();
    private static final int MAX_CAPACITY = 2000;
    private int ticksExisted = 0;
    /**
     * Active liquefaction recipe
     */
    private WellLiquefaction.LiquefactionEntry running = null;

    // 1.7.10: Using custom PrecisionFluidTank for fractional amount support
    private final PrecisionFluidTank tank;

    private double starlightBuffer = 0;
    private float posDistribution = -1;

    // ModularUI inventory handler - wraps the base inventory
    private final IItemHandlerModifiable itemHandlerWrapper;

    public TileWell() {
        super(1); // Inventory size 1 for catalyst
        // Initialize PrecisionFluidTank for fractional amount support
        this.tank = new PrecisionFluidTank(MAX_CAPACITY);
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
        ticksExisted += 1;

        if (!worldObj.isRemote) {
            // Check if well can see sky (simplified 1.7.10 version)
            if (worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) {
                // Calculate starlight distribution based on time of day
                // In 1.7.10, we use world time to estimate day/night cycle
                long time = worldObj.getWorldTime();
                float dayProgress = ((time % 24000) / 24000F);
                // Starlight is stronger at night (0.3 to 1.0)
                double sbDayDistribution = 0.3 + (0.7 * (1F - dayProgress));

                int yLevel = yCoord;
                float dstr;
                if (yLevel > 120) {
                    dstr = 1F;
                } else {
                    dstr = yLevel / 120F;
                }
                if (posDistribution == -1) {
                    // Generate position-based distribution using world seed
                    // Simplified noise distribution for 1.7.10
                    long seed = worldObj.getSeed();
                    double noise = Math.sin((xCoord * 0.1) + (zCoord * 0.1) + (seed % 1000) * 0.01);
                    posDistribution = (float) ((noise + 1) / 2); // Normalize to 0-1
                }

                sbDayDistribution *= dstr;
                sbDayDistribution *= 1 + (1.2 * posDistribution);
                starlightBuffer += Math.max(0.0001, sbDayDistribution);
            }

            ItemStack stack = getInventoryHandler().getStackInSlot(0);

            // Auto-collect items above well
            if (stack == null || stack.stackSize <= 0) {
                attemptCollectItem();
            }

            stack = getInventoryHandler().getStackInSlot(0);
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
                        }
                    } else {
                        starlightBuffer = 0;
                    }
                }
            } else {
                starlightBuffer = 0;
            }

            // Auto-transfer liquid to nearby chalices
            // Check every 100 ticks (5 seconds)
            if ((ticksExisted % 100 == 0) && getHeldFluid() != null && getFluidAmount() > 100) {
                int mb = Math.min(400, getFluidAmount());
                FluidStack fluidStack = new FluidStack(getHeldFluid(), mb);

                // Find nearby chalices with space
                java.util.List<TileChalice> nearbyChalices = findNearbyChalicesWithSpace(fluidStack);
                if (!nearbyChalices.isEmpty()) {
                    TileChalice target = nearbyChalices.get(rand.nextInt(nearbyChalices.size()));
                    // Transfer fluid to chalice
                    int transferred = target.fill(ForgeDirection.UNKNOWN, fluidStack, true);
                    if (transferred > 0) {
                        this.tank.drain(transferred, true);
                    }
                }
            }
        } else {
            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if (stack != null && stack.stackSize > 0) {
                running = WellLiquefaction.getLiquefactionEntry(stack);

                if (running != null) {
                    // Client-side rendering effects (not yet implemented)
                    // Color color = running.catalystColor != null ? running.catalystColor : Color.WHITE;
                    // doCatalystEffect(color);
                }
            }
            // Client-side rendering effects (not yet implemented)
            // if(tank.getFluidAmount() > 0 && tank.getFluid() != null) {
            // doStarlightEffect();
            // }
        }
    }

    public void breakCatalyst() {
        setInventorySlotContents(0, null);
        // Network packet system not yet migrated for particle effects
        // 1.7.10: Play sound effect locally
        worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "step.stone", 1F, 1F);
    }

    // Client-side rendering effects (not yet implemented for 1.7.10)
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

    // Client-side rendering effects (not yet implemented for 1.7.10)
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
     * Find nearby chalices with space for the given fluid
     * Searches in a 10 block radius
     *
     * @param fluid The fluid to transfer
     * @return List of nearby chalices that can accept the fluid
     */
    private java.util.List<TileChalice> findNearbyChalicesWithSpace(FluidStack fluid) {
        java.util.List<TileChalice> chalices = new java.util.ArrayList<>();

        // Search in a 10 block radius
        int radius = 10;
        for (int x = xCoord - radius; x <= xCoord + radius; x++) {
            for (int y = yCoord - radius; y <= yCoord + radius; y++) {
                for (int z = zCoord - radius; z <= zCoord + radius; z++) {
                    // Skip center (this well)
                    if (x == xCoord && y == yCoord && z == zCoord) {
                        continue;
                    }

                    // Check if this is a chalice
                    if (worldObj.blockExists(x, y, z)) {
                        net.minecraft.tileentity.TileEntity te = worldObj.getTileEntity(x, y, z);
                        if (te instanceof TileChalice) {
                            TileChalice chalice = (TileChalice) te;
                            // Check if chalice can accept this fluid
                            if (chalice.canFill(ForgeDirection.UNKNOWN, fluid.getFluid())) {
                                // Try to fill in simulation mode to check available space
                                int canAccept = chalice.fill(ForgeDirection.UNKNOWN, fluid, false);
                                if (canAccept > 0) {
                                    chalices.add(chalice);
                                }
                            }
                        }
                    }
                }
            }
        }

        return chalices;
    }

    /**
     * Attempt to collect an item entity above the well
     * Automatically picks up items that have liquefaction recipes
     */
    private void attemptCollectItem() {
        // Check if slot 0 is empty
        ItemStack currentCatalyst = getInventoryHandler().getStackInSlot(0);
        if (currentCatalyst != null && currentCatalyst.stackSize > 0) {
            return; // Already has a catalyst
        }

        // Check if block above is air
        if (!worldObj.isAirBlock(xCoord, yCoord + 1, zCoord)) {
            return; // Block above is not air
        }

        // Search for item entities above the well
        // Search area: 0.5 block around center, from y+1 to y+3
        AxisAlignedBB searchBox = AxisAlignedBB
            .getBoundingBox(xCoord + 0.25, yCoord + 1, zCoord + 0.25, xCoord + 0.75, yCoord + 3, zCoord + 0.75);

        @SuppressWarnings("unchecked")
        java.util.List<net.minecraft.entity.item.EntityItem> items = worldObj
            .getEntitiesWithinAABB(net.minecraft.entity.item.EntityItem.class, searchBox);

        if (items.isEmpty()) {
            return;
        }

        // Try to find a valid liquefaction item
        for (net.minecraft.entity.item.EntityItem entity : items) {
            if (entity == null || entity.isDead) {
                continue;
            }

            ItemStack entityItem = entity.getEntityItem();
            if (entityItem == null || entityItem.stackSize <= 0) {
                continue;
            }

            // Check if this item has a liquefaction recipe
            WellLiquefaction.LiquefactionEntry entry = WellLiquefaction.getLiquefactionEntry(entityItem);
            if (entry != null) {
                // Insert the catalyst
                setInventorySlotContents(0, entityItem.splitStack(1));

                // Play sound
                worldObj.playSoundEffect(
                    xCoord + 0.5,
                    yCoord + 0.5,
                    zCoord + 0.5,
                    "random.pop",
                    0.2F,
                    ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

                // Update entity
                if (entityItem.stackSize <= 0) {
                    entity.setDead();
                }

                // Mark for update
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

                return; // Only collect one item per tick
            }
        }
    }

    /**
     * Fill tank with produced fluid
     * Matches 1.12.2 implementation - uses addAmount(double) for fractional support
     */
    private void fillAndDiscardRest(WellLiquefaction.LiquefactionEntry entry, double gain) {
        if (tank.getTankFluid() == null) {
            tank.setTankFluid(entry.producing.getFluid());
        } else if (!entry.producing.getFluid()
            .equals(tank.getTankFluid())) {
                // Different fluid - drain and replace
                tank.drain(tank.getFluidAmount(), true);
                tank.setTankFluid(entry.producing.getFluid());
            }
        tank.addAmount(gain); // KEY: Adds fractional amount directly
    }

    // Particle system not yet implemented for 1.7.10
    // @SideOnly(Side.CLIENT)
    // public static void catalystBurst(PktParticleEvent event) {
    // BlockPos at = event.getVec().toBlockPos();
    // EffectHandler.getInstance().registerFX(new EntityFXCrystalBurst(rand.nextInt(), at.getX() + 0.5, at.getY() + 1.3,
    // at.getZ() + 0.5, 1.5F));
    // }

    // ITransmissionReceiver not yet implemented for 1.7.10
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
            // 1.7.10: Mark block for update
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

    // ITransmissionReceiver not yet implemented for 1.7.10
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
