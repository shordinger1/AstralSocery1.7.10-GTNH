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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.DirectionalLayerBlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileIlluminator
 * Created by HellFirePvP
 * Date: 01.11.2016 / 16:01
 */
public class TileIlluminator extends TileEntityTick {

    private static final Random rand = new Random();
    public static final LightCheck illuminatorCheck = new LightCheck();

    public static final int SEARCH_RADIUS = 64;
    public static final int STEP_WIDTH = 4;

    private LinkedList<BlockPos>[] validPositions = null;
    private boolean recalcRequested = false;
    private int ticksUntilNext = 180;
    private boolean playerPlaced = false;

    private int boost = 0;
    private EnumDyeColor chosenColor = EnumDyeColor.YELLOW;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!playerPlaced) return;

        if (!getWorld().isRemote) {
            if (validPositions == null) recalculate();
            if (rand.nextInt(3) == 0 && placeFlares()) {
                recalcRequested = true;
            }
            boost--;
            ticksUntilNext--;
            if (ticksUntilNext <= 0) {
                ticksUntilNext = boost > 0 ? 30 : 180;
                if (recalcRequested) {
                    recalcRequested = false;
                    recalculate();
                }
            }
        }
        if (getWorld().isRemote) {
            playEffects();
        }
    }

    public void setPlayerPlaced() {
        this.playerPlaced = true;
        this.markForUpdate();
    }

    public void onWandUsed(EnumDyeColor color) {
        this.boost = 10 * 60 * 20;
        this.chosenColor = color;
        this.markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        if (Minecraft.isFancyGraphicsEnabled() || rand.nextInt(5) == 0) {
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
            p.motion(
                (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F);

            Color col = MiscUtils.flareColorFromDye(EnumDyeColor.YELLOW);
            if (this.chosenColor != null) {
                col = MiscUtils.flareColorFromDye(this.chosenColor);
            }

            switch (rand.nextInt(3)) {
                case 0:
                    p.setColor(Color.WHITE);
                    break;
                case 1:
                    p.setColor(
                        col.brighter()
                            .brighter());
                    break;
                case 2:
                    p.setColor(col);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean placeFlares() {
        boolean needsRecalc = false;
        for (LinkedList<BlockPos> list : validPositions) {
            // 1.7.10: LinkedList uses isEmpty() or size() instead of stackSize
            if (list == null || list.isEmpty()) {
                needsRecalc = true;
                continue;
            }
            int index = rand.nextInt(list.size());
            BlockPos at = list.remove(index);
            // 1.7.10: Check isEmpty() after removal
            if (!needsRecalc && list.isEmpty()) needsRecalc = true;
            at = at.add(rand.nextInt(5) - 2, rand.nextInt(13) - 6, rand.nextInt(5) - 2);
            // 1.7.10: Use chunk check instead of isBlockLoaded(BlockPos)
            if (getWorld().blockExists(at.getX(), at.getY(), at.getZ()) && at.getY() >= 0 && at.getY() <= 255
            // 1.7.10: Pass block and metadata (0 for air check)
                && illuminatorCheck.isStateValid(getWorld(), at, getWorld().getBlock(at.posX, at.posY, at.posZ), 0)) {
                EnumDyeColor color = EnumDyeColor.YELLOW;
                if (this.chosenColor != null) {
                    color = this.chosenColor;
                }
                // 1.7.10: Use metadata instead of withProperty
                int metadata = color != null ? color.ordinal() : EnumDyeColor.YELLOW.ordinal();
                if (getWorld().setBlock(at.getX(), at.getY(), at.getZ(), BlocksAS.blockVolatileLight, metadata, 3)) {
                    if (rand.nextInt(4) == 0) {
                        EntityFlare.spawnAmbient(
                            getWorld(),
                            new Vector3(this).add(-1 + rand.nextFloat() * 3, 0.6, -1 + rand.nextFloat() * 3));
                    }
                }
            }
        }
        return needsRecalc;
    }

    private void recalculate() {
        int parts = Math.max(0, getPos().getY() - 7);
        validPositions = new LinkedList[parts];
        for (int i = 1; i <= parts; i++) {
            float yPart = ((float) i) / ((float) parts);
            int yLevel = Math.round(yPart * (getPos().getY() - 7));
            LinkedList<BlockPos> calcPositions = new DirectionalLayerBlockDiscoverer(
                new BlockPos(getPos().getX(), yLevel, getPos().getZ()),
                SEARCH_RADIUS,
                STEP_WIDTH).discoverApplicableBlocks();
            validPositions[i - 1] = repeatList(calcPositions);
        }
    }

    private LinkedList<BlockPos> repeatList(LinkedList<BlockPos> list) {
        LinkedList<BlockPos> rep = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            rep.addAll(list);
        }
        return rep;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("playerPlaced", this.playerPlaced);
        compound.setInteger("boostTimeout", this.boost);
        // 1.7.10: EnumDyeColor uses ordinal() instead of getMetadata()
        if (chosenColor != null) {
            compound.setInteger("wandColor", this.chosenColor.ordinal());
        } else {
            compound.setInteger("wandColor", EnumDyeColor.YELLOW.ordinal());
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerPlaced = compound.getBoolean("playerPlaced");
        this.boost = compound.getInteger("boostTimeout");
        if (compound.hasKey("wandColor")) {
            // 1.7.10: Use EnumDyeColor values()[index] instead of byMetadata()
            int colorIndex = compound.getInteger("wandColor");
            if (colorIndex >= 0 && colorIndex < EnumDyeColor.values().length) {
                this.chosenColor = EnumDyeColor.values()[colorIndex];
            }
        }
        if (this.chosenColor == null) {
            this.chosenColor = EnumDyeColor.YELLOW;
        }
    }

    @Override
    protected void onFirstTick() {
        recalculate();
    }

    public static class LightCheck implements BlockStateCheck.WorldSpecific {

        @Override
        // 1.7.10: BlockStateCheck.WorldSpecific requires metadata parameter
        public boolean isStateValid(World world, BlockPos pos, Block state, int metadata) {
            return world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())
                && !MiscUtils.canSeeSky(world, pos, false, false)
                && world.getBlockLightValue(pos.getX(), pos.getY(), pos.getZ()) < 8
                // 1.7.10: Use EnumSkyBlock.Sky instead of EnumSkyBlock.SKY
                && world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, pos.getX(), pos.getY(), pos.getZ()) < 6;
        }

    }

}
