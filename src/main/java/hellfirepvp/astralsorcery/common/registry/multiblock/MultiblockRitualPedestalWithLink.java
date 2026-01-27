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
 * Ritual Pedestal with Link structure
 * Same as Ritual Pedestal but with a ritual link on top
 */
public class MultiblockRitualPedestalWithLink {

    private static final String STRUCTURE_ID = "pattern_ritual_pedestal_link";
    private static IStructureDefinition<MultiblockRitualPedestalWithLink> STRUCTURE_DEFINITION = null;

    public MultiblockRitualPedestalWithLink() {
        // Structure defined via getStructureDefinition()
    }

    public IStructureDefinition<MultiblockRitualPedestalWithLink> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MultiblockRitualPedestalWithLink>builder()
                .addShape(STRUCTURE_ID, transpose(shape))
                .addElement('P', ofBlock(BlocksAS.ritualPedestal, 0))
                .addElement('L', ofBlock(BlocksAS.ritualLink, 0))
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

        // Y = 1 (Ritual Link)
        { "        ", "        ", "        ", "        ", "   L    ", "        ", "        ", "        ", "        " },

        // Y = 2 (Air)
        { "        ", "        ", "        ", "        ", "        ", "        ", "        ", "        ",
            "        " }, };

}
