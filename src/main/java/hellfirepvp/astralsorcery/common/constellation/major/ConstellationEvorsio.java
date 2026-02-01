/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationEvorsio - The Consumption/Decay constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.major;

import java.awt.*;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * Evorsio - The Consumption/Decay constellation
 * <p>
 * This constellation represents consumption, decay, and entropy.
 * It is associated with destruction, consumption of resources, and entropy.
 * <p>
 * Color: Orange (#FF6600)
 * Type: Major Constellation
 */
public class ConstellationEvorsio extends ConstellationBase.Major implements IMajorConstellation {

    public ConstellationEvorsio() {
        super("evorsio", new Color(255, 102, 0));

        // Define constellation shape - spiral/consuming pattern
        StarLocation s1 = addStar(15, 15); // Center
        StarLocation s2 = addStar(16, 14);
        StarLocation s3 = addStar(17, 13);
        StarLocation s4 = addStar(17, 12);
        StarLocation s5 = addStar(16, 11);
        StarLocation s6 = addStar(15, 10);
        StarLocation s7 = addStar(14, 11);
        StarLocation s8 = addStar(13, 12);
        StarLocation s9 = addStar(13, 13);
        StarLocation s10 = addStar(14, 14);
        StarLocation s11 = addStar(18, 13);
        StarLocation s12 = addStar(18, 11);
        StarLocation s13 = addStar(12, 13);
        StarLocation s14 = addStar(12, 11);

        // Connect stars to form spiral
        if (s1 != null && s2 != null) addConnection(s1, s2);
        if (s2 != null && s3 != null) addConnection(s2, s3);
        if (s3 != null && s4 != null) addConnection(s3, s4);
        if (s4 != null && s5 != null) addConnection(s4, s5);
        if (s5 != null && s6 != null) addConnection(s5, s6);
        if (s6 != null && s7 != null) addConnection(s6, s7);
        if (s7 != null && s8 != null) addConnection(s7, s8);
        if (s8 != null && s9 != null) addConnection(s8, s9);
        if (s9 != null && s10 != null) addConnection(s9, s10);
        if (s10 != null && s1 != null) addConnection(s10, s1);

        // Outer extensions
        if (s4 != null && s11 != null) addConnection(s4, s11);
        if (s5 != null && s12 != null) addConnection(s5, s12);
        if (s8 != null && s13 != null) addConnection(s8, s13);
        if (s9 != null && s14 != null) addConnection(s9, s14);

        // TODO: Add signature items for constellation discovery
    }
}
