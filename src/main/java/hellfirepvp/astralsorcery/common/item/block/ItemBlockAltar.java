/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockAltar
 * Created by HellFirePvP
 * Date: 10.11.2016 / 10:37
 */
public class ItemBlockAltar extends ItemBlockCustomName {

    public ItemBlockAltar() {
        super(BlocksAS.blockAltar);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    // 1.7.10: placeBlockAt has different signature than 1.12.2
    // Note: In 1.7.10 Forge, ItemBlock.placeBlockAt exists but signature differs
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // 1.7.10: Check altar type using metadata instead of IBlockState
        int metadata = stack.getItemDamage();
        BlockAltar.AltarType type = BlockAltar.AltarType.values()[metadata % BlockAltar.AltarType.values().length];

        switch (type) {
            case ALTAR_1:
                break;
            case ALTAR_2:
            case ALTAR_3:
            case ALTAR_4:
            case ALTAR_5:
                // 1.7.10: Use x, y, z coordinates instead of BlockPos
                for (int xx = -1; xx <= 1; xx++) {
                    for (int zz = -1; zz <= 1; zz++) {
                        int checkX = x + xx;
                        int checkZ = z + zz;
                        // 1.7.10: isAirBlock and getBlock take x, y, z coordinates
                        if (!world.isAirBlock(checkX, y, checkZ)) {
                            Block blk = world.getBlock(checkX, y, checkZ);
                            // 1.7.10: isReplaceable takes (World, int x, int y, int z)
                            if (blk != null && !blk.isReplaceable(world, checkX, y, checkZ)) {
                                return false;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }

        // 1.7.10: Call super with correct parameters (including metadata)
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
    }
}
