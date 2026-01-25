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
 * This class is part of the 1.11.2 port of Reika's mods.
 * Original code for this project on Minecraft 1.7.10
 * is available under the same licence on Github:
 * https://github.com/ReikaKalseki/DragonAPI
 * Class: MultiblockAltarTrait
 * Author: HellFirePvP
 * Owner & Author: Reika Kalseki
 * Date: 24.02.2017 / 17:33
 */
public class MultiblockAltarTrait {

    private static final String STRUCTURE_ID = "pattern_altar_t4";
    private static IStructureDefinition<MultiblockAltarTrait> STRUCTURE_DEFINITION = null;

    public MultiblockAltarTrait() {
        // Structure defined via getStructureDefinition()
        // Includes base from MultiblockAltarConstellation
    }

    public IStructureDefinition<MultiblockAltarTrait> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockAltarTrait>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('A', ofBlock(BlocksAS.blockAltar, 3)) // ALTAR_4
                .addElement('B', ofBlock(BlocksAS.blockBlackMarble, 0)) // RAW
                .addElement('C', ofBlock(BlocksAS.blockMarble, 2)) // BRICKS
                .addElement('R', ofBlock(BlocksAS.blockMarble, 0)) // RAW
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
        // Y = -1 (Base layer - same as Altar Constellation)
        {"CCCCCCCC", "CCRRRCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCRRRCCC", "CCCCCCCC"},
        {"CCCCCCCC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CCCCCCCC"},
        {"CCCCCCCC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CBBBBBBC", "CCCCCCCC"},

        // Y = 0
        {"        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        "},
        {"        ", "        ", "        ", "U       ", "        ", "        ", "        ", "        ", "        ", "U       ", "        ", "        "},

        // Y = 1
        {"        ", "        ", "        ", "P       ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        "},

        // Y = 2
        {"        ", "        ", "        ", "P       ", "        ", "        ", "        ", "        ", "        ", "P       ", "        ", "        "},

        // Y = 3 (Bricks added at corners)
        {"        ", "        ", "        ", "HCCC    ", "        ", "        ", "        ", "        ", "        ", "CCCH    ", "        ", "        "},
        {"        ", "        ", "        ", "C       ", "        ", "        ", "        ", "        ", "        ", "C      ", "        ", "        "},

        // Y = 4 (New layer - ring of bricks)
        {"        ", "        ", "        ", "CCCCCCC ", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", "CCCCCCCC", " CCCCCCC", "        ", "        "},
    };

}
