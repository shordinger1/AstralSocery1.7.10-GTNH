/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * Helper class to access protected RenderItem instance
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.entity.RenderItem;

/**
 * Helper class to access the protected RenderItem instance from GuiScreen.
 * Since GuiScreen.itemRender is protected static, we use this accessor.
 */
public class RenderItemAccessor {

    /**
     * Get the RenderItem instance from GuiScreen.
     * Since itemRender is protected, we extend GuiScreen to access it.
     */
    public static class GuiScreenAccessor extends net.minecraft.client.gui.GuiScreen {

        public static RenderItem getRenderItem() {
            return itemRender;
        }
    }

    /**
     * Get the RenderItem instance for GUI rendering.
     */
    public static RenderItem getRenderItem() {
        return GuiScreenAccessor.getRenderItem();
    }
}
