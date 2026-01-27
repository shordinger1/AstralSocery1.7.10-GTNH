/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerIO;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileOreGenerator
 * Created by HellFirePvP
 * Date: 20.07.2017 / 18:37
 */
public class TileOreGenerator extends TileEntitySynchronized {

    private static boolean generatingOre = false;

    private boolean structural = false;
    private int remainingGuaranteed = 0;

    public static void createStructuralTile(World world, BlockPos pos) {
        TileOreGenerator gen = new TileOreGenerator();
        gen.structural = true;
        gen.remainingGuaranteed = ConfigEntryMultiOre.guaranteedOres;
        // 1.7.10: Manually set tile entity fields
        gen.xCoord = pos.getX();
        gen.yCoord = pos.getY();
        gen.zCoord = pos.getZ();
        gen.worldObj = world;
        gen.validate(); // 1.7.10: validate() takes no arguments or (world, x, y, z)
        // 1.7.10: Direct chunk tile entity map access not available, use setTileEntity
        world.setTileEntity(pos.getX(), pos.getY(), pos.getZ(), gen);
    }

    // 1.7.10: shouldRefresh() doesn't exist, use canUpdate() instead
    @Override
    public boolean canUpdate() {
        return false; // TileOreGenerator doesn't need ticks
    }

    public boolean checkRefresh(World world, BlockPos pos, @Nonnull Block oldState, @Nonnull Block newState) {
        if (world.isRemote) return false;
        if (generatingOre) return false;
        if (newState.getClass()
            .equals(oldState.getClass())) {
            return false; // Uhm... shit like redstone ore and stuff.
        }
        if (remainingGuaranteed > 0) {
            if (!isActualPlayerNearby(world, pos)) {
                return true;
            }
            generatingOre = true;
            boolean stopGen = false;
            if (!world.setBlock(pos.getX(), pos.getY(), pos.getZ(), oldState, 0, 3)) {
                return true; // Rip gen. can't replace block.
            }
            if (world instanceof WorldServer) {
                BlockCustomOre.allowCrystalHarvest = true;
                if (!MiscUtils.breakBlockWithoutPlayer((WorldServer) world, pos, oldState, false, true, true)) {
                    stopGen = true;
                }
                BlockCustomOre.allowCrystalHarvest = false;
            }
            if (!world.setBlock(pos.getX(), pos.getY(), pos.getZ(), newState, 0, 3) || stopGen) {
                return true; // Rip gen.
            }

            Block state;
            int metadata = 0;
            // 1.7.10: Clearer logic for ore generation
            if (ConfigEntryMultiOre.oreChance > 0 && rand.nextInt(ConfigEntryMultiOre.oreChance) == 0) {
                // Generate ore
                if (rand.nextInt(200) == 0) {
                    // 1.7.10: Set block with metadata for rock crystal
                    state = BlocksAS.customOre;
                    metadata = BlockCustomOre.OreType.ROCK_CRYSTAL.ordinal();
                } else {
                    ItemStack stack = OreTypes.TREASURE_SHRINE_GEN.getRandomOre(rand);
                    state = ItemUtils.createBlockState(stack);
                    if (state == null) {
                        state = Blocks.stone;
                    }
                }
            } else {
                state = Blocks.stone;
            }
            if (!world.setBlock(pos.getX(), pos.getY(), pos.getZ(), state, metadata, 3)) {
                return true;
            }
            if (structural) {
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.GEN_STRUCTURE, pos);
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, pos, 32));
            }
            generatingOre = false;
            remainingGuaranteed--;
            markForUpdate();
            return false;
        }
        return true;
    }

    private boolean isActualPlayerNearby(World world, BlockPos pos) {
        // 1.7.10: Use AxisAlignedBB and manual filtering instead of lambda
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x - 9, y - 9, z - 9, x + 9, y + 9, z + 9);
        java.util.List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, box);
        for (EntityPlayer p : players) {
            if (p instanceof EntityPlayerMP && !p.isEntityAlive()
                && p.getDistanceSq(x, y, z) < 81
                && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.structural = compound.getBoolean("struct");
        this.remainingGuaranteed = compound.getInteger("remaining");

        if (FMLCommonHandler.instance()
            .getEffectiveSide() == Side.SERVER) {
            EventHandlerIO.generatorQueue.add(this);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("struct", this.structural);
        compound.setInteger("remaining", this.remainingGuaranteed);
    }

    @SideOnly(Side.CLIENT)
    public static void playGenerateStructureEffect(PktParticleEvent pktParticleEvent) {
        BlockPos pos = pktParticleEvent.getVec()
            .toBlockPos();
        for (int i = 0; i < 40 + rand.nextInt(30); i++) {
            Vector3 particlePos = new Vector3(
                pos.getX() - 3 + rand.nextFloat() * 7,
                pos.getY() - 3 + rand.nextFloat() * 7,
                pos.getZ() - 3 + rand.nextFloat() * 7);
            Vector3 dir = particlePos.clone()
                .subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                .normalize()
                .divide(-30);
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ())
                .setAlphaMultiplier(1F)
                .setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .scale(0.2F + rand.nextFloat() * 0.1F)
                .setColor(Constellations.armara.getConstellationColor());
        }
    }

    public static class ConfigEntryMultiOre extends ConfigEntry {

        public static final ConfigEntryMultiOre instance = new ConfigEntryMultiOre();

        private static int guaranteedOres = 550;
        private static int chanceDespawn = 100;
        private static int oreChance = 2;

        private ConfigEntryMultiOre() {
            super(Section.MACHINERY, "multi-ore");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            guaranteedOres = cfg.getInt(
                "guaranteedBlocks",
                getConfigurationSection(),
                guaranteedOres,
                0,
                Integer.MAX_VALUE,
                "This value defines how often the block can be broken and will 100% respawn again.");
            chanceDespawn = cfg.getInt(
                "chanceDespawn",
                getConfigurationSection(),
                chanceDespawn,
                1,
                Integer.MAX_VALUE,
                "This value defines how high the chance is after 'guaranteedBlocks' has been reached that the block-respawner despawns. The higher this number, the more unlikely it is to despawn.");
            oreChance = cfg.getInt(
                "oreChance",
                getConfigurationSection(),
                oreChance,
                0,
                Integer.MAX_VALUE,
                "This defines how often an ore will be generated instead of a stone. The higher the number the more rare. Set to 0 to have it never generate ore, only stone.");
        }

    }

}
