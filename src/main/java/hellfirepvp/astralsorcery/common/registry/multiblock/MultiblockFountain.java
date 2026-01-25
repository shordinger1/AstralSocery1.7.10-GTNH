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
 * Class: MultiblockFountain
 * Created by HellFirePvP
 * Date: 31.10.2017 / 15:36
 */
public class MultiblockFountain {

    private static final String STRUCTURE_ID = "pattern_fountain";
    private static IStructureDefinition<MultiblockFountain> STRUCTURE_DEFINITION = null;

    public MultiblockFountain() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockFountain> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockFountain>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('B', ofBlock(BlocksAS.blockBore, 0))
                .addElement('S', ofBlock(BlocksAS.blockBlackMarble, 0)) // RAW
                .addElement('P', ofBlock(BlocksAS.blockMarble, 5)) // PILLAR
                .addElement('U', ofBlock(BlocksAS.blockMarble, 4)) // RUNED
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -2
        {"        ", "        ", "        ", "   P    ", "   P    ", "        ", "        ", "        "},
        {"        ", "        ", "   U    ", "   P    ", "   P    ", "   U    ", "        ", "        "},
        {"        ", "   U    ", "   U    ", "   P    ", "   P    ", "   U    ", "   U    ", "        "},
        {"        ", "        ", "   U    ", "   P    ", "   P    ", "   U    ", "        ", "        "},
        {"        ", "        ", "        ", "   P    ", "   P    ", "        ", "        ", "        "},

        // Y = -1
        {"        ", "        ", "        ", "   P    ", "   P    ", "        ", "        ", "        "},
        {"        ", "        ", "   U    ", "   P    ", "   P    ", "   U    ", "        ", "        "},
        {"        ", "   U    ", "   U    ", "   P    ", "   P    ", "   U    ", "   U    ", "        "},
        {"        ", "        ", "   U    ", "   P    ", "   P    ", "   U    ", "        ", "        "},
        {"        ", "        ", "        ", "   P    ", "   P    ", "        ", "        ", "        "},

        // Y = 0 (Main layer)
        {"        ", "        ", "        ", "   S    ", "   B    ", "   S    ", "        ", "        "},
        {"        ", "        ", "   U    ", "   U    ", "   U    ", "   U    ", "   U    ", "        "},
        {"        ", "   U    ", "   U    ", "   U    ", "   U    ", "   U    ", "   U    ", "        "},
        {"        ", "        ", "   U    ", "   U    ", "   U    ", "   U    ", "   U    ", "        "},
        {"        ", "        ", "        ", "   S    ", "   S    ", "        ", "        ", "        "},

        // Y = 1
        {"        ", "        ", "        ", "   P    ", "        ", "   P    ", "        ", "        "},
        {"        ", "        ", "   U    ", "        ", "        ", "        ", "   U    ", "        "},
        {"        ", "   U    ", "        ", "        ", "        ", "        ", "   U    ", "        "},
        {"        ", "        ", "   U    ", "        ", "        ", "        ", "   U    ", "        "},
        {"        ", "        ", "        ", "   P    ", "        ", "   P    ", "        ", "        "},

        // Y = 2
        {"        ", "        ", "        ", "   P    ", "        ", "   P    ", "        ", "        "},
        {"        ", "        ", "   U    ", "        ", "        ", "        ", "   U    ", "        "},
        {"        ", "   U    ", "        ", "        ", "        ", "        ", "   U    ", "        "},
        {"        ", "        ", "   U    ", "        ", "        ", "        ", "   U    ", "        "},
        {"        ", "        ", "        ", "   P    ", "        ", "   P    ", "        ", "        "},
    };

}
