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
 * Texture definitions for Hand Telescope GUI
 */
public class HandTelescopeGuiTextures {

    /**
     * Background grid texture
     * Location: assets/astralsorcery/textures/gui/gridhandtelescope.png
     * Size: 216x216
     */
    public static final UITexture BACKGROUND = UITexture.builder()
        .location(new ResourceLocation(Constants.MODID, "textures/gui/gridhandtelescope"))
        .imageSize(216, 216)
        .uv(0, 0, 216, 216)
        .build();

    private HandTelescopeGuiTextures() {} // Prevent instantiation
}
