/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/
package hellfirepvp.astralsorcery.common.registry.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 16:35
 */
public class MultiblockStarlightInfuser {

    private static final String STRUCTURE_ID = "pattern_starlight_infuser";
    private static IStructureDefinition<MultiblockStarlightInfuser> STRUCTURE_DEFINITION = null;

    public MultiblockStarlightInfuser() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockStarlightInfuser> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockStarlightInfuser>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('I', ofBlock(BlocksAS.starlightInfuser, 0))
                .addElement('C', ofBlock(BlocksAS.blockMarble, 3)) // ARCH
                .addElement('R', ofBlock(BlocksAS.blockMarble, 0)) // RAW
                .addElement('P', ofBlock(BlocksAS.blockMarble, 5)) // PILLAR
                .addElement('H', ofBlock(BlocksAS.blockMarble, 1)) // CHISELED
                .addElement('U', ofBlock(BlocksAS.blockMarble, 4)) // RUNED
                .addElement('L', ofBlock(BlocksAS.blockLiquidStarlight, 0))
                .addElement('X', ofBlock(Blocks.lapis_block, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -2 (Liquid starlight base)
        {"        ", "        ", "   R    ", "   R    ", "   R    ", "        ", "        ", "        "},
        {"        ", "   R    ", "   R    ", "   R    ", "   R    ", "   R    ", "   R    ", "        "},
        {"        ", "        ", "   R    ", "   R    ", "   R    ", "        ", "        ", "        "},

        // Y = -1 (Main base with liquid starlight)
        {"CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCUCCUCC", "CULXCLUC", "CCUCCUCC", "CCCCCCCC", "CCCCCCCC"},
        {"CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC"},

        // Y = 0 (Infuser at center)
        {"        ", "        ", "        ", "        ", "   I    ", "        ", "        ", "        "},

        // Y = 1
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},

        // Y = 2
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},

        // Y = 3 (Pillars)
        {"        ", "        ", "        ", "P       ", "        ", "        ", "       P", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},

        // Y = 4
        {"        ", "        ", "        ", "P       ", "        ", "        ", "       P", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},

        // Y = 5 (Top arches)
        {"        ", "        ", "        ", "H       ", "        ", "        ", "       H", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},

        // Y = 6 (Top platform)
        {"        ", "        ", "        ", "   C    ", "        ", "        ", "    C   ", "        "},
        {"        ", "        ", "   C    ", "   C    ", "        ", "   C    ", "   C    ", "        "},
        {"        ", "        ", "        ", "   C    ", "        ", "        ", "    C   ", "        "},

        // Y = 7 (Arch decorations)
        {"        ", "        ", "        ", "        ", "   C    ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "   C    ", "        ", "   C    ", "        ", "        "},
        {"        ", "        ", "        ", "        ", "   C    ", "        ", "        ", "        "},
    };

}
