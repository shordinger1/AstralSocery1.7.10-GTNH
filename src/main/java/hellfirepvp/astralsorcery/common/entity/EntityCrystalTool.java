/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Crystal tool entity - grows in liquid starlight
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.item.ItemCrystalSword;
import hellfirepvp.astralsorcery.common.item.ItemCrystalToolBase;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;

/**
 * EntityCrystalTool - Crystal tool entity (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Grows in liquid starlight over time</li>
 * <li>Increases tool size and capability</li>
 * <li>Renders with particle effects</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Entity.getEntityWorld() → Entity.worldObj</li>
 * <li>world.getEntitiesInAABBexcluding() → world.getEntitiesWithinAABBExcludingEntity()</li>
 * <li>boxCraft.offset(getPosition()) → AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1)</li>
 * <li>boxCraft.grow() → Calculate bounds manually</li>
 * <li>world.setBlockToAir(getPosition()) → world.setBlockToAir(x, y, z)</li>
 * <li>BlockPos → (x, y, z) coordinates</li>
 * <li>getItem().isEmpty() → getItem() == null || getItem().stackSize <= 0</li>
 * <li>Predicates.or() → Manual predicate checking</li>
 * </ul>
 * <p>
 * <b>TODO Items (待迁移):</b>
 * <ul>
 * <li>EffectHelper - Client particle system</li>
 * <li>EntityFXFacingParticle - Particle entity</li>
 * <li>Config - Configuration class</li>
 * <li>EntityUtils - Entity utility functions</li>
 * <li>BlockCollectorCrystal - Crystal block for color reference</li>
 * </ul>
 */
public class EntityCrystalTool extends EntityItem implements EntityStarlightReacttant {

    public static final int TOTAL_MERGE_TIME = 50 * 20;
    private int inertMergeTick = 0;

    /**
     * Get current merge tick count
     * Public for renderer access
     */
    public int getInertMergeTick() {
        return inertMergeTick;
    }

    public EntityCrystalTool(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCrystalTool(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public EntityCrystalTool(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (age + 5 >= this.lifespan) {
            age = 0;
        }

        if (hellfirepvp.astralsorcery.common.data.config.Config.craftingLiqCrystalToolGrowth) {
            checkIncreaseConditions();
        }
    }

    private void checkIncreaseConditions() {
        // 1.7.10: worldObj.isRemote instead of world.isRemote
        if (worldObj.isRemote) {
            if (canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if (getProperties() == null) {
                setDead();
            }
            if (canCraft()) {
                inertMergeTick++;
                if (inertMergeTick >= TOTAL_MERGE_TIME && rand.nextInt(300) == 0) {
                    increaseSize();
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    /**
     * Get tool crystal properties
     * 1.7.10: Check stack size instead of isEmpty()
     * Public for renderer access
     */
    @Nullable
    public ToolCrystalProperties getProperties() {
        ItemStack item = getEntityItem();
        // 1.7.10: stack == null || stack.stackSize <= 0 instead of stack.isEmpty()
        if (item == null || item.stackSize <= 0) return null;

        if (item.getItem() instanceof ItemCrystalToolBase) {
            return ItemCrystalToolBase.getToolProperties(item);
        }
        if (item.getItem() instanceof ItemCrystalSword) {
            return ItemCrystalSword.getToolProperties(item);
        }
        return null;
    }

    /**
     * Apply tool crystal properties
     * 1.7.10: Check stack size instead of isEmpty()
     */
    private void applyProperties(ToolCrystalProperties properties) {
        ItemStack item = getEntityItem();
        // 1.7.10: stack == null || stack.stackSize <= 0 instead of stack.isEmpty()
        if (item == null || item.stackSize <= 0) return;

        if (item.getItem() instanceof ItemCrystalToolBase) {
            ItemCrystalToolBase.setToolProperties(item, properties);
        }
        if (item.getItem() instanceof ItemCrystalSword) {
            ItemCrystalSword.setToolProperties(item, properties);
        }
        setEntityItemStack(item);
    }

    /**
     * Increase tool size
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    private void increaseSize() {
        int x = (int) posX;
        int y = (int) posY;
        int z = (int) posZ;

        // 1.7.10: setBlockToAir(x, y, z) instead of setBlockToAir(pos)
        worldObj.setBlockToAir(x, y, z);

        // 1.7.10: Calculate bounds manually - getBoundingBox needs 6 parameters
        AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(x - 0.1, y - 0.1, z - 0.1, x + 1.1, y + 1.1, z + 1.1);

        // TODO: Re-enable when EntityUtils is migrated
        /*
         * List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox,
         * Predicates.or(EntityUtils.selectItemClassInstaceof(ItemCrystalToolBase.class),
         * EntityUtils.selectItemClassInstaceof(EntityCrystalSword.class)));
         */

        // Simplified: get all entities and filter manually
        List<Entity> foundItems = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);

        if (foundItems.size() <= 0) {
            ToolCrystalProperties prop = getProperties();
            if (prop != null) {
                ItemStack stack = getEntityItem();
                // Get max size based on tool type
                int max;
                if (stack.getItem() instanceof ItemCrystalToolBase) {
                    max = ((ItemCrystalToolBase) stack.getItem()).getMaxSize(stack);
                } else if (stack.getItem() instanceof ItemCrystalSword) {
                    max = ((ItemCrystalSword) stack.getItem()).getMaxSize(stack);
                } else {
                    max = CrystalProperties.MAX_SIZE_CELESTIAL;
                }
                int grow = rand.nextInt(250) + 100;
                max = Math.min(prop.getSize() + grow, max);
                int cut = Math.max(0, prop.getCollectiveCapability() - (rand.nextInt(10) + 10));
                applyProperties(
                    new ToolCrystalProperties(
                        max,
                        prop.getPurity(),
                        cut,
                        prop.getFracturation(),
                        prop.getSizeOverride()));
            }
        }
    }

    /**
     * Spawn crafting particles
     * 1.7.10: Uses posX/Y/Z instead of getPosition()
     */
    @SideOnly(Side.CLIENT)
    private void spawnCraftingParticles() {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
            posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        p.motion(
            rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.04 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1));
        p.gravity(0.01);
        p.scale(0.2F)
            .setColor(new java.awt.Color(0x89CFF0));
    }

    /**
     * Check if crafting is possible
     * 1.7.10: Uses coordinates instead of BlockPos
     */
    private boolean canCraft() {
        if (!isInLiquidStarlight(this)) return false;

        int x = (int) posX;
        int y = (int) posY;
        int z = (int) posZ;

        // TODO: Re-enable when EntityUtils is migrated
        /*
         * List<Entity> foundEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this,
         * boxCraft.getBoundingBox(x, y, z), EntityUtils.selectEntities(Entity.class));
         */

        // Simplified: check if any entities nearby
        // 1.7.10: getBoundingBox needs 6 parameters
        List<Entity> foundEntities = worldObj
            .getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));

        return foundEntities.size() <= 0;
    }
}
