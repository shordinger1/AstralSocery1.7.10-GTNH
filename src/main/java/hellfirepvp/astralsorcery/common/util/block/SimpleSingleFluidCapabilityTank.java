/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;
// TODO: Forge fluid system - manual review needed

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleSingleFluidCapabilityTank
 * Created by HellFirePvP
 * Date: 10.03.2017 / 15:25
 */
public class SimpleSingleFluidCapabilityTank implements IFluidTank, IFluidHandler {

    private int amount = 0;
    private Fluid fluid = null;
    private int maxCapacity;
    private Runnable onUpdate = null;

    private boolean allowInput = true, allowOutput = true;

    public List<EnumFacing> accessibleSides = new ArrayList<>();

    private SimpleSingleFluidCapabilityTank() {}

    public SimpleSingleFluidCapabilityTank(int maxCapacity) {
        this(maxCapacity, EnumFacing.values());
    }

    public SimpleSingleFluidCapabilityTank(int capacity, EnumFacing... accessibleFrom) {
        this.maxCapacity = Math.max(0, capacity);
        this.accessibleSides = Arrays.asList(accessibleFrom);
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void setAllowInput(boolean allowInput) {
        this.allowInput = allowInput;
    }

    public void setAllowOutput(boolean allowOutput) {
        this.allowOutput = allowOutput;
    }

    // returns min(toAdd, what can be added at most)
    public int getMaxAddable(int toAdd) {
        return Math.min(toAdd, maxCapacity - amount);
    }

    public int getMaxDrainable(int toDrain) {
        return Math.min(toDrain, amount);
    }

    // leftover amount that could not be added
    public int addAmount(int amount) {
        if (this.fluid == null) return amount;
        int addable = getMaxAddable(amount);
        this.amount += addable;
        if (Math.abs(addable) > 0 && this.onUpdate != null) {
            this.onUpdate.run();
        }
        return amount - addable;
    }

    // returns amount drained
    @Nullable
    public FluidStack drain(int amount) {
        if (this.fluid == null) return null;
        int drainable = getMaxDrainable(amount);
        this.amount -= drainable;
        Fluid drainedFluid = this.fluid;
        if (this.amount <= 0) {
            setFluid(null);
        }
        if (Math.abs(drainable) > 0 && this.onUpdate != null) {
            this.onUpdate.run();
        }
        return new FluidStack(drainedFluid, drainable);
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        if (fluid == null) return null;
        return new FluidStack(fluid, amount);
    }

    @Nullable
    public Fluid getTankFluid() {
        return fluid;
    }

    public void setFluid(@Nullable Fluid fluid) {
        boolean update = false;
        if (fluid != this.fluid) {
            this.amount = 0;
            update = true;
        }
        this.fluid = fluid;
        if (update && this.onUpdate != null) {
            this.onUpdate.run();
        }
    }

    @Override
    public int getFluidAmount() {
        return amount;
    }

    @Nullable
    // IFluidTank method - not an override in 1.7.10
    public FluidStack getContents() {
        return getFluid();
    }

    @Override
    public int getCapacity() {
        return this.maxCapacity;
    }

    // IFluidTank methods
    public boolean canFill() {
        return this.allowInput && this.amount < this.maxCapacity;
    }

    // IFluidTank methods
    public boolean canDrain() {
        return this.allowOutput && this.amount > 0 && this.fluid != null;
    }

    // IFluidTank methods - not overrides in 1.7.10
    public boolean canFillFluidType(FluidStack fluidStack) {
        return canFill() && (this.fluid == null || fluidStack.getFluid()
            .equals(this.fluid));
    }

    // IFluidTank methods - not overrides in 1.7.10
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return canDrain() && (this.fluid != null && fluidStack.getFluid()
            .equals(this.fluid));
    }

    public float getPercentageFilled() {
        return (((float) amount) / ((float) maxCapacity));
    }

    // IFluidTank method - not an override in 1.7.10
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    // ========== IFluidHandler implementation for 1.7.10 ==========
    // These methods take ForgeDirection as first parameter

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!canAccess(convertForgeDirection(from))) return 0;
        return fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (!canAccess(convertForgeDirection(from))) return null;
        return drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (!canAccess(convertForgeDirection(from))) return null;
        return drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return canAccess(convertForgeDirection(from)) && canFill();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return canAccess(convertForgeDirection(from)) && canDrain();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (canAccess(convertForgeDirection(from))) {
            return new FluidTankInfo[] { getInfo() };
        }
        return new FluidTankInfo[0];
    }

    // Helper method to convert ForgeDirection to EnumFacing
    private EnumFacing convertForgeDirection(ForgeDirection dir) {
        if (dir == null) return null;
        return EnumFacing.values()[dir.ordinal()];
    }

    // ========== End IFluidHandler implementation ==========

    // IFluidTank.fill() - delegates from IFluidHandler.fill(ForgeDirection, ...)
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFillFluidType(resource)) return 0;
        int maxAdded = resource.amount;
        int addable = getMaxAddable(maxAdded);
        if (addable > 0 && this.fluid == null && doFill) {
            setFluid(resource.getFluid());
        }
        if (doFill) {
            addable -= addAmount(addable);
        }
        return addable;
    }

    // IFluidTank.drain(FluidStack, ...) - delegates from IFluidHandler.drain(ForgeDirection, ...)
    @Nullable
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!canDrainFluidType(resource)) return null;
        return drain(resource.amount, doDrain);
    }

    // IFluidTank.drain(int, ...) - delegates from IFluidHandler.drain(ForgeDirection, ...)
    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrain()) return null;
        int maxDrainable = getMaxDrainable(maxDrain);
        if (doDrain) {
            return drain(maxDrainable);
        }
        return new FluidStack(this.fluid, maxDrainable);
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("amt", this.amount);
        tag.setInteger("capacity", this.maxCapacity);
        tag.setBoolean("aIn", this.allowInput);
        tag.setBoolean("aOut", this.allowOutput);
        if (this.fluid != null) {
            tag.setString("fluid", this.fluid.getName());
        }
        int[] sides = new int[accessibleSides.size()];
        for (int i = 0; i < accessibleSides.size(); i++) {
            EnumFacing side = accessibleSides.get(i);
            sides[i] = side.ordinal();
        }
        tag.setIntArray("sides", sides);
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        this.amount = tag.getInteger("amt");
        this.maxCapacity = tag.getInteger("capacity");
        this.allowInput = tag.getBoolean("aIn");
        this.allowOutput = tag.getBoolean("aOut");
        if (tag.hasKey("fluid")) {
            this.fluid = FluidRegistry.getFluid(tag.getString("fluid"));
        } else {
            this.fluid = null;
        }
        int[] sides = tag.getIntArray("sides");
        for (int i : sides) {
            this.accessibleSides.add(EnumFacing.values()[i]);
        }
    }

    public static SimpleSingleFluidCapabilityTank deserialize(NBTTagCompound tag) {
        SimpleSingleFluidCapabilityTank tank = new SimpleSingleFluidCapabilityTank();
        tank.readNBT(tag);
        return tank;
    }

    /**
     * Check if fluid can be accessed from the given side.
     */
    public boolean canAccess(EnumFacing facing) {
        return (facing == null) || accessibleSides.contains(facing);
    }

}
