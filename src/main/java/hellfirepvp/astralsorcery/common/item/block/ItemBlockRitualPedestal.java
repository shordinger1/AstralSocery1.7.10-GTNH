/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.BlockRitualPedestal;
import hellfirepvp.astralsorcery.common.registry.reference.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockRitualPedestal
 * Created by HellFirePvP
 * Date: 17.08.2016 / 19:57
 */
public class ItemBlockRitualPedestal extends ItemBlock {

    public ItemBlockRitualPedestal() {
        super(BlocksAS.blockRitualPedestal);
        // 1.7.10: Ritual pedestal doesn't use ItemBlockRitualPedestal
        setHasSubtypes(true);
    }

    public ItemBlockRitualPedestal(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    // 1.7.10: Simplified version - 1.12.2 has additional functionality
}
