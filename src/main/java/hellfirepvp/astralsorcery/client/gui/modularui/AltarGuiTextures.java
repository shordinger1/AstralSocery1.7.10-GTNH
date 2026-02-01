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

/**
 * Custom texture definitions for Astral Sorcery altar GUIs
 */
public class AltarGuiTextures {

    // Background textures for each altar level
    public static final UITexture DISCOVERY_BACKGROUND = UITexture.builder()
        .location(new ResourceLocation("astralsorcery", "textures/gui/guialtar1.png"))
        .imageSize(176, 166)
        .uv(0, 0, 176, 166)
        .build();

    public static final UITexture ATTUNEMENT_BACKGROUND = UITexture.builder()
        .location(new ResourceLocation("astralsorcery", "textures/gui/guialtar2.png"))
        .imageSize(256, 202)
        .uv(0, 0, 256, 202)
        .build();

    public static final UITexture CONSTELLATION_BACKGROUND = UITexture.builder()
        .location(new ResourceLocation("astralsorcery", "textures/gui/guialtar3.png"))
        .imageSize(255, 202)
        .uv(0, 0, 255, 202)
        .build();

    public static final UITexture TRAIT_BACKGROUND = UITexture.builder()
        .location(new ResourceLocation("astralsorcery", "textures/gui/guialtar4.png"))
        .imageSize(255, 202)
        .uv(0, 0, 255, 202)
        .build();

    // Slot background texture (gridslot.png)
    public static final UITexture SLOT_BACKGROUND = UITexture.builder()
        .location(new ResourceLocation("astralsorcery", "textures/gui/gridslot.png"))
        .imageSize(18, 18)
        .uv(0, 0, 18, 18)
        .build();
}
