/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.resource;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BindableResource
 * Created by HellFirePvP
 * Date: 09.08.2016 / 11:00
 *
 * 1.7.10 Port:
 * - Simplified texture resource wrapper
 * - Handles texture binding for 1.7.10
 */
public class BindableResource {

    private final ResourceLocation location;
    private int glTextureId = -1;

    public BindableResource(String modId, String texturePath) {
        this.location = new ResourceLocation(modId, texturePath);
    }

    public BindableResource(ResourceLocation location) {
        this.location = location;
    }

    /**
     * Bind this texture for rendering
     * 1.7.10: Uses TextureManager
     */
    public void bind() {
        TextureManager textureManager = net.minecraft.client.Minecraft.getMinecraft()
            .getTextureManager();
        textureManager.bindTexture(location);
        this.glTextureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }

    /**
     * Get the GL texture ID
     */
    public int getGlTextureId() {
        if (glTextureId == -1) {
            bind();
        }
        return glTextureId;
    }

    /**
     * Get the resource location
     */
    public ResourceLocation getLocation() {
        return location;
    }

    /**
     * Invalidate and reload the texture
     * 1.7.10: Reload by re-binding
     */
    public void invalidateAndReload() {
        glTextureId = -1;
    }

}
