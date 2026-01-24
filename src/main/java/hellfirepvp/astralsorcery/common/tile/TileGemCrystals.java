/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.block.BlockGemCrystals;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileGemCrystals
 * Created by HellFirePvP
 * Date: 27.11.2018 / 19:05
 */
public class TileGemCrystals extends TileSkybound {

    private static final Random rand = new Random();

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, Block oldState, Block newSate) {
        return oldState != newSate;
    }

    @Nullable
    public BlockGemCrystals.GrowthStageType getGrowth() {
        Block state = getBlockState();
        if (!(state instanceof BlockGemCrystals)) return null;
        return state.getValue(BlockGemCrystals.STAGE);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!getWorld().isRemote) {
            tryGrow();
        } else {
            BlockGemCrystals.GrowthStageType growthStage = getGrowth();
            if (growthStage != null && growthStage.getGrowthStage() == 2) {
                playHarvestEffects(growthStage);
            }
        }
    }

    private void tryGrow() {
        int r = 50000;
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(world);
        if (doesSeeSky() && handle != null) {
            double dstr = ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(world);
            if (dstr > 0) {
                r *= (0.7 + ((1 - dstr) * 0.3));
            }
        }

        if (world.rand.nextInt(Math.max(r, 1)) == 0) {
            grow();
        }
    }

    public void grow() {
        Block current = world.getBlock(getPos());
        if (!(current instanceof BlockGemCrystals)) {
            return;
        }

        BlockGemCrystals.GrowthStageType stageType = current.getValue(BlockGemCrystals.STAGE);
        BlockGemCrystals.GrowthStageType next = null;
        switch (stageType) {
            case STAGE_0:
                next = BlockGemCrystals.GrowthStageType.STAGE_1;
                break;
            case STAGE_1:
                if (ConstellationSkyHandler.getInstance()
                    .getCurrentDaytimeDistribution(world) <= 0.1) {
                    next = BlockGemCrystals.GrowthStageType.STAGE_2_DAY;
                } else if (ConstellationSkyHandler.getInstance()
                    .getCurrentDaytimeDistribution(world) >= 0.8) {
                        next = BlockGemCrystals.GrowthStageType.STAGE_2_NIGHT;
                    } else {
                        next = BlockGemCrystals.GrowthStageType.STAGE_2_SKY;
                    }
                break;
            case STAGE_2_SKY:
                next = BlockGemCrystals.GrowthStageType.STAGE_1;
                break;
            case STAGE_2_DAY:
                next = BlockGemCrystals.GrowthStageType.STAGE_1;
                break;
            case STAGE_2_NIGHT:
                next = BlockGemCrystals.GrowthStageType.STAGE_1;
                break;
        }
        if (next != null) {
            world.setBlock(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                BlocksAS.gemCrystals.withProperty(BlockGemCrystals.STAGE, next),
                0,
                3);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playHarvestEffects(BlockGemCrystals.GrowthStageType growthStage) {
        if (rand.nextInt(4) == 0) {

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX() + rand.nextFloat(),
                pos.getY() + rand.nextFloat(),
                pos.getZ() + rand.nextFloat());
            p.gravity(0.004);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.setColor(growthStage.getDisplayColor());
            p.scale(0.35F);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playBreakParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        BlockGemCrystals.GrowthStageType growthStage = BlockGemCrystals.GrowthStageType
            .values()[(int) event.getAdditionalDataLong()];
        float scale = 0.4F;
        SpriteSheetResource sprite;
        switch (growthStage) {
            case STAGE_0:
                sprite = SpriteLibrary.spriteGemExplodeGray;
                scale = 0.5F;
                break;
            case STAGE_1:
                sprite = SpriteLibrary.spriteGemExplodeGray;
                scale = 0.8F;
                break;
            case STAGE_2_SKY:
                sprite = SpriteLibrary.spriteGemExplodeBlue;
                scale = 1.2F;
                break;
            case STAGE_2_DAY:
                sprite = SpriteLibrary.spriteGemExplodeRed;
                scale = 1.2F;
                break;
            case STAGE_2_NIGHT:
                sprite = SpriteLibrary.spriteGemExplodeWhite;
                scale = 1.2F;
                break;
            default:
                sprite = SpriteLibrary.spriteGemExplodeGray;
                break;
        }
        EffectHandler.getInstance()
            .registerFX(
                EntityFXFacingSprite
                    .fromSpriteSheet(sprite, at.getX() + 0.5, at.getY() + (scale * 0.25), at.getZ() + 0.5, scale, 0));
    }

    @Override
    protected void onFirstTick() {}
}
