/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import java.awt.*;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.controller.orbital.OrbitalEffectCollector;
import hellfirepvp.astralsorcery.client.effect.controller.orbital.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectCorruptedCelestialCrystal;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.change.ChangeSubscriber;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import hellfirepvp.astralsorcery.common.tile.IMultiblockDependantTile;
import hellfirepvp.astralsorcery.common.tile.IStructureAreaOfInfluence;
import hellfirepvp.astralsorcery.common.tile.base.TileSourceBase;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.PatternMatchHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:25
 */
public class TileCollectorCrystal extends TileSourceBase
    implements IMultiblockDependantTile, IStructureAreaOfInfluence {

    private static final UUID DUMMY_UUID = UUID.fromString("0cd550cc-8341-4b96-8d1e-d4a12deb8ca3");
    public static final BlockPos[] offsetsLiquidStarlight = new BlockPos[] { new BlockPos(-1, -4, -1),
        new BlockPos(0, -4, -1), new BlockPos(1, -4, -1), new BlockPos(1, -4, 0), new BlockPos(1, -4, 1),
        new BlockPos(0, -4, 1), new BlockPos(-1, -4, 1), new BlockPos(-1, -4, 0), };

    private static final Random rand = new Random();

    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private BlockCollectorCrystalBase.CollectorCrystalType type;
    private CrystalProperties usedCrystalProperties;
    private UUID playerRef;
    private boolean multiBlockPresent = false;
    private IWeakConstellation associatedType;
    private IMinorConstellation associatedTrait;

    private Object[] orbitals = new Object[4];

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!getWorld().isRemote) {
            // 1.7.10: updateEntity() is the equivalent of tick() in ITickable
            if (ticksExisted > 4 && associatedType == null) {
                if (!getWorld().setBlockToAir(getPos().getX(), getPos().getY(), getPos().getZ())) {
                    return;
                }
            }
            if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
                if (this.structureMatch == null) {
                    this.structureMatch = PatternMatchHelper
                        .getOrCreateMatcher(getWorld(), getPos(), getRequiredStructure());
                }
                boolean found = this.structureMatch.matches(getWorld());
                if (found != this.multiBlockPresent) {
                    LogCategory.STRUCTURE_MATCH.info(
                        () -> "Structure match updated: " + this.getClass()
                            .getName() + " at " + this.getPos() + " (" + this.multiBlockPresent + " -> " + found + ")");
                    this.multiBlockPresent = found;
                    setEnhanced(found);
                    markForUpdate();
                }
            }
        } else {
            if (!doesSeeSky()) {
                EntityFXFacingParticle p = EffectHelper
                    .genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
                p.motion(
                    (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.04F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
                p.scale(0.2F)
                    .setMaxAge(35);
                Color c = Color.WHITE;
                if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
                    if (playerRef != null && MiscUtils.contains(
                        PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerRef),
                        pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                        c = Color.RED;
                    } else {
                        c = Color.CYAN;
                    }
                }
                p.setColor(c);
            } else {
                if (isEnhanced() && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL
                    && associatedType != null) {
                    playEnhancedEffects();
                }
            }
        }
    }

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            // 1.7.10: Need to cast since MultiBlockArrays pattern returns Object
            return (PatternBlockArray) MultiBlockArrays.patternCollectorEnhancement;
        }
        return null;
    }

    @Nonnull
    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    @SideOnly(Side.CLIENT)
    private void playEnhancedEffects() {
        if (Minecraft.isFancyGraphicsEnabled()) {
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
            p.motion(
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F)
                .setMaxAge(25);

            Color c = Color.CYAN;
            if (playerRef != null && MiscUtils.contains(
                PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerRef),
                pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                c = Color.RED;
            }
            p.setColor(c);
        }

        for (int i = 0; i < orbitals.length; i++) {
            OrbitalEffectController ctrl = (OrbitalEffectController) orbitals[i];
            if (ctrl == null) {
                OrbitalEffectCollector prop = new OrbitalEffectCollector(this);
                ctrl = EffectHandler.getInstance()
                    .orbital(prop, null, null);
                ctrl.setOffset(new Vector3(this).add(0.5, 0.5, 0.5));
                ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.4);
                ctrl.setOrbitAxis(Vector3.random());
                ctrl.setTicksPerRotation(60);
                orbitals[i] = ctrl;
            } else {
                if (ctrl.canRemove() || ctrl.isRemoved()) {
                    orbitals[i] = null;
                }
            }
        }

        BlockPos randomPos = offsetsLiquidStarlight[rand.nextInt(offsetsLiquidStarlight.length)]
            .add(getPos().getX(), getPos().getY(), getPos().getZ());
        Vector3 from = new Vector3(randomPos).add(rand.nextFloat(), 0.8, rand.nextFloat());
        Vector3 to = new Vector3(this).add(0.5, 0.5, 0.5);
        Vector3 mov = to.clone()
            .subtract(from)
            .normalize()
            .multiply(0.1);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
        p.motion(mov.getX(), mov.getY(), mov.getZ())
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.gravity(0.004)
            .scale(0.25F)
            .setMaxAge(30 + rand.nextInt(10));
        Color c;
        switch (rand.nextInt(4)) {
            case 0:
                c = Color.WHITE;
                break;
            case 1:
                c = associatedType.getConstellationColor()
                    .brighter();
                break;
            case 2:
            default:
                c = associatedType.getConstellationColor();
                break;
        }
        p.setColor(c);

        if (usedCrystalProperties != null
            && (usedCrystalProperties.getPurity() > 90 || usedCrystalProperties.getCollectiveCapability() > 90)
            && rand.nextInt(100) == 0) {
            // 1.7.10: use getWorld() instead of world
            AstralSorcery.proxy.fireLightning(getWorld(), to, from, c);
        }
    }

    @Override
    public boolean onSelect(EntityPlayer player) {
        if (player.isSneaking()) {
            for (BlockPos linkTo : Lists.newArrayList(getLinkedPositions())) {
                tryUnlink(player, linkTo);
            }
            // 1.7.10: ChatStyle works differently - chain the calls on the component
            ChatComponentTranslation msg = new ChatComponentTranslation("misc.link.unlink.all");
            msg.getChatStyle()
                .setColor(EnumChatFormatting.GREEN);
            player.addChatMessage(msg);
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Color getEffectRenderColor() {
        return providesEffect() ? Color.WHITE : null;
    }

    @Override
    public double getRadius() {
        return providesEffect() ? 16 : 0;
    }

    @Override
    public boolean providesEffect() {
        return this.doesSeeSky();
    }

    @Override
    public int getDimensionId() {
        return this.worldObj.provider.dimensionId;
    }

    public boolean isPlayerMade() {
        return playerRef != null;
    }

    public UUID getPlayerReference() {
        return playerRef;
    }

    public CrystalProperties getCrystalProperties() {
        return usedCrystalProperties;
    }

    public IWeakConstellation getConstellation() {
        return associatedType;
    }

    public IMinorConstellation getTrait() {
        return associatedTrait;
    }

    public void onPlace(IWeakConstellation constellation, @Nullable IMinorConstellation trait,
        CrystalProperties properties, @Nullable UUID player, BlockCollectorCrystalBase.CollectorCrystalType type) {
        this.associatedType = constellation;
        this.associatedTrait = trait;
        this.playerRef = player;
        this.usedCrystalProperties = properties;
        this.type = type;

        this.needsUpdate = true;
        markDirty();
    }

    public void setEnhanced(boolean enhanced) {
        if (!getWorld().isRemote && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            this.multiBlockPresent = enhanced;
            // 1.7.10: use getWorld() instead of world
            WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(getWorld());
            IIndependentStarlightSource source = handle.getSourceAt(getPos());
            if (source instanceof IndependentCrystalSource) {
                ((IndependentCrystalSource) source).setEnhanced(enhanced);
                handle.markDirty();
            }
            markForUpdate();
        }
    }

    public boolean isEnhanced() {
        return multiBlockPresent;
    }

    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {
        BlockPos at = event.getVec()
            .toBlockPos();
        EffectHandler.getInstance()
            .registerFX(new EntityFXBurst(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, 3F));
    }

    public static void breakDamage(World world, BlockPos pos) {}

    public BlockCollectorCrystalBase.CollectorCrystalType getType() {
        return type;
    }

    @Override
    public boolean hasBeenLinked() {
        return playerRef == null;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        // 1.7.10: hasUniqueId/getUniqueId don't exist, use manual UUID handling
        if (compound.hasKey("playerRefMost") && compound.hasKey("playerRefLeast")) {
            this.playerRef = new UUID(compound.getLong("playerRefMost"), compound.getLong("playerRefLeast"));
        } else if (compound.hasKey("player") && compound.getBoolean("player")) {
            this.playerRef = DUMMY_UUID; // Legacy data conversion..
        } else {
            this.playerRef = null;
        }
        this.associatedType = (IWeakConstellation) IConstellation.readFromNBT(compound);
        this.associatedTrait = (IMinorConstellation) IConstellation
            .readFromNBT(compound, IConstellation.getDefaultSaveKey() + "trait");
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
        this.type = BlockCollectorCrystalBase.CollectorCrystalType.values()[compound.getInteger("collectorType")];
        this.multiBlockPresent = compound.hasKey("enhanced") ? compound.getBoolean("enhanced")
            : compound.getBoolean("multiBlockPresent");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        // 1.7.10: setUniqueId doesn't exist, use manual UUID handling
        if (this.playerRef != null) {
            compound.setLong("playerRefMost", this.playerRef.getMostSignificantBits());
            compound.setLong("playerRefLeast", this.playerRef.getLeastSignificantBits());
        }
        if (associatedType != null) {
            associatedType.writeToNBT(compound);
        }
        if (associatedTrait != null) {
            associatedTrait.writeToNBT(compound, IConstellation.getDefaultSaveKey() + "trait");
        }
        if (usedCrystalProperties != null) {
            usedCrystalProperties.writeToNBT(compound);
        }
        if (type != null) {
            compound.setInteger("collectorType", type.ordinal());
        }
        compound.setBoolean("multiBlockPresent", multiBlockPresent);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        // 1.7.10: FULL_BLOCK_AABB doesn't exist, create AxisAlignedBB manually
        return AxisAlignedBB.getBoundingBox(
            getPos().getX() - 1,
            getPos().getY() - 1,
            getPos().getZ() - 1,
            getPos().getX() + 2,
            getPos().getY() + 2,
            getPos().getZ() + 2);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockcollectorcrystal.name";
    }

    @Override
    @Nonnull
    public IIndependentStarlightSource provideNewSourceNode() {
        return new IndependentCrystalSource(usedCrystalProperties, associatedType, doesSeeSky, hasBeenLinked(), type);
    }

    @Override
    @Nonnull
    public ITransmissionSource provideSourceNode(BlockPos at) {
        return new SimpleTransmissionSourceNode(at);
    }

}
