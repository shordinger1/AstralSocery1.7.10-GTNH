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
 * Class: MultiblockRitualPedestal
 * Created by HellFirePvP
 * Date: 02.10.2016 / 16:48
 */
public class MultiblockRitualPedestal {

    private static final String STRUCTURE_ID = "pattern_ritual_pedestal";
    private static IStructureDefinition<MultiblockRitualPedestal> STRUCTURE_DEFINITION = null;

    public MultiblockRitualPedestal() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockRitualPedestal> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockRitualPedestal>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('P', ofBlock(BlocksAS.ritualPedestal, 0))
                .addElement('H', ofBlock(BlocksAS.blockMarble, 1)) // CHISELED
                .addElement('B', ofBlock(BlocksAS.blockMarble, 2)) // BRICKS
                .addElement('R', ofBlock(BlocksAS.blockMarble, 0)) // RAW
                .addElement('A', ofBlock(BlocksAS.blockMarble, 3)) // ARCH
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public ResourceLocation getStructureId() {
        return new ResourceLocation(AstralSorcery.MODID, STRUCTURE_ID);
    }

    private final String[][] shape = new String[][] {
        // Y = -1 (Base layer - complex pattern)
        { "A     A", "A     A", "A     A", "BB   BB", "B  H  B", "BB   BB", "BB   BB", "B  H  B", "BB   BB" },
        { "    B  ", "   BBB ", "  BBBBB", " BBBBB ", "BBHBBHBB", " BBBBB ", " BBBBB ", "  BBBBB", "   BBB " },
        { "    B  ", "   BBB ", "  BBBBB", " BBBBB ", "BBHBBHBB", " BBBBB ", " BBBBB ", "  BBBBB", "   BBB " },
        { "    B  ", "   BBB ", "  BBBBB", " BBBBB ", "BBHBBHBB", " BBBBB ", " BBBBB ", "  BBBBB", "   BBB " },
        { "    B  ", "   BBB ", "  BBBBB", " BBBBB ", "BBHBBHBB", " BBBBB ", " BBBBB ", "  BBBBB", "   BBB " },
        { "A     A", "A     A", "A     A", "BB   BB", "B  H  B", "BB   BB", "BB   BB", "B  H  B", "BB   BB" },
        { "AAA AAA", "AAA AAA", "AAA AAA", "BB   BB", "B  H  B", "BB   BB", "BB   BB", "B  H  B", "BB   BB" },
        { "B     B", "B     B", "BB   BB", " B RRB ", " BRH RB", " BRRB  ", " B RRB ", " BRH RB", " BRRB  " },
        { "B     B", "B     B", "BB   BB", " B RRB ", " BRH RB", " BRRB  ", " B RRB ", " BRH RB", " BRRB  " },

        // Y = 0 (Pedestal at center)
        { "        ", "        ", "        ", "        ", "   P    ", "        ", "        ", "        ", "        " },

        // Y = 1 (Air)
        { "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        " },

        // Y = 2 (Air)
        { "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ",
            "        " }, };

}
