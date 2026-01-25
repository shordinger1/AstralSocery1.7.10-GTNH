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

import net.minecraft.util.ResourceLocation;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAltarConstellation
 * Created by HellFirePvP
 * Date: 22.10.2016 / 12:48
 */
public class MultiblockAltarConstellation {

    private static final String STRUCTURE_ID = "pattern_altar_t3";
    private static IStructureDefinition<MultiblockAltarConstellation> STRUCTURE_DEFINITION = null;

    public MultiblockAltarConstellation() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockAltarConstellation> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockAltarConstellation>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('A', ofBlock(BlocksAS.blockAltar, 2)) // ALTAR_3
                .addElement('B', ofBlock(BlocksAS.blockBlackMarble, 0)) // RAW
                .addElement('C', ofBlock(BlocksAS.blockMarble, 2)) // BRICKS
                .addElement('R', ofBlock(BlocksAS.blockMarble, 0)) // RAW
                .addElement('U', ofBlock(BlocksAS.blockMarble, 4)) // RUNED (using metadata 4 for 1.7.10)
                .addElement('P', ofBlock(BlocksAS.blockMarble, 5)) // PILLAR
                .addElement('H', ofBlock(BlocksAS.blockMarble, 1)) // CHISELED
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -1 (Base layer)
        {"CCCCCCCC", "CCRRRCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCRRRCCC", "CCCCCCCC"},
        {"CCCCCCCC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CCCCCCCC"},
        {"CCCCCCCC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CCCCCCCC"},

        // Y = 0 (Runed corners)
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "U       ", "        ", "        ", "        ", "        ", "        ", "U       ", "        ", "        "},

        // Y = 1 (Pillars)
        {"        ", "        ", "        ", "P       ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        "},

        // Y = 2 (Pillars)
        {"        ", "        ", "        ", "P       ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        "},

        // Y = 3 (Top)
        {"        ", "        ", "        ", "H       ", "        ", "        ", "        ", "        ", "        ", "H       ", "        ", "        "},
    };

}
