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
 * Class: MultiblockGateway
 * Created by HellFirePvP
 * Date: 17.04.2017 / 10:50
 */
public class MultiblockGateway {

    private static final String STRUCTURE_ID = "pattern_celestial_gateway";
    private static IStructureDefinition<MultiblockGateway> STRUCTURE_DEFINITION = null;

    public MultiblockGateway() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockGateway> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockGateway>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('A', ofBlock(BlocksAS.celestialGateway, 0))
                .addElement('C', ofBlock(BlocksAS.blockMarble, 3)) // ARCH
                .addElement('B', ofBlock(BlocksAS.blockBlackMarble, 0)) // RAW
                .addElement('U', ofBlock(BlocksAS.blockMarble, 4)) // RUNED
                .addElement('G', ofBlock(BlocksAS.blockMarble, 7)) // ENGRAVED
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -1 (Base)
        { "CCCCCC", "CBBBBC", "CBBBBC", "CBBBBC", "CBBBBC", "CBBBBC", "CBBBBC", "CCCCCC" },

        // Y = 0 (Gateway at center, runed corners, engraved corners)
        { "      ", "      ", "U     ", "      ", "  A   ", "      ", "     U", "      " },
        { "      ", "      ", "      ", "      ", "      ", "      ", "      ", "      " },
        { "      ", "      ", "G     ", "      ", "      ", "      ", "     G", "      " }, };

}
