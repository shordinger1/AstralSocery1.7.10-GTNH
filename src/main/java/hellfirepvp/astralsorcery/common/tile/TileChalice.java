/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;
// TODO: Forge fluid system - manual review needed

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.base.LiquidInteraction;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.entities.EntityLiquidSpark;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ChunkPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.block.SimpleSingleFluidCapabilityTank;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 21:58
 */
public class TileChalice extends TileEntityTick
    implements IFluidHandler, ILiquidStarlightPowered, IStructureAreaOfInfluence {

    private static final int TANK_SIZE = 24000;
    private SimpleSingleFluidCapabilityTank tank;

    public Vector3 rotationDegreeAxis = new Vector3();
    public Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationVecAxis1 = null, rotationVecAxis2 = null;
    private Vector3 rotationVec = null;

    private int nextTest = -1;

    public TileChalice() {
        tank = new SimpleSingleFluidCapabilityTank(TANK_SIZE, EnumFacing.DOWN);
        this.tank.setOnUpdate(new Runnable() {

            @Override
            public void run() {
                markForUpdate();
            }
        });
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (getWorld().isRemote) {
            if (rotationVecAxis1 == null) {
                rotationVecAxis1 = Vector3.random()
                    .multiply(360);
            }
            if (rotationVecAxis2 == null) {
                rotationVecAxis2 = Vector3.random()
                    .multiply(360);
            }
            if (rotationVec == null) {
                rotationVec = Vector3.random()
                    .normalize()
                    .multiply(1.5F);
            }

            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(this.rotationVec);

            playFluidEffect();
        } else {
            // 1.7.10: isBlockIndirectlyGettingPowered(x,y,z) returns boolean, not int
            if (getWorld().isBlockIndirectlyGettingPowered(getPos().getX(), getPos().getY(), getPos().getZ())) {
                return;
            }

            if (nextTest == -1) {
                nextTest = ticksExisted + 40 + rand.nextInt(90);
            }
            if (ticksExisted >= nextTest) {
                nextTest = ticksExisted + 40 + rand.nextInt(90);
                if (this.getTank()
                    .getFluid() == null
                    || this.getTank()
                        .getFluid().amount <= 0) {
                    return;
                }
                List<LiquidInteraction> interactions = LiquidInteraction.getPossibleInteractions(this.tank.getFluid());
                // 1.7.10: Fix logic and use size() instead of stackSize
                if (interactions != null && interactions.size() > 0) {
                    List<TileChalice> tch = collectChalicesFlat();
                    Collections.shuffle(tch);
                    for (TileChalice ch : tch) {
                        if (ch.getPos()
                            .equals(getPos())) continue;
                        // 1.7.10: use getPos() instead of pos, isBlockIndirectlyGettingPowered returns boolean
                        if (getWorld().isBlockIndirectlyGettingPowered(
                            ch.getPos()
                                .getX(),
                            ch.getPos()
                                .getY(),
                            ch.getPos()
                                .getZ()))
                            continue;
                        TileChalice other = MiscUtils.getTileAt(worldObj, ch.getPos(), TileChalice.class, true);
                        if (other != null) {
                            if (new Vector3(this).distance(ch.getPos()) <= ConfigEntryChalice.chaliceRange) {
                                RaytraceAssist rta = new RaytraceAssist(getPos(), ch.getPos());
                                if (rta.isClear(worldObj)) {
                                    FluidStack otherC = other.getTank()
                                        .getFluid();
                                    LiquidInteraction exec = LiquidInteraction
                                        .getMatchingInteraction(interactions, otherC);
                                    if (exec != null && exec.drainComponents(this, other)) {
                                        BlockPos pos = getPos();
                                        EntityLiquidSpark els1 = new EntityLiquidSpark(worldObj, pos, exec);
                                        EntityLiquidSpark els2 = new EntityLiquidSpark(worldObj, ch.getPos(), exec);

                                        els1.setTarget(els2);
                                        els1.setFluidRepresented(exec.getComponent1());

                                        els2.setTarget(els1);
                                        els2.setFluidRepresented(exec.getComponent2());

                                        worldObj.spawnEntityInWorld(els1);
                                        worldObj.spawnEntityInWorld(els2);

                                        this.markForUpdate();
                                        other.markForUpdate();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<TileChalice> collectChalicesFlat() {
        int ceilRange = WrapMathHelper.ceil(ConfigEntryChalice.chaliceRange);
        // In 1.7.10, BlockPos doesn't have add() method - create new BlockPos manually
        BlockPos pos = getPos();
        BlockPos min = new BlockPos(pos.getX() - ceilRange, pos.getY() - ceilRange, pos.getZ() - ceilRange);
        BlockPos max = new BlockPos(pos.getX() + ceilRange, pos.getY() + ceilRange, pos.getZ() + ceilRange);
        ChunkPos chMin = new ChunkPos(min), chMax = new ChunkPos(max);
        List<TileChalice> out = new LinkedList<>();
        for (int xx = chMin.x; xx <= chMax.x; xx++) {
            for (int zz = chMin.z; zz <= chMax.z; zz++) {
                if (MiscUtils.isChunkLoaded(worldObj, new ChunkPos(xx, zz))) {
                    Chunk lChunk = worldObj.getChunkFromChunkCoords(xx, zz);
                    // 1.7.10: Chunk doesn't have getTileEntityMap(), need to scan chunk tile entities differently
                    // Use chunk tile entity list if available, or iterate positions
                    java.util.List<TileEntity> tileEntities = new java.util.LinkedList<>();
                    // Scan the chunk for tile entities
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 256; y++) {
                                TileEntity te = worldObj
                                    .getTileEntity(lChunk.xPosition * 16 + x, y, lChunk.zPosition * 16 + z);
                                if (te != null && !tileEntities.contains(te)) {
                                    tileEntities.add(te);
                                }
                            }
                        }
                    }
                    for (TileEntity te : tileEntities) {
                        if (te instanceof TileChalice && !te.isInvalid()) {
                            out.add((TileChalice) te);
                        }
                    }
                }
            }
        }
        return out;
    }

    @SideOnly(Side.CLIENT)
    private void playFluidEffect() {
        FluidStack fs = getTank().getFluid();
        if (fs == null || fs.getFluid() == null) return;

        TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(fs);

        EntityFXFloatingCube cube;
        if (rand.nextInt(2 * (DrawSize.values().length - getDrawSize().ordinal()) * 4) == 0) {
            Vector3 at = new Vector3(this).add(0.5, 1.4, 0.5);
            at.add(
                getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
            cube = RenderingUtils.spawnFloatingBlockCubeParticle(at, tas);
            cube.setBlendMode(null)
                .setTextureSubSizePercentage(1F / 16F)
                .setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().theWorld, at.toBlockPos());
            cube.setColorHandler(
                cb -> new Color(
                    fs.getFluid()
                        .getColor(fs)));
            cube.setScale(0.03F * (getDrawSize().ordinal() + 1))
                .tumble()
                .setMotion(
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1));
        }

        Vector3 perp = rotationVecAxis1.clone()
            .perpendicular()
            .normalize();
        perp.rotate(Math.toRadians(360 * ((ClientScheduler.getClientTick() % 140D) / 140D)), rotationVecAxis1);
        perp.add(getPos())
            .add(0.5, 0.5, 0.5)
            .addY(1);

        cube = RenderingUtils.spawnFloatingBlockCubeParticle(perp, tas);
        cube.setBlendMode(null)
            .setTextureSubSizePercentage(1F / 16F)
            .setMaxAge(20 + rand.nextInt(20));
        cube.setWorldLightCoord(Minecraft.getMinecraft().theWorld, perp.toBlockPos());
        cube.setColorHandler(
            cb -> new Color(
                fs.getFluid()
                    .getColor(fs)));
        cube.setScale(rand.nextFloat() * 0.05F + 0.05F)
            .tumble()
            .setMotion(
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1));

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(perp);
        p.setColor(Color.WHITE)
            .scale(0.1F + rand.nextFloat() * 0.05F)
            .setMaxAge(15 + rand.nextInt(10));

        if (rand.nextInt(5) == 0) {
            p = EffectHelper.genericFlareParticle(perp);
            p.setColor(Color.WHITE)
                .scale(0.1F + rand.nextFloat() * 0.1F)
                .setMaxAge(20 + rand.nextInt(20));
            p.motion(
                rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1));
        }

        if (getDrawSize().ordinal() > 1) {
            perp = rotationVecAxis2.clone()
                .perpendicular()
                .normalize();
            perp.rotate(Math.toRadians(360 * ((ClientScheduler.getClientTick() % 170D) / 170D)), rotationVecAxis2);
            perp.add(getPos())
                .add(0.5, 0.5, 0.5)
                .addY(1);

            cube = RenderingUtils.spawnFloatingBlockCubeParticle(perp, tas);
            cube.setBlendMode(null)
                .setTextureSubSizePercentage(1F / 16F)
                .setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().theWorld, perp.toBlockPos());
            cube.setColorHandler(
                cb -> new Color(
                    fs.getFluid()
                        .getColor(fs)));
            cube.setScale(rand.nextFloat() * 0.05F + 0.05F)
                .tumble()
                .setMotion(
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1));

            p = EffectHelper.genericFlareParticle(perp);
            p.setColor(Color.WHITE)
                .scale(0.05F + rand.nextFloat() * 0.05F)
                .setMaxAge(15 + rand.nextInt(5));

            if (rand.nextInt(5) == 0) {
                p = EffectHelper.genericFlareParticle(perp);
                p.setColor(Color.WHITE)
                    .scale(0.1F + rand.nextFloat() * 0.1F)
                    .setMaxAge(20 + rand.nextInt(20));
                p.motion(
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1));
            }
        }
    }

    public SimpleSingleFluidCapabilityTank getTank() {
        return tank;
    }

    @Override
    public boolean canAcceptStarlight(int mbLiquidStarlight) {
        return getHeldFluid() == null || getFluidAmount() <= 0
            || (getHeldFluid() == BlocksAS.fluidLiquidStarlight && getFluidAmount() + mbLiquidStarlight <= TANK_SIZE);
    }

    @Override
    public void acceptStarlight(int mbLiquidStarlight) {
        if (canAcceptStarlight(mbLiquidStarlight)) {
            getTank().fill(new FluidStack(BlocksAS.fluidLiquidStarlight, mbLiquidStarlight), true);
            markForUpdate();
        }
    }

    @Nullable
    @Override
    public Color getEffectRenderColor() {
        return providesEffect() ? Constellations.octans.getConstellationColor() : null;
    }

    @Override
    public double getRadius() {
        return providesEffect() ? ConfigEntryChalice.chaliceRange : 0;
    }

    @Override
    public boolean providesEffect() {
        // 1.7.10: isBlockIndirectlyGettingPowered returns boolean, invert logic
        return !this.worldObj.isBlockIndirectlyGettingPowered(getPos().getX(), getPos().getY(), getPos().getZ());
    }

    @Override
    public int getDimensionId() {
        return this.worldObj.provider.dimensionId;
    }

    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    @Override
    protected void onFirstTick() {}

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    @Nullable
    public Fluid getHeldFluid() {
        return tank.getTankFluid();
    }

    public float getPercFilled() {
        return tank.getPercentageFilled();
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

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setTag("tank", tank.writeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.tank = SimpleSingleFluidCapabilityTank.deserialize(compound.getCompoundTag("tank"));
        if (!tank.canAccess(EnumFacing.DOWN)) {
            tank.accessibleSides.add(EnumFacing.DOWN);
        }
        this.tank.setOnUpdate(new Runnable() {

            @Override
            public void run() {
                markForUpdate();
            }
        });
    }

    // 1.7.10: IFluidHandler methods have different signatures - include ForgeDirection parameter
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return tank.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return tank.canFill();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return tank.canDrain();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
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

    public static class ConfigEntryChalice extends ConfigEntry {

        public static final ConfigEntryChalice instance = new ConfigEntryChalice();

        public static float chaliceRange = 16F;

        private ConfigEntryChalice() {
            super(Section.MACHINERY, "chalice");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            chaliceRange = cfg.getFloat(
                getKey() + "Range",
                getConfigurationSection(),
                chaliceRange,
                4F,
                64F,
                "Defines the Range where the Chalice look for other chalices to interact with.");
        }

    }

}
