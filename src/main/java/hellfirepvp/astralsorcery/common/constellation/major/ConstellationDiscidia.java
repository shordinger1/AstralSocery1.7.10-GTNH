/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationDiscidia - The Conflict/Combat constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.major;

import java.awt.*;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * Discidia - The Conflict/Combat constellation
 * <p>
 * This constellation represents conflict, combat, and struggle.
 * It is associated with weapons, battles, and offensive magic.
 * <p>
 * Color: Red (#CC0000)
 * Type: Major Constellation
 */
public class ConstellationDiscidia extends ConstellationBase.Major implements IMajorConstellation {

    public ConstellationDiscidia() {
        super("discidia", new Color(204, 0, 0));

        // Define constellation shape - sword/weapon-like pattern
        StarLocation s1 = addStar(15, 18); // Handle bottom
        StarLocation s2 = addStar(15, 16);
        StarLocation s3 = addStar(15, 14); // Guard
        StarLocation s4 = addStar(13, 14);
        StarLocation s5 = addStar(17, 14);
        StarLocation s6 = addStar(15, 12); // Blade start
        StarLocation s7 = addStar(15, 10);
        StarLocation s8 = addStar(15, 8);
        StarLocation s9 = addStar(15, 6);
        StarLocation s10 = addStar(14, 4); // Point
        StarLocation s11 = addStar(16, 4);
        StarLocation s12 = addStar(15, 5);

        // Connect stars to form sword shape
        if (s1 != null && s2 != null) addConnection(s1, s2);
        if (s2 != null && s3 != null) addConnection(s2, s3);

        // Guard
        if (s3 != null && s4 != null) addConnection(s3, s4);
        if (s3 != null && s5 != null) addConnection(s3, s5);

        // Blade
        if (s3 != null && s6 != null) addConnection(s3, s6);
        if (s6 != null && s7 != null) addConnection(s6, s7);
        if (s7 != null && s8 != null) addConnection(s7, s8);
        if (s8 != null && s9 != null) addConnection(s8, s9);

        // Point
        if (s9 != null && s10 != null) addConnection(s9, s10);
        if (s9 != null && s11 != null) addConnection(s9, s11);
        if (s10 != null && s12 != null) addConnection(s10, s12);
        if (s11 != null && s12 != null) addConnection(s11, s12);

        // TODO: Add signature items for constellation discovery
    }
}
