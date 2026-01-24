/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 16:35
 */
public class MultiblockStarlightInfuser extends PatternBlockArray {

    public MultiblockStarlightInfuser() {
        super(new ResourceLocation(AstralSorcery.MODID, "pattern_starlight_infuser"));
        load();
    }

    private void load() {
        Block mar = BlocksAS.blockMarble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        Block mrw = BlocksAS.blockMarble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        Block mpl = BlocksAS.blockMarble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        Block mch = BlocksAS.blockMarble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        Block mru = BlocksAS.blockMarble.withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);

        Block liquidStarlight = BlocksAS.blockLiquidStarlight;

        addBlock(0, 0, 0, BlocksAS.starlightInfuser);

        addBlockCube(mar, -2, -1, -3, 2, -1, 3);
        addBlockCube(mar, -3, -1, -2, 3, -1, 2);

        addBlockCube(mru, -2, -1, -2, 2, -1, 2);

        addBlock(0, -1, 0, Blocks.LAPIS_BLOCK);
        addBlock(-2, -1, -1, liquidStarlight);
        addBlock(-2, -1, 0, liquidStarlight);
        addBlock(-2, -1, 1, liquidStarlight);
        addBlock(2, -1, -1, liquidStarlight);
        addBlock(2, -1, 0, liquidStarlight);
        addBlock(2, -1, 1, liquidStarlight);
        addBlock(-1, -1, -2, liquidStarlight);
        addBlock(0, -1, -2, liquidStarlight);
        addBlock(1, -1, -2, liquidStarlight);
        addBlock(-1, -1, 2, liquidStarlight);
        addBlock(0, -1, 2, liquidStarlight);
        addBlock(1, -1, 2, liquidStarlight);

        addBlock(-2, -2, -1, mrw);
        addBlock(-2, -2, 0, mrw);
        addBlock(-2, -2, 1, mrw);
        addBlock(2, -2, -1, mrw);
        addBlock(2, -2, 0, mrw);
        addBlock(2, -2, 1, mrw);
        addBlock(-1, -2, -2, mrw);
        addBlock(0, -2, -2, mrw);
        addBlock(1, -2, -2, mrw);
        addBlock(-1, -2, 2, mrw);
        addBlock(0, -2, 2, mrw);
        addBlock(1, -2, 2, mrw);

        addBlock(3, 3, 2, mar);
        addBlock(3, 3, -2, mar);
        addBlock(-3, 3, 2, mar);
        addBlock(-3, 3, -2, mar);
        addBlock(2, 3, 3, mar);
        addBlock(-2, 3, 3, mar);
        addBlock(2, 3, -3, mar);
        addBlock(-2, 3, -3, mar);

        addBlock(3, 4, 1, mar);
        addBlock(3, 4, 0, mar);
        addBlock(3, 4, -1, mar);
        addBlock(-3, 4, 1, mar);
        addBlock(-3, 4, 0, mar);
        addBlock(-3, 4, -1, mar);
        addBlock(1, 4, 3, mar);
        addBlock(0, 4, 3, mar);
        addBlock(-1, 4, 3, mar);
        addBlock(1, 4, -3, mar);
        addBlock(0, 4, -3, mar);
        addBlock(-1, 4, -3, mar);

        addBlock(-3, 0, 3, mpl);
        addBlock(-3, 1, 3, mpl);
        addBlock(-3, 2, 3, mch);
        addBlock(-3, 0, -3, mpl);
        addBlock(-3, 1, -3, mpl);
        addBlock(-3, 2, -3, mch);
        addBlock(3, 0, 3, mpl);
        addBlock(3, 1, 3, mpl);
        addBlock(3, 2, 3, mch);
        addBlock(3, 0, -3, mpl);
        addBlock(3, 1, -3, mpl);
        addBlock(3, 2, -3, mch);
    }

}
