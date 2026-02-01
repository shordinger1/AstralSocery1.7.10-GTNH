/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationVectras - The Teleportation/Portal constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.major;

import java.awt.*;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * Vectras - The Teleportation/Portal constellation
 * <p>
 * This constellation represents teleportation, gates, and dimensional travel.
 * It is associated with portals, teleportation, and the Celestial Gateway.
 * <p>
 * Color: Lime Green (#00FF66)
 * Type: Major Constellation
 */
public class ConstellationVectras extends ConstellationBase.Major implements IMajorConstellation {

    public ConstellationVectras() {
        super("vectras", new Color(0, 255, 102));

        // Define constellation shape - portal/gate pattern
        StarLocation s1 = addStar(15, 18); // Bottom left of portal
        StarLocation s2 = addStar(17, 18); // Bottom right of portal
        StarLocation s3 = addStar(17, 16);
        StarLocation s4 = addStar(17, 14);
        StarLocation s5 = addStar(17, 12);
        StarLocation s6 = addStar(17, 10);
        StarLocation s7 = addStar(17, 8);
        StarLocation s8 = addStar(15, 8); // Top left of portal
        StarLocation s9 = addStar(13, 8);
        StarLocation s10 = addStar(11, 8);
        StarLocation s11 = addStar(15, 10); // Inside swirl
        StarLocation s12 = addStar(16, 11);
        StarLocation s13 = addStar(16, 12);
        StarLocation s14 = addStar(15, 13);
        StarLocation s15 = addStar(14, 12);

        // Portal frame (right side)
        if (s1 != null && s2 != null) addConnection(s1, s2);
        if (s2 != null && s3 != null) addConnection(s2, s3);
        if (s3 != null && s4 != null) addConnection(s3, s4);
        if (s4 != null && s5 != null) addConnection(s4, s5);
        if (s5 != null && s6 != null) addConnection(s5, s6);
        if (s6 != null && s7 != null) addConnection(s6, s7);

        // Portal top
        if (s7 != null && s8 != null) addConnection(s7, s8);
        if (s8 != null && s9 != null) addConnection(s8, s9);
        if (s9 != null && s10 != null) addConnection(s9, s10);

        // Portal frame (left side back to bottom)
        if (s10 != null && s9 != null) addConnection(s10, s9);
        if (s8 != null && s1 != null) addConnection(s8, s1);

        // Inside swirl (portal effect)
        if (s11 != null && s12 != null) addConnection(s11, s12);
        if (s12 != null && s13 != null) addConnection(s12, s13);
        if (s13 != null && s14 != null) addConnection(s13, s14);
        if (s14 != null && s15 != null) addConnection(s14, s15);
        if (s15 != null && s11 != null) addConnection(s15, s11);

        // Connect swirl to frame
        if (s8 != null && s15 != null) addConnection(s8, s15);
        if (s7 != null && s11 != null) addConnection(s7, s11);

        // TODO: Add signature items for constellation discovery
    }
}
