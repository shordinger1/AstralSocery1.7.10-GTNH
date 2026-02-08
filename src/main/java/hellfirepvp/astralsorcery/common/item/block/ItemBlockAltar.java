/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;

// REMOVED: IBlockState, BlockPos, EnumFacing - using 1.7.10 coordinate system

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
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    // REMOVED: placeBlockAt method with IBlockState - 1.7.10 doesn't have this override
    // In 1.7.10, ItemBlock doesn't have placeBlockAt - placement is handled by the block itself

}
