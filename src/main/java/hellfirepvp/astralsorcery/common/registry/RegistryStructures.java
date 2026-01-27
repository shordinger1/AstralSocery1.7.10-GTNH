/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import static hellfirepvp.astralsorcery.common.lib.MultiBlockArrays.*;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.registry.multiblock.*;
import hellfirepvp.astralsorcery.common.registry.structures.*;
import hellfirepvp.astralsorcery.common.structure.MatchableStructure;
import hellfirepvp.astralsorcery.common.structure.ObservableAreaBoundingBox;
import hellfirepvp.astralsorcery.common.structure.StructureMatcher;
import hellfirepvp.astralsorcery.common.structure.StructureMatcherRegistry;
import hellfirepvp.astralsorcery.common.structure.StructureRegistry;
import hellfirepvp.astralsorcery.common.structure.adapter.StructureBlockArrayAdapter;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherGTStructureLib;
import hellfirepvp.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import hellfirepvp.astralsorcery.common.util.Provider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryStructures
 * Created by HellFirePvP
 * Date: 16.05.2016 / 15:45
 */
public class RegistryStructures {

    public static void init() {
        // Initialize natural structure instances for world gen
        ancientShrine = new StructureAncientShrine();
        desertShrine = new StructureDesertShrine();
        smallShrine = new StructureSmallShrine();
        treasureShrine = new StructureTreasureShrine();
        smallRuin = new StructureSmallRuin();

        // Register natural structures as matchable PatternBlockArray structures
        // This allows them to be validated by the structure matching system
        registerStructureBlockArray("ancient_shrine", ancientShrine);
        registerStructureBlockArray("desert_shrine", desertShrine);
        registerStructureBlockArray("small_shrine", smallShrine);
        registerStructureBlockArray("treasure_shrine", treasureShrine);
        registerStructureBlockArray("small_ruin", smallRuin);

        // 1.7.10: Register GregTech StructureLib multiblocks
        // These use registerGTMultiblock() instead of registerPattern()
        // because they use IStructureDefinition, not PatternBlockArray
        MultiblockRitualPedestal ritualPedestal = new MultiblockRitualPedestal();
        patternRitualPedestal = ritualPedestal;
        registerGTMultiblock(ritualPedestal, ritualPedestal.getStructureId());

        MultiblockRitualPedestalWithLink ritualPedestalWithLink = new MultiblockRitualPedestalWithLink();
        patternRitualPedestalWithLink = ritualPedestalWithLink;
        registerGTMultiblock(ritualPedestalWithLink, ritualPedestalWithLink.getStructureId());

        MultiblockAltarAttunement altarAttunement = new MultiblockAltarAttunement();
        patternAltarAttunement = altarAttunement;
        registerGTMultiblock(altarAttunement, altarAttunement.getStructureId());

        MultiblockAltarConstellation altarConstellation = new MultiblockAltarConstellation();
        patternAltarConstellation = altarConstellation;
        registerGTMultiblock(altarConstellation, altarConstellation.getStructureId());

        MultiblockAltarTrait altarTrait = new MultiblockAltarTrait();
        patternAltarTrait = altarTrait;
        registerGTMultiblock(altarTrait, altarTrait.getStructureId());

        MultiblockAttunementFrame attunementFrame = new MultiblockAttunementFrame();
        patternAttunementFrame = attunementFrame;
        registerGTMultiblock(attunementFrame, attunementFrame.getStructureId());

        MultiblockStarlightInfuser starlightInfuser = new MultiblockStarlightInfuser();
        patternStarlightInfuser = starlightInfuser;
        registerGTMultiblock(starlightInfuser, starlightInfuser.getStructureId());
        // Also register starlightInfuser as PatternBlockArray for tiles that require it
        patternStarlightInfuserPattern = new PatternBlockArray(starlightInfuser.getStructureId()) {};
        registerPattern(patternStarlightInfuserPattern);

        MultiblockStarlightRelay starlightRelay = new MultiblockStarlightRelay();
        patternCollectorRelay = starlightRelay;
        registerGTMultiblock(starlightRelay, starlightRelay.getStructureId());

        MultiblockGateway gateway = new MultiblockGateway();
        patternCelestialGateway = gateway;
        registerGTMultiblock(gateway, gateway.getStructureId());

        MultiblockCrystalEnhancement crystalEnhancement = new MultiblockCrystalEnhancement();
        patternCollectorEnhancement = crystalEnhancement;
        registerGTMultiblock(crystalEnhancement, crystalEnhancement.getStructureId());

        MultiblockFountain fountain = new MultiblockFountain();
        patternFountain = fountain;
        registerGTMultiblock(fountain, fountain.getStructureId());
        // Also register fountain as PatternBlockArray for tiles that require it
        patternFountainPattern = new PatternBlockArray(fountain.getStructureId()) {};
        registerPattern(patternFountainPattern);

        // Also register smallRuin as a PatternBlockArray for direct access
        patternSmallRuin = StructureBlockArrayAdapter
            .fromStructureBlockArray(new ResourceLocation(AstralSorcery.MODID, "pattern_small_ruin"), smallRuin);
        registerPattern(patternSmallRuin);
    }

