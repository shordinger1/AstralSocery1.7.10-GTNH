/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base Entity class for all AstralSorcery entities
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * AstralBaseEntity - Base class for all AstralSorcery entities
 * <p>
 * Provides common functionality including:
 * - Simplified NBT read/write
 * - Lifecycle management
 * - Drop item helpers
 * - Automatic data sync support
 * - Persistence control
 * <p>
 * All AstralSorcery entities should extend this class.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public abstract class AstralBaseEntity extends Entity {

    // ========== Configuration Constants ==========

    /** Whether to prevent natural despawn */
    protected boolean persistenceRequired = true;

    /** Whether entity is invulnerable */
    protected boolean invulnerable = false;

    // ========== DataWatcher Indices ==========

    /** Custom data start index (subclasses start from this) */
    protected static final int DATA_CUSTOM_START = 20;

    // ========== Constructors ==========

    /**
     * Create entity
     * 
     * @param world World instance
     */
    public AstralBaseEntity(World world) {
        super(world);
        this.initEntity();
    }

    // ========== Core Initialization ==========

    /**
     * Initialize entity (called in constructor)
     * Subclasses should override this instead of constructor
     */
    protected void initEntity() {
        this.setSize(1.0F, 1.0F);
        this.persistenceRequired = true;
    }

    /**
     * Set entity size
     * 
     * @param width  Width
     * @param height Height
     */
    @Override
    protected void setSize(float width, float height) {
        super.setSize(width, height);
    }

    // ========== Must Implement ==========

    @Override
    protected abstract void entityInit();

    @Override
    public abstract void readEntityFromNBT(NBTTagCompound tagCompund);

    @Override
    public abstract void writeEntityToNBT(NBTTagCompound tagCompound);

    // ========== Lifecycle ==========

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.invulnerable && this.ticksExisted % 10 == 0) {
            this.extinguish();
        }

        if (!this.worldObj.isRemote && !this.persistenceRequired) {
            this.despawn();
        }
    }

    /**
     * Natural despawn logic
     */
    protected void despawn() {
        if (this.canDespawn()) {
            int i = 128;
            if (this.worldObj.getClosestPlayerToEntity(this, (double) i) == null) {
                this.setDead();
            }
        }
    }

    /**
     * Check if can despawn
     * 
     * @return true if can despawn
     */
    protected boolean canDespawn() {
        return !this.persistenceRequired;
    }

    // ========== NBT Helpers ==========

    /**
     * Write position to NBT
     * 
     * @param tag NBT tag
     * @return Tag with position
     */
    protected NBTTagCompound writePositionToNBT(NBTTagCompound tag) {
        tag.setDouble("PosX", this.posX);
        tag.setDouble("PosY", this.posY);
        tag.setDouble("PosZ", this.posZ);
        return tag;
    }

    /**
     * Read position from NBT
     * 
     * @param tag NBT tag
     */
    protected void readPositionFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("PosX")) {
            this.posX = tag.getDouble("PosX");
        }
        if (tag.hasKey("PosY")) {
            this.posY = tag.getDouble("PosY");
        }
        if (tag.hasKey("PosZ")) {
            this.posZ = tag.getDouble("PosZ");
        }

        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * Write rotation to NBT
     * 
     * @param tag NBT tag
     * @return Tag with rotation
     */
    protected NBTTagCompound writeRotationToNBT(NBTTagCompound tag) {
        tag.setFloat("RotationYaw", this.rotationYaw);
        tag.setFloat("RotationPitch", this.rotationPitch);
        return tag;
    }

    /**
     * Read rotation from NBT
     * 
     * @param tag NBT tag
     */
    protected void readRotationFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("RotationYaw")) {
            this.rotationYaw = tag.getFloat("RotationYaw");
        }
        if (tag.hasKey("RotationPitch")) {
            this.rotationPitch = tag.getFloat("RotationPitch");
        }

        this.setRotation(this.rotationYaw, this.rotationPitch);
    }

    // ========== Drop Item Helpers ==========

    /**
     * Drop item stack
     * 
     * @param stack Item stack
     * @return Dropped entity item
     */
    public EntityItem dropItemStack(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return null;
        }

        if (!this.worldObj.isRemote) {
            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + 0.5D, this.posZ, stack);

            entityitem.delayBeforeCanPickup = 40;
            this.worldObj.spawnEntityInWorld(entityitem);
            return entityitem;
        }

        return null;
    }

    /**
     * Drop single item
     * 
     * @param item Item
     * @param size Count
     * @return Dropped entity item
     */
    public EntityItem dropItem(ItemStack item, int size) {
        if (item == null) {
            return null;
        }

        ItemStack stack = item.copy();
        stack.stackSize = size;
        return dropItemStack(stack);
    }

    /**
     * Drop item at random offset
     * 
     * @param item    Item
     * @param size    Count
     * @param yOffset Y offset
     * @return Dropped entity item
     */
    public EntityItem dropItemWithOffset(ItemStack item, int size, float yOffset) {
        if (item == null) {
            return null;
        }

        if (!this.worldObj.isRemote) {
            ItemStack stack = item.copy();
            stack.stackSize = size;

            EntityItem entityitem = new EntityItem(
                this.worldObj,
                this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                this.posY + (double) yOffset,
                this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                stack);

            entityitem.delayBeforeCanPickup = 40;
            this.worldObj.spawnEntityInWorld(entityitem);
            return entityitem;
        }

        return null;
    }

    /**
     * Drop item list
     * 
     * @param drops Item stack list
     */
    public void dropItems(List<ItemStack> drops) {
        if (drops == null || drops.isEmpty()) {
            return;
        }

        for (ItemStack drop : drops) {
            if (drop != null && drop.stackSize > 0) {
                dropItemWithOffset(drop, drop.stackSize, 0.5F);
            }
        }
    }

    // ========== Damage and Death ==========

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.invulnerable) {
            return false;
        }

        if (this.isDead) {
            return false;
        }

        if (this.worldObj.isRemote) {
            return false;
        }

        this.handleDamage(source, amount);

        return true;
    }

    /**
     * Handle damage
     * Subclasses can override to customize damage handling
     * 
     * @param source Damage source
     * @param amount Damage amount
     */
    protected void handleDamage(DamageSource source, float amount) {
        this.setDead();
    }

    /**
     * Drop items on death
     * Subclasses can override to customize drops
     */
    protected void dropItemsOnDeath() {
        // Default: no drops
    }

    // ========== Setters ==========

    /**
     * Set persistence required
     * 
     * @param persistence true to prevent despawn
     */
    public void setPersistenceRequired(boolean persistence) {
        this.persistenceRequired = persistence;
    }

    /**
     * Set invulnerable
     * 
     * @param invulnerable true for invulnerable
     */
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return this.invulnerable || super.isEntityInvulnerable();
    }

    // ========== Collision and Interaction ==========

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

}
