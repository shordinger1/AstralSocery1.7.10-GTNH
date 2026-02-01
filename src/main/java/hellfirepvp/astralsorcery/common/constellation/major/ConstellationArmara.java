/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationArmara - The Protection/Defense constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.major;

import java.awt.*;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * Armara - The Protection/Defense constellation
 * <p>
 * This constellation represents defense, protection, and shielding.
 * It is associated with armor, shields, and defensive magic.
 * <p>
 * Color: Blue (#2843CC)
 * Type: Major Constellation
 */
public class ConstellationArmara extends ConstellationBase.Major implements IMajorConstellation {

    public ConstellationArmara() {
        super("armara", new Color(40, 67, 204));

        // Define constellation shape - shield-like pattern
        StarLocation s1 = addStar(15, 15); // Center
        StarLocation s2 = addStar(17, 13);
        StarLocation s3 = addStar(18, 11);
        StarLocation s4 = addStar(17, 9);
        StarLocation s5 = addStar(15, 8);
        StarLocation s6 = addStar(13, 9);
        StarLocation s7 = addStar(12, 11);
        StarLocation s8 = addStar(13, 13);
        StarLocation s9 = addStar(15, 18); // Bottom extension
        StarLocation s10 = addStar(12, 16);
        StarLocation s11 = addStar(18, 16);

        // Connect stars to form shield shape
        if (s1 != null && s2 != null) addConnection(s1, s2);
        if (s2 != null && s3 != null) addConnection(s2, s3);
        if (s3 != null && s4 != null) addConnection(s3, s4);
        if (s4 != null && s5 != null) addConnection(s4, s5);
        if (s5 != null && s6 != null) addConnection(s5, s6);
        if (s6 != null && s7 != null) addConnection(s6, s7);
        if (s7 != null && s8 != null) addConnection(s7, s8);
        if (s8 != null && s1 != null) addConnection(s8, s1);

        // Bottom connections
        if (s1 != null && s9 != null) addConnection(s1, s9);
        if (s10 != null && s9 != null) addConnection(s10, s9);
        if (s11 != null && s9 != null) addConnection(s11, s9);
        if (s8 != null && s10 != null) addConnection(s8, s10);
        if (s2 != null && s11 != null) addConnection(s2, s11);

        // TODO: Add signature items for constellation discovery
    }
}
