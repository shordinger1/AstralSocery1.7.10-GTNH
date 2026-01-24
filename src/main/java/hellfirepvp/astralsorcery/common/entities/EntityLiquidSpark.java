/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;
// TODO: Forge fluid system - manual review needed

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.base.LiquidInteraction;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktLiquidInteractionBurst;
import hellfirepvp.astralsorcery.common.tile.ILiquidStarlightPowered;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityLiquidSpark
 * Created by HellFirePvP
 * Date: 28.10.2017 / 14:39
 */
public class EntityLiquidSpark extends EntityFlying implements EntityTechnicalAmbient {

    private static final int ENTITY_TARGET_DATAWATCHER_ID = 20;
    private static final int FLUID_REPRESENTED_DATAWATCHER_ID = 21;

    private LiquidInteraction purpose;
    private TileEntity tileTarget;
    private BlockPos resolvableTilePos = null;

    public EntityLiquidSpark(World worldIn) {
        super(worldIn);
        setSize(0.4F, 0.4F);
        this.noClip = true;
        this.moveHelper = new EntityFlyHelper(this);
        this.purpose = null;
    }

    public EntityLiquidSpark(World world, BlockPos spawnPos, LiquidInteraction purposeOfLiving) {
        super(world);
        setSize(0.4F, 0.4F);
        setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        this.noClip = true;
        this.moveHelper = new EntityFlyHelper(this);
        this.purpose = purposeOfLiving;
    }

    public EntityLiquidSpark(World world, BlockPos spawnPos, TileEntity target) {
        super(world);
        setSize(0.4F, 0.4F);
        setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        this.noClip = true;
        this.moveHelper = new EntityFlyHelper(this);
        this.tileTarget = target;
    }

    public void setTarget(EntityLiquidSpark other) {
        this.dataWatcher.updateObject(ENTITY_TARGET_DATAWATCHER_ID, other.getEntityId());
    }

    public void setFluidRepresented(FluidStack fs) {
        this.dataWatcher.updateObject(FLUID_REPRESENTED_DATAWATCHER_ID, fs);
    }

    public FluidStack getRepresentitiveFluid() {
        return (FluidStack) this.dataWatcher.getWatchableObjectObject(FLUID_REPRESENTED_DATAWATCHER_ID);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataWatcher.addObject(ENTITY_TARGET_DATAWATCHER_ID, -1);
        this.dataWatcher.addObject(FLUID_REPRESENTED_DATAWATCHER_ID, null);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getAttributeMap()
            .registerAttribute(SharedMonsterAttributes.FLYING_SPEED);

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED)
            .setBaseValue(0.35);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (isDead) return;

        this.noClip = worldObj.getBlock(new BlockPos(this))
            .equals(BlocksAS.blockChalice);

        if (this.resolvableTilePos != null) {
            this.tileTarget = MiscUtils.getTileAt(worldObj, resolvableTilePos, TileEntity.class, true);
            this.resolvableTilePos = null;
        }

