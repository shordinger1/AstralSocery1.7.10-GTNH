/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.awt.*;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemHighlighted
 * Created by HellFirePvP
 * Date: 13.05.2016 / 13:59
 */
public class EntityItemHighlighted extends EntityItem {

    private static final int DATA_COLOR_DATAWATCHER_ID = 20;

    public EntityItemHighlighted(World worldIn) {
        super(worldIn);
        applyColor(Color.WHITE);
    }

    public EntityItemHighlighted(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        applyColor(
            (!(stack == null || stack.stackSize <= 0) && stack.getItem() instanceof ItemHighlighted)
                ? ((ItemHighlighted) stack.getItem()).getHightlightColor(stack)
                : Color.WHITE);
    }

    public EntityItemHighlighted(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        applyColor(Color.WHITE);
    }

    // 1.7.10: EntityItem doesn't have setItem() method to override
    // If you need to update the item after creation, use reflection or recreate the entity

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(DATA_COLOR_DATAWATCHER_ID, Color.WHITE.getRGB());
    }

    public void applyColor(Color color) {
        this.dataWatcher.updateObject(DATA_COLOR_DATAWATCHER_ID, color.getRGB());
        this.dataWatcher.setObjectWatched(DATA_COLOR_DATAWATCHER_ID);
    }

    public Color getHighlightColor() {
        int colorInt = this.dataWatcher.getWatchableObjectInt(DATA_COLOR_DATAWATCHER_ID);
        return new Color(colorInt, false);
    }

}
