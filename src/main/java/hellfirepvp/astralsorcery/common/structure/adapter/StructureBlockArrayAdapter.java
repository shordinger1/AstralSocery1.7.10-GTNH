/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/
package hellfirepvp.astralsorcery.common.structure.adapter;

import java.util.Map;

import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.common.structure.MatchableStructure;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray.BlockInformation;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * Adapter class to convert StructureBlockArray (used for world generation)
 * to PatternBlockArray (used for structure validation)
 *
 * This allows natural structures to be registered as matchable structures
 */
public class StructureBlockArrayAdapter extends PatternBlockArray {

    /**
     * Create a PatternBlockArray from a StructureBlockArray
     */
    public static PatternBlockArray fromStructureBlockArray(ResourceLocation registryName,
        StructureBlockArray structureArray) {
        PatternBlockArray pattern = new PatternBlockArray(registryName);

        // Copy all blocks from StructureBlockArray to PatternBlockArray
        for (Map.Entry<BlockPos, BlockInformation> entry : structureArray.getPattern()
            .entrySet()) {
            BlockPos offset = entry.getKey();
            BlockInformation info = entry.getValue();

            // Use addBlock with match instead of directly accessing pattern.map
            // This ensures size is calculated correctly
            if (info.matcher != null) {
                pattern.addBlock(offset, info.state, info.matcher);
            } else {
                pattern.addBlock(offset, info.state);
            }
        }

        return pattern;
    }

    /**
     * Create a MatchableStructure adapter that wraps a StructureBlockArray
     */
    public static MatchableStructure asMatchable(ResourceLocation registryName, StructureBlockArray structureArray) {
        return new MatchableStructure() {

            private final PatternBlockArray pattern = fromStructureBlockArray(registryName, structureArray);

            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }

            /**
             * Get the underlying PatternBlockArray for validation
             */
            public PatternBlockArray getPattern() {
                return pattern;
            }
        };
    }

    public StructureBlockArrayAdapter(ResourceLocation registryName, StructureBlockArray structureArray) {
        super(registryName);

        // Copy all blocks using addBlock instead of direct pattern access
        for (Map.Entry<BlockPos, BlockInformation> entry : structureArray.getPattern()
            .entrySet()) {
            BlockPos offset = entry.getKey();
            BlockInformation info = entry.getValue();
            if (info.matcher != null) {
                this.addBlock(offset, info.state, info.matcher);
            } else {
                this.addBlock(offset, info.state);
            }
        }
    }

    /**
     * Create an adapter from an existing StructureBlockArray instance
     */
    public StructureBlockArrayAdapter(ResourceLocation registryName, BlockArray blockArray) {
        super(registryName);

        // Copy all blocks using addBlock instead of direct pattern access
        for (Map.Entry<BlockPos, BlockInformation> entry : blockArray.getPattern()
            .entrySet()) {
            BlockPos offset = entry.getKey();
            BlockInformation info = entry.getValue();
            if (info.matcher != null) {
                this.addBlock(offset, info.state, info.matcher);
            } else {
                this.addBlock(offset, info.state);
            }
        }
    }

}
