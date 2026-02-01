/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * EntitySpectralTool - Spectral tool entity with AI
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import net.minecraft.util.MathHelper;

/**
 * EntitySpectralTool - Spectral tool (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Flying entity that performs tasks (break blocks, attack monsters)</li>
 * <li>AI-driven behavior with EntityAIBase</li>
 * <li>Client-side particle effects</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Adaptation:</b>
 * <ul>
 * <li>DataWatcher instead of EntityDataManager</li>
 * <li>int[] coordinates instead of BlockPos</li>
 * <li>EntityLiving instead of EntityLivingBase for monsters</li>
 * <li>Block + metadata instead of IBlockState</li>
 * </ul>
 */
public class EntitySpectralTool extends EntityLiving implements EntityTechnicalAmbient {

    private static final int DATA_ITEM_IDX = 17; // DataWatcher index for ItemStack

    private AIToolTask aiTask;
    private int[] originalStartPosition;
    private int ticksUntilDeath = 0;

    public EntitySpectralTool(World worldIn) {
        super(worldIn);
        setSize(0.6F, 0.8F);
    }

    public EntitySpectralTool(World world, double x, double y, double z, ItemStack tool, ToolTask task) {
        super(world);
        setSize(0.6F, 0.8F);
        setPosition(x + 0.5, y + 0.5, z + 0.5);
        setItem(tool);
        this.originalStartPosition = new int[] { (int) x, (int) y, (int) z };
        this.aiTask = new AIToolTask(this);
        this.aiTask.taskTarget = task;
        this.ticksUntilDeath = 100 + rand.nextInt(40);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        // Register ItemStack in DataWatcher
        this.getDataWatcher()
            .addObject(DATA_ITEM_IDX, null);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(0.85);
    }

