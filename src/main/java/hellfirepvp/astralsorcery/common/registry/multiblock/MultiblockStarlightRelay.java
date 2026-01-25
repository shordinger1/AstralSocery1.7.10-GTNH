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
 * Class: MultiblockStarlightRelay
 * Created by HellFirePvP
 * Date: 30.03.2017 / 14:07
 */
public class MultiblockStarlightRelay {

    private static final String STRUCTURE_ID = "pattern_starlight_relay";
    private static IStructureDefinition<MultiblockStarlightRelay> STRUCTURE_DEFINITION = null;

    public MultiblockStarlightRelay() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockStarlightRelay> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockStarlightRelay>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('R', ofBlock(BlocksAS.attunementRelay, 0))
                .addElement('H', ofBlock(BlocksAS.blockMarble, 1)) // CHISELED
                .addElement('A', ofBlock(BlocksAS.blockMarble, 3)) // ARCH
                .addElement('B', ofBlock(BlocksAS.blockBlackMarble, 0)) // RAW
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -1 (Simple base)
        {"   A   ", " AHHA  ", "AHBRHA ", " AHAHA ", "  R   "},
    };

}
