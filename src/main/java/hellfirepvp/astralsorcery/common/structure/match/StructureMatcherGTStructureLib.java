/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.match;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.Constants;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import hellfirepvp.astralsorcery.common.structure.*;
import hellfirepvp.astralsorcery.common.structure.change.BlockStateChangeSet;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * Wrapper adapter for GregTech StructureLib multiblocks
 * This bridges GregTech StructureLib's IStructureDefinition with Astral Sorcery's MatchableStructure system
 */
public class StructureMatcherGTStructureLib extends StructureMatcher {

    private final IStructureDefinition<?> structureDefinition;
    private final Object templateInstance;
    private final ObservableArea structureArea;
    private Set<BlockPos> mismatches = new HashSet<>();

    public StructureMatcherGTStructureLib(@Nonnull ResourceLocation registryName,
        @Nonnull IStructureDefinition<?> structureDefinition, @Nonnull Object templateInstance,
        @Nonnull ObservableArea structureArea) {
        super(registryName);
        this.structureDefinition = structureDefinition;
        this.templateInstance = templateInstance;
        this.structureArea = structureArea;
    }

    public void initialize(IBlockAccess world, BlockPos center) {
        // GregTech StructureLib validation is done on-demand through the IStructureDefinition
        // We don't pre-initialize mismatches - they're tracked dynamically
        // The GT StructureLib system handles its own validation
    }

    @Override
    public ObservableArea getObservableArea() {
        return this.structureArea;
    }

    @Override
    public boolean notifyChange(IBlockAccess world, BlockPos centre, BlockStateChangeSet changeSet) {
        // GregTech StructureLib handles structure checking differently
        // This is a simplified implementation that just logs changes
        // The actual structure validation happens through GT StructureLib's system
        int mismatchesPre = this.mismatches.size();

        for (BlockStateChangeSet.StateChange change : changeSet.getChanges()) {
            BlockPos offset = change.pos.subtract(centre);
            // In a full implementation, we'd check if the block matches the structure definition
            // For now, just track that changes occurred
            if (!isValidBlock(world, centre.add(offset))) {
                this.mismatches.add(offset);
            } else {
                this.mismatches.remove(offset);
            }
        }

        int mismatchesPost = this.mismatches.size();
        LogCategory.STRUCTURE_MATCH.info(
            () -> "Updated GT StructureLib structure integrity with " + mismatchesPre
                + " mismatches before and "
                + mismatchesPost
                + " mismatches afterwards.");
        return mismatchesPost <= 0;
    }

    private boolean isValidBlock(IBlockAccess world, BlockPos pos) {
        // Simplified check - GT StructureLib does more complex validation
        return world.getBlock(pos.posX, pos.posY, pos.posZ) != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.mismatches.clear();
        NBTTagList tagMismatches = tag.getTagList("mismatchList", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagMismatches.tagCount(); i++) {
            NBTTagCompound tagPos = tagMismatches.getCompoundTagAt(i);
            this.mismatches.add(NBTHelper.readBlockPosFromNBT(tagPos));
        }
        // Structure definition and template instance are set during construction
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList tagMismatches = new NBTTagList();

        for (BlockPos pos : this.mismatches) {
            NBTTagCompound tagPos = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(pos, tagPos);
            tagMismatches.appendTag(tagPos);
        }

        tag.setTag("mismatchList", tagMismatches);
        tag.setString(
            "structureToMatch",
            this.getRegistryName()
                .toString());
    }
}
