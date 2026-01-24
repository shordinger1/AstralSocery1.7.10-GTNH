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
 * Class: MultiblockGateway
 * Created by HellFirePvP
 * Date: 17.04.2017 / 10:50
 */
public class MultiblockGateway extends PatternBlockArray {

    public MultiblockGateway() {
        super(new ResourceLocation(AstralSorcery.MODID, "pattern_celestial_gateway"));
        load();
    }

    private void load() {
        Block mar = BlockMarble.MarbleBlockType.ARCH.asBlock();
        Block mru = BlockMarble.MarbleBlockType.RUNED.asBlock();
        Block mgr = BlockMarble.MarbleBlockType.ENGRAVED.asBlock();
        Block sooty = BlockBlackMarble.BlackMarbleBlockType.RAW.asBlock();

        addBlockCube(mar, -3, -1, -3, 3, -1, 3);
        addBlockCube(sooty, -2, -1, -2, 2, -1, 2);
        addBlock(0, 0, 0, BlocksAS.celestialGateway);

        addBlock(-3, -1, -3, mru);
        addBlock(3, -1, -3, mru);
        addBlock(3, -1, 3, mru);
        addBlock(-3, -1, 3, mru);

        addBlock(-3, 0, -3, mgr);
        addBlock(3, 0, -3, mgr);
        addBlock(3, 0, 3, mgr);
        addBlock(-3, 0, 3, mgr);
    }

}
