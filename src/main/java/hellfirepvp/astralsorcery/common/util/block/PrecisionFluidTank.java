/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * PrecisionFluidTank - Fluid tank with fractional amount support
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Precision Fluid Tank for 1.7.10
 * <p>
 * <b>Purpose:</b> Supports fractional fluid amounts for precise production tracking
 * <p>
 * <b>Key Features:</b>
 * <ul>
 * <li>Internal amount stored as double (e.g., 0.2929 mB)</li>
 * <li>Reports integer amount via getFluidAmount() (floored)</li>
 * <li>Adds fractional amounts via addAmount(double)</li>
 * <li>Compatible with standard Forge fluid systems</li>
 * </ul>
 * <p>
 * <b>Why this is needed:</b>
 * Standard FluidTank only accepts int amounts, causing precision loss when
 * production rates are less than 1 mB per tick (e.g., 0.2929 mB/tick).
 * <p>
 * <b>Usage:</b>
 *
 * <pre>
 * PrecisionFluidTank tank = new PrecisionFluidTank(2000);
 * tank.addAmount(0.2929); // Add fractional amount
 * int intAmount = tank.getFluidAmount(); // Returns 0 (floored)
 * tank.addAmount(0.8); // Total now 1.0929
 * intAmount = tank.getFluidAmount(); // Returns 1 (floored)
 * </pre>
 */
public class PrecisionFluidTank extends FluidTank {

    /** Internal amount stored as double for precision */
    private double amount = 0.0;

    /**
     * Create a precision fluid tank
     *
     * @param capacity Maximum capacity in mB
     */
    public PrecisionFluidTank(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    /**
     * Create a precision fluid tank with a specific fluid
     *
     * @param fluid    The fluid to store
     * @param amount   The initial amount
     * @param capacity The maximum capacity
     */
    public PrecisionFluidTank(Fluid fluid, int amount, int capacity) {
        super(fluid, amount, capacity);
        this.fluid = new FluidStack(fluid, amount);
        this.amount = amount;
        this.capacity = capacity;
    }

    /**
     * Add a fractional amount to the tank
     * <p>
     * This is the key method - it accepts a double and accumulates
     * fractional amounts until they reach 1 mB or more.
     *
     * @param amount The amount to add (can be fractional, e.g., 0.2929)
     * @return The amount that could NOT be added (due to capacity)
     */
    public double addAmount(double amount) {
        if (this.fluid == null) {
            return amount; // Can't add if no fluid type is set
        }

        double space = this.capacity - this.amount;
        double canAdd = Math.min(amount, space);

        this.amount += canAdd;

        // Update the super class's fluid field for compatibility
        if (this.amount >= 1.0) {
            super.setFluid(new FluidStack(this.fluid, (int) Math.floor(this.amount)));
        }

        return amount - canAdd; // Return leftover amount
    }

    /**
     * Drain a fractional amount from the tank
     *
     * @param amount The amount to drain
     * @return FluidStack containing the drained fluid (floored to int)
     */
    public FluidStack drain(double amount) {
        if (this.fluid == null || this.amount <= 0) {
            return null;
        }

        int canDrain = (int) Math.min(Math.floor(amount), this.amount);
        if (canDrain <= 0) {
            return null;
        }

        this.amount -= canDrain;

        FluidStack drained = new FluidStack(this.fluid, canDrain);

        if (this.amount < 1.0) {
            super.setFluid(null); // Clear if less than 1 mB
        } else {
            super.setFluid(new FluidStack(this.fluid, (int) Math.floor(this.amount)));
        }

        return drained;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
        if (fluid == null) {
            this.amount = 0;
        } else {
            this.amount = fluid.amount;
        }
    }

    @Override
    public int getFluidAmount() {
        // Return floored amount for external compatibility
        return (int) Math.floor(this.amount);
    }

    /**
     * Get the internal amount as a double
     *
     * @return The precise amount (including fractional part)
     */
    public double getAmount() {
        return this.amount;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.fluid == null) {
            return 0;
        }

        if (this.fluid != null && !resource.isFluidEqual(this.fluid)) {
            // Different fluid - check if we can replace
            if (this.amount > 0) {
                return 0; // Can't replace existing fluid
            }
            // Set new fluid type
            if (doFill) {
                this.fluid = new FluidStack(resource.fluid, 0);
                this.amount = 0;
            }
        }

        double space = this.capacity - this.amount;
        int canFill = (int) Math.min(Math.floor(space), resource.amount);

        if (doFill && canFill > 0) {
            this.amount += canFill;
            super.setFluid(new FluidStack(this.fluid, (int) Math.floor(this.amount)));
        }

        return canFill;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (this.fluid == null || this.amount <= 0) {
            return null;
        }

        int canDrain = (int) Math.min(maxDrain, Math.floor(this.amount));

        if (doDrain && canDrain > 0) {
            this.amount -= canDrain;
            if (this.amount < 1.0) {
                super.setFluid(null);
            } else {
                super.setFluid(new FluidStack(this.fluid, (int) Math.floor(this.amount)));
            }
        }

        return new FluidStack(this.fluid, canDrain);
    }

    /**
     * Get the fluid type (not amount)
     *
     * @return The fluid type, or null if empty
     */
    public Fluid getTankFluid() {
        return this.fluid != null ? this.fluid.getFluid() : null;
    }

    /**
     * Set just the fluid type (resets amount to 0)
     *
     * @param fluid The fluid type
     */
    public void setTankFluid(Fluid fluid) {
        this.fluid = fluid != null ? new FluidStack(fluid, 0) : null;
        this.amount = 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        nbt.setDouble("preciseAmount", this.amount);
        return nbt;
    }

    @Override
    public FluidTank readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("preciseAmount")) {
            this.amount = nbt.getDouble("preciseAmount");
        } else {
            // Legacy compatibility - use super's amount
            this.amount = super.getFluidAmount();
        }
        return this;
    }

    /**
     * Get the percentage filled (0.0 to 1.0)
     *
     * @return Percentage filled
     */
    public float getPercentageFilled() {
        if (this.capacity <= 0) {
            return 0;
        }
        return (float) (this.amount / this.capacity);
    }
}
