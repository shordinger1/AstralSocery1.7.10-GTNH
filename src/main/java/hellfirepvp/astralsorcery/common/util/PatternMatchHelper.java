/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import hellfirepvp.astralsorcery.common.structure.MatchableStructure;
import hellfirepvp.astralsorcery.common.structure.StructureRegistry;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.change.ChangeSubscriber;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherGTStructureLib;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternMatchHelper
 * Created by HellFirePvP
 * Date: 30.12.2018 / 12:47
 */
public class PatternMatchHelper {

    /**
     * Get or create a matcher for PatternBlockArray structures
     */
    public static ChangeSubscriber<StructureMatcherPatternArray> getOrCreateMatcher(World world, BlockPos pos,
        PatternBlockArray pattern) {
        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChangeSubscriber<?> existingSubscriber = buf.getSubscriber(pos);
        if (existingSubscriber != null) {
            return (ChangeSubscriber<StructureMatcherPatternArray>) existingSubscriber;
        } else {
            return buf.observeAndInitializePattern(world, pos, pattern);
        }
    }

    /**
     * Get or create a matcher for GregTech StructureLib multiblocks
     * 1.7.10: New method for GT StructureLib integration
     */
    public static ChangeSubscriber<StructureMatcherGTStructureLib> getOrCreateGTMatcher(World world, BlockPos pos,
        ResourceLocation structureId, Object multiblock) {
        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChangeSubscriber<?> existingSubscriber = buf.getSubscriber(pos);
        if (existingSubscriber != null) {
            return (ChangeSubscriber<StructureMatcherGTStructureLib>) existingSubscriber;
        } else {
            // Create new matcher for GT StructureLib multiblock
            // This requires the multiblock to have getStructureDefinition() method
            StructureMatcherGTStructureLib matcher = createGTMatcher(structureId, multiblock);
            return buf.observeAndInitializeGT(world, pos, matcher);
        }
    }

    /**
     * Create a GregTech StructureLib matcher
     */
    private static StructureMatcherGTStructureLib createGTMatcher(ResourceLocation structureId, Object multiblock) {
        try {
            java.lang.reflect.Method method = multiblock.getClass()
                .getMethod("getStructureDefinition");
            com.gtnewhorizon.structurelib.structure.IStructureDefinition<?> definition = (com.gtnewhorizon.structurelib.structure.IStructureDefinition<?>) method
                .invoke(multiblock);

            return new StructureMatcherGTStructureLib(
                structureId,
                definition,
                multiblock,
                new hellfirepvp.astralsorcery.common.structure.ObservableAreaBoundingBox(
                    new BlockPos(-5, -5, -5),
                    new BlockPos(5, 5, 5)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create GT StructureLib matcher for " + structureId, e);
        }
    }

    /**
     * Get a matcher by registry name (works for both PatternBlockArray and GT StructureLib)
     */
    public static ChangeSubscriber<?> getOrCreateMatcher(World world, BlockPos pos, ResourceLocation structureId) {
        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChangeSubscriber<?> existingSubscriber = buf.getSubscriber(pos);
        if (existingSubscriber != null) {
            return existingSubscriber;
        }

        // Try to find the structure and create appropriate matcher
        MatchableStructure structure = StructureRegistry.INSTANCE.getStructure(structureId);
        if (structure != null) {
            if (structure instanceof PatternBlockArray) {
                return buf.observeAndInitializePattern(world, pos, (PatternBlockArray) structure);
            }
            // For GT StructureLib structures, the matcher is created during registration
            // This is a fallback that shouldn't normally be hit
        }
        return null;
    }

}
