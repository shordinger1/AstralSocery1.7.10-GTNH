/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;
// TODO: Forge fluid system - manual review needed

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import hellfirepvp.astralsorcery.common.entities.EntityLiquidSpark;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.ILiquidStarlightPowered;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidStarlightChaliceHandler
 * Created by HellFirePvP
 * Date: 04.11.2017 / 17:16
 */
public class LiquidStarlightChaliceHandler {

    public static boolean doFluidTransfer(@Nonnull TileEntity source, @Nonnull TileEntity target,
        @Nonnull FluidStack toTransfer) {
        if (target.isInvalid() || source.isInvalid()) {
            return false;
        }

        // In 1.7.10, check if the target TileEntity implements IFluidHandler directly
        IFluidHandler targetHandler = null;
        if (target instanceof IFluidHandler) {
            targetHandler = (IFluidHandler) target;
        }
        if (targetHandler == null) {
            return false;
        }
        if (targetHandler.fill(ForgeDirection.UNKNOWN, toTransfer, false) < toTransfer.amount) {
            return false;
        }
        World world = source.getWorldObj();
        EntityLiquidSpark spark = new EntityLiquidSpark(
            world,
            new BlockPos(source.xCoord, source.yCoord + 1, source.zCoord),
            target);
        spark.setFluidRepresented(toTransfer);
        world.spawnEntityInWorld(spark);
        return true;
    }

    public static boolean requestLiquidStarlightAndTransferTo(ILiquidStarlightPowered target, TileChalice source,
        int tileTicksExisted, int mbRequested) {
        if (!(target instanceof TileEntity) || (tileTicksExisted % 100) != 0) {
            return false;
        }

        FluidStack expected = new FluidStack(BlocksAS.fluidLiquidStarlight, mbRequested);
        World world = ((TileEntity) target).getWorldObj();
        if (source.getTank() != null && source.getTank()
            .getFluid() != null
            && source.getTank()
                .getFluid().getFluidID() == expected.getFluidID()
            && source.getTank().getFluid().amount >= expected.amount) {
            FluidStack drained = source.getTank()
                .drain(expected, true);
            if (drained != null) {
                source.markForUpdate();
                EntityLiquidSpark spark = new EntityLiquidSpark(
                    world,
                    source.getPos()
                        .up(),
                    (TileEntity) target);
                spark.setFluidRepresented(new FluidStack(BlocksAS.fluidLiquidStarlight, drained.amount));
                world.spawnEntityInWorld(spark);
                return true;
            }
        }
        return false;
    }

