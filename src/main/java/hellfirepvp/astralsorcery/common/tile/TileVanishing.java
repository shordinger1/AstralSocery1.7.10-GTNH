/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.tool.wand.WandAugment;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileVanishing
 * Created by HellFirePvP
 * Date: 30.07.2017 / 17:35
 */
public class TileVanishing extends TileEntityTick {

    private static final AxisAlignedBB topBox = AxisAlignedBB.getBoundingBox(-0.9, 0, -0.9, 0.9, 0.9, 0.9);

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!getWorld().isRemote && ticksExisted % 10 == 0) {
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, topBox.offset(pos));
            for (EntityPlayer player : players) {
                ItemStack held = player.getCurrentEquippedItem();
                if (!(held == null || held.stackSize <= 0) && held.getItem() instanceof ItemWand
                    && WandAugment.AEVITAS == ItemWand.getAugment(held)) {
                    return;
                }
                // 1.7.10: No off-hand, skip offhand check
            }
            getWorld().setBlockToAir(getPos());
        }
    }

    @Override
    protected void onFirstTick() {}

}
