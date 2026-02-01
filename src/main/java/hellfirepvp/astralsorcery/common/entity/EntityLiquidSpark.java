/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * EntityLiquidSpark - Liquid spark entity
 *
 * SKELETON VERSION - Complex fluid interaction logic commented out with TODOs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * EntityLiquidSpark - Liquid spark (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Flying entity that carries fluids</li>
 * <li>Can target other entities or tile entities</li>
 * <li>Complex fluid interaction logic</li>
 * <li>Particle effects when ambient</li>
 * </ul>
 */
public class EntityLiquidSpark extends EntityFlying implements EntityTechnicalAmbient {

    private static final int DATA_TILE_ID_IDX = 16; // DataWatcher index for TileEntity target ID

    // TODO: Uncomment when LiquidInteraction is available
    // private LiquidInteraction purpose;
    private TileEntity tileTarget;
    // 1.7.10: Store coordinates instead of BlockPos
    private int[] resolvableTilePos = null;

    public EntityLiquidSpark(World worldIn) {
        super(worldIn);
        setSize(0.4F, 0.4F);
        this.noClip = true;
    }

    public EntityLiquidSpark(World world, double x, double y, double z) {
        super(world);
        setSize(0.4F, 0.4F);
        setPosition(x + 0.5, y + 0.5, z + 0.5);
        this.noClip = true;
    }

    public EntityLiquidSpark(World world, double x, double y, double z, Object purposeOfLiving) {
        super(world);
        setSize(0.4F, 0.4F);
        setPosition(x + 0.5, y + 0.5, z + 0.5);
        this.noClip = true;
        // TODO: Set purpose when LiquidInteraction is available
        // this.purpose = purposeOfLiving;
    }

    public EntityLiquidSpark(World world, double x, double y, double z, TileEntity target) {
        super(world);
        setSize(0.4F, 0.4F);
        setPosition(x + 0.5, y + 0.5, z + 0.5);
        this.noClip = true;
        this.tileTarget = target;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        // 1.7.10: Register TileEntity target ID in DataWatcher
        // We encode x, y, z into a single integer: x in high bits, z in mid, y in low
        this.getDataWatcher()
            .addObject(DATA_TILE_ID_IDX, Integer.valueOf(-1));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getAttributeMap()
            .registerAttribute(SharedMonsterAttributes.movementSpeed);

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(2.0D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (isDead) return;

        if (this.resolvableTilePos != null) {
            // TODO: Resolve tile entity from coordinates
            this.resolvableTilePos = null;
        }

        if (!worldObj.isRemote) {
            if (ticksExisted > 800) {
                setDead();
                return;
            }

            // Simplified entity filtering for 1.7.10
            List<Entity> nearby = this.worldObj
                .getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1, 1, 1));
            if (nearby.size() > 2) {
                setDead();
                return;
            }

            if (tileTarget != null) {
                if (tileTarget.isInvalid()) {
                    setDead();
                    return;
                }

                // 1.7.10: Use coordinates
                double targetX = tileTarget.xCoord + 0.5;
                double targetY = tileTarget.yCoord + 0.5;
                double targetZ = tileTarget.zCoord + 0.5;

                if (getDistance(targetX, targetY, targetZ) < 1.1F) {
                    setDead();
                    // TODO: Implement fluid transfer when fluid system is available
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
    protected String getDeathSound() {
        return null;
    }

    @Nullable
    @Override
    protected String getHurtSound() {
        return null;
    }

    @Nullable
    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0;
    }

    /**
     * Set the TileEntity target
     * 1.7.10: Encodes TileEntity position into DataWatcher
     */
    public void setTileTarget(TileEntity tile) {
        this.tileTarget = tile;
        if (tile == null) {
            this.getDataWatcher()
                .updateObject(DATA_TILE_ID_IDX, Integer.valueOf(-1));
        } else {
            // Encode x, y, z into a single integer
            int encoded = (tile.xCoord & 0xFFF) << 20 | (tile.yCoord & 0xFF) << 12 | (tile.zCoord & 0xFFF);
            this.getDataWatcher()
                .updateObject(DATA_TILE_ID_IDX, Integer.valueOf(encoded));
        }
    }

    /**
     * Get the TileEntity target
     * 1.7.10: Decodes position from DataWatcher and looks up TileEntity
     */
    @Nullable
    public TileEntity getTileTarget() {
        if (this.tileTarget != null && !this.tileTarget.isInvalid()) {
            return this.tileTarget;
        }

        // Decode position from DataWatcher
        int encoded = this.getDataWatcher()
            .getWatchableObjectInt(DATA_TILE_ID_IDX);
        if (encoded == -1) {
            return null;
        }

        int x = (encoded >> 20) & 0xFFF;
        int y = (encoded >> 12) & 0xFF;
        int z = encoded & 0xFFF;

        this.tileTarget = this.worldObj.getTileEntity(x, y, z);
        return this.tileTarget;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("tileTarget")) {
            // TODO: Read coordinates from NBT
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (this.tileTarget != null) {
            // TODO: Write coordinates to NBT
        }
    }

    /**
     * Play ambient particle effects
     * 1.7.10: Simplified version (EntityFXFloatingCube not available)
     * EffectHelper is now implemented for 1.7.10
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    private void playAmbientParticles() {
        // 1.7.10: Simplified particle effect - only flare particles
        // EntityFXFloatingCube and advanced texture rendering not available
        Vector3 at = Vector3.atEntityCenter(this);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
        p.setColor(java.awt.Color.WHITE)
            .scale(0.3F + rand.nextFloat() * 0.1F)
            .setMaxAge(20 + rand.nextInt(10));
    }

}
