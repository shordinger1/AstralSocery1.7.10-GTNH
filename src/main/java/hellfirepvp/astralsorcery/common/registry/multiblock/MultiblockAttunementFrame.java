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
 * Class: MultiblockAttunementFrame
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:36
 */
public class MultiblockAttunementFrame {

    private static final String STRUCTURE_ID = "pattern_attunement_frame";
    private static IStructureDefinition<MultiblockAttunementFrame> STRUCTURE_DEFINITION = null;

    public MultiblockAttunementFrame() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockAttunementFrame> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockAttunementFrame>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('A', ofBlock(BlocksAS.attunementAltar, 0))
                .addElement('B', ofBlock(BlocksAS.blockBlackMarble, 0)) // RAW
                .addElement('C', ofBlock(BlocksAS.blockMarble, 3)) // ARCH
                .addElement('U', ofBlock(BlocksAS.blockMarble, 4)) // RUNED
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
        // Y = -1 (Base floor)
        {"CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC"},
        {"CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC"},

        // Y = 0 (Altar at center, pillars at corners)
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "U       ", "        ", "        ", "        ", "        ", "        ", "        ", "U       ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "   A    ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},

        // Y = 1 (Pillars)
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        ", "        "},

        // Y = 2 (Pillars)
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        ", "        "},

        // Y = 3 (Pillars)
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        ", "        "},

        // Y = 4 (Pillar tops)
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "H       ", "        ", "        ", "        ", "        ", "        ", "        ", "H       ", "        ", "        ", "        "},
    };

}
