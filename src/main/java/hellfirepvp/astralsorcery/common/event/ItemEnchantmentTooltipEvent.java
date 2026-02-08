/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemEnchantmentTooltipEvent
 * Created by HellFirePvP
 * Date: 20.05.2018 / 16:53
 */
public class ItemEnchantmentTooltipEvent extends Event {

    @Nonnull
    private final ItemStack itemStack;
    private final EntityPlayer entityPlayer;
    private final List<String> toolTip;
    private final boolean showAdvanced;

    public ItemEnchantmentTooltipEvent(@Nonnull ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip,
        boolean showAdvanced) {
        this.itemStack = itemStack;
        this.entityPlayer = entityPlayer;
        this.toolTip = toolTip;
        this.showAdvanced = showAdvanced;
    }

    public boolean isShowAdvanced() {
        return showAdvanced;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public List<String> getToolTip() {
        return toolTip;
    }

}
