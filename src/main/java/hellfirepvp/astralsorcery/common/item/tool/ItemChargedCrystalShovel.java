/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalShovel
 * Created by HellFirePvP
 * Date: 14.03.2017 / 12:43
 */
public class ItemChargedCrystalShovel extends ItemCrystalShovel implements ChargedCrystalToolBase {

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        // 1.7.10: No CooldownTracker, removed cooldown check; use world variable
        if (!world.isRemote && !player.isSneaking()) {
            Block at = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            if (at.isToolEffective("shovel", meta)) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockArray shovelables = BlockDiscoverer
                    .discoverBlocksWithSameStateAround(world, pos, true, 8, 100, true);
                if (shovelables != null) {
                    Map<BlockPos, BlockArray.BlockInformation> pattern = shovelables.getPattern();
                    for (Map.Entry<BlockPos, BlockArray.BlockInformation> blocks : pattern.entrySet()) {
                        if (world.setBlock(
                            blocks.getKey()
                                .getX(),
                            blocks.getKey()
                                .getY(),
                            blocks.getKey()
                                .getZ(),
                            BlocksAS.blockFakeTree,
                            0,
                            3)) {
                            TileFakeTree tt = MiscUtils.getTileAt(world, blocks.getKey(), TileFakeTree.class, true);
                            if (tt != null) {
                                tt.setupTile(player, itemstack, blocks.getValue().state);
                                itemstack.damageItem(1, player);
                            } else {
                                world.setBlock(
                                    blocks.getKey()
                                        .getX(),
                                    blocks.getKey()
                                        .getY(),
                                    blocks.getKey()
                                        .getZ(),
                                    blocks.getValue().state,
                                    0,
                                    3);
                            }
                        }
                    }
                    // 1.7.10: No cooldown to set
                    return true;
                }
            }
        }
        return super.onBlockStartBreak(itemstack, x, y, z, player);
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalShovel;
    }

}
