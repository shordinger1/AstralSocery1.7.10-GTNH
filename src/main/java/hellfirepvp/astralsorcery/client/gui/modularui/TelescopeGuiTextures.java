/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;

import hellfirepvp.astralsorcery.common.lib.Constants;
import net.minecraft.util.ResourceLocation;

/**
 * Texture definitions for Telescope GUI
 */
public class TelescopeGuiTextures {

    /**
     * Background grid texture
     * Location: assets/astralsorcery/textures/gui/gridtelescope.png
     * Size: 280x280
     */
    public static final UITexture BACKGROUND = UITexture.builder()
        .location(new ResourceLocation(Constants.MODID, "textures/gui/gridtelescope"))
        .imageSize(280, 280)
        .uv(0, 0, 280, 280)
        .build();

    /**
     * Clockwise rotation arrow
     * Location: assets/astralsorcery/textures/gui/guijarrow.png
     * Size: Variable (will be defined by texture size)
     */
    public static final UITexture ARROW_CW = UITexture.builder()
        .location(new ResourceLocation(Constants.MODID, "textures/gui/guijarrow"))
        .imageSize(20, 20)
        .uv(0, 0, 20, 20)
        .build();

    /**
     * Counter-clockwise rotation arrow (reversed CW arrow)
     */
    public static final UITexture ARROW_CCW = UITexture.builder()
        .location(new ResourceLocation(Constants.MODID, "textures/gui/guijarrow"))
        .imageSize(20, 20)
        .uv(0, 0, 20, 20)
        .build();

    private TelescopeGuiTextures() {} // Prevent instantiation
}