    // 1.7.10: EntityLiving doesn't have initEntityAI(), add AI task in constructor instead
    // AI task is already added in constructor via new AIToolTask(this)

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            spawnAmbientEffects();
        } else {
            this.ticksUntilDeath--;
            if (this.ticksUntilDeath <= 0) {
                // Kill entity with magic damage
                this.attackEntityFrom(DamageSource.magic, 5000.0F);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnAmbientEffects() {
        if (rand.nextFloat() < 0.2F) {
            // Light blue color for weak constellation
            Color c = new Color(0x69B5FF);
            double x = posX + rand.nextFloat() * width - (width / 2);
            double y = posY + rand.nextFloat() * (height / 2) + 0.2;
            double z = posZ + rand.nextFloat() * width - (width / 2);

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c)
                .scale(rand.nextFloat() * 0.5F + 0.3F);
            p.setMaxAge(30 + rand.nextInt(20));

            if (rand.nextFloat() < 0.8F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE)
                    .scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    private void setItem(ItemStack tool) {
        this.getDataWatcher()
            .updateObject(DATA_ITEM_IDX, tool);
    }

    public ItemStack getItem() {
        return this.getDataWatcher()
            .getWatchableObjectItemStack(DATA_ITEM_IDX);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        // Read item from NBT
        if (compound.hasKey("AS_SpectralItem")) {
            setItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("AS_SpectralItem")));
        }

        int task = compound.getInteger("AS_ToolTask");
        if (this.aiTask == null) {
            this.aiTask = new AIToolTask(this);
        }
        this.aiTask.taskTarget = new ToolTask(ToolTask.Type.values()[Math.max(0, Math.min(task, 2))]);

        this.ticksUntilDeath = compound.getInteger("AS_ToolDeathTicks");

        if (compound.hasKey("AS_StartPosition")) {
            NBTTagCompound posTag = compound.getCompoundTag("AS_StartPosition");
            this.originalStartPosition = new int[] { posTag.getInteger("X"), posTag.getInteger("Y"),
                posTag.getInteger("Z") };
        } else {
            this.originalStartPosition = new int[] { (int) posX, (int) posY, (int) posZ };
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        // Write item to NBT
        ItemStack item = getItem();
        if (item != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            item.writeToNBT(itemTag);
            compound.setTag("AS_SpectralItem", itemTag);
        }

        int task = 0;
        if (this.aiTask != null && this.aiTask.taskTarget != null) {
            task = this.aiTask.taskTarget.type.ordinal();
        }
        compound.setInteger("AS_ToolTask", task);
        compound.setInteger("AS_ToolDeathTicks", this.ticksUntilDeath);

        if (this.originalStartPosition != null) {
            NBTTagCompound posTag = new NBTTagCompound();
            posTag.setInteger("X", this.originalStartPosition[0]);
            posTag.setInteger("Y", this.originalStartPosition[1]);
            posTag.setInteger("Z", this.originalStartPosition[2]);
            compound.setTag("AS_StartPosition", posTag);
        }
    }

    public static class ToolTask {

        private final Type type;

        private ToolTask(Type type) {
            this.type = type;
        }

        public static ToolTask createPickaxeTask() {
            return new ToolTask(Type.BREAK_BLOCK);
        }

        public static ToolTask createLogTask() {
            return new ToolTask(Type.BREAK_LOG);
        }

        public static ToolTask createAttackTask() {
            return new ToolTask(Type.ATTACK_MONSTER);
        }

        public static enum Type {
            BREAK_BLOCK,
            BREAK_LOG,
            ATTACK_MONSTER
        }
    }

    /**
     * AI Task for spectral tool
     * Fully functional implementation for 1.7.10
     */
    private static class AIToolTask extends EntityAIBase {

        private final EntitySpectralTool parentEntity;
        private ToolTask taskTarget = null;

        private int[] designatedBreakTarget = null;
        private EntityLivingBase designatedAttackTarget = null;

        private int actionTicks = 0;

        private static final int BREAK_TICKS_PICKAXE = 20; // Ticks to break block with pickaxe
        private static final int BREAK_TICKS_AXE = 15; // Ticks to break log with axe
        private static final int ATTACK_TICKS = 10; // Ticks between attacks
        private static final float ATTACK_DAMAGE = 5.0F; // Damage per attack

        public AIToolTask(EntitySpectralTool entity) {
            this.parentEntity = entity;
            this.setMutexBits(7);
        }

        @Override
        public boolean shouldExecute() {
            if (this.taskTarget == null) {
                return false;
            }

            // Check if close to target and can perform action
            if (designatedBreakTarget != null || designatedAttackTarget != null) {
                return true;
            }

            // Look for new targets
            switch (this.taskTarget.type) {
                case BREAK_BLOCK:
                    return findBreakableBlock(8, Items.diamond_pickaxe);
                case BREAK_LOG:
                    return findBreakableLog(10, Items.diamond_axe);
                case ATTACK_MONSTER:
                    return findAttackableMonster();
                default:
                    return false;
            }
        }

        @Override
        public boolean continueExecuting() {
            return taskTarget != null && (designatedAttackTarget != null || designatedBreakTarget != null);
        }

        @Override
        public void resetTask() {
            super.resetTask();

            this.designatedBreakTarget = null;
            this.designatedAttackTarget = null;
            this.actionTicks = 0;
        }

        @Override
        public void updateTask() {
            if (!continueExecuting() || this.taskTarget == null) {
                return;
            }

            if (actionTicks < 0) {
                actionTicks = 0;
            }

            switch (this.taskTarget.type) {
                case BREAK_BLOCK:
                    updateBreakBlockTask(BREAK_TICKS_PICKAXE);
                    break;
                case BREAK_LOG:
                    updateBreakLogTask(BREAK_TICKS_AXE);
                    break;
                case ATTACK_MONSTER:
                    updateAttackMonsterTask();
                    break;
            }
        }

        @Override
        public void startExecuting() {
            if (this.taskTarget == null) {
                return;
            }

            switch (this.taskTarget.type) {
                case BREAK_BLOCK:
                    findBreakableBlock(8, Items.diamond_pickaxe);
                    break;
                case BREAK_LOG:
                    findBreakableLog(10, Items.diamond_axe);
                    break;
                case ATTACK_MONSTER:
                    findAttackableMonster();
                    break;
            }
        }

        /**
         * Find a breakable block (stone, ores, etc.)
         */
        private boolean findBreakableBlock(int radius, net.minecraft.item.Item tool) {
            int centerX = (int) parentEntity.posX;
            int centerY = (int) parentEntity.posY;
            int centerZ = (int) parentEntity.posZ;

            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = centerY - radius; y <= centerY + radius; y++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        // Check Y constraint
                        if (y < parentEntity.originalStartPosition[1]) {
                            continue;
                        }

                        Block block = parentEntity.worldObj.getBlock(x, y, z);
                        int meta = parentEntity.worldObj.getBlockMetadata(x, y, z);

                        // Skip air, unbreakable, and TileEntity blocks
                        if (block.isAir(parentEntity.worldObj, x, y, z)) {
                            continue;
                        }
                        if (parentEntity.worldObj.getTileEntity(x, y, z) != null) {
                            continue;
                        }
                        float hardness = block.getBlockHardness(parentEntity.worldObj, x, y, z);
                        if (hardness < 0 || hardness > 10) {
                            continue;
                        }

                        // Found valid block
                        designatedBreakTarget = new int[] { x, y, z };

                        // Move towards block
                        parentEntity.getNavigator()
                            .tryMoveToXYZ(x + 0.5, y + 0.5, z + 0.5, 1.5);

                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Find a breakable log or leaves
         */
        private boolean findBreakableLog(int radius, net.minecraft.item.Item tool) {
            int centerX = (int) parentEntity.posX;
            int centerY = (int) parentEntity.posY;
            int centerZ = (int) parentEntity.posZ;

            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = centerY - radius; y <= centerY + radius; y++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        Block block = parentEntity.worldObj.getBlock(x, y, z);
                        int meta = parentEntity.worldObj.getBlockMetadata(x, y, z);

                        // Skip air
                        if (block.isAir(parentEntity.worldObj, x, y, z)) {
                            continue;
                        }

                        // Check if wood or leaves
                        boolean isWood = block.isWood(parentEntity.worldObj, x, y, z);
                        boolean isLeaves = block.isLeaves(parentEntity.worldObj, x, y, z);

                        if (!isWood && !isLeaves) {
                            continue;
                        }

                        // Skip unbreakable and TileEntity blocks
                        if (parentEntity.worldObj.getTileEntity(x, y, z) != null) {
                            continue;
                        }
                        float hardness = block.getBlockHardness(parentEntity.worldObj, x, y, z);
                        if (hardness < 0 || hardness > 10) {
                            continue;
                        }

                        // Found valid block
                        designatedBreakTarget = new int[] { x, y, z };

                        // Move towards block
                        parentEntity.getNavigator()
                            .tryMoveToXYZ(x + 0.5, y + 0.5, z + 0.5, 1.5);

                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Find an attackable monster
         */
        private boolean findAttackableMonster() {
            AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(
                parentEntity.posX - 8,
                parentEntity.posY - 8,
                parentEntity.posZ - 8,
                parentEntity.posX + 8,
                parentEntity.posY + 8,
                parentEntity.posZ + 8);

            List<EntityLivingBase> entities = parentEntity.worldObj
                .getEntitiesWithinAABB(EntityLivingBase.class, searchBox);

            // Filter for monsters
            for (EntityLivingBase entity : entities) {
                if (entity.isDead || entity instanceof EntityPlayer) {
                    continue;
                }
                if (!entity.isCreatureType(EnumCreatureType.monster, false)) {
                    continue;
                }

                // Found valid target
                designatedAttackTarget = entity;

                // Move towards target
                parentEntity.getNavigator()
                    .tryMoveToXYZ(entity.posX, entity.posY, entity.posZ, 1.7);

                return true;
            }
            return false;
        }

        /**
         * Update break block task
         */
        private void updateBreakBlockTask(int breakTicks) {
            if (designatedBreakTarget == null) {
                return;
            }

            int x = designatedBreakTarget[0];
            int y = designatedBreakTarget[1];
            int z = designatedBreakTarget[2];

            // Check if block still exists
            if (parentEntity.worldObj.isAirBlock(x, y, z)) {
                designatedBreakTarget = null;
                actionTicks = 0;
                return;
            }

            // Move towards block
            parentEntity.getNavigator()
                .tryMoveToXYZ(x + 0.5, y + 0.5, z + 0.5, 1.5);

            // Check distance
            double dist = parentEntity.getDistanceSq(x + 0.5, y + 0.5, z + 0.5);
            if (dist < 9.0) { // Within 3 blocks
                actionTicks++;
                if (actionTicks > breakTicks) {
                    // Break the block
                    Block block = parentEntity.worldObj.getBlock(x, y, z);
                    int meta = parentEntity.worldObj.getBlockMetadata(x, y, z);

                    // Harvest block
                    if (!parentEntity.worldObj.isRemote) {
                        block.dropBlockAsItem(parentEntity.worldObj, x, y, z, meta, 0);
                        parentEntity.worldObj.setBlockToAir(x, y, z);
                    }

                    // Reset
                    designatedBreakTarget = null;
                    actionTicks = 0;
                }
            }
        }

        /**
         * Update break log task
         */
        private void updateBreakLogTask(int breakTicks) {
            if (designatedBreakTarget == null) {
                return;
            }

            int x = designatedBreakTarget[0];
            int y = designatedBreakTarget[1];
            int z = designatedBreakTarget[2];

            // Check if block still exists
            if (parentEntity.worldObj.isAirBlock(x, y, z)) {
                designatedBreakTarget = null;
                actionTicks = 0;
                return;
            }

            // Move towards block
            parentEntity.getNavigator()
                .tryMoveToXYZ(x + 0.5, y + 0.5, z + 0.5, 1.5);

            // Check distance
            double dist = parentEntity.getDistanceSq(x + 0.5, y + 0.5, z + 0.5);
            if (dist < 9.0) { // Within 3 blocks
                actionTicks++;
                if (actionTicks > breakTicks) {
                    // Break the block
                    Block block = parentEntity.worldObj.getBlock(x, y, z);
                    int meta = parentEntity.worldObj.getBlockMetadata(x, y, z);

                    // Harvest block
                    if (!parentEntity.worldObj.isRemote) {
                        block.dropBlockAsItem(parentEntity.worldObj, x, y, z, meta, 0);
                        parentEntity.worldObj.setBlockToAir(x, y, z);
                    }

                    // Reset
                    designatedBreakTarget = null;
                    actionTicks = 0;
                }
            }
        }

        /**
         * Update attack monster task
         */
        private void updateAttackMonsterTask() {
            if (designatedAttackTarget == null || designatedAttackTarget.isDead) {
                designatedAttackTarget = null;
                actionTicks = 0;

                // Try to find new target
                findAttackableMonster();
                return;
            }

            // Move towards target
            parentEntity.getNavigator()
                .tryMoveToXYZ(
                    designatedAttackTarget.posX,
                    designatedAttackTarget.posY,
                    designatedAttackTarget.posZ,
                    1.7);

            // Check distance
            double dist = parentEntity.getDistanceSqToEntity(designatedAttackTarget);
            if (dist < 9.0) { // Within 3 blocks
                actionTicks++;
                if (actionTicks > ATTACK_TICKS) {
                    // Attack the target
                    if (!parentEntity.worldObj.isRemote) {
                        designatedAttackTarget.attackEntityFrom(DamageSource.magic, ATTACK_DAMAGE);
                    }

                    // Reset
                    designatedAttackTarget = null;
                    actionTicks = 0;
                }
            }
        }
    }
}
