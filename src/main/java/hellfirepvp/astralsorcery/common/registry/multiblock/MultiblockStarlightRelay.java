/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockStarlightRelay
 * Created by HellFirePvP
 * Date: 30.03.2017 / 14:07
 */
public class MultiblockStarlightRelay extends PatternBlockArray {

    public MultiblockStarlightRelay() {
        super(new ResourceLocation(AstralSorcery.MODID, "pattern_starlight_relay"));
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        Block chiseled = marble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        Block arch = marble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        Block sooty = BlocksAS.blockBlackMarble
            .withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.RAW);

        addBlock(0, 0, 0, BlocksAS.attunementRelay);

        addBlock(-1, -1, -1, chiseled);
        addBlock(1, -1, -1, chiseled);
        addBlock(1, -1, 1, chiseled);
        addBlock(-1, -1, 1, chiseled);

        addBlock(-1, -1, 0, arch);
        addBlock(1, -1, 0, arch);
        addBlock(0, -1, 1, arch);
        addBlock(0, -1, -1, arch);
        addBlock(0, -1, 0, sooty);
    }

}
