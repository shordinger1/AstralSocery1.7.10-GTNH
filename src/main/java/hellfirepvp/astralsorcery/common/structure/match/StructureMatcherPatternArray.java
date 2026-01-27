/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure.match;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.Constants;

import hellfirepvp.astralsorcery.common.structure.*;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.change.BlockStateChangeSet;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatcherPatternArray
 * Created by HellFirePvP
 * Date: 02.12.2018 / 13:24
 */
public class StructureMatcherPatternArray extends StructureMatcher {

    private PatternBlockArray structure;
    private ObservableArea structureArea;

    private Set<BlockPos> mismatches = new HashSet<>();

    public StructureMatcherPatternArray(@Nonnull ResourceLocation registryName) {
        super(registryName);
        setStructure(registryName);
    }

    private void setStructure(ResourceLocation structName) {
        MatchableStructure struct = StructureRegistry.INSTANCE.getStructure(structName);
        if (struct instanceof PatternBlockArray) {
            this.structure = (PatternBlockArray) struct;
            // 1.7.10: ObservableAreaBoundingBox expects Vec3i, not BlockPos
            this.structureArea = new ObservableAreaBoundingBox(
                new Vec3i(structure.getMin().posX, structure.getMin().posY, structure.getMin().posZ),
                new Vec3i(structure.getMax().posX, structure.getMax().posY, structure.getMax().posZ));
        } else {
            throw new IllegalArgumentException(
                "Passed structure matcher key does not have a registered underlying structure pattern: " + structName);
        }
    }

    public void initialize(IBlockAccess world, BlockPos center) {
        for (BlockPos offset : this.structure.getPattern()
            .keySet()) {
            if (!this.structure.matchSingleBlock(world, center, offset)) {
                this.mismatches.add(offset);
            }
        }
        LogCategory.STRUCTURE_MATCH.info(
            () -> "Structure matcher initialized at " + center
                + " with "
                + this.mismatches.size()
                + " initial mismatches!");
    }

    @Override
    public ObservableArea getObservableArea() {
        return this.structureArea;
    }

    @Override
    public boolean notifyChange(IBlockAccess world, BlockPos centre, BlockStateChangeSet changeSet) {
        int mismatchesPre = this.mismatches.size();

        for (BlockStateChangeSet.StateChange change : changeSet.getChanges()) {
            if (this.structure.hasBlockAt(change.pos)
                && !this.structure.matchSingleBlockState(change.pos, change.newState)) {

                this.mismatches.add(change.pos);
            } else {
                this.mismatches.remove(change.pos);
            }
        }

        Iterator<BlockPos> it = this.mismatches.iterator();
        while (it.hasNext()) {
            BlockPos mismatchPos = it.next();
            if (!this.structure.hasBlockAt(mismatchPos)) {
                it.remove();
            }
        }

        int mismatchesPost = this.mismatches.size();
        LogCategory.STRUCTURE_MATCH.info(
            () -> "Updated structure integrity with " + mismatchesPre
                + " mismatches before and "
                + mismatchesPost
                + " mismatches afterwards.");
        if (mismatchesPost > 0) {
            // 1.7.10: Use joinBlockPositions for BlockPos set instead of joinPositions
            LogCategory.STRUCTURE_MATCH
                .info(() -> "Found mismatches at (relative to center): " + joinBlockPositions(this.mismatches));
        }
        return mismatchesPost <= 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.mismatches.clear();
        NBTTagList tagMismatches = tag.getTagList("mismatchList", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagMismatches.tagCount(); i++) {
            NBTTagCompound tagPos = tagMismatches.getCompoundTagAt(i);
            this.mismatches.add(NBTHelper.readBlockPosFromNBT(tagPos));
        }

        setStructure(new ResourceLocation(tag.getString("structureToMatch")));
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
            this.structure.getRegistryName()
                .toString());
    }

    private String joinPositions(Set<Vec3i> positions) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Vec3i pos : positions) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(pos.toString());
            first = false;
        }
        return sb.toString();
    }

    // 1.7.10: Add helper to convert BlockPos set to Vec3i set for logging
    private String joinBlockPositions(Set<BlockPos> positions) {
        Set<Vec3i> vecSet = new HashSet<>();
        for (BlockPos pos : positions) {
            vecSet.add(new Vec3i(pos.posX, pos.posY, pos.posZ));
        }
        return joinPositions(vecSet);
    }

}
