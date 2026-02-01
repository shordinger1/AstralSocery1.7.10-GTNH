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

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

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

        if (!worldObj.isRemote && ticksExisted % 10 == 0) {
            BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
            AxisAlignedBB box = topBox.getOffsetBoundingBox(pos.getX(), pos.getY(), pos.getZ());
            List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, box);
            for (EntityPlayer player : players) {
                // TODO: Re-enable after ItemWand is migrated
                // ItemStack held = player.getHeldItemMainhand();
                // if(held != null && held.stackSize > 0 && held.getItem() instanceof ItemWand && WandAugment.AEVITAS ==
                // ItemWand.getAugment(held)) {
                // return;
                // }
                // held = player.getHeldItemOffhand();
                // if(held != null && held.stackSize > 0 && held.getItem() instanceof ItemWand && WandAugment.AEVITAS ==
                // ItemWand.getAugment(held)) {
                // return;
                // }

                // 1.7.10: Check item in main hand
                ItemStack held = player.getCurrentEquippedItem();
                if (held != null && held.stackSize > 0) {
                    // TODO: Add ItemWand check after migration
                    // if (held.getItem() instanceof ItemWand && WandAugment.AEVITAS == ItemWand.getAugment(held)) {
                    // return;
                    // }
                }
            }
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }
    }

    @Override
    protected void onFirstTick() {}

}
