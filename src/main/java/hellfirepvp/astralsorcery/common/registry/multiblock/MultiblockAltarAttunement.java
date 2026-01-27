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
 * Class: MultiblockAltarAttunement
 * Created by HellFirePvP
 * Date: 17.10.2016 / 11:46
 */
public class MultiblockAltarAttunement {

    private static final String STRUCTURE_ID = "pattern_altar_t2";
    private static IStructureDefinition<MultiblockAltarAttunement> STRUCTURE_DEFINITION = null;

    public MultiblockAltarAttunement() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockAltarAttunement> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockAltarAttunement>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('A', ofBlock(BlocksAS.blockAltar, 1)) // ALTAR_2
                .addElement('B', ofBlock(BlocksAS.blockBlackMarble, 0)) // RAW
                .addElement('C', ofBlock(BlocksAS.blockMarble, 3)) // ARCH
                .addElement('D', ofBlock(BlocksAS.blockMarble, 2)) // BRICKS
                .addElement('E', ofBlock(BlocksAS.blockMarble, 1)) // CHISELED
                .addElement('F', ofBlock(BlocksAS.blockMarble, 5)) // PILLAR
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -1 (Base layer - y+2 in shape array since we build from bottom)
        { "DDDDDCCD", "DBBBBBBD", "DBBBBBBD", "DBBBBBBD", "DBBBBBBD", "DBBBBBBD", "DBBBBBBD", "DDDDDCCD" },
        { "DCCCCCCC", "CDDDDDDC", "CDDDDDDC", "CDDDDDDC", "CDDDDDDC", "CDDDDDDC", "CDDDDDDC", "DCCCCCCC" },

        // Y = 0
        { "        ", "        ", "        ", "        ", "   A    ", "        ", "        ", "        " },

        // Y = 1 (Pillars)
        { "        ", "        ", "        ", "   F    ", "        ", "   F    ", "        ", "        " },

        // Y = 2 (Pillars)
        { "        ", "        ", "        ", "   F    ", "        ", "   F    ", "        ", "        " },

        // Y = 3 (Top of pillars)
        { "        ", "        ", "        ", "   E    ", "        ", "   E    ", "        ", "        " }, };

}
