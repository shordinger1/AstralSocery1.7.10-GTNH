/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 21:58
 */
public class TileChalice extends TileEntityTick implements IFluidHandler {

    // TODO: Implement after ILiquidStarlightPowered interface is migrated
    // implements ILiquidStarlightPowered, IStructureAreaOfInfluence

    private static final int TANK_SIZE = 24000;

    // 1.7.10: Using Forge's FluidTank instead of Capability system
    private final FluidTank tank;

    // TODO: Replace Vector3 with 1.7.10 equivalent
    public Vector3 rotationDegreeAxis = new Vector3();
    public Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationVecAxis1 = null, rotationVecAxis2 = null;
    private Vector3 rotationVec = null;

    private int nextTest = -1;

    public TileChalice() {
        // Initialize Forge FluidTank
        this.tank = new FluidTank(TANK_SIZE);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
                return;
            }

            if (nextTest == -1) {
                nextTest = ticksExisted + 40 + worldObj.rand.nextInt(90);
            }
            if (ticksExisted >= nextTest) {
                nextTest = ticksExisted + 40 + worldObj.rand.nextInt(90);

                // TODO: Re-enable after LiquidInteraction is migrated
                // if(this.getTank().getFluid() == null || this.getTank().getFluid().amount <= 0) {
                // return;
                // }
                // List<LiquidInteraction> interactions =
                // LiquidInteraction.getPossibleInteractions(this.tank.getFluid());
                // if(!interactions.isEmpty()) {
                // List<TileChalice> tch = collectChalicesFlat();
                // Collections.shuffle(tch);
                // for (TileChalice ch : tch) {
                // if(ch.getPos().equals(getPos())) continue;
                // if(world.isBlockIndirectlyGettingPowered(ch.pos) > 0) continue;
                // TileChalice other = MiscUtils.getTileAt(world, ch.pos, TileChalice.class, true);
                // if (other != null) {
                // if(new Vector3(this).distance(ch.getPos()) <= ConfigEntryChalice.chaliceRange) {
                // RaytraceAssist rta = new RaytraceAssist(getPos(), ch.getPos());
                // if(rta.isClear(this.world)) {
                // FluidStack otherC = other.getTank().getFluid();
                // LiquidInteraction exec = LiquidInteraction.getMatchingInteraction(interactions, otherC);
                // if(exec != null && exec.drainComponents(this, other)) {
                // EntityLiquidSpark els1 = new EntityLiquidSpark(this.world, this.pos, exec);
                // EntityLiquidSpark els2 = new EntityLiquidSpark(this.world, ch.getPos(), exec);
                //
                // els1.setTarget(els2);
                // els1.setFluidRepresented(exec.getComponent1());
                //
                // els2.setTarget(els1);
                // els2.setFluidRepresented(exec.getComponent2());
                //
                // this.world.spawnEntity(els1);
                // this.world.spawnEntity(els2);
                //
                // this.markForUpdate();
                // other.markForUpdate();
                // break;
                // }
                // }
                // }
                // }
                // }
                // }
            }
        } else {
            // EffectHelper is now implemented
            if (rotationVecAxis1 == null) {
                rotationVecAxis1 = Vector3.random()
                    .multiply(360);
            }
            if (rotationVecAxis2 == null) {
                rotationVecAxis2 = Vector3.random()
                    .multiply(360);
            }
            // if(rotationVec == null) {
            // rotationVec = Vector3.random().normalize().multiply(1.5F);
            // }
            //
            // this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            // this.rotationDegreeAxis.add(this.rotationVec);
            //
            // playFluidEffect();
        }
    }

    private List<TileChalice> collectChalicesFlat() {
        // TODO: Re-enable after ConfigEntryChalice is migrated
        // int ceilRange = MathHelper.ceil(ConfigEntryChalice.chaliceRange);
        int ceilRange = (int) Math.ceil(16F); // Default range - TODO: MathHelper.ceil_float not available in 1.7.10

        BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
        // BlockPos min = this.pos.add(-ceilRange, -ceilRange, -ceilRange);
        // BlockPos max = this.pos.add( ceilRange, ceilRange, ceilRange);

        int minX = xCoord - ceilRange;
        int minY = yCoord - ceilRange;
        int minZ = zCoord - ceilRange;
        int maxX = xCoord + ceilRange;
        int maxY = yCoord + ceilRange;
        int maxZ = zCoord + ceilRange;

        List<TileChalice> out = new LinkedList<>();

        int chMinX = minX >> 4;
        int chMinZ = minZ >> 4;
        int chMaxX = maxX >> 4;
        int chMaxZ = maxZ >> 4;

        for (int xx = chMinX; xx <= chMaxX; xx++) {
            for (int zz = chMinZ; zz <= chMaxZ; zz++) {
                // TODO: isLoaded() not available in 1.7.10 - assume chunk is loaded
                // if (worldObj.getChunkFromChunkCoords(xx, zz).isLoaded()) {
                // TODO: tileEntityList access different in 1.7.10 - use alternative method
                // Chunk lChunk = worldObj.getChunkFromChunkCoords(xx, zz);
                // In 1.7.10, we need to iterate differently
                // for (int i = 0; i < lChunk.tileEntityList.size(); i++) {
                // TileEntity te = (TileEntity) lChunk.tileEntityList.get(i);
                // if (te instanceof TileChalice && !te.isInvalid()) {
                // out.add((TileChalice) te);
                // }
                // }
                // }
            }
        }
        return out;
    }

    /**
     * Play fluid effect
     * EffectHelper is now implemented for 1.7.10
     */
    @SideOnly(Side.CLIENT)
    private void playFluidEffect() {
        // FluidStack fs = // getTank() not available.getFluid();
        // if(fs == null || fs.getFluid() == null) return;
        //
        // TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(fs);
        //
        // // ... particle effects
    }

    // IFluidHandler implementation for 1.7.10
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int filled = tank.fill(resource, doFill);
        if (filled > 0 && doFill) {
            markForUpdate();
        }
        return filled;
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
            markForUpdate();
        }
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return tank.getFluidAmount() < tank.getCapacity();
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

    // Simple tank getters
    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    @Nullable
    public Fluid getHeldFluid() {
        FluidStack fluid = tank.getFluid();
        return fluid != null ? fluid.getFluid() : null;
    }

    public float getPercFilled() {
        return tank.getFluidAmount() / (float) tank.getCapacity();
    }

    public DrawSize getDrawSize() {
        float perc = getPercFilled();
        if (perc >= 0.75) {
            return DrawSize.FULL;
        }
        if (perc >= 0.5) {
            return DrawSize.BIG;
        }
        if (perc >= 0.25) {
            return DrawSize.MEDIUM;
        }
        return DrawSize.SMALL;
    }

    // TODO: Re-enable after ILiquidStarlightPowered is migrated
    // @Override
    // public boolean canAcceptStarlight(int mbLiquidStarlight) {
    // return getHeldFluid() == null ||
    // getFluidAmount() <= 0 ||
    // (getHeldFluid() == BlocksAS.fluidLiquidStarlight &&
    // getFluidAmount() + mbLiquidStarlight <= TANK_SIZE);
    // }
    //
    // @Override
    // public void acceptStarlight(int mbLiquidStarlight) {
    // if(canAcceptStarlight(mbLiquidStarlight)) {
    // getTank().fill(new FluidStack(BlocksAS.fluidLiquidStarlight, mbLiquidStarlight), true);
    // markForUpdate();
    // }
    // }
    //
    // @Nullable
    // @Override
    // public Color getEffectRenderColor() {
    // return providesEffect() ? Constellations.octans.getConstellationColor() : null;
    // }
    //
    // @Override
    // public double getRadius() {
    // return providesEffect() ? ConfigEntryChalice.chaliceRange : 0;
    // }
    //
    // @Override
    // public boolean providesEffect() {
    // return this.getWorld().isBlockIndirectlyGettingPowered(getPos()) == 0;
    // }
    //
    // @Override
    // public int getDimensionId() {
    // return this.getWorld().provider.getDimension();
    // }
    //
    // @Override
    // public BlockPos getLocationPos() {
    // return this.getPos();
    // }

    @Override
    protected void onFirstTick() {}

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        // Save FluidTank to NBT
        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        compound.setTag("tank", tankTag);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        // Load FluidTank from NBT
        if (compound.hasKey("tank")) {
            NBTTagCompound tankTag = compound.getCompoundTag("tank");
            tank.readFromNBT(tankTag);
        }
    }

    public static enum DrawSize {

        SMALL(1),
        MEDIUM(2),
        BIG(4),
        FULL(8);

        public final float partTexture;
        public final int mulSize;

        DrawSize(int mulSize) {
            this.partTexture = ((float) mulSize) / 16F;
            this.mulSize = mulSize;
        }
    }

    // TODO: Re-enable after ConfigEntry system is migrated
    // public static class ConfigEntryChalice extends ConfigEntry {
    //
    // public static final ConfigEntryChalice instance = new ConfigEntryChalice();
    //
    // public static float chaliceRange = 16F;
    //
    // private ConfigEntryChalice() {
    // super(Section.MACHINERY, "chalice");
    // }
    //
    // @Override
    // public void loadFromConfig(Configuration cfg) {
    // chaliceRange = cfg.getFloat(getKey() + "Range", getConfigurationSection(), chaliceRange, 4F, 64F, "Defines the
    // Range where the Chalice look for other chalices to interact with.");
    // }
    //
    // }

}
