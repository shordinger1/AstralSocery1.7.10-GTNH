/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import java.awt.*;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingDepthParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectLucerna
 * Created by HellFirePvP
 * Date: 16.10.2017 / 23:09
 */
public class CapeEffectLucerna extends CapeArmorEffect {

    private static boolean findSpawners = true;
    private static float range = 36F;

    public CapeEffectLucerna(NBTTagCompound cmp) {
        super(cmp, "lucerna");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.lucerna;
    }

    @SideOnly(Side.CLIENT)
    public void playClientHighlightTick(EntityPlayer player) {
        if (player != Minecraft.getMinecraft().thePlayer) return;

        World w = player.worldObj;
        // 1.7.10: EntitySelectors doesn't exist, use manual AABB check
        final double px = player.posX;
        final double py = player.posY;
        final double pz = player.posZ;
        final double r = range;
        // 1.7.10: Use getEntitiesWithinAABB instead
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(px - r, py - r, pz - r, px + r, py + r, pz + r);
        List<EntityLivingBase> entities = w.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        for (EntityLivingBase entity : entities) {
            if (entity != null && !entity.isDead && !entity.equals(player)) {
                if (rand.nextFloat() > 0.4) continue;
                Vector3 at = Vector3.atEntityCorner(entity);
                if (at.distance(new Vector3(new BlockPos(player))) < 6) continue;

                at.add(
                    entity.width * rand.nextFloat(),
                    entity.height * rand.nextFloat(),
                    entity.width * rand.nextFloat());
                EntityFXFacingDepthParticle p = EffectHelper
                    .genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
                p.setColor(Constellations.lucerna.getConstellationColor())
                    .setAlphaMultiplier(1F)
                    .scale(0.6F * rand.nextFloat() + 0.6F)
                    .gravity(0.004)
                    .setMaxAge(30 + rand.nextInt(20));
                if (rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }

                if (rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
                    p.setColor(Color.WHITE)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.3F + 0.3F)
                        .gravity(0.004);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }
        }
        List<TileEntityMobSpawner> list = Lists.newArrayList();
        int minX = WrapMathHelper.floor((player.posX - range) / 16.0D);
        int maxX = WrapMathHelper.floor((player.posX + range) / 16.0D);
        int minZ = WrapMathHelper.floor((player.posZ - range) / 16.0D);
        int maxZ = WrapMathHelper.floor((player.posZ + range) / 16.0D);

        for (int xx = minX; xx <= maxX; ++xx) {
            for (int zz = minZ; zz <= maxZ; ++zz) {
                Chunk ch = w.getChunkFromChunkCoords(xx, zz);
                if (ch != null && ch.isChunkLoaded) {
                    // 1.7.10: Iterate through chunk tile entities manually
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 256; y++) {
                                TileEntity te = ch.getTileEntityUnsafe(x, y, z);
                                if (te instanceof TileEntityMobSpawner) {
                                    if (rand.nextFloat() > 0.4) continue;
                                    list.add((TileEntityMobSpawner) te);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (TileEntityMobSpawner spawner : list) {
            // 1.7.10: TileEntity doesn't have getPos(), use xCoord, yCoord, zCoord
            Vector3 at = new Vector3(spawner.xCoord, spawner.yCoord, spawner.zCoord);
            at.add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

            EntityFXFacingDepthParticle p = EffectHelper
                .genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
            p.setColor(new Color(0x9C0100))
                .setAlphaMultiplier(1F)
                .scale(0.6F * rand.nextFloat() + 0.6F)
                .gravity(0.004)
                .setMaxAge(30 + rand.nextInt(20));
            if (rand.nextInt(3) == 0) {
                p.setColor(IConstellation.weak);
            }

            if (rand.nextFloat() < 0.8F) {
                p = EffectHelper.genericDepthIgnoringFlareParticle(at.getX(), at.getY(), at.getZ());
                p.setColor(Color.WHITE)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.3F + 0.3F)
                    .gravity(0.004);
                p.setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.14F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        findSpawners = cfg.getBoolean(
            getKey() + "FindSpawners",
            getConfigurationSection(),
            findSpawners,
            "If this is set to true, particles spawned by the lucerna cape effect will also highlight spawners nearby.");
        range = cfg.getFloat(
            getKey() + "Range",
            getConfigurationSection(),
            range,
            12,
            512,
            "Sets the maximum range of where the lucerna cape effect will get entities (and potentially spawners given the config option is enabled) to highlight.");
    }

}