        if (!worldObj.isRemote) {
            if (ticksExisted > 800) {
                setDead();
                return;
            }

            // 1.7.10: EntitySelectors doesn't exist, use IEntitySelector
            List<Entity> nearby = worldObj.getEntitiesInAABBexcluding(
                this,
                this.boundingBox.grow(1),
                new net.minecraft.command.IEntitySelector() {

                    @Override
                    public boolean isEntityApplicable(Entity entity) {
                        // Check if alive and not spectating
                        return entity.isEntityAlive()
                            && !(entity instanceof EntityPlayerMP && ((EntityPlayerMP) entity).isSpectator());
                    }
                });
            if (nearby.size() > 2) {
                setDead();
                return;
            }

            if (purpose != null) {
                int target = this.dataWatcher.getWatchableObjectInt(ENTITY_TARGET_DATAWATCHER_ID);
                if (target == -1) {
                    setDead();
                    return;
                }
                Entity e = worldObj.getEntityByID(target);
                if (e == null || e.isDead || !(e instanceof EntityLiquidSpark)) {
                    setDead();
                    return;
                }

                if (getDistance(e) < 0.7F) {
                    setDead();
                    e.setDead();
                    Vector3 at = Vector3.atEntityCenter(e)
                        .subtract(Vector3.atEntityCenter(this))
                        .divide(2)
                        .add(Vector3.atEntityCenter(this));
                    purpose.triggerInteraction(worldObj, at);
                    PktLiquidInteractionBurst ev = new PktLiquidInteractionBurst(
                        this.purpose.getComponent1(),
                        this.purpose.getComponent2(),
                        at);
                    PacketChannel.CHANNEL
                        .sendToAllAround(ev, PacketChannel.pointFromPos(worldObj, at.toBlockPos(), 32));
                } else {
                    this.moveHelper.setMoveTo(e.posX, e.posY, e.posZ, 2.4F);
                }
            } else if (tileTarget != null) {
                if (tileTarget.isInvalid()
                    || MiscUtils.getTileAt(worldObj, tileTarget.getPos(), tileTarget.getClass(), true) == null) {
                    setDead();
                    return;
                }
                Vector3 target = new Vector3(tileTarget.getPos()).add(0.5, 0.5, 0.5);

                if (getDistance(target.getX(), target.getY(), target.getZ()) < 1.1F) {
                    setDead();
                    FluidStack contained = getRepresentitiveFluid();
                    if (contained == null) {
                        return;
                    }

                    if (contained.getFluid() == BlocksAS.fluidLiquidStarlight
                        && tileTarget instanceof ILiquidStarlightPowered) {
                        ((ILiquidStarlightPowered) tileTarget).acceptStarlight(contained.amount);
                    } else if (tileTarget instanceof IFluidHandler) {
                        IFluidHandler handler = (IFluidHandler) tileTarget;
                        // 1.7.10: Use ForgeDirection.UNKNOWN
                        handler.fill(ForgeDirection.UNKNOWN, contained, true);
                    }
                    if (tileTarget instanceof TileEntitySynchronized) {
                        ((TileEntitySynchronized) tileTarget).markForUpdate();
                    } else {
                        tileTarget.markDirty();
                    }
                    Vector3 at = Vector3.atEntityCenter(this);

                    PktLiquidInteractionBurst ev = new PktLiquidInteractionBurst(contained, contained, at);
                    PacketChannel.CHANNEL
                        .sendToAllAround(ev, PacketChannel.pointFromPos(worldObj, at.toBlockPos(), 32));
                } else {
                    this.moveHelper.setMoveTo(target.getX(), target.getY(), target.getZ(), 2.4F);
                }
            } else {
                setDead();
            }
        } else {
            playAmbientParticles();
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Nullable
    @Override
    // 1.7.10: Return String instead of SoundEvent
    protected String getDeathSound() {
        return null;
    }

    @Nullable
    @Override
    // 1.7.10: Return String instead of SoundEvent, takes float parameter
    protected String getHurtSound() {
        return null;
    }

    @Nullable
    @Override
    // 1.7.10: Return String instead of SoundEvent
    protected String getLivingSound() {
        return null;
    }

    @Override
    // 1.7.10: getFallSound doesn't exist, entity fall sounds are handled differently
    protected String getFallSoundString() {
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("tileTarget")) {
            this.resolvableTilePos = NBTHelper.readBlockPosFromNBT(compound.getCompoundTag("tileTarget"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (this.tileTarget != null) {
            NBTHelper.setAsSubTag(
                compound,
                "tileTarget",
                tag -> NBTHelper.writeBlockPosToNBT(this.tileTarget.getPos(), tag));
        }
    }

    @SideOnly(Side.CLIENT)
    private void playAmbientParticles() {
        FluidStack stack = getRepresentitiveFluid();
        if (stack == null) return;
        TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(stack);

        Vector3 at = Vector3.atEntityCenter(this);
        EntityFXFloatingCube cube = RenderingUtils.spawnFloatingBlockCubeParticle(at, tas);
        cube.setTextureSubSizePercentage(1F / 16F)
            .setMaxAge(20 + rand.nextInt(20));
        cube.setWorldLightCoord(Minecraft.getMinecraft().theWorld, at.toBlockPos());
        cube.setColorHandler(
            cb -> new Color(
                stack.getFluid()
                    .getColor(stack)));
        cube.setScale(0.14F)
            .tumble()
            .setMotion(
                rand.nextFloat() * 0.02F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.02F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.02F * (rand.nextBoolean() ? 1 : -1));

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at);
        p.setColor(Color.WHITE)
            .scale(0.3F + rand.nextFloat() * 0.1F)
            .setMaxAge(20 + rand.nextInt(10));
    }

}
