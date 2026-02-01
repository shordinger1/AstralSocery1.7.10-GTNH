/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationAtertea - A minor constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.minor;

import java.awt.*;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * Atertea - A minor constellation
 * <p>
 * This minor constellation appears during specific moon phases.
 * It is associated with darkness and mystery.
 * <p>
 * Color: Dark Gray (#4A4A4A)
 * Type: Minor Constellation
 * Moon Phases: NEW, WANING1_4
 */
public class ConstellationAtertea extends ConstellationBase.Minor implements IMinorConstellation {

    public ConstellationAtertea() {
        super("attertea", new Color(74, 74, 74), MoonPhase.NEW, MoonPhase.WANING1_4);

        // Define constellation shape - mysterious pattern
        StarLocation s1 = addStar(15, 15); // Center
        StarLocation s2 = addStar(16, 17);
        StarLocation s3 = addStar(18, 18);
        StarLocation s4 = addStar(20, 17);
        StarLocation s5 = addStar(21, 15);
        StarLocation s6 = addStar(20, 13);
        StarLocation s7 = addStar(18, 12);
        StarLocation s8 = addStar(16, 13);
        StarLocation s9 = addStar(13, 13);
        StarLocation s10 = addStar(12, 15);
        StarLocation s11 = addStar(13, 17);

        // Connect stars to form mysterious shape
        if (s1 != null && s2 != null) addConnection(s1, s2);
        if (s2 != null && s3 != null) addConnection(s2, s3);
        if (s3 != null && s4 != null) addConnection(s3, s4);
        if (s4 != null && s5 != null) addConnection(s4, s5);
        if (s5 != null && s6 != null) addConnection(s5, s6);
        if (s6 != null && s7 != null) addConnection(s6, s7);
        if (s7 != null && s8 != null) addConnection(s7, s8);
        if (s8 != null && s1 != null) addConnection(s8, s1);

        // Left side
        if (s1 != null && s9 != null) addConnection(s1, s9);
        if (s9 != null && s10 != null) addConnection(s9, s10);
        if (s10 != null && s11 != null) addConnection(s10, s11);
        if (s11 != null && s1 != null) addConnection(s11, s1);

        // TODO: Add signature items for constellation discovery
    }
}
