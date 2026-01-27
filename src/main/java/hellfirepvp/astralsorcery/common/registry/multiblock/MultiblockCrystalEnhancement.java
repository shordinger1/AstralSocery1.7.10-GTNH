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
 * Class: MultiblockCrystalEnhancement
 * Created by HellFirePvP
 * Date: 22.04.2017 / 11:23
 */
public class MultiblockCrystalEnhancement {

    private static final String STRUCTURE_ID = "pattern_collector_crystal_enhanced";
    private static IStructureDefinition<MultiblockCrystalEnhancement> STRUCTURE_DEFINITION = null;

    public MultiblockCrystalEnhancement() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockCrystalEnhancement> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockCrystalEnhancement>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('A', ofBlock(BlocksAS.celestialCollectorCrystal, 0))
                .addElement('R', ofBlock(BlocksAS.blockMarble, 0)) // RAW
                .addElement('U', ofBlock(BlocksAS.blockMarble, 4)) // RUNED
                .addElement('P', ofBlock(BlocksAS.blockMarble, 5)) // PILLAR
                .addElement('H', ofBlock(BlocksAS.blockMarble, 1)) // CHISELED
                .addElement('G', ofBlock(BlocksAS.blockMarble, 7)) // ENGRAVED
                .addElement('L', ofBlock(BlocksAS.blockLiquidStarlight, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -5 (Bottom base)
        { "   R   " },

        // Y = -4 (Main platform)
        { " HUGUH ", "HUGGUGH", "HUGGUGH", " HUGUH " },

        // Y = -3
        { "   H   ", " HGGH ", "HGGGGH", " HGGH ", "   H   " },

        // Y = -2
        { "   P   " },

        // Y = -1
        { "   H   " },

        // Y = 0 (Crystal at center)
        { "   A   " },

        // Y = 1 (Air)
        { "       " }, };

}