    private static <T extends PatternBlockArray> T registerPattern(T pattern) {
        StructureRegistry.INSTANCE.register(pattern);
        StructureMatcherRegistry.INSTANCE.register(new Provider<StructureMatcher>() {

            @Override
            public StructureMatcher provide() {
                return new StructureMatcherPatternArray(pattern.getRegistryName());
            }
        });
        return pattern;
    }

    /**
     * Register a StructureBlockArray (used for world generation) as a matchable structure
     * This converts it to a PatternBlockArray so it can be validated by tiles
     */
    private static void registerStructureBlockArray(String name,
        hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray structureArray) {
        ResourceLocation registryName = new ResourceLocation(AstralSorcery.MODID, name);
        PatternBlockArray pattern = StructureBlockArrayAdapter.fromStructureBlockArray(registryName, structureArray);
        StructureRegistry.INSTANCE.register(pattern);
        StructureMatcherRegistry.INSTANCE.register(new Provider<StructureMatcher>() {

            @Override
            public StructureMatcher provide() {
                return new StructureMatcherPatternArray(registryName);
            }
        });
    }

    // 1.7.10: Register GregTech StructureLib multiblocks
    // These use IStructureDefinition instead of PatternBlockArray
    private static void registerGTMultiblock(Object multiblock, ResourceLocation structureId) {
        // Create a MatchableStructure adapter for GregTech StructureLib
        MatchableStructure adapter = new MatchableStructure() {

            @Override
            public ResourceLocation getRegistryName() {
                return structureId;
            }
        };

        // Register the adapter with the structure registry
        StructureRegistry.INSTANCE.register(adapter);

        // Register a matcher that uses GT StructureLib's validation
        // The actual structure checking is done by GT StructureLib's IStructureDefinition.check()
        final ResourceLocation id = structureId;
        final Object mb = multiblock;
        StructureMatcherRegistry.INSTANCE.register(new Provider<StructureMatcher>() {

            @Override
            public StructureMatcher provide() {
                return new StructureMatcherGTStructureLib(
                    id,
                    getStructureDefinition(mb),
                    mb,
                    new ObservableAreaBoundingBox(new Vec3i(-5, -5, -5), new Vec3i(5, 5, 5)));
            }
        });
    }

    // Helper method to get IStructureDefinition from multiblock object using reflection
    private static IStructureDefinition<?> getStructureDefinition(Object multiblock) {
        try {
            java.lang.reflect.Method method = multiblock.getClass()
                .getMethod("getStructureDefinition");
            return (IStructureDefinition<?>) method.invoke(multiblock);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to get structure definition from " + multiblock.getClass()
                    .getName(),
                e);
        }
    }

}
