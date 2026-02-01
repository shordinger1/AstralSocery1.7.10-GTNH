/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * ConstellationAevitas - The Day/Life constellation
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.major;

import java.awt.*;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;

/**
 * Aevitas - The Day/Life constellation
 * <p>
 * This constellation represents life, growth, and the day.
 * It is associated with biological processes, agriculture, and living creatures.
 * <p>
 * Color: Cyan (#00FFFF)
 * Type: Major Constellation
 */
public class ConstellationAevitas extends ConstellationBase.Major implements IMajorConstellation {

    public ConstellationAevitas() {
        super("aevitas", new Color(0, 255, 255));

        // Define constellation shape
        // Aevitas has a complex shape with multiple stars
        StarLocation s1 = addStar(15, 15); // Center star
        StarLocation s2 = addStar(17, 14);
        StarLocation s3 = addStar(18, 12);
        StarLocation s4 = addStar(18, 10);
        StarLocation s5 = addStar(16, 9);
        StarLocation s6 = addStar(14, 9);
        StarLocation s7 = addStar(12, 10);
        StarLocation s8 = addStar(12, 12);
        StarLocation s9 = addStar(13, 14);
        StarLocation s10 = addStar(19, 8);
        StarLocation s11 = addStar(20, 6);
        StarLocation s12 = addStar(11, 8);
        StarLocation s13 = addStar(10, 6);

        // Connect stars to form constellation shape
        if (s1 != null && s2 != null) addConnection(s1, s2);
        if (s2 != null && s3 != null) addConnection(s2, s3);
        if (s3 != null && s4 != null) addConnection(s3, s4);
        if (s4 != null && s5 != null) addConnection(s4, s5);
        if (s5 != null && s6 != null) addConnection(s5, s6);
        if (s6 != null && s7 != null) addConnection(s6, s7);
        if (s7 != null && s8 != null) addConnection(s7, s8);
        if (s8 != null && s9 != null) addConnection(s8, s9);
        if (s9 != null && s1 != null) addConnection(s9, s1);

        // Additional outer connections
        if (s4 != null && s10 != null) addConnection(s4, s10);
        if (s10 != null && s11 != null) addConnection(s10, s11);
        if (s8 != null && s12 != null) addConnection(s8, s12);
        if (s12 != null && s13 != null) addConnection(s12, s13);

        // TODO: Add signature items for constellation discovery
        // addSignatureItem(new ItemHandle(...));
    }
}
