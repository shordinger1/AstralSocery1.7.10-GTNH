/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.drawable.UITexture;

import hellfirepvp.astralsorcery.common.lib.Constants;

/**
 * Texture definitions for Observatory GUI
 * <p>
 * The Observatory is a full-screen constellation viewing interface.
 * Uses larger background than telescope for better constellation visibility.
 */
public class ObservatoryGuiTextures {

    /**
     * Background grid texture
     * Location: assets/astralsorcery/textures/gui/gridtelescope.png
     * Size: 280x280 (larger than hand telescope for better viewing)
     */
    public static final UITexture BACKGROUND = UITexture.builder()
        .location(new ResourceLocation(Constants.MODID, "textures/gui/gridtelescope"))
        .imageSize(280, 280)
        .uv(0, 0, 280, 280)
        .build();

    /**
     * Space/constellation background
     * Location: assets/astralsorcery/textures/gui/guijspaceconstellation.png
     * Used for constellation viewing overlay
     */
    public static final UITexture SPACE_BACKGROUND = UITexture.builder()
        .location(new ResourceLocation(Constants.MODID, "textures/gui/guijspaceconstellation"))
        .imageSize(256, 256)
        .uv(0, 0, 256, 256)
        .build();

    /**
     * Observatory frame icon
     * Location: assets/astralsorcery/textures/gui/observatoryframe.png
     * Size: 20x20
     */
    public static final UITexture FRAME = UITexture.builder()
        .location(new ResourceLocation(Constants.MODID, "textures/gui/observatoryframe"))
        .imageSize(20, 20)
        .uv(0, 0, 20, 20)
        .build();

    private ObservatoryGuiTextures() {} // Prevent instantiation
}
