/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Highlighted item entity base class
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import java.awt.*;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;

/**
 * EntityItemHighlighted - Highlighted item entity with custom color (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Extends EntityItem with custom highlight color</li>
 * <li>Synchronized color via DataWatcher (client-server)</li>
 * <li>Retrieves color from ItemHighlighted items</li>
 * <li>Default white color for non-highlighted items</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>EntityDataManager → DataWatcher</li>
 * <li>createKey() → addObject()</li>
 * <li>getDataManager() → getDataWatcher()</li>
 * <li>set() → updateObject()</li>
 * <li>get() → getWatchableObjectInt()</li>
 * <li>setDirty() → 无需调用（自动同步）</li>
 * <li>stack.isEmpty() → stack == null || stack.stackSize <= 0</li>
 * </ul>
 * <p>
 * <b>DataWatcher Index:</b>
 * 
 * <pre>
 * Index 16: Integer (RGB color)
 * </pre>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * // Spawn highlighted item
 * EntityItemHighlighted entity = new EntityItemHighlighted(world, x, y, z, stack);
 * entity.applyColor(Color.CYAN);
 * world.spawnEntityInWorld(entity);
 * </pre>
 */
public class EntityItemHighlighted extends EntityItem {

    // 1.7.10: 使用DataWatcher索引常量而非DataParameter
    private static final int DATA_COLOR_IDX = 16;

    /**
     * Default constructor - white color
     */
    public EntityItemHighlighted(World worldIn) {
        super(worldIn);
        applyColor(Color.WHITE);
    }

    /**
     * Position constructor with ItemStack
     */
    public EntityItemHighlighted(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);

        // 1.7.10: stack.isEmpty() → stack == null || stack.stackSize <= 0
        if (stack != null && stack.getItem() instanceof ItemHighlighted) {
            applyColor(((ItemHighlighted) stack.getItem()).getHighlightColor(stack));
        } else {
            applyColor(Color.WHITE);
        }
    }

    /**
     * Position constructor - no item
     */
    public EntityItemHighlighted(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        applyColor(Color.WHITE);
    }

    /**
     * Set item and update color
     * 1.7.10: setEntityItemStack() instead of setItem()
     */
    @Override
    public void setEntityItemStack(ItemStack stack) {
        super.setEntityItemStack(stack);

        // 1.7.10: Check null/empty manually
        if (stack != null && stack.stackSize > 0 && stack.getItem() instanceof ItemHighlighted) {
            applyColor(((ItemHighlighted) stack.getItem()).getHighlightColor(stack));
        } else {
            applyColor(Color.WHITE);
        }
    }

    /**
     * Initialize DataWatcher
     * 1.7.10: entityInit() uses dataWatcher.addObject()
     */
    @Override
    protected void entityInit() {
        super.entityInit();
        // 1.7.10: Register DataWatcher object with index
        this.getDataWatcher()
            .addObject(DATA_COLOR_IDX, Integer.valueOf(Color.WHITE.getRGB()));
    }

    /**
     * Apply highlight color
     * 1.7.10: updateObject() instead of set()
     * 注意：1.7.10的DataWatcher会自动标记脏，无需setDirty()
     */
    public void applyColor(Color color) {
        this.getDataWatcher()
            .updateObject(DATA_COLOR_IDX, Integer.valueOf(color.getRGB()));
        // 1.7.10: 不需要setDirty()调用，DataWatcher自动同步
    }

    /**
     * Get current highlight color
     * 1.7.10: getWatchableObjectInt() instead of get()
     */
    public Color getHighlightColor() {
        int colorInt = this.getDataWatcher()
            .getWatchableObjectInt(DATA_COLOR_IDX);
        return new Color(colorInt, false);
    }

}
