/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationVicio - The Greed/Vice constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.major;

import java.awt.*;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * Vicio - The Greed/Vice constellation
 * <p>
 * This constellation represents greed, excess, and temptation.
 * It is associated with accumulation, hoarding, and vice.
 * <p>
 * Color: Purple (#6600CC)
 * Type: Major Constellation
 */
public class ConstellationVicio extends ConstellationBase.Major implements IMajorConstellation {

    public ConstellationVicio() {
        super("vicio", new Color(102, 0, 204));

        // Define constellation shape - complex greedy pattern
        StarLocation s1 = addStar(15, 15); // Center
        StarLocation s2 = addStar(17, 15);
        StarLocation s3 = addStar(18, 13);
        StarLocation s4 = addStar(17, 11);
        StarLocation s5 = addStar(15, 10);
        StarLocation s6 = addStar(13, 11);
        StarLocation s7 = addStar(12, 13);
        StarLocation s8 = addStar(13, 15);
        StarLocation s9 = addStar(15, 12);
        StarLocation s10 = addStar(17, 12);
        StarLocation s11 = addStar(13, 12);
        StarLocation s12 = addStar(15, 18);
        StarLocation s13 = addStar(15, 8);

        // Connect stars to form complex shape
        if (s1 != null && s2 != null) addConnection(s1, s2);
        if (s2 != null && s3 != null) addConnection(s2, s3);
        if (s3 != null && s4 != null) addConnection(s3, s4);
        if (s4 != null && s5 != null) addConnection(s4, s5);
        if (s5 != null && s6 != null) addConnection(s5, s6);
        if (s6 != null && s7 != null) addConnection(s6, s7);
        if (s7 != null && s8 != null) addConnection(s7, s8);
        if (s8 != null && s1 != null) addConnection(s8, s1);

        // Inner connections (greedy accumulation)
        if (s1 != null && s9 != null) addConnection(s1, s9);
        if (s1 != null && s10 != null) addConnection(s1, s10);
        if (s1 != null && s11 != null) addConnection(s1, s11);

        // Vertical extensions
        if (s1 != null && s12 != null) addConnection(s1, s12);
        if (s1 != null && s13 != null) addConnection(s1, s13);

        // TODO: Add signature items for constellation discovery
    }
}