    public static boolean requestLiquidStarlightAndTransferTo(ILiquidStarlightPowered target, int tileTicksExisted,
        int mbRequested) {
        if (!(target instanceof TileEntity) || (tileTicksExisted % 100) != 0) {
            return false;
        }

        // In 1.7.10, TileEntity doesn't have getPos(), use coordinates directly
        TileEntity targetTe = (TileEntity) target;
        Vector3 thisV = new Vector3(targetTe.xCoord, targetTe.yCoord, targetTe.zCoord).add(0.5, 0.5, 0.5);
        FluidStack expected = new FluidStack(BlocksAS.fluidLiquidStarlight, mbRequested);
        World world = targetTe.getWorldObj(); // Use getter method instead of direct field access
        int chX = targetTe.xCoord >> 4;
        int chZ = targetTe.zCoord >> 4;

        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                int cX = chX + xx;
                int cZ = chZ + zz;
                // In 1.7.10, use world.blockExists to check if chunk is loaded
                if (world.blockExists(cX * 16, 1, cZ * 16)) {
                    Chunk ch = world.getChunkFromBlockCoords(cX * 16, cZ * 16);
                    // In 1.7.10, Chunk doesn't have getTileEntityMap(). Use world.getChunkTileEntityMap() instead
                    // But that doesn't exist either. Need to iterate differently.
                    // For now, let's use a different approach - iterate over tile entities in the world near the
                    // position
                    List<TileEntity> tileEntities = new LinkedList<>();
                    for (int tx = cX * 16; tx < (cX + 1) * 16; tx++) {
                        for (int tz = cZ * 16; tz < (cZ + 1) * 16; tz++) {
                            for (int ty = 0; ty < 256; ty++) {
                                TileEntity te = world.getTileEntity(tx, ty, tz);
                                if (te != null) {
                                    tileEntities.add(te);
                                }
                            }
                        }
                    }
                    for (TileEntity te : tileEntities) {
                        if (!te.isInvalid() && te instanceof TileChalice
                            && new Vector3(te.xCoord, te.yCoord, te.zCoord).distance(thisV) <= 16) {
                            if (world.isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord)) continue;
                            TileChalice tc = (TileChalice) te;
                            RaytraceAssist rta = new RaytraceAssist(
                                thisV,
                                new Vector3(tc.xCoord + 0.5, tc.yCoord + 0.5, tc.zCoord + 0.5));
                            if (!rta.isClear(world)) {
                                continue;
                            }
                            if (tc.getTank() != null && tc.getTank()
                                .getFluid() != null
                                && tc.getTank()
                                    .getFluid().getFluidID() == expected.getFluidID()
                                && tc.getTank().getFluid().amount >= expected.amount) {
                                FluidStack drained = tc.getTank()
                                    .drain(expected, true);
                                if (drained != null) {
                                    tc.markDirty();
                                    EntityLiquidSpark spark = new EntityLiquidSpark(
                                        world,
                                        new BlockPos(tc.xCoord, tc.yCoord + 1, tc.zCoord),
                                        targetTe);
                                    spark.setFluidRepresented(
                                        new FluidStack(BlocksAS.fluidLiquidStarlight, drained.amount));
                                    world.spawnEntityInWorld(spark);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    public static List<TileChalice> findNearbyChalicesWithSpaceFor(TileEntity origin, FluidStack stackExpectedToFit) {
        List<TileChalice> out = new LinkedList<>();
        FluidStack expected = stackExpectedToFit.copy();
        Vector3 thisV = new Vector3(origin).add(0.5, 0.5, 0.5);
        World world = origin.getWorldObj();

        int chX = origin.xCoord >> 4;
        int chZ = origin.zCoord >> 4;
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                int cX = chX + xx;
                int cZ = chZ + zz;
                // In 1.7.10, use world.blockExists to check if chunk is loaded
                if (world.blockExists(cX * 16, 1, cZ * 16)) {
                    // In 1.7.10, Chunk doesn't have getTileEntityMap(). Iterate over tile entities manually
                    for (int tx = cX * 16; tx < (cX + 1) * 16; tx++) {
                        for (int tz = cZ * 16; tz < (cZ + 1) * 16; tz++) {
                            for (int ty = 0; ty < 256; ty++) {
                                TileEntity te = world.getTileEntity(tx, ty, tz);
                                if (te != null && !te.isInvalid()
                                    && te instanceof TileChalice
                                    && new Vector3(te.xCoord, te.yCoord, te.zCoord).distance(thisV) <= 16) {
                                    if (world.isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord))
                                        continue;
                                    TileChalice tc = (TileChalice) te;
                                    if (tc.getTank() != null && tc.getTank()
                                        .canFillFluidType(expected)
                                        && tc.getTank()
                                            .fill(ForgeDirection.UNKNOWN, expected, false) >= expected.amount) {
                                        out.add(tc);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Iterator<TileChalice> iterator = out.iterator();
        while (iterator.hasNext()) {
            TileChalice tc = iterator.next();
            RaytraceAssist rta = new RaytraceAssist(
                thisV,
                new Vector3(tc.xCoord + 0.5, tc.yCoord + 0.5, tc.zCoord + 0.5));
            if (!rta.isClear(world)) {
                iterator.remove();
            }
        }

        return out;
    }

    @Nonnull
    public static List<TileChalice> findNearbyChalicesThatContain(TileEntity origin, FluidStack expected) {
        List<TileChalice> out = new LinkedList<>();
        expected = expected.copy();
        Vector3 thisV = new Vector3(origin).add(0.5, 0.5, 0.5);
        World world = origin.getWorldObj();

        int chX = origin.xCoord >> 4;
        int chZ = origin.zCoord >> 4;
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                int cX = chX + xx;
                int cZ = chZ + zz;
                // In 1.7.10, use world.blockExists to check if chunk is loaded
                if (world.blockExists(cX * 16, 1, cZ * 16)) {
                    // In 1.7.10, Chunk doesn't have getTileEntityMap(). Iterate over tile entities manually
                    for (int tx = cX * 16; tx < (cX + 1) * 16; tx++) {
                        for (int tz = cZ * 16; tz < (cZ + 1) * 16; tz++) {
                            for (int ty = 0; ty < 256; ty++) {
                                TileEntity te = world.getTileEntity(tx, ty, tz);
                                if (te != null && !te.isInvalid()
                                    && te instanceof TileChalice
                                    && new Vector3(te.xCoord, te.yCoord, te.zCoord).distance(thisV) <= 16) {
                                    if (world.isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord))
                                        continue;
                                    TileChalice tc = (TileChalice) te;
                                    if (tc.getTank() != null && tc.getTank()
                                        .getFluid() != null && tc.getTank()
                                        .getFluid().getFluidID() == expected.getFluidID()) {
                                        tc.getTank().getFluid();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Iterator<TileChalice> iterator = out.iterator();
        while (iterator.hasNext()) {
            TileChalice tc = iterator.next();
            RaytraceAssist rta = new RaytraceAssist(
                thisV,
                new Vector3(tc.xCoord + 0.5, tc.yCoord + 0.5, tc.zCoord + 0.5));
            if (!rta.isClear(world)) {
                iterator.remove();
            }
        }

        return out;
    }

}
