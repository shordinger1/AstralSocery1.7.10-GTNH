/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiBlockArrays
 * Created by HellFirePvP
 * Date: 16.05.2016 / 15:45
 */
public class MultiBlockArrays {

    public static StructureBlockArray ancientShrine;

    public static StructureBlockArray desertShrine;

    public static StructureBlockArray smallShrine;

    public static StructureBlockArray treasureShrine;

    public static StructureBlockArray smallRuin;
    public static PatternBlockArray patternSmallRuin;

    public static PatternBlockArray patternFountainPattern;
    public static PatternBlockArray patternStarlightInfuserPattern;

    // 1.7.10: GregTech StructureLib multiblocks use Object type
    // since they don't extend PatternBlockArray (they use IStructureDefinition)
    @PasteBlacklist
    public static Object patternRitualPedestal;

    @PasteBlacklist
    public static Object patternRitualPedestalWithLink;

    public static Object patternAltarAttunement;

    public static Object patternAltarConstellation;

    public static Object patternAltarTrait;

    public static Object patternAttunementFrame;

    public static Object patternStarlightInfuser;

    public static Object patternCollectorRelay;

    public static Object patternCelestialGateway;

    public static Object patternFountain;

    @PasteBlacklist
    public static Object patternCollectorEnhancement;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface PasteBlacklist {}

}
